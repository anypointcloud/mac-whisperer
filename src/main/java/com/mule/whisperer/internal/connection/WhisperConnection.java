package com.mule.whisperer.internal.connection;

import com.mule.whisperer.api.STTParamsModelDetails;
import com.mule.whisperer.api.TTSParamsModelDetails;
import org.mule.runtime.api.metadata.TypedValue;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface WhisperConnection {
    CompletableFuture<String> transcribe(TypedValue<InputStream> audioContent, STTParamsModelDetails params);

    CompletableFuture<InputStream> generate(String text, TTSParamsModelDetails params);
}
