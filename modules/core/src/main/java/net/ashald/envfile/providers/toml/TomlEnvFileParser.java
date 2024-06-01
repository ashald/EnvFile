package net.ashald.envfile.providers.toml;

import com.moandjiezana.toml.Toml;
import lombok.AllArgsConstructor;
import lombok.val;
import net.ashald.envfile.providers.EnvFileParser;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class TomlEnvFileParser implements EnvFileParser {
    private final Toml toml;

    @Override
    public Map<String, String> parse(String content) {
        val result = new HashMap<String, String>();
        val envTable = toml
                .read(content)
                .getTable("env");

        if (envTable == null) {
            return result;
        }

        for (val entry : envTable.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toString());
        }

        return result;
    }
}
