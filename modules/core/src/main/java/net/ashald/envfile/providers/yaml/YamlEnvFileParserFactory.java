package net.ashald.envfile.providers.yaml;

import net.ashald.envfile.EnvFileProviderFactory;
import org.jetbrains.annotations.NotNull;

public class YamlEnvFileParserFactory implements EnvFileProviderFactory {

    @Override
    public @NotNull
    YamlEnvFileParser createProvider(boolean shouldSubstituteEnvVar) {
        return new YamlEnvFileParser(shouldSubstituteEnvVar);
    }

    @NotNull
    public String getTitle() {
        return "JSON/YAML";
    }

}
