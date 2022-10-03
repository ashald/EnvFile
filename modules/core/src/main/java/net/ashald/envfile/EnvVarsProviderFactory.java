package net.ashald.envfile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;


public interface EnvVarsProviderFactory {

    @NotNull
    EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar);

    @NotNull String getTitle();

    @Nullable Predicate<String> getFileNamePredicate();

    boolean showHiddenFiles();
}
