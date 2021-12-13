package net.ashald.envfile.products.idea.scala;

import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configuration.RunConfigurationExtensionsManager;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.components.ServiceManager;

public class SbtRunConfigurationExtensionManager extends RunConfigurationExtensionsManager<RunConfigurationBase<?>, RunConfigurationExtension> {

    public SbtRunConfigurationExtensionManager() {
        super(RunConfigurationExtension.EP_NAME);
    }

    public static SbtRunConfigurationExtensionManager getInstance() {
        return ServiceManager.getService(SbtRunConfigurationExtensionManager.class);
    }
}