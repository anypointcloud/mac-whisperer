package com.mule.whisperer.api.error;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

public enum ConnectorError implements ErrorTypeDefinition<ConnectorError> {
    CONNECTION_INCOMPATIBLE,
    TRANSCRIPTION,
    GENERATION,
    TIMEOUT
}
