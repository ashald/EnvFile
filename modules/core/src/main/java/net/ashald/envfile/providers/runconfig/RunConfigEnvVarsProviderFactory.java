package net.ashald.envfile.providers.runconfig;


import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class RunConfigEnvVarsProviderFactory implements EnvVarsProviderFactory {


    @NotNull
    @Override
    public EnvVarsProvider createProvider() {
        return new RunConfigEnvVarsProvider();
    }

    @Override
    public @NotNull String getTitle() {
        return "Run Config";
    }

}
