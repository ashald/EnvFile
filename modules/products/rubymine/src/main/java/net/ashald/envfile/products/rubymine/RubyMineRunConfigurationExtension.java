package net.ashald.envfile.products.rubymine;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import net.ashald.envfile.platform.EnvUtil;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.run.configuration.AbstractRubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.configuration.RubyRunConfigurationExtension;

import java.util.Map;

public class RubyMineRunConfigurationExtension extends RubyRunConfigurationExtension {

    @Nullable
    @Override
    protected String getEditorTitle() {
        return EnvFileConfigurationEditor.getEditorTitle();
    }

    @Nullable
    @Override
    protected <P extends AbstractRubyRunConfiguration<?>> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new EnvFileConfigurationEditor<P>(configuration);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return EnvFileConfigurationEditor.getSerializationId();
    }

    @Override
    protected void writeExternal(@NotNull AbstractRubyRunConfiguration runConfiguration, @NotNull Element element) throws WriteExternalException {
        EnvFileConfigurationEditor.writeExternal(runConfiguration, element);
    }

    @Override
    protected void readExternal(@NotNull AbstractRubyRunConfiguration runConfiguration, @NotNull Element element) throws InvalidDataException {
        EnvFileConfigurationEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void validateConfiguration(@NotNull AbstractRubyRunConfiguration configuration, boolean isExecution) throws Exception {
        EnvFileConfigurationEditor.validateConfiguration(configuration, isExecution);
    }

    @Override
    protected void patchCommandLine(@NotNull AbstractRubyRunConfiguration<?> configuration, @Nullable RunnerSettings runnerSettings, @NotNull GeneralCommandLine cmdLine, @NotNull String runnerId) throws ExecutionException {
        Map<String, String> newEnv = EnvFileConfigurationEditor.collectEnv(configuration, EnvUtil.getInitialEnv(cmdLine));
        Map<String, String> currentEnv = cmdLine.getEnvironment();
        currentEnv.clear();
        currentEnv.putAll(newEnv);
    }

    //

    @Override
    public boolean isApplicableFor(@NotNull AbstractRubyRunConfiguration configuration) {
        return true;
    }

    @Override
    public boolean isEnabledFor(@NotNull AbstractRubyRunConfiguration applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
        return true;
    }
}
