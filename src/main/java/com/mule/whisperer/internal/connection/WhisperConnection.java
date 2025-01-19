package com.mule.whisperer.internal.connection;

import org.mule.runtime.api.metadata.TypedValue;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface WhisperConnection {
    CompletableFuture<String> transcribe(TypedValue<InputStream> audioContent);
    CompletableFuture<String> transcribe(TypedValue<InputStream> audioContent, String fineTuningPrompt);
}
