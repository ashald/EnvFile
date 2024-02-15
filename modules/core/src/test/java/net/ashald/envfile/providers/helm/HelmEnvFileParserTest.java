package net.ashald.envfile.providers.helm;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.val;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;


public class HelmEnvFileParserTest {
    private final HelmEnvFileParser PARSER = new HelmEnvFileParser(new Yaml());

    /**
     * TODO: generalize
     */
    @SneakyThrows
    private static String getFile(String name) {
        val bytes = Files.readAllBytes(Paths.get("src", "test", "resources", "providers", "helm", name));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test
    public void GIVEN_parse_WHEN_parses_THEN_preservesOrder() {
        val result = PARSER.parse(
                getFile("test_values.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, CoreMatchers.hasItems("NAME_A", "NAME_B", "NAME_C"));

        val values = ImmutableList.copyOf(result.values());
        assertThat(values, CoreMatchers.hasItems("VALUE_A", "VALUE_B", "VALUE_C"));
    }

}
