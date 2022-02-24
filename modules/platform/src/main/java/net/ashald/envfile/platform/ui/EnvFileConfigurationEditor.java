package net.ashald.envfile.platform.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.Key;
import net.ashald.envfile.EnvFileErrorException;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvFileSettings;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EnvFileConfigurationEditor<T extends RunConfigurationBase<?>> extends SettingsEditor<T> {
    private static final Logger log = Logger.getInstance(EnvFileConfigurationEditor.class);

    private static final Key<EnvFileSettings> USER_DATA_KEY = new Key<>("EnvFile Settings");

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

    private final EnvFileConfigurationPanel<T> editor;

    public EnvFileConfigurationEditor(T configuration) {
        editor = new EnvFileConfigurationPanel<>(configuration);
    }

    public static String getEditorTitle() {
        return "EnvFile";
    }

    @Override
    protected void resetEditorFrom(@NotNull T configuration) {
        EnvFileSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        if (state != null) {
            editor.setState(state);
        }
    }

    @Override
    protected void applyEditorTo(@NotNull T configuration) {
        configuration.putCopyableUserData(USER_DATA_KEY, editor.getState());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return editor;
    }

    public static void readExternal(@NotNull RunConfigurationBase<?> configuration, @NotNull Element element) {
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

        List<EnvFileEntry> entries = new ArrayList<>();

        final Element entriesElement = element.getChild(ELEMENT_ENTRY_LIST);
        if (entriesElement != null) {
            for (Element o : entriesElement.getChildren(ELEMENT_ENTRY_SINGLE)) {

                String isEntryEnabledStr = o.getAttributeValue(FIELD_IS_ENABLED);
                boolean isEntryEnabled = Boolean.parseBoolean(isEntryEnabledStr);

                String parserId = o.getAttributeValue(FIELD_PARSER, "~");
                String path = o.getAttributeValue(FIELD_PATH);

                entries.add(new EnvFileEntry(configuration, parserId, path, isEntryEnabled, envVarsSubstEnabled));
            }
        }

        // For a while to migrate old users - begin
        boolean hasConfigEntry = false;
        for (EnvFileEntry e : entries) {
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
        configuration.putCopyableUserData(USER_DATA_KEY, state);
    }

    public static void writeExternal(@NotNull RunConfigurationBase<?> configuration, @NotNull Element element) {
        EnvFileSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        if (state != null) {
            JDOMExternalizerUtil.writeField(element, FIELD_IS_ENABLED, Boolean.toString(state.isEnabled()));
            JDOMExternalizerUtil.writeField(element, FIELD_SUBSTITUTE_VARS, Boolean.toString(state.isSubstituteEnvVarsEnabled()));
            JDOMExternalizerUtil.writeField(element, FIELD_PATH_MACRO_VARS, Boolean.toString(state.isPathMacroSupported()));
            JDOMExternalizerUtil.writeField(element, FIELD_IGNORE_MISSING, Boolean.toString(state.isIgnoreMissing()));
            JDOMExternalizerUtil.writeField(element, FIELD_EXPERIMENTAL_INTEGRATIONS, Boolean.toString(state.isEnableExperimentalIntegrations()));

            final Element entriesElement = new Element(ELEMENT_ENTRY_LIST);
            for (EnvFileEntry entry : state.getEntries()) {
                final Element entryElement = new Element(ELEMENT_ENTRY_SINGLE);
                entryElement.setAttribute(FIELD_IS_ENABLED, Boolean.toString(entry.isEnabled()));
                entryElement.setAttribute(FIELD_PARSER, entry.getParserId());
                String path = entry.getPath();
                if (path != null) {
                    entryElement.setAttribute(FIELD_PATH, entry.getPath());
                }
                entriesElement.addContent(entryElement);
            }
            element.addContent(entriesElement);
        }
    }

    public static Map<String, String> collectEnv(@NotNull RunConfigurationBase<?> runConfigurationBase, Map<String, String> runConfigEnv) throws ExecutionException {
        EnvFileSettings state = runConfigurationBase.getCopyableUserData(USER_DATA_KEY);
        log.info("EnvFileConfigurationEditor: state: " + state);
        if (state != null && state.isEnabled()) {
            Map<String, String> result = new HashMap<>();
            for (EnvFileEntry entry : state.getEntries()) {
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

    public static void validateConfiguration(@NotNull RunConfigurationBase<?> configuration, boolean ignored) throws ExecutionException {
        EnvFileSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        if (state != null && state.isEnabled()) {
            for (EnvFileEntry entry : state.getEntries()) {
                if (entry.isEnabled()) {
                    if (!entry.validatePath() && !state.isIgnoreMissing()) {
                        throw new ExecutionException(String.format("EnvFile: invalid path - %s", entry.getPath()));
                    }

                    if (!entry.validateType()) {
                        throw new ExecutionException(String.format("EnvFile: cannot load parser '%s' for '%s'", entry.getParserId(), entry.getPath()));
                    }
                }
            }
        }
    }

    public static boolean isEnableExperimentalIntegrations(@NotNull RunConfigurationBase<?> configuration) {
        EnvFileSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        return state != null && state.isEnableExperimentalIntegrations();
    }

    @NotNull
    @Contract(pure = true)
    public static String getSerializationId() {
        return SERIALIZATION_ID;
    }
}
