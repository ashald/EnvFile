package net.ashald.envfile.providers.ip;

import net.ashald.envfile.EnvSingleProviderFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IpEnvSingleProviderFactory implements EnvSingleProviderFactory {

    @Override
    public @NotNull IpEnvSingleProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new IpEnvSingleProvider(shouldSubstituteEnvVar);
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
