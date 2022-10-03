package net.ashald.envfile.providers.runconfig;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RunConfigEnvVarsProviderFactory implements EnvVarsProviderFactory {

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars) {
        return new RunConfigEnvVarsProvider(baseEnvVars);
    }

    @Override
    public @NotNull String getTitle() {
        return "Run Config";
    }

    @Override
    public boolean isEditable() {
        return false;
    }

}
