package net.ashald.envfile.products.idea.scala;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.project.Project;
import net.ashald.envfile.platform.EnvFileEnvironmentVariables;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jetbrains.sbt.runner.SbtRunConfiguration;
import org.jetbrains.sbt.runner.SbtRunConfigurationForm;

import java.util.Map;

public class SbtRunEnvFileConfigurationForm extends SbtRunConfigurationForm {
    private SbtRunConfiguration configuration;

    public SbtRunEnvFileConfigurationForm(Project project, SbtRunConfiguration sbtRunConfiguration) {
        super(project, sbtRunConfiguration);
        configuration = sbtRunConfiguration;
    }

    @Override
    public Map<String, String> getEnvironmentVariables() {
        try {
            return new EnvFileEnvironmentVariables(
                    EnvFileConfigurationEditor.getEnvFileSetting(configuration)
            )
                    .render(
                            configuration.getProject(),
                            super.getEnvironmentVariables(),
                            true // can we get it dynamically?
                    );
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
