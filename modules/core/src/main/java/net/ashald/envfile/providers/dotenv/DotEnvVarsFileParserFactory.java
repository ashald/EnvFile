package net.ashald.envfile.providers.dotenv;

import net.ashald.envfile.EnvFileProviderFactory;
import net.ashald.envfile.EnvFileProvider;
import org.jetbrains.annotations.NotNull;

public class DotEnvVarsFileParserFactory implements EnvFileProviderFactory {

    @Override
    public @NotNull
    DotEnvFileParser createProvider(boolean shouldSubstituteEnvVar) {
        return new DotEnvFileParser(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return ".env";
    }

}
