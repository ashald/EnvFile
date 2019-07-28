package net.ashald.envfile.providers.yaml;

import net.ashald.envfile.EnvProviderFactory;
import net.ashald.envfile.EnvVarsFileProvider;
import org.jetbrains.annotations.NotNull;

public class YamlEnvFileParserFactory implements EnvProviderFactory {

    @Override
    public @NotNull
    EnvVarsFileProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new YamlEnvFileParser(shouldSubstituteEnvVar);
    }

    @NotNull
    public String getTitle() {
        return "JSON/YAML";
    }

}
