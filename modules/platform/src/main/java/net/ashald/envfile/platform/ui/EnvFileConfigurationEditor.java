package net.ashald.envfile.platform.ui;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.Key;
import lombok.val;
import net.ashald.envfile.platform.EnvFileEntry;
import net.ashald.envfile.platform.EnvFileSettings;
import net.ashald.envfile.platform.EnvVarsProviderExtension;
import net.ashald.envfile.platform.ProjectFileResolver;
import net.ashald.envfile.providers.toml.TomlEnvFileParser;
import net.ashald.envfile.providers.runconfig.RunConfigEnvVarsProvider;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EnvFileConfigurationEditor<T extends RunConfigurationBase<?>> extends SettingsEditor<T> {
    private static final Key<EnvFileSettings> USER_DATA_KEY = new Key<EnvFileSettings>("EnvFile Settings");

    private static final String SERIALIZATION_ID = "net.ashald.envfile";

    private static final String ELEMENT_ENTRY_LIST = "ENTRIES";
    private static final String ELEMENT_ENTRY_SINGLE = "ENTRY";

    private static final String FIELD_IS_ENABLED = "IS_ENABLED";
    private static final String FIELD_SUBSTITUTE_VARS = "IS_SUBST";
    private static final String FIELD_PATH_MACRO_VARS = "IS_PATH_MACRO_SUPPORTED";
    private static final String FIELD_IGNORE_MISSING = "IS_IGNORE_MISSING_FILES";
    private static final String FIELD_EXPERIMENTAL_INTEGRATIONS = "IS_ENABLE_EXPERIMENTAL_INTEGRATIONS";
    private static final String FIELD_PATH = "PATH";
    private static final String FIELD_PARSER = "PARSER";

    private static final String FIELD_IS_EXECUTABLE = "IS_EXECUTABLE";

    private final EnvFileConfigurationPanel<T> editor;

    public EnvFileConfigurationEditor(T configuration) {
        editor = new EnvFileConfigurationPanel<T>(configuration);
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
    protected void applyEditorTo(@NotNull T configuration) throws ConfigurationException {
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
        boolean envVarsSubstEnabled = Boolean.parseBoolean(envVarsSubstEnabledStr);

        String pathMacroSupportedStr = JDOMExternalizerUtil.readField(element, FIELD_PATH_MACRO_VARS, "false");
        boolean pathMacroSupported = Boolean.parseBoolean(pathMacroSupportedStr);

        String ignoreMissingStr = JDOMExternalizerUtil.readField(element, FIELD_IGNORE_MISSING, "false");
        boolean ignoreMissing = Boolean.parseBoolean(ignoreMissingStr);

        String experimentalIntegrationsStr = JDOMExternalizerUtil.readField(element, FIELD_EXPERIMENTAL_INTEGRATIONS, "false");
        boolean experimentalIntegrations = Boolean.parseBoolean(experimentalIntegrationsStr);

        List<EnvFileEntry> entries = new ArrayList<EnvFileEntry>();

        final Element entriesElement = element.getChild(ELEMENT_ENTRY_LIST);
        if (entriesElement != null) {
            for (Element envFileEntry : entriesElement.getChildren(ELEMENT_ENTRY_SINGLE)) {

                String isEntryEnabledStr = envFileEntry.getAttributeValue(FIELD_IS_ENABLED);
                boolean isEntryEnabled = Boolean.parseBoolean(isEntryEnabledStr);

                String parserId = envFileEntry.getAttributeValue(FIELD_PARSER, "~");
                String path = envFileEntry.getAttributeValue(FIELD_PATH);

                String isExecutableStr = envFileEntry.getAttributeValue(FIELD_IS_EXECUTABLE);
                boolean isExecutable = Boolean.parseBoolean(isExecutableStr);

                entries.add(
                        EnvFileEntry.builder()
                                .parserId(parserId)
                                .path(path)
                                .enabled(isEntryEnabled)
                                .executable(isExecutable)
                                .build()
                );
            }
        }

        // For a while to migrate old users - begin
        boolean hasConfigEntry = false;
        for (EnvFileEntry e : entries) {
            if (e.getParserId().equals(RunConfigEnvVarsProvider.PARSER_ID)) {
                hasConfigEntry = true;
                break;
            }
        }
        if (!hasConfigEntry) {
            entries.add(
                    EnvFileEntry.builder()
                            .parserId(RunConfigEnvVarsProvider.PARSER_ID)
                            .enabled(true)
                            .executable(false)
                            .build()
            );
        }
        // For a while to migrate old users - end
        EnvFileSettings settings = EnvFileSettings.builder()
                .pluginEnabled(isEnabled)
                .envVarsSubstitutionEnabled(envVarsSubstEnabled)
                .pathMacroSupported(pathMacroSupported)
                .ignoreMissing(ignoreMissing)
                .enableExperimentalIntegrations(experimentalIntegrations)
                .entries(entries)
                .build();

        configuration.putCopyableUserData(USER_DATA_KEY, settings);
    }


    public static void writeExternal(@NotNull RunConfigurationBase<?> configuration, @NotNull Element element) {
        EnvFileSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        if (state != null) {
            JDOMExternalizerUtil.writeField(element, FIELD_IS_ENABLED, Boolean.toString(state.isPluginEnabledEnabled()));
            JDOMExternalizerUtil.writeField(element, FIELD_SUBSTITUTE_VARS, Boolean.toString(state.isSubstituteEnvVarsEnabled()));
            JDOMExternalizerUtil.writeField(element, FIELD_PATH_MACRO_VARS, Boolean.toString(state.isPathMacroSupported()));
            JDOMExternalizerUtil.writeField(element, FIELD_IGNORE_MISSING, Boolean.toString(state.isIgnoreMissing()));
            JDOMExternalizerUtil.writeField(element, FIELD_EXPERIMENTAL_INTEGRATIONS, Boolean.toString(state.isEnableExperimentalIntegrations()));

            final Element entriesElement = new Element(ELEMENT_ENTRY_LIST);
            for (EnvFileEntry entry : state.getEntries()) {
                final Element entryElement = new Element(ELEMENT_ENTRY_SINGLE);
                entryElement.setAttribute(FIELD_IS_ENABLED, Boolean.toString(entry.getEnabled()));
                entryElement.setAttribute(FIELD_PARSER, entry.getParserId());
                entryElement.setAttribute(FIELD_IS_EXECUTABLE, Boolean.toString(entry.isExecutable()));
                String path = entry.getPath();
                if (path != null) {
                    entryElement.setAttribute(FIELD_PATH, entry.getPath());
                }
                entriesElement.addContent(entryElement);
            }
            element.addContent(entriesElement);
        }
    }

    public static EnvFileSettings getEnvFileSetting(@NotNull RunConfigurationBase<?> runConfigurationBase) {
        return runConfigurationBase.getCopyableUserData(USER_DATA_KEY);
    }

    public static void validateConfiguration(@NotNull RunConfigurationBase<?> configuration, boolean isExecution) throws ExecutionException {
        EnvFileSettings state = configuration.getCopyableUserData(USER_DATA_KEY);
        if (state != null && state.isPluginEnabledEnabled()) {
            for (EnvFileEntry entry : state.getEntries()) {
                if (entry.getEnabled()) {
                    val validPath = ProjectFileResolver.DEFAULT
                            .resolvePath(configuration.getProject(), entry.getPath())
                            .map(File::exists)
                            .orElse(true);

                    if (!validPath && !state.isIgnoreMissing()) {
                        throw new ExecutionException(String.format("EnvFile: invalid path - %s", entry.getPath()));
                    }

                    if (!EnvVarsProviderExtension.getParserFactoryById(entry.getParserId()).isPresent()) {
                        throw new ExecutionException(String.format(
                                "EnvFile: cannot load parser '%s' for '%s'", entry.getParserId(), entry.getPath()
                        ));
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
