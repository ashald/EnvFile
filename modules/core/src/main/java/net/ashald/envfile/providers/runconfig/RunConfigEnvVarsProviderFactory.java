package net.ashald.envfile.providers.runconfig;


import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RunConfigEnvVarsProviderFactory implements EnvVarsProviderFactory {


    @NotNull
    @Override
    public EnvVarsProvider createProvider(boolean shouldSubstituteEnvVar) {
        return new RunConfigEnvVarsProvider(shouldSubstituteEnvVar);
    }

    @Override
    public @NotNull String getTitle() {
        return "Run Config";
    }

    @Override
    public @Nullable Condition<VirtualFile> getFileFilter() {
        return null;
    }

    @Override
    public boolean showHiddenFiles() {
        return false;
    }

}
