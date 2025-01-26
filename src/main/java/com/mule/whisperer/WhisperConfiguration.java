package com.mule.whisperer;

import com.mule.whisperer.internal.SpeechOperations;
import com.mule.whisperer.internal.connection.LocalWhisperConnectionProvider;
import com.mule.whisperer.internal.connection.OpenAiConnectionProvider;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Parameter;


@Operations(SpeechOperations.class)
@ConnectionProviders({OpenAiConnectionProvider.class, LocalWhisperConnectionProvider.class})
public class WhisperConfiguration {
}
