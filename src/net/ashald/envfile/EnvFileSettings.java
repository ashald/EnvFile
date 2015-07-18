package net.ashald.envfile;

import java.util.HashMap;
import java.util.Map;


public class EnvFileSettings {

    protected static final String FIELD_ENV_FILE_ENABLED = "ENV_FILE_IS_ENABLED";
    protected static final String FIELD_ENV_FILE_PATH = "ENV_FILE_PATH";
    protected static final String FIELD_ENV_FILE_PARSER = "ENV_FILE_PARSER";

    protected final Map<String, String> envVars;

    public EnvFileSettings() {
        envVars = new HashMap<String, String>();
    }

    public EnvFileSettings(Map<String, String> env) {
        this();
        envVars.putAll(env);
    }

    public Map<String, String> getEnvVars() {
        return envVars;
    }

    public void putIsEnabled(boolean value) {
        envVars.put(FIELD_ENV_FILE_ENABLED, String.valueOf(value));
    }

    public boolean popIsEnabled() {
        String isEnabledStr = envVars.remove(FIELD_ENV_FILE_ENABLED);
        return isEnabledStr != null && Boolean.parseBoolean(isEnabledStr);
    }

    public void putEnvFilePath(String path) {
        envVars.put(FIELD_ENV_FILE_PATH, path == null ? "" : path);
    }

    public String popEnvFilePath() {
        String path = envVars.remove(FIELD_ENV_FILE_PATH);
        return path == null ? "" : path;
    }

    public void putEnvFileParser(String parserId) {
        envVars.put(FIELD_ENV_FILE_PARSER, parserId == null ? "" : parserId);
    }

    public String popEnvFileParser() {
        String formatStr = envVars.remove(FIELD_ENV_FILE_PARSER);
        return formatStr == null ? "" : formatStr;
    }
}
