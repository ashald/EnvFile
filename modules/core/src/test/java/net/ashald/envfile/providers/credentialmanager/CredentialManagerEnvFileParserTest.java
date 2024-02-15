package net.ashald.envfile.providers.credentialmanager;

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


public class CredentialManagerEnvFileParserTest {
    private final CredentialManagerEnvFileParser PARSER = new CredentialManagerEnvFileParser(new Yaml());

    /**
     * TODO: generalize
     */
    @SneakyThrows
    private static String getFile(String name) {
        val bytes = Files.readAllBytes(Paths.get("src", "test", "resources", "providers", "credentialmanager", name));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test
    public void GIVEN_parse_WHEN_parses_THEN_preservesOrder() {
        val result = PARSER.parse(
                getFile("credentials.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, CoreMatchers.hasItems("SECRET_USER_A", "SECRET_USER_B"));

        val values = ImmutableList.copyOf(result.values());
        assertThat(values, CoreMatchers.hasItems("SECRET_PASS_A", "SECRET_PASS_B"));
    }

}
