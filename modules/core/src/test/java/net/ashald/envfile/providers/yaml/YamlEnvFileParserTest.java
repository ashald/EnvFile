package net.ashald.envfile.providers.yaml;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class YamlEnvFileParserTest {
    private final YamlEnvFileParser PARSER = new YamlEnvFileParser(new Yaml());

    /**
     * TODO: generalize
     */
    @SneakyThrows
    private static String getFile(String name) {
        val bytes = Files.readAllBytes(Paths.get("src", "test", "resources", "providers", "yaml", name));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test
    public void GIVEN_parse_WHEN_parses_THEN_preservesOrder() {
        val result = PARSER.parse(
                getFile("order.yaml")
        );

        val keys = ImmutableList.copyOf(
                result.keySet()
        );

        assertEquals(
                ImmutableList.of("C", "B", "A"),
                keys
        );
    }

}
