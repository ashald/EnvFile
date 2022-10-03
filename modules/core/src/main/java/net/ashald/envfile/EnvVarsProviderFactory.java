package net.ashald.envfile;

import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;


public interface EnvVarsProviderFactory {

    /**
     * @param baseEnvVars env vars defined in the run configuration
     */
    EnvVarsProvider createProvider(Map<String, String> baseEnvVars, Consumer<String> logger);

    String getTitle();

    boolean isEditable();

    @Nullable Predicate<String> getFileNamePredicate();

    boolean showHiddenFiles();
}
