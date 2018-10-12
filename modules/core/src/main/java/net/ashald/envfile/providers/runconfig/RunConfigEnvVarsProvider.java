package net.ashald.envfile.providers.runconfig;

import net.ashald.envfile.EnvVarsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RunConfigEnvVarsProvider implements EnvVarsProvider {

    @Override
    public boolean isEditable() {
        return false;
    }

    @NotNull
    @Override
    public Map<String, String> process(@NotNull Map<String, String> runConfigEnv, String path, @NotNull Map<String, String> aggregatedEnv) {
        Map<String, String> result = new HashMap<>(aggregatedEnv);

        result.putAll(runConfigEnv);

        return result;
    }
}
