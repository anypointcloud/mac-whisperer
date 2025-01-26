package com.mule.whisperer.internal.error;

import com.mule.whisperer.api.error.ConnectorError;
import org.mule.runtime.extension.api.exception.ModuleException;

public class GenerationException extends ModuleException {
    public GenerationException(String message) {
        super(message, ConnectorError.GENERATION);
    }
    public GenerationException(String message, Throwable cause) {
        super(message, ConnectorError.GENERATION, cause);
    }
    public GenerationException(Throwable cause) {
        super(ConnectorError.GENERATION, cause);
    }
}
