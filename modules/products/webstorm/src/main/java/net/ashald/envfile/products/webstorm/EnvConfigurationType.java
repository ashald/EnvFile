package net.ashald.envfile.products.webstorm;

import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.SimpleConfigurationType;
import com.intellij.lang.javascript.buildTools.npm.rc.NpmConfigurationType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.StubMethod;
import org.jetbrains.annotations.NotNull;

import static icons.JavaScriptLanguageIcons.BuildTools;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class EnvConfigurationType extends SimpleConfigurationType implements DumbAware {
  private static final String ID = "js.build_tools.npm";
  private static final String NAME = "npm";
  static {
      ByteBuddyAgent.install();
      new ByteBuddy()
                 .redefine(NpmConfigurationType.class)
                 .method(named("createTemplateConfiguration"))
                 .intercept(Advice.to(EnvConfigurationType.class)
                         .wrap(StubMethod.INSTANCE))
                 .make()
                 .load(NpmConfigurationType.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
    }
  public EnvConfigurationType() {
    super(
        ID,
        NAME,
        null,
        NotNullLazyValue.createValue(() -> BuildTools.Npm.Npm_16));
  }

  @NotNull
  public String getTag() {
    return NAME;
  }

  public String getHelpTopic() {
    return "reference.dialogs.rundebug.js.build_tools.npm";
  }

  @NotNull
  public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
    return new EnvRunConfiguration(project, this, NAME);
  }

  public boolean isEditableInDumbMode() {
    return true;
  }

  @NotNull
  public static EnvConfigurationType getInstance() {
      return ConfigurationTypeUtil.findConfigurationType(EnvConfigurationType.class);
  }
}
