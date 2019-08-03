package net.ashald.envfile.platform;

import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.EnvFileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class EnvFileEntry extends EnvVarsEntry<EnvFileProvider> {

    private String path;

    public EnvFileEntry(RunConfigurationBase envFileRunConfig, String envFileParserId, String envFilePath, boolean enabled, boolean substitutionEnabled) {
        super(envFileRunConfig, envFileParserId, enabled, substitutionEnabled);
        path = envFilePath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String value) {
        path = value;
    }

    public boolean validatePath() {
        File file = getFile();
        return file == null || file.exists();
    }

    public Map<String, String> process(Map<String, String> runConfigEnv, Map<String, String> aggregatedEnv, boolean ignoreMissing) throws IOException, EnvFileErrorException {
        EnvFileProvider parser = getProvider();

        if (isEnabled() && parser != null) {
            File file = getFile();
            if (!parser.isFileLocationValid(file) && ignoreMissing) {
                return aggregatedEnv;
            } else {
                return parser.process(runConfigEnv, file == null ? null : file.getPath(), aggregatedEnv);
            }
        }

        return aggregatedEnv;
    }


    private File getFile() {
        if (path == null) {
            return null;
        }
        String resolvedPath = path;
        if (!FileUtil.isAbsolute(resolvedPath)) {
            VirtualFile virtualFile;
            try {
                virtualFile = getRunConfig().getProject().getBaseDir().findFileByRelativePath(resolvedPath);
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
