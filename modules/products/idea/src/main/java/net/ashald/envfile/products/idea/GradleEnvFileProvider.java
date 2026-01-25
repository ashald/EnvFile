package net.ashald.envfile.products.idea;

import com.intellij.execution.CommonJavaRunConfigurationParameters;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.task.ExecuteRunConfigurationTask;
import net.ashald.envfile.platform.EnvFileEnvironmentVariables;
import net.ashald.envfile.platform.EnvFileSettings;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jetbrains.plugins.gradle.execution.build.GradleExecutionEnvironmentProvider;
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GradleEnvFileProvider implements GradleExecutionEnvironmentProvider {

    @Override
    public boolean isApplicable(final ExecuteRunConfigurationTask executeRunConfigurationTask) {
        Object runProfile = executeRunConfigurationTask.getRunProfile();

        // Check if the run profile is a RunConfigurationBase with EnvFile settings
        // and implements CommonJavaRunConfigurationParameters (for getEnvs/isPassParentEnvs)
        if (runProfile instanceof RunConfigurationBase<?> config && runProfile instanceof CommonJavaRunConfigurationParameters) {
            EnvFileSettings settings = EnvFileConfigurationEditor.getEnvFileSetting(config);
            return settings != null && settings.isPluginEnabledEnabled();
        }

        return false;
    }

    @Override
    public ExecutionEnvironment createExecutionEnvironment(
            final Project project,
            final ExecuteRunConfigurationTask executeRunConfigurationTask,
            final Executor executor
    ) {
        final ExecutionEnvironment environment = delegateProvider(executeRunConfigurationTask)
                .map(provider -> provider.createExecutionEnvironment(project, executeRunConfigurationTask, executor))
                .orElse(null);

        if (environment != null && environment.getRunProfile() instanceof GradleRunConfiguration targetConfig) {
            Object runProfile = executeRunConfigurationTask.getRunProfile();
            if (runProfile instanceof RunConfigurationBase<?> sourceConfig && runProfile instanceof CommonJavaRunConfigurationParameters sourceParams) {
                applyEnvFile(sourceConfig, sourceParams, targetConfig);
            }
        }

        return environment;
    }

    private void applyEnvFile(
            final RunConfigurationBase<?> sourceConfig,
            final CommonJavaRunConfigurationParameters sourceParams,
            final GradleRunConfiguration targetConfig
    ) {
        Map<String, String> newEnv;
        try {
            newEnv = new EnvFileEnvironmentVariables(
                    EnvFileConfigurationEditor.getEnvFileSetting(sourceConfig)
            )
                    .render(
                            sourceConfig.getProject(),
                            sourceParams.getEnvs(),
                            sourceParams.isPassParentEnvs()
                    );

        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (newEnv == null) {
            return;
        }

        targetConfig.getSettings().setEnv(new HashMap<>(newEnv));
    }

    private Optional<GradleExecutionEnvironmentProvider> delegateProvider(
            final ExecuteRunConfigurationTask executeRunConfigurationTask
    ) {
        return GradleExecutionEnvironmentProvider.EP_NAME.extensions()
                .filter(provider -> provider != this && provider.isApplicable(executeRunConfigurationTask))
                .findFirst();
    }
}
