package net.ashald.envfile.providers.runconfig;


import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class RunConfigEnvVarsProviderFactory implements EnvVarsProviderFactory {


    @NotNull
    @Override
    public EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new RunConfigEnvVarsProvider(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return "Run Config";
    }

}
