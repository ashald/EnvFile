package net.ashald.envfile.providers.helm;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;


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
    public void parseThreeVariablePairs() {
        val result = PARSER.parse(
                getFile("test_values.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, hasSize(3));
        assertThat(keys, hasItems("NAME_A", "NAME_B", "NAME_C"));

        val values = ImmutableList.copyOf(result.values());
        assertThat(values, hasSize(3));
        assertThat(values, hasItems("VALUE_A", "VALUE_B", "VALUE_C"));
    }

    @Test
    public void parseThreeVariablePairsAndSecrets() {
        val result = PARSER.parse(
                getFile("test_values.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, hasSize(5));
        assertThat(keys, hasItems("NAME_A", "NAME_B", "NAME_C", "SECRET_A", "SECRET_B"));

        val values = ImmutableList.copyOf(result.values());
        assertThat(values, hasSize(5));
        assertThat(values, hasItems("VALUE_A", "VALUE_B", "VALUE_C", "SECRET_VALUE_A", "SECRET_VALUE_B"));
    }

    @Test
    public void parseEmptyList() {
        val result = PARSER.parse(
                getFile("test_values_none.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, is(empty()));

        val values = ImmutableList.copyOf(result.values());
        assertThat(values, is(empty()));
    }

    @Test
    public void parseMissingList() {
        val result = PARSER.parse(
                getFile("test_values_missing.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, is(empty()));

        val values = ImmutableList.copyOf(result.values());
        assertThat(values, is(empty()));
    }

    @Test
    public void parseInvalidList() {
        val result = PARSER.parse(
                getFile("test_values_invalid.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, hasSize(2));
        assertThat(keys, hasItems("NAME_A", "NAME_C"));

        val values = ImmutableList.copyOf(result.values());
        assertThat(values, hasSize(2));
        assertThat(values, hasItems("VALUE_A", "VALUE_C"));
    }

}
