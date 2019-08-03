package net.ashald.envfile.platform.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.Key;
import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.platform.EnvEntry;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvFileSettings;
import net.ashald.envfile.platform.EnvVarEntry;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvFileConfigurationEditor<T extends RunConfigurationBase> extends SettingsEditor<T> {
    private static final Key<EnvFileSettings> USER_DATA_KEY = new Key<EnvFileSettings>("EnvFile Settings");

    @NonNls private static final String SERIALIZATION_ID = "net.ashald.envfile";

    @NonNls private static final String ELEMENT_ENTRY_LIST = "ENTRIES";
    @NonNls private static final String ELEMENT_ENTRY_SINGLE = "ENTRY";

    @NonNls private static final String FIELD_IS_ENABLED = "IS_ENABLED";
    @NonNls private static final String FIELD_SUBSTITUTE_VARS = "IS_SUBST";
    @NonNls private static final String FIELD_PATH_MACRO_VARS = "IS_PATH_MACRO_SUPPORTED";
    @NonNls private static final String FIELD_IGNORE_MISSING = "IS_IGNORE_MISSING_FILES";
    @NonNls private static final String FIELD_EXPERIMENTAL_INTEGRATIONS = "IS_ENABLE_EXPERIMENTAL_INTEGRATIONS";
    @NonNls private static final String FIELD_PATH = "PATH";
    @NonNls private static final String FIELD_PARSER = "PARSER";

    @NonNls private static final String FIELD_SELECTED_OPTION = "SELECTED_OPTIONS";
    @NonNls private static final String FIELD_ENV_NAME = "ENV_NAME";


    private EnvFileConfigurationPanel editor;

    public EnvFileConfigurationEditor(T configuration) {
        editor = new EnvFileConfigurationPanel<T>(configuration);
    }

    public static String getEditorTitle() {
        return "EnvFile";
    }

    @Override
    protected void resetEditorFrom(@NotNull T configuration) {
        EnvFileSettings state = configuration.getUserData(USER_DATA_KEY);
        if (state != null) {
            editor.setState(state);
        }
    }

    @Override
    protected void applyEditorTo(@NotNull T configuration) throws ConfigurationException {
        configuration.putUserData(USER_DATA_KEY, editor.getState());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return editor;
    }

    public static void readExternal(@NotNull RunConfigurationBase configuration, @NotNull Element element) {
        String isEnabledStr = JDOMExternalizerUtil.readField(element, FIELD_IS_ENABLED);
        boolean isEnabled = Boolean.parseBoolean(isEnabledStr);

        String envVarsSubstEnabledStr = JDOMExternalizerUtil.readField(element, FIELD_SUBSTITUTE_VARS, "false");
        boolean envVarsSubstEnabled  = Boolean.parseBoolean(envVarsSubstEnabledStr);

        String pathMacroSupportedStr = JDOMExternalizerUtil.readField(element, FIELD_PATH_MACRO_VARS, "false");
        boolean pathMacroSupported = Boolean.parseBoolean(pathMacroSupportedStr);

        String ignoreMissingStr = JDOMExternalizerUtil.readField(element, FIELD_IGNORE_MISSING, "false");
        boolean ignoreMissing = Boolean.parseBoolean(ignoreMissingStr);

        String experimentalIntegrationsStr = JDOMExternalizerUtil.readField(element, FIELD_EXPERIMENTAL_INTEGRATIONS, "false");
        boolean experimentalIntegrations = Boolean.parseBoolean(experimentalIntegrationsStr);

        List<EnvEntry> entries = new ArrayList<EnvEntry>();

        final Element entriesElement = element.getChild(ELEMENT_ENTRY_LIST);
        if (entriesElement != null) {
            for (Object o : entriesElement.getChildren(ELEMENT_ENTRY_SINGLE)) {
                Element envElement = (Element) o;

                String isEntryEnabledStr = envElement.getAttributeValue(FIELD_IS_ENABLED);
                boolean isEntryEnabled = Boolean.parseBoolean(isEntryEnabledStr);

                String parserId = envElement.getAttributeValue(FIELD_PARSER, "~");

                String path = envElement.getAttributeValue(FIELD_PATH);

                if (path != null) {
                    entries.add(new EnvFileEntry(configuration, parserId, path, isEntryEnabled, envVarsSubstEnabled));
                } else {
                    String selectedOption = envElement.getAttributeValue(FIELD_SELECTED_OPTION);
                    String envName = envElement.getAttributeValue(FIELD_ENV_NAME);
                    entries.add(new EnvVarEntry(configuration, parserId, envName, selectedOption, isEntryEnabled,
                            envVarsSubstEnabled));
                }
            }
        }

        // For a while to migrate old users - begin
        boolean hasConfigEntry = false;
        for (EnvEntry e : entries) {
            if (e.getParserId().equals("runconfig")) {
                hasConfigEntry = true;
                break;
            }
        }
        if (!hasConfigEntry) {
            entries.add(new EnvFileEntry(configuration, "runconfig", null, true, envVarsSubstEnabled));
        }
        // For a while to migrate old users - end

        EnvFileSettings state = new EnvFileSettings(isEnabled, envVarsSubstEnabled, pathMacroSupported, entries, ignoreMissing, experimentalIntegrations);
        configuration.putUserData(USER_DATA_KEY, state);
    }

    public static void writeExternal(@NotNull RunConfigurationBase configuration, @NotNull Element element) {
        EnvFileSettings state = configuration.getUserData(USER_DATA_KEY);
        if (state != null) {
            JDOMExternalizerUtil.writeField(element, FIELD_IS_ENABLED, Boolean.toString(state.isEnabled()));
            JDOMExternalizerUtil.writeField(element, FIELD_SUBSTITUTE_VARS, Boolean.toString(state.isSubstituteEnvVarsEnabled()));
            JDOMExternalizerUtil.writeField(element, FIELD_PATH_MACRO_VARS, Boolean.toString(state.isPathMacroSupported()));
            JDOMExternalizerUtil.writeField(element, FIELD_IGNORE_MISSING, Boolean.toString(state.isIgnoreMissing()));
            JDOMExternalizerUtil.writeField(element, FIELD_EXPERIMENTAL_INTEGRATIONS, Boolean.toString(state.isEnableExperimentalIntegrations()));

            final Element entriesElement = new Element(ELEMENT_ENTRY_LIST);
            for (EnvEntry entry : state.getEntries()) {
                final Element entryElement = new Element(ELEMENT_ENTRY_SINGLE);
                entryElement.setAttribute(FIELD_IS_ENABLED, Boolean.toString(entry.isEnabled()));
                entryElement.setAttribute(FIELD_PARSER, entry.getParserId());

                if(entry instanceof EnvFileEntry) {
                    String path = ((EnvFileEntry)entry).getPath();
                    if (path != null) {
                        entryElement.setAttribute(FIELD_PATH, ((EnvFileEntry)entry).getPath());
                    }
                } else {
                    String envName = ((EnvVarEntry)entry).getEnvVarName();
                    if (envName != null) {
                        entryElement.setAttribute(FIELD_ENV_NAME, envName);
                    }
                    String selectedOption = ((EnvVarEntry)entry).getSelectedOption();
                    if (selectedOption != null) {
                        entryElement.setAttribute(FIELD_SELECTED_OPTION, selectedOption);
                    }
                }


                entriesElement.addContent(entryElement);
            }
            element.addContent(entriesElement);
        }
    }

    public static Map<String, String> collectEnv(@NotNull RunConfigurationBase runConfigurationBase, Map<String, String> runConfigEnv) throws ExecutionException {
        EnvFileSettings state = runConfigurationBase.getUserData(USER_DATA_KEY);
        if (state != null && state.isEnabled()) {
            Map<String, String> result = new HashMap<>();
            for (EnvEntry entry : state.getEntries()) {
                try {
                    result = entry.process(runConfigEnv, result, state.isIgnoreMissing());
                } catch (EnvFileErrorException | IOException e) {
                    throw new ExecutionException(e);
                }
            }
            if (state.isPathMacroSupported()) {
                // replace $PROJECT_DIR$ by project path
                PathMacroManager macroManager = PathMacroManager.getInstance(runConfigurationBase.getProject());
                result = result.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, o -> macroManager.expandPath(o.getValue())));
            }
            return result;
        } else {
            return runConfigEnv;
        }
    }

    public static void validateConfiguration(@NotNull RunConfigurationBase configuration, boolean isExecution) throws ExecutionException {
        EnvFileSettings state = configuration.getUserData(USER_DATA_KEY);
        if (state != null && state.isEnabled()) {
            for (EnvEntry entry : state.getEntries()) {
                if (entry.isEnabled()) {
                    if (entry instanceof EnvFileEntry) {
                        if (!((EnvFileEntry)entry).validatePath() && !state.isIgnoreMissing()) {
                            throw new ExecutionException(String.format("EnvFile: invalid path - %s",
                                    ((EnvFileEntry)entry).getPath()));
                        }

                        if (!entry.validateType()) {
                            throw new ExecutionException(String.format("EnvFile: cannot load parser '%s' for '%s'",
                                    entry.getParserId(), ((EnvFileEntry)entry).getPath()));
                        }
                    } else {
                        if (!entry.validateType()) {
                            throw new ExecutionException(String.format("EnvFile: cannot load parser '%s' for env var '%s'",
                                    entry.getParserId(), ((EnvVarEntry)entry).getEnvVarName()));
                        }
                    }
                }
            }
        }
    }

    public static boolean isEnableExperimentalIntegrations(@NotNull RunConfigurationBase configuration) {
        EnvFileSettings state = configuration.getUserData(USER_DATA_KEY);
        return state != null && state.isEnableExperimentalIntegrations();
    }

    @NotNull
    @Contract(pure = true)
    public static String getSerializationId() {
        return SERIALIZATION_ID;
    }
}
