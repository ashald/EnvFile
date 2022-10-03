package net.ashald.envfile.providers.dotenv;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

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

    @Override
    public @Nullable Predicate<String> getFileNamePredicate() {
        return null;
    }

    @Override
    public boolean showHiddenFiles() {
        return true;
    }

}
