package net.ashald.envfile.products.rubymine;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import net.ashald.envfile.platform.ui.EnvVarsConfigurationEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.run.configuration.AbstractRubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.configuration.RubyRunConfigurationExtension;

import java.util.HashMap;
import java.util.Map;

public class RubyMineRunConfigurationExtension extends RubyRunConfigurationExtension {

    @Nullable
    @Override
    protected String getEditorTitle() {
        return EnvVarsConfigurationEditor.getEditorTitle();
    }

    @Nullable
    @Override
    protected <P extends AbstractRubyRunConfiguration> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new EnvVarsConfigurationEditor<P>(configuration);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return EnvVarsConfigurationEditor.getSerializationId();
    }

    @Override
    protected void writeExternal(@NotNull AbstractRubyRunConfiguration runConfiguration, @NotNull Element element) throws WriteExternalException {
        EnvVarsConfigurationEditor.writeExternal(runConfiguration, element);
    }

    @Override
    protected void readExternal(@NotNull AbstractRubyRunConfiguration runConfiguration, @NotNull Element element) throws InvalidDataException {
        EnvVarsConfigurationEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void validateConfiguration(@NotNull AbstractRubyRunConfiguration configuration, boolean isExecution) throws Exception {
        EnvVarsConfigurationEditor.validateConfiguration(configuration, isExecution);
    }

    @Override
    protected void patchCommandLine(@NotNull AbstractRubyRunConfiguration configuration, @Nullable RunnerSettings runnerSettings, @NotNull GeneralCommandLine cmdLine, @NotNull String runnerId) throws ExecutionException {
        Map<String, String> currentEnv = cmdLine.getEnvironment();
        Map<String, String> newEnv = EnvVarsConfigurationEditor.collectEnv(configuration, new HashMap<>(currentEnv));
        currentEnv.clear();
        currentEnv.putAll(newEnv);
    }

    //

    @Override
    protected boolean isApplicableFor(@NotNull AbstractRubyRunConfiguration configuration) {
        return true;
    }

    @Override
    protected boolean isEnabledFor(@NotNull AbstractRubyRunConfiguration applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }
}
