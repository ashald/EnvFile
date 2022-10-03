package net.ashald.envfile.platform;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

@Data
@Builder
public class EnvFileEntry {
    @NonNull
    private final RunConfigurationBase<?> runConfig;

    @NonNull
    private final String parserId;

    private String path;

    @NonNull
    private Boolean enabled;

    @NonNull
    private Boolean substitutionEnabled;

    @NonNull
    private Boolean executable;

    public boolean isEnabled() {
        return Boolean.TRUE.equals(getEnabled());
    }

    public boolean isSubstitutionEnabled() {
        return Boolean.TRUE.equals(getSubstitutionEnabled());
    }

    public boolean isExecutable() {
        return Boolean.TRUE.equals(getExecutable());
    }

    public String getTypeTitle() {
        EnvVarsProviderFactory factory = getProviderFactory();
        return factory == null ? String.format("<%s>", parserId) : factory.getTitle();
    }

    public boolean validatePath() {
        File file = getFile();
        return file == null || file.exists();
    }

    public boolean validateType() {
        return getProvider() != null;
    }

    public Map<String, String> process(
            Map<String, String> runConfigEnv,
            Map<String, String> aggregatedEnv,
            boolean ignoreMissing,
            boolean isExecutable
    ) throws IOException, EnvFileErrorException {
        InputStream content = null;
        String filePath = Optional.ofNullable(
                        getFile()
                ).map(File::getPath)
                .orElse(null);

        if (isExecutable) {
            content = Runtime.getRuntime().exec(filePath).getInputStream();
        } else {
            if (filePath != null) {
                content = Files.newInputStream(new File(filePath).toPath());
            }
        }

        try {
            EnvVarsProvider parser = getProvider();

            if (getEnabled() && parser != null) {
                File file = getFile();
                if (!parser.isFileLocationValid(file) && ignoreMissing) {
                    return aggregatedEnv;
                } else {
                    return parser.process(runConfigEnv, aggregatedEnv, content, filePath);
                }
            }
        } finally {
            if (content != null) {
                content.close();
            }
        }

        return aggregatedEnv;
    }

    public boolean isEditable() {
        EnvVarsProvider provider = getProvider();
        return provider != null && getProvider().isEditable();
    }

    @Nullable
    private EnvVarsProviderFactory getProviderFactory() {
        EnvVarsProviderExtension extension = EnvVarsProviderExtension.getParserExtensionById(parserId);
        return extension == null ? null : extension.getFactory();
    }

    @Nullable
    private EnvVarsProvider getProvider() {
        EnvVarsProviderFactory factory = getProviderFactory();
        return factory == null ? null : factory.createProvider(substitutionEnabled);
    }

    private File getFile() {
        if (path == null) {
            return null;
        }
        String resolvedPath = path;
        if (!FileUtil.isAbsolute(resolvedPath)) {
            VirtualFile virtualFile;
            try {
                virtualFile = runConfig.getProject().getBaseDir().findFileByRelativePath(resolvedPath);
            } catch (AssertionError ignored) { // can bee thrown deep from IoC implementation
                virtualFile = null;
            }
            if (virtualFile != null) {
                resolvedPath = virtualFile.getPath();
            }
        }
        return new File(resolvedPath);
    }
}
