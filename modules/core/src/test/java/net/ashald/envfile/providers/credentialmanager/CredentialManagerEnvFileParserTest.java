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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;


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

    // Required entries in Windows Credential Manager
    @Test
    public void parseTwoSecrets() {
        val result = PARSER.parse(
                getFile("credentials.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, hasSize(2));
        assertThat(keys, hasItems("SECRET_USER_A", "SECRET_USER_B"));

        val values = ImmutableList.copyOf(result.values());
        assertThat(values, hasItems("SECRET_PASS_A", "SECRET_PASS_B"));
    }

    @Test
    public void parseEmptyList() {
        val result = PARSER.parse(
                getFile("credentials_empty.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, is(empty()));

        val values = ImmutableList.copyOf(result.values());
        assertThat(keys, is(empty()));
    }

    @Test
    public void parseNoneCredentials() {
        val result = PARSER.parse(
                getFile("credentials_none.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, is(empty()));

        val values = ImmutableList.copyOf(result.values());
        assertThat(keys, is(empty()));
    }

    @Test
    public void parseMissingCredentials() {
        val result = PARSER.parse(
                getFile("credentials_missing.yaml")
        );

        val keys = ImmutableList.copyOf(result.keySet());
        assertThat(keys, hasSize(1));
        assertThat(keys, hasItems("SECRET_USER_A"));

        val values = ImmutableList.copyOf(result.values());
        assertThat(values, hasItems("SECRET_PASS_A"));
    }
}
