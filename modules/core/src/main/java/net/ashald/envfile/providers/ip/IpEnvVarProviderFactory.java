package net.ashald.envfile.providers.ip;

import net.ashald.envfile.EnvVarProviderFactory;
import net.ashald.envfile.EnvVarsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IpEnvVarProviderFactory implements EnvVarProviderFactory {

    @Override
    public @NotNull EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new IpEnvVarProvider(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return "IP";
    }

    @Override
    public List<String> getOptions() {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        return list;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Set the Env-Variable to the IP of the selected Network Interface";
    }
}
