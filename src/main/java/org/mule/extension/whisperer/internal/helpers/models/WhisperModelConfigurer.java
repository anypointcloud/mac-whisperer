package org.mule.extension.whisperer.internal.helpers.models;

import org.apache.commons.io.FileUtils;
import org.mule.extension.whisperer.api.error.ConnectorError;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

public class WhisperModelConfigurer {

  private static final Logger LOGGER = LoggerFactory.getLogger(WhisperModelConfigurer.class);

  public static void setup(String modelURL, String modelFilePath) {

    try {

      LOGGER.info("Downloading Whisper Model from: {}", modelURL);
      FileUtils.copyURLToFile(new URL(modelURL), new File(modelFilePath));
      LOGGER.info("Whisper Model downloaded from: {}", modelURL);

    } catch (Exception e) {

      throw new ModuleException(String.format("Failed to download model file from URL: %s ", modelURL),
                                ConnectorError.MODEL_SETUP_FAILURE,
                                e);
    }
  }
}
