package net.ashald.envfile.providers.dotenv;

import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import net.ashald.envfile.providers.EnvFileExecutor;
import net.ashald.envfile.providers.EnvFileReader;
import net.ashald.envfile.providers.SingleFileEnvVarsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

import java.util.Map;
import java.util.function.Consumer;

public class DotEnvFileParserFactory implements EnvVarsProviderFactory {

    @Override
    public EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger) {
        return SingleFileEnvVarsProvider.builder()
                .reader(EnvFileReader.DEFAULT)
                .executor(EnvFileExecutor.DEFAULT)
                .parser(DotEnvFileParser.INSTANCE)
                .logger(logger)
                .build();
    }


    @Override
    public @NotNull String getTitle() {
        return ".env";
    }

    @Override
    public boolean isEditable() {
        return true;
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
