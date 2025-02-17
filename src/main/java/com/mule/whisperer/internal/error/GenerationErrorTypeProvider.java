package com.mule.whisperer.internal.error;

import com.mule.whisperer.api.error.ConnectorError;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

public class GenerationErrorTypeProvider implements ErrorTypeProvider {
    @Override
    public Set<ErrorTypeDefinition> getErrorTypes() {
        Set<ErrorTypeDefinition> errorTypes = new HashSet<>();
        errorTypes.add(ConnectorError.GENERATION);
        errorTypes.add(ConnectorError.TIMEOUT);
        return errorTypes;
    }
}
