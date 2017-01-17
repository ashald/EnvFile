package net.ashald.envfile.parsers;

import net.ashald.envfile.AbstractEnvFileParser;
import net.ashald.envfile.EnvFileErrorException;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DotEnvFileParser extends AbstractEnvFileParser {

    @NotNull
    @Override
    protected Map<String, String> readFile(@NotNull String path) throws EnvFileErrorException, IOException {
        Map<String, String> result = new HashMap<String, String>();

        InputStream input = null;
        Properties prop = new Properties();
        try {
            input = new FileInputStream(path);
            prop.load(input);
        } catch (IOException ex) {
            throw new EnvFileErrorException(ex);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = trim((String) e.nextElement());
            String value = trim(prop.getProperty(key));
            result.put(key, value);
        }

        return result;
    }

    private static String trim(String value) {
        String trimmed = value.trim();

        if ( (trimmed.startsWith("\"") && trimmed.endsWith("\"")) || (trimmed.startsWith("'") && trimmed.endsWith("'")))
            return trimmed.substring(1, trimmed.length() - 1);

        return trimmed;
    }

    @NotNull
    public String getTitle() {
        return ".env";
    }
}
