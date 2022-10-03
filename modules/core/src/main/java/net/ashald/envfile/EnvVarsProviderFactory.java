package net.ashald.envfile;

import java.util.Map;


public interface EnvVarsProviderFactory {

    /**
     * @param baseEnvVars env vars defined in the run configuration
     */
    EnvVarsProvider createProvider(Map<String, String> baseEnvVars);

    String getTitle();

    boolean isEditable();

}
