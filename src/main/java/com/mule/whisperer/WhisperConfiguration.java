package com.mule.whisperer;

import com.mule.whisperer.internal.SpeechOperations;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.param.Parameter;


@Operations(SpeechOperations.class)
public class WhisperConfiguration {

  @Parameter
   private boolean useLocalWhisper;
  public boolean isUseLocalWhisper() {
    return this.useLocalWhisper;
  }
}
