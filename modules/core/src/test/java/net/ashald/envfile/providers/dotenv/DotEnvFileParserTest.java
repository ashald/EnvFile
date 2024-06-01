package net.ashald.envfile.providers.dotenv;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class DotEnvFileParserTest {
    private static final DotEnvFileParser PARSER = DotEnvFileParser.INSTANCE;

    /**
     * TODO: generalize
     */
    @SneakyThrows
    private static String getFile(String name) {
        val bytes = Files.readAllBytes(Paths.get("src", "test", "resources", "providers", "dotenv", name));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Test
    public void GIVEN_parse_WHEN_charSequenceLooksLikeMalformedUnicode_THEN_doesNotFail() {
        val result = PARSER.parse(
                getFile("malformed-unicode.env")
        );

        assertEquals(ImmutableMap.of("FOO", "\\usr"), result);
    }

    @Test
    public void GIVEN_parse_WHEN_fullLineComment_THEN_ignored() {
        val result = PARSER.parse(
                getFile("full-line-comments.env")
        );

        assertEquals(
                ImmutableMap.of("key", "value"),
                result
        );
    }

    @Test
    public void GIVEN_parse_WHEN_poundSignPresentInline_THEN_ignoredOnlyWhenUnescapedWithPrecedingBlankSpace() {
        val result = PARSER.parse(
                getFile("inline-comments.env")
        );

        assertEquals(
                ImmutableMap.of(
                        "key1", "foo#bar",
                        "key2", "foo",
                        "key3", "foo #bar",
                        "key4", "foo #bar",
                        "key5", "foo #bar"
                ),
                result
        );
    }

    @Test
    public void GIVEN_parse_WEHN_backslahesPresent_THEN_preserved() {
        val result = PARSER.parse(
                getFile("backslashes.env")
        );

        assertEquals(
                ImmutableMap.of(
                        "TEST_VAR", "value\\1"
                ),
                result
        );
    }

    @Test
    public void GIVEN_parse_WHEN_parses_THEN_preservesOrder() {
        val result = PARSER.parse(
                getFile("order.env")
        );

        val keys = ImmutableList.copyOf(
                result.keySet()
        );

        assertEquals(
                ImmutableList.of("C", "B", "A"),
                keys
        );
    }

    @Test
    public void GIVEN_parse_WHEN_multilineVariable_THEN_preserved() {
        val result = PARSER.parse(
                getFile("multi-line-variable.env")
        );

        assertEquals(
                ImmutableMap.of(
                        "PRIVATE_KEY", "-----BEGIN RSA PRIVATE KEY-----\nHkVN9...\n-----END DSA PRIVATE KEY-----\n"
                ),
                result
        );
    }

    @Test
    public void GIVEN_parse_WHEN_multilineVariableWithLineBreaks_THEN_preserved() {
        val result = PARSER.parse(
                getFile("multi-line-variable-with-line-breaks.env")
        );

        assertEquals(
                ImmutableMap.of(
                        "PRIVATE_KEY", "-----BEGIN RSA PRIVATE KEY-----\n" +
                                "...\n" +
                                "HkVN9...\n" +
                                "...\n" +
                                "-----END DSA PRIVATE KEY-----"
                ),
                result
        );
    }

    @Test
    public void GIVEN_parse_WHEN_multilineVariableWithLineBreaksAndQuotes_THEN_preserved() {
        val result = PARSER.parse(
                getFile("multi-line-variable-with-line-breaks-and-quotes.env")
        );

        assertEquals(
                ImmutableMap.of(
                        "JSON", "{\n" +
                                "\"foo\": \"bar\",\n" +
                                "\"fizz\": \"fuzz\"\n" +
                                "}"
                ),
                result
        );
    }
}
