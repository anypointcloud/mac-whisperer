package com.mule.whisperer.internal;

import com.mule.whisperer.api.STTParamsModelDetails;
import com.mule.whisperer.api.TTSParamsModelDetails;
import com.mule.whisperer.internal.connection.WhisperConnection;
import com.mule.whisperer.internal.error.GenerationErrorTypeProvider;
import com.mule.whisperer.internal.error.TranscriptionErrorTypeProvider;
import com.mule.whisperer.internal.metadata.TranscriptionOutputResolver;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.*;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.process.CompletionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class SpeechOperations {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpeechOperations.class);

    @DisplayName("Speech to Text")
    @Alias("speech-to-text")
    @OutputResolver(output = TranscriptionOutputResolver.class, attributes = TranscriptionOutputResolver.class)
    @MediaType(value = MediaType.TEXT_PLAIN, strict = false)
    @Throws(TranscriptionErrorTypeProvider.class)
    public void transcribe(@Connection WhisperConnection connection,
                           @Content TypedValue<InputStream> audioContent,
                           @Optional String finetuningPrompt,
                           @MetadataKeyId @ParameterGroup(name = "Transcription Options") STTParamsModelDetails transcriptionOptions,
                           CompletionCallback<String, Object> callback) {
        connection.transcribe(audioContent, finetuningPrompt, transcriptionOptions).whenComplete((result, e) -> {
            if (null == e) {
                callback.success(Result.<String, Object>builder()
                        .output(result.getOutput())
                        .build());
            } else {
                callback.error(e.getCause());
            }
        });
    }

    @DisplayName("Text to Speech")
    @Alias("text-to-speech")
    @MediaType(value = "audio/mp3", strict = false)
    @Throws(GenerationErrorTypeProvider.class)
    public void generateSpeech(@Connection WhisperConnection connection,
                               @Content String text,
                               @ParameterGroup(name="Generation Options") TTSParamsModelDetails generationOptions,
                               CompletionCallback<InputStream, Void> callback) {

        connection.generate(text, generationOptions).whenComplete((audioData, e) -> {

            if (null == e) {
                callback.success(Result.<InputStream, Void>builder()
                        .output(audioData)
                        .mediaType(inferMediaType(generationOptions))
                        .build());
            } else {
                callback.error(e.getCause());
            }
        });
    }

    private org.mule.runtime.api.metadata.MediaType inferMediaType(TTSParamsModelDetails params) {
        switch (params.getResponseFormat()) {
            case "mp3":
                return org.mule.runtime.api.metadata.MediaType.create("audio", "mp3");
            case "ogg":
                return org.mule.runtime.api.metadata.MediaType.create("audio", "ogg");
            case "aac":
                return org.mule.runtime.api.metadata.MediaType.create("audio", "aac");
            case "flac":
                return org.mule.runtime.api.metadata.MediaType.create("audio", "flac");
            case "pcm":
                return org.mule.runtime.api.metadata.MediaType.create("audio", "pcm");
            case "wav":
                return org.mule.runtime.api.metadata.MediaType.create("audio", "wav");
            default:
                // normally this will be impossible
                LOGGER.warn("unknown media type for speech response format " + params.getResponseFormat());
                return org.mule.runtime.api.metadata.MediaType.BINARY;
        }
    }
}
