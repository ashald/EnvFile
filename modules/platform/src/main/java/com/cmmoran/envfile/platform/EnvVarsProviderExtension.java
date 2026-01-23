package com.cmmoran.envfile.platform;

import com.intellij.diagnostic.PluginException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.PluginAware;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.extensions.RequiredElement;
import com.intellij.util.xmlb.annotations.Attribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.val;
import com.cmmoran.envfile.EnvVarsProviderFactory;
import org.jetbrains.annotations.NotNull;


public class EnvVarsProviderExtension implements PluginAware {
  @SuppressWarnings("WeakerAccess")
  public static final ExtensionPointName<EnvVarsProviderExtension> EP_NAME =
      new ExtensionPointName<>("com.cmmoran.envfile.envVarsProvider");

  @Attribute("id")
  @RequiredElement
  public String id;

  @Attribute("factory")
  @RequiredElement
  public String factory;

  private EnvVarsProviderFactory implementation;
  private PluginDescriptor pluginDescriptor;

  @Override
  public void setPluginDescriptor(@NotNull PluginDescriptor pluginDescriptor) {
    this.pluginDescriptor = pluginDescriptor;
  }

  private EnvVarsProviderFactory getImplementation() {
    if (implementation == null) {
      try {
        ClassLoader classLoader =
            pluginDescriptor != null ? pluginDescriptor.getPluginClassLoader() : getClass().getClassLoader();
        @SuppressWarnings("unchecked")
        Class<EnvVarsProviderFactory> factoryClass =
            (Class<EnvVarsProviderFactory>) Class.forName(factory, true, classLoader);
        implementation = factoryClass.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Failed to instantiate factory: " + factory, e);
      }
    }
    return implementation;
  }

  public EnvVarsProviderFactory getFactory() {
    return getImplementation();
  }

  public static Optional<EnvVarsProviderFactory> getParserFactoryById(@NonNull String parserId) {
    Map<String, EnvVarsProviderExtension> parsers = new HashMap<>();
    for (EnvVarsProviderExtension extension : EP_NAME.getExtensionList()) {
      if (parsers.containsKey(extension.getId())) {
        Logger.getInstance(EnvVarsProviderExtension.class).error(String.format(
            "Cannot load parser '%s' with implementation class '%s' since there is already parser '%s' " +
                "registered with the same id. Skipping.",
            extension.getId(), extension.factory, parsers.get(extension.getId())
        ));
      } else {
        parsers.put(extension.getId(), extension);
      }
    }
    val extension = parsers.get(parserId);
    return Optional.ofNullable(extension)
        .map(EnvVarsProviderExtension::getFactory);
  }

  public static List<EnvVarsProviderExtension> getParserExtensions() {
    List<EnvVarsProviderExtension> extensions = EP_NAME.getExtensionList();
    extensions.sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));
    return extensions;
  }

  public String getId() {
    if (id == null) {
      String error = "No id specified for environment variable provider factory " + factory;
      if (pluginDescriptor != null) {
        throw new PluginException(error, pluginDescriptor.getPluginId());
      }
      throw new IllegalArgumentException(error);
    }
    return id.toLowerCase();
  }

  @Override
  public String toString() {
    return implementation.getTitle();
  }
}
