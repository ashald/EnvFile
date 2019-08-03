package net.ashald.envfile.products.goland;

import com.goide.execution.GoRunConfigurationBase;
import com.goide.execution.extension.GoRunConfigurationExtension;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import net.ashald.envfile.platform.ui.EnvVarsConfigurationEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GolandRunConfigurationExtension extends GoRunConfigurationExtension {

    @Nullable
    @Override
    protected String getEditorTitle() {
        return EnvVarsConfigurationEditor.getEditorTitle();
    }

    @Override
    protected void patchCommandLine(@NotNull GoRunConfigurationBase goRunConfigurationBase, @Nullable RunnerSettings runnerSettings, @NotNull GeneralCommandLine generalCommandLine, @NotNull String s) throws ExecutionException {
        Map<String, String> currentEnv = generalCommandLine.getEnvironment();
        Map<String, String> newEnv = EnvVarsConfigurationEditor.collectEnv(goRunConfigurationBase, new HashMap<>(currentEnv));
        currentEnv.clear();
        currentEnv.putAll(newEnv);
    }

    @Override
    protected void validateConfiguration(@NotNull GoRunConfigurationBase configuration, boolean isExecution) throws Exception {
        EnvVarsConfigurationEditor.validateConfiguration(configuration, isExecution);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return EnvVarsConfigurationEditor.getSerializationId();
    }

    @Override
    protected void readExternal(@NotNull GoRunConfigurationBase runConfiguration, @NotNull Element element) {
        EnvVarsConfigurationEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void writeExternal(@NotNull GoRunConfigurationBase runConfiguration, @NotNull Element element) {
        EnvVarsConfigurationEditor.writeExternal(runConfiguration, element);
    }

    @Nullable
    @Override
    protected <P extends GoRunConfigurationBase> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new EnvVarsConfigurationEditor<P>(configuration);
    }

    @Override
    protected boolean isApplicableFor(@NotNull GoRunConfigurationBase goRunConfigurationBase) {
        return true;
    }

    @Override
    protected boolean isEnabledFor(@NotNull GoRunConfigurationBase goRunConfigurationBase, @Nullable RunnerSettings runnerSettings) {
        return true;
    }
}
