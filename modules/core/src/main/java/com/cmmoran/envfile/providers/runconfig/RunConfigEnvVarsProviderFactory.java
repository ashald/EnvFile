package com.cmmoran.envfile.providers.runconfig;

import com.cmmoran.envfile.EnvVarsProvider;
import com.cmmoran.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class RunConfigEnvVarsProviderFactory implements EnvVarsProviderFactory {

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
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
