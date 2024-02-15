package net.ashald.envfile.providers.helm;

import lombok.AllArgsConstructor;
import net.ashald.envfile.providers.EnvFileParser;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class HelmEnvFileParser implements EnvFileParser {
    private final Yaml yaml;

    @Override
    public Map<String, String> parse(String content) {

        Map<String, Object> root = yaml.load(content);
        ArrayList<Map<String, String>> envs = (ArrayList<Map<String, String>>) root.get("env");

        return envs.stream().collect(Collectors.toMap(s -> s.get("name"), s -> s.get("value")));
    }
}
