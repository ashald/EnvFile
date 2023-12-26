package net.ashald.envfile.providers.yaml;

import lombok.AllArgsConstructor;
import net.ashald.envfile.providers.EnvFileParser;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class YamlEnvFileParser implements EnvFileParser {
    private final Yaml yaml;

    @Override
    public Map<String, String> parse(String content) {
        Map<String, Object> value = yaml.load(content);
        Set<String> keys = value.keySet();
        Map<String, String> result = new LinkedHashMap<>();
        for (String key : keys) {
            Object v = value.get(key);
            if (v != null && v.getClass().equals(String.class)) {
                result.put(key, (String) v);
            } else {
                String stringValue = String.valueOf(v);
                result.put(key, stringValue);
            }
        }
        return result;
    }
}
