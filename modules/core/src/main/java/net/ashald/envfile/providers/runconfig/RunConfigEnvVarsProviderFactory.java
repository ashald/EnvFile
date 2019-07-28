package net.ashald.envfile.providers.runconfig;

import net.ashald.envfile.EnvProviderFactory;
import net.ashald.envfile.EnvVarsFileProvider;
import org.jetbrains.annotations.NotNull;

public class RunConfigEnvVarsProviderFactory implements EnvProviderFactory {


    @NotNull
    @Override
    public EnvVarsFileProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new RunConfigEnvVarsProvider(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return "Run Config";
    }

}
