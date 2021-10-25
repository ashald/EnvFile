package net.ashald.envfile;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface EnvVarsProviderFactory {

    @NotNull
    EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar);

    @NotNull String getTitle();

    @Nullable Condition<VirtualFile> getFileFilter();

    boolean showHiddenFiles();
}
