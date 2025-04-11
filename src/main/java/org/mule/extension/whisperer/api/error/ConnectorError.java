package org.mule.extension.whisperer.api.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public enum ConnectorError implements ErrorTypeDefinition<ConnectorError> {
    CONNECTION_INCOMPATIBLE,
    TRANSCRIPTION,
    MODEL_SETUP_FAILURE,
    GENERATION,
    AUDIO_FORMAT_NOT_SUPPORTED,
    TIMEOUT
}
