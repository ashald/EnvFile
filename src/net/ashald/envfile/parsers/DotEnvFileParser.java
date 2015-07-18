package net.ashald.envfile.parsers;

import com.intellij.openapi.util.io.FileUtilRt;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class DotEnvFileParser implements EnvFileParser {
    @NotNull
    @Override
    public Map<String, String> readFile(@NotNull String path) throws EnvFileErrorException, IOException {
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
            String key = (String) e.nextElement();
            String value = prop.getProperty(key);
            result.put(key, value);
        }

        return result;
    }

    @NotNull
    public String getTitle() {
        return ".env";
    }
}
