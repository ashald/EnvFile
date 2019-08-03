package net.ashald.envfile.providers.ip;

import net.ashald.envfile.EnvSingleProviderFactory;
import net.ashald.envfile.utils.NetworkInterfaceUtil;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public List<String> getOptions() {
        return NetworkInterfaceUtil.getNetworkInterfaceWithIpV4Names();
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Set the Env-Variable to the IP (v4) of the selected Network Interface";
    }
}
