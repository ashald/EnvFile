package net.ashald.envfile.providers.dotenv;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: cleanup - these tests are total mess
 */
public class DotEnvFileParserTest {
    private static final DotEnvFileParser PARSER = new DotEnvFileParser(true);

    private static InputStream getFile(String name) throws IOException {
        return Files.newInputStream(Paths.get("src", "test", "resources", "providers", "dotenv", name));
    }

    @SneakyThrows
    private static Map<String, String> parse(String file, Map<String, String> context) {
        try (val content = getFile(file)) {
            return PARSER.getEnvVars(context, content, null);
        }
    }

    @Test
    @SneakyThrows
    public void testMalformedEncoding() {
        parse("malformed-unicode.env", Collections.emptyMap()); // should not fail
    }

    @Test
    @SneakyThrows
    public void testLinesStartingWithHasAreIgnored() {
        Map<String, String> result = parse("full-line-comments.env", Collections.emptyMap());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("value", result.get("key"));
    }

    @Test
    @SneakyThrows
    public void testInlineComments() {
        Map<String, String> result = parse("inline-comments.env", Collections.emptyMap());

        Assert.assertEquals("foo#bar", result.get("key1"));
        Assert.assertEquals("foo", result.get("key2"));
        Assert.assertEquals("foo #bar", result.get("key3"));
        Assert.assertEquals("foo #bar", result.get("key4"));
        Assert.assertEquals("foo #bar", result.get("key5"));
    }

    @Test
    @SneakyThrows
    public void testBackslashesArePreserved() {
        Map<String, String> result = parse("backslashes.env", Collections.emptyMap());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("value\\1", result.get("TEST_VAR"));
    }

    @Test
    @SneakyThrows
    public void testSubstitutions() {
        Map<String, String> context = new HashMap<String, String>() {{
            put("FOO", "BAR");
        }};

        Map<String, String> result;

        try (val content = getFile("substitutions.env")) {
            result = PARSER.process(Collections.emptyMap(), context, content, null);
        }
        Assert.assertEquals("", result.get("A"));
        Assert.assertEquals("default", result.get("B"));
        Assert.assertEquals("BAR", result.get("C"));
        Assert.assertEquals("BAR default", result.get("D"));
        Assert.assertEquals("BAR", result.get("E"));
    }

    @Test
    @SneakyThrows
    public void testOrder() {
        Map<String, String> result;
        try (val content = getFile("order.env")) {
            result = PARSER.process(Collections.emptyMap(), Collections.emptyMap(), content, null);
        }

        Assert.assertEquals("A(B(C))", result.get("A"));
    }

    @Test
    @SneakyThrows
    public void testMultiLineVariables() {
        Map<String, String> result = parse("multi-line-variable.env", Collections.emptyMap());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("-----BEGIN RSA PRIVATE KEY-----\nHkVN9...\n-----END DSA PRIVATE KEY-----\n", result.get("PRIVATE_KEY"));
    }

    @Test
    @SneakyThrows
    public void testMultiLineVariablesWithLineBreaks() {
        Map<String, String> result = parse("multi-line-variable-with-line-breaks.env", Collections.emptyMap());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("-----BEGIN RSA PRIVATE KEY-----\n" +
                "...\n" +
                "HkVN9...\n" +
                "...\n" +
                "-----END DSA PRIVATE KEY-----", result.get("PRIVATE_KEY"));
    }
}
