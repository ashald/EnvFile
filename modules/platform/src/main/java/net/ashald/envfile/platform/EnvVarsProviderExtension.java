package net.ashald.envfile.platform;

import com.intellij.diagnostic.PluginException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.AbstractExtensionPointBean;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.NullableLazyValue;
import com.intellij.util.xmlb.annotations.Attribute;
import net.ashald.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvVarsProviderExtension extends AbstractExtensionPointBean {
  @SuppressWarnings("WeakerAccess")
  public static final ExtensionPointName<EnvVarsProviderExtension> EP_NAME =
      new ExtensionPointName<>("net.ashald.envfile.envVarsProvider");

  @Attribute("id")
  public String id;

  @Attribute("factory")
  public String factory;

  private final NullableLazyValue<EnvVarsProviderFactory> implementation =
      new NullableLazyValue<>() {
        @Override
        protected @Nullable EnvVarsProviderFactory compute() {
          try {
            Class<EnvVarsProviderFactory> clazz = findClass(factory);

            return clazz.getConstructor().newInstance();
          } catch (Exception e) {
            e.printStackTrace();
          }
          return null;
        }
      };

  public EnvVarsProviderFactory getFactory() {
    return implementation.getValue();
  }

  static EnvVarsProviderExtension getParserExtensionById(@NotNull String parserId) {
    Map<String, EnvVarsProviderExtension> parsers = new HashMap<>();
    for (EnvVarsProviderExtension extension : Extensions.getExtensions(EP_NAME)) {
      if (parsers.containsKey(extension.getId())) {
        Logger.getInstance(EnvVarsProviderExtension.class)
            .error(
                String.format(
                    "Cannot load parser '%s' with implementation class '%s' since there is already parser '%s' "
                        + "registered with the same id. Skipping.",
                    extension.getId(), extension.factory, parsers.get(extension.getId())));
      } else {
        parsers.put(extension.getId(), extension);
      }
    }
    return parsers.get(parserId);
  }

  public static List<EnvVarsProviderExtension> getParserExtensions() {
    List<EnvVarsProviderExtension> extensions = Arrays.asList(Extensions.getExtensions(EP_NAME));
    extensions.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
    return extensions;
  }

  public String getId() {
    if (id == null) {
      String error = "No id specified for environment variable provider factory " + factory;
      if (myPluginDescriptor != null) {
        throw new PluginException(error, myPluginDescriptor.getPluginId());
      }
      throw new IllegalArgumentException(error);
    }
    return id.toLowerCase();
  }

  @Override
  public String toString() {
    return getFactory().getTitle();
  }
}
