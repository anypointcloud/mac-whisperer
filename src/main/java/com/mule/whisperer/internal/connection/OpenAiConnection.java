package com.mule.whisperer.internal.connection;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.http.api.client.HttpClient;
import org.mule.runtime.http.api.domain.entity.multipart.HttpPart;
import org.mule.runtime.http.api.domain.entity.multipart.MultipartHttpEntity;
import org.mule.runtime.http.api.domain.entity.multipart.Part;
import org.mule.runtime.http.api.domain.message.request.HttpRequest;
import org.mule.runtime.http.api.domain.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import static org.mule.runtime.http.api.HttpConstants.Method.GET;
import static org.mule.runtime.http.api.HttpConstants.Method.POST;

public class OpenAiConnection implements WhisperConnection {
    private static Logger LOGGER = LoggerFactory.getLogger(OpenAiConnection.class);
    private final String apiKey;
    private final HttpClient httpClient;
    private final URI apiUri;
    private final String model;
    public OpenAiConnection(String apiKey, HttpClient httpClient, URI apiUri, String model) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.apiUri = apiUri;
        this.model = model;
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
    public CompletableFuture<String> transcribe(TypedValue<InputStream> audioContent) {
        URI transcriptionEndpoint = apiUri.resolve("audio/transcriptions");
        byte[] audioBytes = IOUtils.toByteArray(audioContent.getValue());
        HttpPart modelPart = new HttpPart("model", model.getBytes(), MediaType.TEXT_PLAIN, model.getBytes().length);
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
    public CompletableFuture<String> transcribe(TypedValue<InputStream> audioContent, String fineTuningPrompt) {
        return null;
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
            case "audio.webm":
                extension = "weba";
                break;
        }
        return extension;
    }
}
