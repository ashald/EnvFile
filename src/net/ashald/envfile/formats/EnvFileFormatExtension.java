package net.ashald.envfile.formats;

import com.intellij.diagnostic.PluginException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.AbstractExtensionPointBean;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class EnvFileFormatExtension extends AbstractExtensionPointBean {
    protected static final ExtensionPointName<EnvFileFormatExtension> EP_NAME =
            new ExtensionPointName<EnvFileFormatExtension>("net.ashald.envfile.envfileFormat");

    protected static ExtensionPointName<EnvFileFormatExtension> getExtensionPointName() {
        return EP_NAME;
    }

    public static String getParserIdByExtension(@NotNull String givenFileExtension) {
        Map<String, List<String>> parsersByExtension = new HashMap<String, List<String>>();

        for (EnvFileFormatExtension extension: Extensions.getExtensions(getExtensionPointName())) {
            if (!parsersByExtension.containsKey(extension.getFileExtension())) {
                parsersByExtension.put(extension.getFileExtension(), new ArrayList<String>());
            }

            List<String> parsersForExtension = parsersByExtension.get(extension.getFileExtension());
            if (parsersForExtension.contains(extension.getParserId())) {
                Logger.getInstance(EnvFileFormatExtension.class).error(String.format(
                        "Duplicate definition for environment variables file format extension with parser id '%s' " +
                                "and file extension '%s'. Skipping.",
                        extension.getParserId(), extension.getFileExtension()
                ));
            } else {
                parsersForExtension.add(extension.getParserId());
            }
        }

        for (List<String> handlers: parsersByExtension.values()) {
            Collections.sort(handlers, new Comparator<String>() {
                @Override
                public int compare(String s, String str) {
                    return s.compareToIgnoreCase(str);
                }
            });
        }

        List<String> knownParsers = parsersByExtension.get(givenFileExtension);
        return knownParsers == null || knownParsers.size() == 0 ? null : knownParsers.get(0);
    }

    @Attribute("fileExtension")
    public String fileExtension;

    @Attribute("parserId")
    public String parserId;

    public String getFileExtension() {
        if (fileExtension == null) {
            String error = "No file extension specified for environment variables file format";
            if (parserId != null) {
                error += " with parser id=" + parserId;
            }
            if (myPluginDescriptor != null) {
                throw new PluginException(error, myPluginDescriptor.getPluginId());
            }
            throw new IllegalArgumentException(error);
        }
        return fileExtension.toLowerCase();
    }

    public String getParserId() {
        if (parserId == null) {
            String error = "No file extension specified for environment variables file format";
            if (fileExtension != null) {
                error += String.format(" with parser file extension '%s'",  fileExtension);
            }
            if (myPluginDescriptor != null) {
                throw new PluginException(error, myPluginDescriptor.getPluginId());
            }
            throw new IllegalArgumentException(error);
        }
        return parserId;
    }
}
