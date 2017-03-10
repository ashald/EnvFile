package net.ashald.envfile.products.idea;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IdeaRunConfigurationExtension extends RunConfigurationExtension {

    @Nullable
    @Override
    protected String getEditorTitle() {
        return EnvFileConfigurationEditor.getEditorTitle();
    }

    @Nullable
    @Override
    protected <P extends RunConfigurationBase> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new EnvFileConfigurationEditor<P>(configuration);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return EnvFileConfigurationEditor.getSerializationId();
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws WriteExternalException {
        EnvFileConfigurationEditor.writeExternal(runConfiguration, element);
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws InvalidDataException {
        EnvFileConfigurationEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void validateConfiguration(@NotNull RunConfigurationBase configuration, boolean isExecution) throws Exception {
        EnvFileConfigurationEditor.validateConfiguration(configuration, isExecution);
    }

    @Override
    public <T extends RunConfigurationBase> void updateJavaParameters(T configuration, JavaParameters params, RunnerSettings runnerSettings) throws ExecutionException {
        EnvFileConfigurationEditor.patchJavaParameters(configuration, params, runnerSettings);
    }

    //

    @Override
    protected boolean isApplicableFor(@NotNull RunConfigurationBase configuration) {
        return true;
    }

    @Override
    protected boolean isEnabledFor(@NotNull RunConfigurationBase applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }

}
