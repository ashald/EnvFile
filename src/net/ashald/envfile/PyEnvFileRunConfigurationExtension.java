package net.ashald.envfile;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.run.PythonRunConfigurationExtension;
import net.ashald.envfile.parsers.EnvFileErrorException;
import net.ashald.envfile.parsers.EnvFileParserExtension;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PyEnvFileRunConfigurationExtension extends PythonRunConfigurationExtension {

    @Override
    protected void readExternal(@NotNull AbstractPythonRunConfiguration runConfiguration, @NotNull Element element) throws InvalidDataException {
    }

    @Override
    protected void writeExternal(@NotNull AbstractPythonRunConfiguration runConfiguration, @NotNull Element element) throws WriteExternalException {
    }

    protected void validateConfiguration(@NotNull final AbstractPythonRunConfiguration configuration, final boolean isExecution) throws Exception {
        EnvFileSettings settings = new EnvFileSettings(configuration.getEnvs());

        if (settings.popIsEnabled()) {
            String path = FileUtil.toSystemDependentName(settings.popEnvFilePath());

            if (path.isEmpty()) {
                throw new ExecutionException("Environment variables file enabled but path is not set!");
            }

            File envFile = getFile(configuration.getProject().getBaseDir(), path);

            if (!envFile.exists()) {
                throw new ExecutionException(String.format("The '%s' environment variables file doesn't exist!", path));
            }

            if (envFile.isDirectory()) {
                throw new ExecutionException("The environment variables file set to a directory!");
            }

            String parserId = settings.popEnvFileParser();
            if (parserId == null || parserId.isEmpty()) {
                throw new ExecutionException("The environment variables file format is not set!");
            }
        }
    }

    protected File getFile(VirtualFile baseDir, String systemPath) {
        if (!FileUtil.isAbsolute(systemPath)) {
            VirtualFile virtualFile = baseDir.findFileByRelativePath(systemPath);
            if (virtualFile != null) {
                systemPath = virtualFile.getPath();
            }
        }
        return new File(systemPath);
    }

    @Nullable
    @Override
    protected String getEditorTitle() {
        return null;
    }

    @Override
    protected boolean isApplicableFor(@NotNull AbstractPythonRunConfiguration configuration) {
        return true;
    }

    @Override
    protected boolean isEnabledFor(@NotNull AbstractPythonRunConfiguration applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }

    @Override
    protected void patchCommandLine(@NotNull AbstractPythonRunConfiguration configuration, @Nullable RunnerSettings runnerSettings, @NotNull GeneralCommandLine cmdLine, @NotNull String runnerId) throws ExecutionException {
        Map<String, String> cmdEnv = cmdLine.getEnvironment();
        EnvFileSettings settings = new EnvFileSettings(cmdEnv);

        boolean isEnabled = settings.popIsEnabled();
        String systemPath = FileUtil.toSystemDependentName(settings.popEnvFilePath());
        String resolvedPath = getFile(configuration.getProject().getBaseDir(), systemPath).getPath();

        String parserId = settings.popEnvFileParser();

        cmdEnv.clear();

        if (isEnabled) {
            Map<String, String> envsFromFile;
            try {
                envsFromFile = EnvFileParserExtension.getParserExtensionById(parserId)
                        .getParser().readFile(resolvedPath);

            } catch (EnvFileErrorException e) {
                throw new ExecutionException(e);
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
            cmdEnv.putAll(envsFromFile);
        }

        cmdEnv.putAll(settings.getEnvVars());
    }

    @Nullable
    @Override
    protected SettingsEditor<AbstractPythonRunConfiguration> createEditor(@NotNull AbstractPythonRunConfiguration configuration) {
        return null;
    }
}
