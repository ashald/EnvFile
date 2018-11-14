package net.ashald.envfile.providers.dotenv;
import net.ashald.envfile.EnvFileErrorException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DotEnvFileParserTest {

    private DotEnvFileParser parser = new DotEnvFileParser(true);

    private String getFile(String name) {
        return Paths.get("src","test", "resources", "providers", "dotenv", name).toString();
    }

    @Test
    public void testMalformedEncoding() throws EnvFileErrorException {
        parser.getEnvVars(Collections.emptyMap(), getFile("malformed-unicode.env")); // should not fail
    }

    @Test
    public void testLinesStartingWithHasAreIgnored() throws EnvFileErrorException {
        Map<String, String> result = parser.getEnvVars(Collections.emptyMap(), getFile("full-line-comments.env"));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("value", result.get("key"));
    }

    @Test
    public void testInlineComments() throws EnvFileErrorException {
        Map<String, String> result = parser.getEnvVars(Collections.emptyMap(), getFile("inline-comments.env"));
        Assert.assertEquals("foo#bar", result.get("key1"));
        Assert.assertEquals("foo", result.get("key2"));
        Assert.assertEquals("foo #bar", result.get("key3"));
        Assert.assertEquals("foo #bar", result.get("key4"));
        Assert.assertEquals("foo #bar", result.get("key5"));
    }

    @Test
    public void testBackslashesArePreserved() throws EnvFileErrorException {
        Map<String, String> result = parser.getEnvVars(Collections.emptyMap(), getFile("backslashes.env"));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("value\\1", result.get("TEST_VAR"));
    }

    @Test
    public void testSubstitutions() throws EnvFileErrorException, IOException {
        Map<String, String> context = new HashMap<String, String>() {{
            put("FOO", "BAR");
        }};

        Map<String, String> result = parser.process(Collections.emptyMap(), getFile("substitutions.env"), context);
        Assert.assertEquals("", result.get("A"));
        Assert.assertEquals("default", result.get("B"));
        Assert.assertEquals("BAR", result.get("C"));
        Assert.assertEquals("BAR default", result.get("D"));
        Assert.assertEquals("BAR", result.get("E"));
    }

    @Test
    public void testOrder() throws EnvFileErrorException, IOException {
        Map<String, String> result = parser.process(Collections.emptyMap(), getFile("order.env"), Collections.emptyMap());
        Assert.assertEquals("A(B(C))", result.get("A"));
    }

}
