package org.mule.extension.whisperer.internal.error;

import org.mule.extension.whisperer.api.error.ConnectorError;
import org.mule.runtime.extension.api.exception.ModuleException;

public class TranscriptionException extends ModuleException {
    public TranscriptionException(String message) {
        super(message, ConnectorError.TRANSCRIPTION);
    }
    public TranscriptionException(String message, Throwable cause) {
        super(message, ConnectorError.TRANSCRIPTION, cause);
    }

    public TranscriptionException(Throwable cause) {
        super(ConnectorError.TRANSCRIPTION, cause);
    }
}
