package net.ashald.envfile.platform;

import com.intellij.diagnostic.PluginException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.AbstractExtensionPointBean;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.LazyInstance;
import com.intellij.util.xmlb.annotations.Attribute;
import net.ashald.envfile.EnvFileParser;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class EnvFileParserExtension extends AbstractExtensionPointBean {
    public static final ExtensionPointName<EnvFileParserExtension> EP_NAME =
            new ExtensionPointName<EnvFileParserExtension>("net.ashald.envfile.envfileParser");

    protected static ExtensionPointName<EnvFileParserExtension> getExtensionPointName() {
        return EP_NAME;
    }

    public static EnvFileParserExtension getParserExtensionById(@NotNull String parserId) {
        Map<String, EnvFileParserExtension> parsers = new HashMap<String, EnvFileParserExtension>();
        for (EnvFileParserExtension extension: Extensions.getExtensions(getExtensionPointName())) {
            if (parsers.containsKey(extension.getId())) {
                Logger.getInstance(EnvFileParserExtension.class).error(String.format(
                        "Cannot load parser '%s' with implementation class '%s' since there is already parser '%s' " +
                                "registered with the same id. Skipping.",
                        extension.getId(), extension.implementationClass, parsers.get(extension.getId())
                ));
            } else {
                parsers.put(extension.getId(), extension);
            }
        }
        return parsers.get(parserId);
    }

    public static List<EnvFileParserExtension> getParserExtensions() {
        List<EnvFileParserExtension> extensions = Arrays.asList(Extensions.getExtensions(getExtensionPointName()));
        Collections.sort(extensions, new Comparator<EnvFileParserExtension>() {
            @Override
            public int compare(EnvFileParserExtension o1, EnvFileParserExtension o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        return extensions;
    }

    @Attribute("id")
    public String id;

    @Attribute("implementationClass")
    public String implementationClass;

    protected final LazyInstance<EnvFileParser> implementation = new LazyInstance<EnvFileParser>() {
        protected Class<EnvFileParser> getInstanceClass() throws ClassNotFoundException {
            return findClass(implementationClass);
        }
    };

    public EnvFileParser getParser() {
        return implementation.getValue();
    }

    public String getId() {
        if (id == null) {
            String error = "No id specified for environment variables files parser implementation class "
                    + implementationClass;
            if (myPluginDescriptor != null) {
                throw new PluginException(error, myPluginDescriptor.getPluginId());
            }
            throw new IllegalArgumentException(error);
        }
        return id.toLowerCase();
    }

    @Override
    public String toString() {
        return getParser().getTitle();
    }
}
