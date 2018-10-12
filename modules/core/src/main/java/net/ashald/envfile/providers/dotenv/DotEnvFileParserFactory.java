package net.ashald.envfile.providers.dotenv;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class DotEnvFileParserFactory implements EnvVarsProviderFactory {

    @Override
    public @NotNull
    EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new DotEnvFileParser(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return ".env";
    }

}
