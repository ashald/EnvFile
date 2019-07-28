package net.ashald.envfile.providers.ip;

import net.ashald.envfile.EnvProviderFactory;
import net.ashald.envfile.EnvVarsProvider;
import org.jetbrains.annotations.NotNull;

public class IpEnvVarProviderFactory implements EnvProviderFactory {

    @Override
    public @NotNull EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new IpEnvVarProvider(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return "IP";
    }
}
