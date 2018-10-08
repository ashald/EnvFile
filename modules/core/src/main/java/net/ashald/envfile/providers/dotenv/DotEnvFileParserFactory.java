package net.ashald.envfile.providers.dotenv;

import net.ashald.envfile.EnvFileParser;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;

public class DotEnvFileParserFactory implements EnvVarsProviderFactory {

    @Override
    public @NotNull EnvFileParser createParser() {
        return new DotEnvFileParser();
    }

    @Override
    public @NotNull String getTitle() {
        return ".env";
    }

}
