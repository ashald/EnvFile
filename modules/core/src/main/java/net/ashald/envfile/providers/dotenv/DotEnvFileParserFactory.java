package net.ashald.envfile.providers.dotenv;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public @Nullable Condition<VirtualFile> getFileFilter() {
        return null;
    }

    @Override
    public boolean showHiddenFiles() {
        return true;
    }

}
