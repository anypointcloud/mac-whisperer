package com.mule.whisperer.internal.connection;

import com.mule.whisperer.api.STTParamsModelDetails;
import com.mule.whisperer.api.TTSParamsModelDetails;
import org.json.JSONObject;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.domain.entity.ByteArrayHttpEntity;
import org.mule.runtime.http.api.domain.entity.multipart.HttpPart;
import org.mule.runtime.http.api.domain.entity.multipart.MultipartHttpEntity;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.mule.runtime.http.api.HttpConstants.Method.GET;
import static org.mule.runtime.http.api.HttpConstants.Method.POST;

public class OpenAiConnection implements WhisperConnection {
    private static Logger LOGGER = LoggerFactory.getLogger(OpenAiConnection.class);
    private final String apiKey;
    private final HttpClient httpClient;
    private final URI apiUri;
    public OpenAiConnection(String apiKey, HttpClient httpClient, URI apiUri) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.apiUri = apiUri;
    }

    public void validate() throws ConnectionException {
        // health check with https://platform.openai.com/docs/api-reference/models
        URI healthCheckEndpoint = apiUri.resolve("models");
        HttpRequest request = HttpRequest.builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .method(GET)
                .uri(healthCheckEndpoint)
                .build();
        try {
            HttpResponse response = httpClient.send(request);

            if (200 != response.getStatusCode()) {
                throw new ConnectionException("Unexpected status code " + response.getStatusCode() + " from " + healthCheckEndpoint.toString());
            }
            LOGGER.trace("Successfully validated connection " + healthCheckEndpoint.toString());
        } catch (IOException e) {
            throw new ConnectionException(e);
        } catch (TimeoutException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public CompletableFuture<String> transcribe(TypedValue<InputStream> audioContent, STTParamsModelDetails params) {
        URI transcriptionEndpoint = apiUri.resolve("audio/transcriptions");
        byte[] audioBytes = IOUtils.toByteArray(audioContent.getValue());
        HttpPart modelPart = new HttpPart("model", params.getModelName().getBytes(), MediaType.TEXT_PLAIN, params.getModelName().getBytes().length);
        HttpPart formatPart = new HttpPart("response_format", "text".getBytes(), MediaType.TEXT_PLAIN, "text".getBytes().length);
        HttpPart audioPart = new HttpPart("file", "speech." + guessAudioFileExtension(audioContent.getDataType().getMediaType()), audioBytes, audioContent.getDataType().getMediaType().toString(), audioBytes.length);

        HttpRequest request = HttpRequest.builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .method(POST)
                .uri(transcriptionEndpoint)
                .entity(new MultipartHttpEntity(Arrays.asList(audioPart, modelPart, formatPart)))
                .build();
        return httpClient.sendAsync(request)
                .thenApply(response -> {
                    if (200 != response.getStatusCode()) {
                        throw new RuntimeException("Unexpected status code " + response.getStatusCode() + " from OpenAI API");
                    }
                    return IOUtils.toString(response.getEntity().getContent());
                });
    }

    @Override
    public CompletableFuture<InputStream> generate(String text, TTSParamsModelDetails params) {
        URI speechEndpoint = apiUri.resolve("audio/speech");

        JSONObject requestObject = new JSONObject();
        requestObject.put("model", params.getModelName());
        requestObject.put("input", text);
        requestObject.put("voice", params.getVoice());
        requestObject.put("response_format", params.getResponseFormat());
        requestObject.put("speed", params.getSpeed());

        HttpRequest request = HttpRequest.builder()
                .addHeader("Authorization", "Bearer " + apiKey)
                .method(POST)
                .uri(speechEndpoint)
                .addHeader("Content-Type", "application/json")
                .entity(new ByteArrayHttpEntity(requestObject.toString().getBytes()))
                .build();

        return httpClient.sendAsync(request).thenApply(response -> {
            if (200 != response.getStatusCode()) {
                throw new RuntimeException("Unexpected status code " + response.getStatusCode() + " from OpenAI API");
            }
            return response.getEntity().getContent();
        });
    }

    private static String guessAudioFileExtension(org.mule.runtime.api.metadata.MediaType audioContentType) {
        String extension = "mp3";
        switch (audioContentType.withoutParameters().toString()) {
            case "audio/m4a":
            case "audio/mp4":
                extension = "m4a";
                break;
            case "audio/flac":
            case "audio/x-flac":
                extension = "flac";
                break;
            case "audio/wav":
            case "audio/vnd.wav":
            case "audio/vnd.wave":
            case "audio/wave":
            case "audio/x-wav":
            case "audio/x-pn-wav":
                extension = "wav";
                break;
            case "audio/ogg":
                extension = "ogg";
                break;
            case "audio/webm":
                extension = "weba";
                break;
            case "audio/aac":
                extension = "aac";
                break;
        }
        return extension;
    }
}
