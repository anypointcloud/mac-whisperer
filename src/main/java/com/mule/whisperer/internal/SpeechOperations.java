package com.mule.whisperer.internal;

import com.mule.whisperer.internal.connection.WhisperConnection;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.annotation.Alias;
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
    @MediaType(MediaType.TEXT_PLAIN)
    public void transcribe(@Connection WhisperConnection connection,
                           @Content TypedValue<InputStream> audioContent,
                           @Optional String finetuningPrompt,
                           CompletionCallback<String, Void> callback) {
        connection.transcribe(audioContent).whenComplete((transcribedText, e) -> {
            if (null == e) {
                // no error
                callback.success(Result.<String, Void>builder()
                        .output(transcribedText)
                        .build());
            } else {
                // error, TODO: implement as Mule error
                callback.error(e);
            }
        });
    }
}
