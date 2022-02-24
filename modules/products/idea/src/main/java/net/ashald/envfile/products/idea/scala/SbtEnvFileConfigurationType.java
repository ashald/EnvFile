package net.ashald.envfile.products.idea.scala;

import com.intellij.execution.configurations.ConfigurationFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.sbt.runner.SbtConfigurationType;

public class SbtEnvFileConfigurationType extends SbtConfigurationType {
    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new SbtEnvFileConfigurationFactory(this)};
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Sbt Task EnvFile";
    }
}
