package org.mule.extension.whisperer.internal.config;

import org.mule.extension.whisperer.internal.operation.WhispererOperations;
import org.mule.extension.whisperer.internal.connection.LocalWhisperConnectionProvider;
import org.mule.extension.whisperer.internal.connection.OpenAiConnectionProvider;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;


@Operations(WhispererOperations.class)
@ConnectionProviders({OpenAiConnectionProvider.class, LocalWhisperConnectionProvider.class})
public class WhisperConfiguration {
}
