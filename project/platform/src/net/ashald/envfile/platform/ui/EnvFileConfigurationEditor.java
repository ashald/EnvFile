package net.ashald.envfile.platform.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.ConfigurationException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvFileConfigurationEditor<T extends RunConfigurationBase> extends SettingsEditor<T> {
    private static final Key<EnvFileSettings> USER_DATA_KEY = new Key<EnvFileSettings>("EnvFile Settings");

    @NonNls private static final String SERIALIZATION_ID = "net.ashald.envfile";

    @NonNls private static final String ELEMENT_ENTRY_LIST = "ENTRIES";
    @NonNls private static final String ELEMENT_ENTRY_SINGLE = "ENTRY";

    @NonNls private static final String FIELD_IS_ENABLED = "IS_ENABLED";
    @NonNls private static final String FIELD_PATH = "PATH";
    @NonNls private static final String FIELD_PARSER = "PARSER";

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
        boolean isEnabled = isEnabledStr != null && Boolean.parseBoolean(isEnabledStr);

        List<EnvFileEntry> entries = new ArrayList<EnvFileEntry>();

        final Element entriesElement = element.getChild(ELEMENT_ENTRY_LIST);
        if (entriesElement != null) {
            for (Object o : entriesElement.getChildren(ELEMENT_ENTRY_SINGLE)) {
                Element envElement = (Element) o;

                String isEntryEnabledStr = envElement.getAttributeValue(FIELD_IS_ENABLED);
                boolean isEntryEnabled = isEntryEnabledStr != null && Boolean.parseBoolean(isEntryEnabledStr);

                String parserId = envElement.getAttributeValue(FIELD_PARSER, "~");
                String path = envElement.getAttributeValue(FIELD_PATH, "~");

                entries.add(new EnvFileEntry(configuration, parserId, path, isEntryEnabled));
            }
        }

        EnvFileSettings state = new EnvFileSettings(isEnabled, entries);
        configuration.putUserData(USER_DATA_KEY, state);
    }

    public static void writeExternal(@NotNull RunConfigurationBase configuration, @NotNull Element element) {
        EnvFileSettings state = configuration.getUserData(USER_DATA_KEY);
        if (state != null) {
            JDOMExternalizerUtil.writeField(element, FIELD_IS_ENABLED, Boolean.toString(state.isEnabled()));

            final Element entriesElement = new Element(ELEMENT_ENTRY_LIST);
            for (EnvFileEntry entry : state.getEntries()) {
                final Element entryElement = new Element(ELEMENT_ENTRY_SINGLE);
                entryElement.setAttribute(FIELD_IS_ENABLED, Boolean.toString(entry.isEnabled()));
                entryElement.setAttribute(FIELD_PARSER, entry.getParserId());
                entryElement.setAttribute(FIELD_PATH, entry.getPath());
                entriesElement.addContent(entryElement);
            }
            element.addContent(entriesElement);
        }
    }

    public static void patchEnv(@NotNull RunConfigurationBase runConfigurationBase, Map<String, String> currentEnv) throws ExecutionException {
        Map<String, String> newEnv = EnvFileConfigurationEditor.collectEnv(runConfigurationBase, currentEnv);
        currentEnv.clear();
        currentEnv.putAll(newEnv);
    }

    public static Map<String, String> collectEnv(@NotNull RunConfigurationBase runConfigurationBase, Map<String, String> userEnv) throws ExecutionException {
        Map<String, String> result = new HashMap<>();
        result.putAll(collectEnvFromFiles(runConfigurationBase));
        // user defined env vars override env files
        result.putAll(userEnv);
        return result;
    }

    private static Map<String, String> collectEnvFromFiles(@NotNull RunConfigurationBase runConfigurationBase) throws ExecutionException {
        Map<String, String> result = new HashMap<>();

        EnvFileSettings state = runConfigurationBase.getUserData(USER_DATA_KEY);
        if (state != null && state.isEnabled()) {
            for (EnvFileEntry entry : state.getEntries()) {
                try {
                    result = entry.process(result);
                } catch (EnvFileErrorException e) {
                    throw new ExecutionException(e);
                } catch (IOException e) {
                    throw new ExecutionException(e);
                }
            }
        }

        return result;
    }

    public static void validateConfiguration(@NotNull RunConfigurationBase configuration, boolean isExecution) throws ExecutionException {
        EnvFileSettings state = configuration.getUserData(USER_DATA_KEY);
        if (state != null && state.isEnabled()) {
            for (EnvFileEntry entry : state.getEntries()) {
                if (entry.isEnabled()) {
                    if (!entry.validatePath()) {
                        throw new ExecutionException(String.format("EnvFile: invalid path - %s", entry.getPath()));
                    }

                    if (!entry.validateType()) {
                        throw new ExecutionException(String.format("EnvFile: cannot load parser '%s' for '%s'", entry.getParserId(), entry.getPath()));
                    }
                }
            }
        }
    }

    @NotNull
    @Contract(pure = true)
    public static String getSerializationId() {
        return SERIALIZATION_ID;
    }
}
