package net.ashald.envfile.providers.runconfig;

import net.ashald.envfile.EnvFileProviderFactory;
import net.ashald.envfile.EnvVarsProviderFactory;
import net.ashald.envfile.EnvFileProvider;
import org.jetbrains.annotations.NotNull;

public class RunConfigEnvVarsProviderFactory implements EnvFileProviderFactory {


    @NotNull
    @Override
    public RunConfigEnvProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new RunConfigEnvProvider(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return "Run Config";
    }

}
