package net.ashald.envfile.platform;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.project.Project;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.ashald.envfile.EnvVarsProvider;
import net.ashald.envfile.exceptions.EnvFileException;
import net.ashald.envfile.exceptions.InvalidEnvFileException;
import org.apache.commons.text.StringSubstitutor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Builder
@RequiredArgsConstructor
public class EnvFileEnvironmentVariables {
    private static final String NOTIFICATION_GROUP = "net.ashald.envfile";

    private final ProjectFileResolver projectFileResolver = ProjectFileResolver.DEFAULT;
    private final EnvFileSettings envFileSettings;

    public Map<String, String> render(
            @NotNull Project project,
            @NotNull Map<String, String> runConfigEnv,
            boolean includeParentEnv
    ) throws ExecutionException {
        Map<String, String> baseEnv = new HashMap<>();

        if (includeParentEnv) {
            baseEnv.putAll(
                    new GeneralCommandLine()
                            .withParentEnvironmentType(
                                    GeneralCommandLine.ParentEnvironmentType.CONSOLE
                            )
                            .getParentEnvironment()
            );
        }

        baseEnv.putAll(runConfigEnv);

        if (envFileSettings == null || !envFileSettings.isPluginEnabledEnabled()) {
            return new HashMap<>(baseEnv);
        }

        PathMacroManager macroManager = PathMacroManager.getInstance(project);

        Map<String, String> result = new HashMap<>();
        for (EnvFileEntry entry : envFileSettings.getEntries()) {
            if (!entry.isEnabled()) {
                continue;
            }

            try {
                val file = projectFileResolver.resolvePath(project, entry.getPath());
                val envVarsProvider = resolveEnvVarsProvider(project, baseEnv, entry);

                envVarsProvider
                        .getEnvVars(file.orElse(null), entry.isExecutable(), result)
                        .forEach(
                                (key, value) -> result.put(key, renderValue(value, result, macroManager))
                        );

            } catch (InvalidEnvFileException e) {
                if (envFileSettings.isIgnoreMissing()) {
                    continue;
                }
                throw new ExecutionException(e);
            } catch (EnvFileException e) {
                throw new ExecutionException(e);
            }
        }

        return result;
    }

    private EnvVarsProvider resolveEnvVarsProvider(
            Project project,
            Map<String, String> baseEnv,
            EnvFileEntry entry
    )
            throws EnvFileException {
        val parserFactory = EnvVarsProviderExtension.getParserFactoryById(entry.getParserId())
                .orElseThrow(() ->
                        EnvFileException.format(
                                "Cannot find implementation for Environment Variables provider '%s'. " +
                                        "Deactivate, or delete entry, or enable 'Ignore missing files' setting.",
                                entry.getParserId()
                        )
                );

        EnvVarsProvider instance;
        try {
            instance = parserFactory.createProvider(baseEnv, value ->
                    notify(project, entry, value)
            );
        } catch (Exception e) {
            throw new EnvFileException(e);
        }

        if (instance == null) {
            throw EnvFileException.format(
                    "Environment Variables provider '%s' cannot be instantiated. " +
                            "Deactivate, or delete entry, or enable 'Ignore missing files' setting.",
                    entry.getParserId()
            );
        }

        return instance;
    }

    @NotNull
    private String renderValue(String template, @NotNull Map<String, String> context, PathMacroManager macroManager) {
        val postMacro = envFileSettings.isPathMacroSupported()
                ? macroManager.expandPath(template)
                : template;

        if (!envFileSettings.isSubstituteEnvVarsEnabled()) {
            return postMacro;
        }
        // resolve taking into account default values
        String stage1 = new StringSubstitutor(context).replace(template);

        // if ${FOO} was not resolved - replace it with empty string as it would've worked in bash
        String stage2 = new StringSubstitutor(key -> context.getOrDefault(key, "")).replace(stage1);

        return stage2;
    }

    private void notify(Project project, EnvFileEntry entry, String content) {
        val notification = new Notification(NOTIFICATION_GROUP, content, NotificationType.INFORMATION);
        notification.setTitle(
                projectFileResolver.resolvePath(project, entry.getPath())
                        .map(File::getAbsolutePath)
                        .orElse(entry.getParserId())
        );
        notification.setSubtitle(
                entry.isExecutable() ? "Executing" : "Reading"
        );

        Notifications.Bus.notify(notification, project);
    }
}
