package net.ashald.envfile.platform;

import com.intellij.execution.configurations.RunConfigurationBase;
import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.EnvVarsProvider;

import java.io.IOException;
import java.util.Map;

public class EnvVarEntry extends EnvEntry<EnvVarsProvider> {


    private String envVar;
    private String selectedOption;

    public EnvVarEntry(RunConfigurationBase runConfig, String envFileParserId, String envVar, String selectedOption,
                       boolean enabled, boolean substitutionEnabled) {
        super(runConfig, envFileParserId, enabled, substitutionEnabled);
        this.envVar = envVar;
        this.selectedOption = selectedOption;
    }

    @Override
    public Map<String, String> process(Map<String, String> runConfigEnv, Map<String, String> aggregatedEnv, boolean ignoreMissing) throws IOException, EnvFileErrorException {
        EnvVarsProvider parser = getProvider();

        if (isEnabled() && parser != null) {
            return parser.process(runConfigEnv, this.envVar, this.selectedOption, aggregatedEnv);
        }

        return aggregatedEnv;
    }
}
