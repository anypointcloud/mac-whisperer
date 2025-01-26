package com.mule.whisperer.internal.connection;

import com.mule.whisperer.api.STTParamsModelDetails;
import com.mule.whisperer.api.TTSParamsModelDetails;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface WhisperConnection {
    CompletableFuture<Result<String, Object>> transcribe(TypedValue<InputStream> audioContent, String fineTuningPrompt, STTParamsModelDetails params);

    CompletableFuture<InputStream> generate(String text, TTSParamsModelDetails params);
}
