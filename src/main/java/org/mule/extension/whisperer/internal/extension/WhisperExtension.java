package org.mule.extension.whisperer.internal.extension;

import org.mule.extension.whisperer.api.error.ConnectorError;
import org.mule.extension.whisperer.internal.config.WhispererConfiguration;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;



@Xml(prefix = "whisperer")
@Extension(name = "MAC Whisperer")
@Configurations(WhispererConfiguration.class)
@ErrorTypes(ConnectorError.class)
@JavaVersionSupport({JAVA_11, JAVA_17})
public class WhisperExtension {

}
