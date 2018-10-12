package net.ashald.envfile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

public abstract class AbstractEnvVarsProvider implements EnvVarsProvider {

    private static final Pattern pattern = Pattern.compile("\\$\\{([A-Za-z0-9._]+)}");

    @NotNull
    protected abstract Map<String, String> readFile(@NotNull String path) throws EnvFileErrorException, IOException;

    @Override
    public boolean isEditable() {
        return true;
    }

    @NotNull
    @Override
    public Map<String, String> process(@NotNull Map<String, String> runConfigEnv, String path, @NotNull Map<String, String> aggregatedEnv) throws EnvFileErrorException, IOException {
        Map<String, String> sourceEnv = new HashMap<>(aggregatedEnv);
        Map<String, String> overrides = readFile(path);

        sourceEnv.putAll(overrides);

        return expandEnvironmentVariables(sourceEnv);
    }

    Map<String, String> expandEnvironmentVariables(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();

            Matcher matcher = pattern.matcher(value);
            while (matcher.find()) {
                String envValue = getSystemValue(matcher.group(1), map);
                if (envValue != null) {
                    envValue = envValue.replace("\\", "\\\\");
                    Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
                    value = subexpr.matcher(value).replaceAll(envValue);
                }
            }

            entry.setValue(value);
        }

        return map;
    }

    // precedence placeholder before System properties before environment variable
    private String getSystemValue(final String name, Map<String, String> map) {
        String property = map.get(name);
        return property != null ? property : System.getProperty(name, System.getenv(name.toUpperCase()));
    }

}
