package net.ashald.envfile.providers.toml;

import com.google.common.collect.ImmutableList;
import com.moandjiezana.toml.Toml;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class TomlEnvFileParserTest {
    private final TomlEnvFileParser PARSER = new TomlEnvFileParser(new Toml());

    /**
     * TODO: generalize
     */
    @SneakyThrows
    private static String getFile(String name) {
        val bytes = Files.readAllBytes(Paths.get("src", "test", "resources", "providers", "toml", name));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test
    public void GIVEN_parse_WHEN_parses_THEN_preservesOrder() {
        val result = PARSER.parse(
                getFile("mise.toml")
        );

        val keys = ImmutableList.copyOf(
                result.keySet()
        );

        assertEquals(
                ImmutableList.of("A", "B", "C"),
                keys
        );
    }

}
