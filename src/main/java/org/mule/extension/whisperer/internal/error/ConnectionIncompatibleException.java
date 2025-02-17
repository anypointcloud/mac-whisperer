package org.mule.extension.whisperer.internal.error;

import org.mule.extension.whisperer.api.error.ConnectorError;
import org.mule.runtime.extension.api.exception.ModuleException;

public class ConnectionIncompatibleException extends ModuleException {
    public ConnectionIncompatibleException(String message) {
        super(message, ConnectorError.CONNECTION_INCOMPATIBLE);
    }
}
