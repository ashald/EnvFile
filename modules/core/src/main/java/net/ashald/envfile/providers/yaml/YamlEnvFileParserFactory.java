package net.ashald.envfile.providers.yaml;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class YamlEnvFileParserFactory implements EnvVarsProviderFactory {

    @Override
    public @NotNull
    EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new YamlEnvFileParser(shouldSubstituteEnvVar);
    }

    @NotNull
    public String getTitle() {
        return "JSON/YAML";
    }

}
