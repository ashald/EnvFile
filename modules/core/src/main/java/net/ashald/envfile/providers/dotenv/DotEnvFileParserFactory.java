package net.ashald.envfile.providers.dotenv;

import net.ashald.envfile.EnvProviderFactory;
import net.ashald.envfile.EnvVarsFileProvider;
import org.jetbrains.annotations.NotNull;

public class DotEnvFileParserFactory implements EnvProviderFactory {

    @Override
    public @NotNull
    EnvVarsFileProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new DotEnvFileParser(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return ".env";
    }

}
