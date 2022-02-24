package net.ashald.envfile.products.webstorm;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.lang.javascript.buildTools.npm.NpmScriptsService;
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
import com.intellij.lang.javascript.buildTools.npm.rc.NpmCommand;
import com.intellij.lang.javascript.buildTools.npm.rc.NpmRunSettings;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.EnvironmentUtil;
import net.ashald.envfile.platform.ui.EnvFileConfigurationEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

public class EnvRunConfigurationProducer extends RunConfigurationProducer<EnvRunConfiguration> {
  private static final Logger log = Logger.getInstance(EnvRunConfigurationProducer.class);
  public EnvRunConfigurationProducer() {
    super(false);
  }

  @NotNull
  @Override
  public ConfigurationFactory getConfigurationFactory() {
    log.info("EnvRunConfigurationProducer:getConfigurationFactory()");
    return ConfigurationTypeUtil.findConfigurationType(EnvConfigurationType.class);
  }

  @Override
  public boolean shouldReplace(@NotNull ConfigurationFromContext self, @NotNull ConfigurationFromContext other) {

    return super.shouldReplace(self, other);
  }

  @Override
  protected boolean setupConfigurationFromContext(
      @NotNull EnvRunConfiguration configuration,
      @NotNull ConfigurationContext context,
      @NotNull Ref<PsiElement> sourceElement) {
    NpmRunSettings runSettings =
        createRunSettingsFromContext(configuration.getRunSettings(), context, sourceElement);
    log.info(
        "EnvRunConfigurationProducer:setupConfigurationFromContext():"
            + configuration.getName()
            + ":"
            + context
            + ":"
            + sourceElement);
    if (runSettings == null) {
      log.info(
          "EnvRunConfigurationProducer:setupConfigurationFromContext():Context RunSettings was null, updating current...");
      runSettings = configuration.getRunSettings();
      try {
        Map<String, String> envs =
            EnvFileConfigurationEditor.collectEnv(
                configuration, EnvironmentUtil.getEnvironmentMap());
        EnvironmentVariablesData data = EnvironmentVariablesData.create(envs, false);
        NpmRunSettings.Builder builder = runSettings.toBuilder();
        builder.setEnvData(data);
        configuration.setRunSettings(builder.build());
        log.info("EnvRunConfigurationProducer:setupConfigurationFromContext(): Injected new run settings:");
        envs.forEach((k,v) -> log.info(k + ":" + v));
      } catch (Exception e) {
        e.printStackTrace();
        log.error("Could not collect environment", e);
      }
      return false;
    } else {
      setupConfigurationFromSettings(configuration, runSettings);
      return true;
    }
  }

  public static void setupConfigurationFromSettings(
      @NotNull EnvRunConfiguration configuration, @NotNull NpmRunSettings runSettings) {
    log.info(
        "EnvRunConfigurationProducer:setupConfigurationFromSettings():"
            + configuration.getName()
            + ":"
            + runSettings);
    try {
      Map<String, String> envs =
          EnvFileConfigurationEditor.collectEnv(configuration, EnvironmentUtil.getEnvironmentMap());
      EnvironmentVariablesData data = EnvironmentVariablesData.create(envs, false);
      NpmRunSettings.Builder builder = runSettings.toBuilder();
      builder.setEnvData(data);
      log.info("EnvRunConfigurationProducer:injected env data:");
      envs.forEach((k,v) -> log.info(k + ":" + v));
    } catch (Exception e) {
      e.printStackTrace();
      log.error("Could not collect environment", e);
    }
    configuration.setRunSettings(runSettings);
    configuration.setGeneratedName();
  }

  @Nullable
  private NpmRunSettings createRunSettingsFromContext(
      @NotNull NpmRunSettings templateRunSettings,
      @NotNull ConfigurationContext context,
      @Nullable Ref<PsiElement> sourceElement) {
    PsiElement element = getElement(context);
    if (element != null && element.isValid()) {
      JsonFile psiPackageJson = PackageJsonUtil.getContainingPackageJsonFile(element);
      if (psiPackageJson == null) {
        return null;
      } else {
        VirtualFile virtualPackageJson = psiPackageJson.getVirtualFile();
        if (virtualPackageJson == null) {
          return null;
        } else {
          JsonProperty scriptProperty = findContainingScriptProperty(element);
          if (scriptProperty == null) {
            return null;
          } else {
            NpmRunSettings.Builder builder = templateRunSettings.toBuilder();
            builder.setPackageJsonPath(virtualPackageJson.getPath());
            builder.setCommand(NpmCommand.RUN_SCRIPT);
            builder.setScriptNames(Collections.singletonList(scriptProperty.getName()));
            if (sourceElement != null) {
              sourceElement.set(scriptProperty);
            }

            return builder.build();
          }
        }
      }
    } else {
      return null;
    }
  }

  @Override
  public boolean isConfigurationFromContext(
      @NotNull EnvRunConfiguration configuration, @NotNull ConfigurationContext context) {
    NpmRunSettings thisRunSettings =
        createRunSettingsFromContext(configuration.getRunSettings(), context, null);
    return thisRunSettings != null
        && NpmScriptsService.getInstance(configuration.getProject())
            .isConfigurationMatched(configuration, thisRunSettings);
  }

  private PsiElement getElement(@NotNull ConfigurationContext context) {
    Location<?> location = context.getLocation();
    return location != null ? location.getPsiElement() : null;
  }

  @Nullable
  private JsonProperty findContainingScriptProperty(@NotNull PsiElement element) {
    JsonProperty scriptProperty = PackageJsonUtil.findContainingProperty(element);
    JsonProperty scriptsProperty = PackageJsonUtil.findContainingTopLevelProperty(scriptProperty);
    return scriptsProperty != null
            && "scripts".equals(scriptsProperty.getName())
            && scriptProperty != scriptsProperty
        ? scriptProperty
        : null;
  }
}
