package net.ashald.envfile.products.idea.scala;

import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.sbt.runner.SbtRunConfiguration;
import org.jetbrains.sbt.runner.SbtRunConfigurationFactory;

public class SbtEnvFileConfigurationFactory extends SbtRunConfigurationFactory {
    public SbtEnvFileConfigurationFactory(ConfigurationType typez) {
        super(typez);
    }

    @NotNull
    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return new SbtEnvFileRunConfiguration(project, this, "");
    }
}
