package net.ashald.envfile.products.goland;

import com.goide.execution.GoRunConfigurationBase;
import com.goide.execution.GoRunningState;
import com.goide.execution.extension.GoRunConfigurationExtension;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.target.TargetedCommandLine;
import com.intellij.execution.target.TargetedCommandLineBuilder;
import com.intellij.openapi.options.SettingsEditor;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GolandRunConfigurationExtension extends GoRunConfigurationExtension {

  @Nullable
  @Override
  protected String getEditorTitle() {
    return EnvFileConfigurationEditor.getEditorTitle();
  }

  @Override
  protected void patchCommandLine(
      @NotNull GoRunConfigurationBase<?> configuration,
      @Nullable RunnerSettings runnerSettings,
      @NotNull TargetedCommandLineBuilder cmdLine,
      @NotNull String runnerId,
      @NotNull GoRunningState<? extends GoRunConfigurationBase<?>> state,
      @NotNull GoRunningState.CommandLineType commandLineType)
      throws ExecutionException {
    TargetedCommandLine currentCmdLine = cmdLine.build();
    Map<String, String> currentEnv = currentCmdLine.getEnvironmentVariables();
    Map<String, String> newEnv =
        EnvFileConfigurationEditor.collectEnv(state.getConfiguration(), new HashMap<>(currentEnv));
    newEnv.forEach(cmdLine::addEnvironmentVariable);
  }

  @Override
  protected void validateConfiguration(
      @NotNull GoRunConfigurationBase configuration, boolean isExecution) throws Exception {
    EnvFileConfigurationEditor.validateConfiguration(configuration, isExecution);
  }

  @NotNull
  @Override
  protected String getSerializationId() {
    return EnvFileConfigurationEditor.getSerializationId();
  }

  @Override
  protected void readExternal(
      @NotNull GoRunConfigurationBase runConfiguration, @NotNull Element element) {
    EnvFileConfigurationEditor.readExternal(runConfiguration, element);
  }

  @Override
  protected void writeExternal(
      @NotNull GoRunConfigurationBase runConfiguration, @NotNull Element element) {
    EnvFileConfigurationEditor.writeExternal(runConfiguration, element);
  }

  @Nullable
  @Override
  public <P extends GoRunConfigurationBase<?>> SettingsEditor<P> createEditor(
      @NotNull P configuration) {
    return new EnvFileConfigurationEditor<>(configuration);
  }

  @Override
  public boolean isApplicableFor(@NotNull GoRunConfigurationBase goRunConfigurationBase) {
    return true;
  }

  @Override
  public boolean isEnabledFor(
      @NotNull GoRunConfigurationBase goRunConfigurationBase,
      @Nullable RunnerSettings runnerSettings) {
    return true;
  }
}
