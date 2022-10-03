package net.ashald.envfile.providers.yaml;

import lombok.AllArgsConstructor;
import net.ashald.envfile.providers.EnvFileParser;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

@AllArgsConstructor
public class YamlEnvFileParser implements EnvFileParser {
    private final Yaml yaml;

    @Override
    public Map<String, String> parse(String content) {
        return yaml.load(content);
    }
}
