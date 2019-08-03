package net.ashald.envfile.platform;

import com.intellij.execution.configurations.RunConfigurationBase;
import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.EnvSingleProvider;

import java.io.IOException;
import java.util.Map;

public class EnvSingleEntry extends EnvVarsEntry<EnvSingleProvider> {


    private String envVar;
    private String selectedOption;

    public EnvSingleEntry(RunConfigurationBase runConfig, String envFileParserId, String envVar, String selectedOption,
                          boolean enabled, boolean substitutionEnabled) {
        super(runConfig, envFileParserId, enabled, substitutionEnabled);
        this.envVar = envVar;
        this.selectedOption = selectedOption;
    }

    @Override
    public Map<String, String> process(Map<String, String> runConfigEnv, Map<String, String> aggregatedEnv, boolean ignoreMissing) throws IOException, EnvFileErrorException {
        EnvSingleProvider parser = getProvider();

        if (isEnabled() && parser != null) {
            return parser.process(runConfigEnv, this.envVar, this.selectedOption, aggregatedEnv);
        }

        return aggregatedEnv;
    }

    public String getEnvVarName() {
        return envVar;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setEnvVar(String envVar) {
        this.envVar = envVar;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}
