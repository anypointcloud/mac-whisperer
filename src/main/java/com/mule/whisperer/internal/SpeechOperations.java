package com.mule.whisperer.internal;

import com.mule.whisperer.api.STTParamsModelDetails;
import com.mule.whisperer.api.TTSParamsModelDetails;
import com.mule.whisperer.internal.connection.WhisperConnection;
import org.mule.runtime.api.meta.ExpressionSupport;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
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
    @MediaType(MediaType.TEXT_PLAIN) // TODO: consider changing this for verbose-text
    public void transcribe(@Connection WhisperConnection connection,
                           @Content TypedValue<InputStream> audioContent,
                           @Optional String finetuningPrompt,
                           @ParameterDsl(allowReferences = false) @Expression(ExpressionSupport.NOT_SUPPORTED) STTParamsModelDetails transcriptionOptions,
                           CompletionCallback<String, Void> callback) {
        connection.transcribe(audioContent, transcriptionOptions).whenComplete((transcribedText, e) -> {
            if (null == e) {
                callback.success(Result.<String, Void>builder()
                        .output(transcribedText)
                        .build());
            } else {
                // error, TODO: implement as Mule error
                callback.error(e);
            }
        });
    }

    @DisplayName("Text to Speech")
    @Alias("text-to-speech")
    @MediaType(value = "audio/mp3", strict = false)
    public void generateSpeech(@Connection WhisperConnection connection,
                               @Content String text,
                               @ParameterDsl(allowReferences = false) @Expression(ExpressionSupport.NOT_SUPPORTED) TTSParamsModelDetails generationOptions,
                               CompletionCallback<InputStream, Void> callback) {

        connection.generate(text, generationOptions).whenComplete((audioData, e) -> {

            if (null == e) {
                callback.success(Result.<InputStream, Void>builder()
                        .output(audioData)
                        .mediaType(inferMediaType(generationOptions))
                        .build());
            } else {
                // error, TODO: implement as Mule error
                callback.error(e);
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
