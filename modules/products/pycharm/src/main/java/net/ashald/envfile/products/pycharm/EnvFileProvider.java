package net.ashald.envfile.products.pycharm;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.AbstractPythonRunConfiguration;
import com.jetbrains.python.run.PythonExecution;
import com.jetbrains.python.run.PythonRunParams;
import com.jetbrains.python.run.target.HelpersAwareTargetEnvironmentRequest;
import com.jetbrains.python.run.target.PythonCommandLineTargetEnvironmentProvider;
import net.ashald.envfile.platform.EnvFileEnvironmentVariables;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jetbrains.annotations.NotNull;

public class EnvFileProvider implements PythonCommandLineTargetEnvironmentProvider {
    @Override
    public void extendTargetEnvironment(
            @NotNull Project project,
            @NotNull HelpersAwareTargetEnvironmentRequest helpersAwareTargetEnvironmentRequest,
            @NotNull PythonExecution pythonExecution,
            @NotNull PythonRunParams pythonRunParams
    ) {
        if (pythonRunParams instanceof AbstractPythonRunConfiguration<?>) {
            try {
                final AbstractPythonRunConfiguration<?> runConfiguration = (AbstractPythonRunConfiguration<?>) pythonRunParams;

                new EnvFileEnvironmentVariables(
                        EnvFileConfigurationEditor.getEnvFileSetting(runConfiguration)
                )
                        .render(
                                runConfiguration.getProject(),
                                pythonRunParams.getEnvs(),
                                pythonRunParams.isPassParentEnvs()
                        )
                        .forEach(pythonExecution::addEnvironmentVariable);

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}