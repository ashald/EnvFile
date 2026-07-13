package net.ashald.envfile.products.goland;

import com.goide.execution.GoRunConfigurationBase;
import com.goide.execution.GoRunningState;
import com.goide.execution.extension.GoRunConfigurationExtension;
import com.goide.util.GoExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import net.ashald.envfile.platform.EnvFileEnvironmentVariables;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GolandRunConfigurationExtension extends GoRunConfigurationExtension {

    private static final Logger LOG = Logger.getInstance(GolandRunConfigurationExtension.class);

    @Nullable
    @Override
    protected String getEditorTitle() {
        return EnvFileConfigurationEditor.getEditorTitle();
    }

    // Local runs: GoLand builds a GeneralCommandLine and invokes this legacy hook.
    @Override
    protected void patchCommandLine(
            @NotNull GoRunConfigurationBase<?> goRunConfigurationBase,
            @Nullable RunnerSettings runnerSettings,
            @NotNull GeneralCommandLine generalCommandLine,
            @NotNull String runnerId
    )
            throws ExecutionException
    {
        Map<String, String> newEnv = new EnvFileEnvironmentVariables(
                EnvFileConfigurationEditor.getEnvFileSetting(goRunConfigurationBase)
        )
                .render(
                        goRunConfigurationBase.getProject(),
                        generalCommandLine.getEnvironment(),
                        generalCommandLine.isPassParentEnvironment()
                );

        if (newEnv == null) {
            return;
        }

        generalCommandLine.getEnvironment().clear();
        generalCommandLine.getEnvironment().putAll(newEnv);
    }

    // Target runs (WSL / remote): GoLand bypasses the GeneralCommandLine hook above and goes through the
    // Targets API. The executor-level hook below fires for those runs (confirmed for WSL), so we inject the
    // EnvFile variables into the GoExecutor here. We add only the delta over the (Windows) parent environment
    // to avoid leaking host-only variables (PATH, ProgramFiles, ...) into the target (e.g. Linux under WSL).
    @Override
    protected void patchExecutor(
            @NotNull GoRunConfigurationBase<?> configuration,
            @Nullable RunnerSettings runnerSettings,
            @NotNull GoExecutor executor,
            @NotNull String runnerId,
            @NotNull GoRunningState<? extends GoRunConfigurationBase<?>> state,
            @NotNull GoRunningState.CommandLineType commandLineType
    )
            throws ExecutionException
    {
        if (commandLineType != GoRunningState.CommandLineType.RUN) {
            return;
        }

        Map<String, String> newEnv = new EnvFileEnvironmentVariables(
                EnvFileConfigurationEditor.getEnvFileSetting(configuration)
        )
                .render(
                        configuration.getProject(),
                        configuration.getCustomEnvironment(),
                        configuration.isPassParentEnvironment()
                );

        if (newEnv == null) {
            return;
        }

        Map<String, String> parentEnv = new GeneralCommandLine()
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
                .getParentEnvironment();

        Map<String, String> delta = new HashMap<>();
        for (Map.Entry<String, String> entry : newEnv.entrySet()) {
            if (!entry.getValue().equals(parentEnv.get(entry.getKey()))) {
                delta.put(entry.getKey(), entry.getValue());
            }
        }

        executor.withUserDefinedEnvironment(delta);

        LOG.debug("EnvFile: injected " + delta.size()
                + " variable(s) into GoExecutor for target run: " + delta.keySet());
    }

    @Override
    protected void validateConfiguration(@NotNull GoRunConfigurationBase configuration, boolean isExecution) throws Exception {
        EnvFileConfigurationEditor.validateConfiguration(configuration, isExecution);
    }

    @NotNull
    @Override
    protected String getSerializationId() {
        return EnvFileConfigurationEditor.getSerializationId();
    }

    @Override
    protected void readExternal(@NotNull GoRunConfigurationBase runConfiguration, @NotNull Element element) {
        EnvFileConfigurationEditor.readExternal(runConfiguration, element);
    }

    @Override
    protected void writeExternal(@NotNull GoRunConfigurationBase runConfiguration, @NotNull Element element) {
        EnvFileConfigurationEditor.writeExternal(runConfiguration, element);
    }

    @Nullable
    @Override
    public <P extends GoRunConfigurationBase<?>> SettingsEditor<P> createEditor(@NotNull P configuration) {
        return new EnvFileConfigurationEditor<P>(configuration);
    }

    @Override
    public boolean isApplicableFor(@NotNull GoRunConfigurationBase goRunConfigurationBase) {
        return true;
    }

    @Override
    public boolean isEnabledFor(@NotNull GoRunConfigurationBase goRunConfigurationBase, @Nullable RunnerSettings runnerSettings) {
        return true;
    }
}
