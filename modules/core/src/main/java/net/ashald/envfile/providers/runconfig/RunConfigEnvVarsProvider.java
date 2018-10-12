package net.ashald.envfile.providers.runconfig;

import net.ashald.envfile.AbstractEnvVarsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RunConfigEnvVarsProvider extends AbstractEnvVarsProvider {

    public RunConfigEnvVarsProvider(boolean shouldSubstituteEnvVar) {
        super(shouldSubstituteEnvVar);
    }

    @NotNull
    @Override
    protected Map<String, String> getEnvVars(@NotNull Map<String, String> runConfigEnv, String path) {
        return new HashMap<>(runConfigEnv);
    }

    @Override
    public boolean isEditable() {
        return false;
    }

}
