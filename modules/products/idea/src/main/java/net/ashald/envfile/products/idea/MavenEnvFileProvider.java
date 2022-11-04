package net.ashald.envfile.products.idea;

import java.util.Map;
import java.util.Optional;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.task.ExecuteRunConfigurationTask;
import net.ashald.envfile.platform.EnvFileEnvironmentVariables;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunConfiguration;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.execution.build.MavenExecutionEnvironmentProvider;

public class MavenEnvFileProvider implements MavenExecutionEnvironmentProvider {
  @Override
  public boolean isApplicable(@NotNull final ExecuteRunConfigurationTask task) {
    return false;
  }

  @Override
  public @Nullable ExecutionEnvironment createExecutionEnvironment(
      @NotNull final Project project,
      @NotNull final ExecuteRunConfigurationTask executeRunConfigurationTask,
      @Nullable final Executor executor
  ) {
    final ExecutionEnvironment environment = delegateProvider(executeRunConfigurationTask)
        .map(provider -> provider.createExecutionEnvironment(
            project,
            executeRunConfigurationTask,
            executor
        ))
        .orElse(null);

    if (environment != null && environment.getRunProfile() instanceof MavenRunConfiguration) {
      final ApplicationConfiguration sourceConfig =
          (ApplicationConfiguration) executeRunConfigurationTask.getRunProfile();
      final MavenRunConfiguration targetConfig =
          (MavenRunConfiguration) environment.getRunProfile();
      applyEnvFile(sourceConfig, targetConfig);
    }

    return environment;
  }

  private void applyEnvFile(
      final ApplicationConfiguration sourceConfig,
      final MavenRunConfiguration targetConfig
  ) {
    Map<String, String> newEnv;
    try {
      newEnv = new EnvFileEnvironmentVariables(
          EnvFileConfigurationEditor.getEnvFileSetting(sourceConfig)
      )
          .render(
              sourceConfig.getProject(),
              sourceConfig.getEnvs(),
              sourceConfig.isPassParentEnvs()
          );

    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }

    if (newEnv != null) {
      MavenRunnerSettings runnerSettings = targetConfig.getRunnerSettings();
      if (runnerSettings != null) {
        runnerSettings.setEnvironmentProperties(newEnv);
      }
    }

  }

  private Optional<MavenExecutionEnvironmentProvider> delegateProvider(
      final ExecuteRunConfigurationTask executeRunConfigurationTask
  ) {
    return MavenExecutionEnvironmentProvider.EP_NAME.extensions()
        .filter(provider -> provider != this && provider.isApplicable(executeRunConfigurationTask))
        .findFirst();
  }

}
