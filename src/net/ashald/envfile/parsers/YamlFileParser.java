package net.ashald.envfile.parsers;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class YamlFileParser implements EnvFileParser {
    @NotNull
    @Override
    public Map<String, String> readFile(@NotNull String path) throws EnvFileErrorException, IOException {
        Map<String, String> result;
        InputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            result = (Map< String, String>) new Yaml().load(input);
        } catch (ClassCastException e) {
            throw new EnvFileErrorException(
                    String.format("Cannot read '%s' as YAML dict - not all keys and/or values are strings", path), e);
        } catch (FileNotFoundException e) {
            throw new EnvFileErrorException(e);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        if (result == null) {
            result = new HashMap<String, String>();
        }
        return result;
    }

    @NotNull
    public String getTitle() {
        return "YAML";
    }
}
