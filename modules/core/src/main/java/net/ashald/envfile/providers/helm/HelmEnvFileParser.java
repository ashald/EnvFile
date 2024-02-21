package net.ashald.envfile.providers.helm;

import lombok.AllArgsConstructor;
import net.ashald.envfile.providers.EnvFileParser;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class HelmEnvFileParser implements EnvFileParser {
    private final Yaml yaml;

    @Override
    public Map<String, String> parse(String content) {
        Map<String, Object> yamlRoot = yaml.load(content);

        Map<String, String> envMap = extractVariables(yamlRoot, "env");
        Map<String, String> secretMap = extractVariables(yamlRoot, "secret");
        return mergeMaps(envMap, secretMap);
    }

    Map<String, String> mergeMaps(Map<String, String> env, Map<String, String> secret){
        return Stream.of(env, secret)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    Map<String, String> extractVariables(Map<String, Object> root, String key) {
        ArrayList<Map<String, String>> envs = (ArrayList<Map<String, String>>) root.get(key);
        if(envs != null) {
            return envs.stream()
                    .filter(this::isValdEntry)
                    .collect(Collectors.toMap(s -> s.get("name"), s -> s.get("value")));
        }
        return Collections.emptyMap();
    }

    boolean isValdEntry(Map<String, String> mapEntry) {
        return mapEntry.containsKey("name") && mapEntry.containsKey("value");
    }

}
