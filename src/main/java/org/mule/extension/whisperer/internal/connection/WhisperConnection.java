package org.mule.extension.whisperer.internal.connection;

import org.mule.extension.whisperer.api.STTParamsModelDetails;
import org.mule.extension.whisperer.api.TTSParamsModelDetails;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.extension.api.runtime.operation.Result;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface WhisperConnection {
    CompletableFuture<Result<String, Object>> transcribe(TypedValue<InputStream> audioContent, String fineTuningPrompt, STTParamsModelDetails params);

    CompletableFuture<InputStream> generate(String text, TTSParamsModelDetails params);
}
