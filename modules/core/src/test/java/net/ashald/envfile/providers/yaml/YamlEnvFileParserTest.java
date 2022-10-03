package net.ashald.envfile.providers.yaml;

import lombok.val;
import net.ashald.envfile.EnvFileErrorException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class YamlEnvFileParserTest {
    private final YamlEnvFileParser PARSER = new YamlEnvFileParser(true);

    private static InputStream getFile(String name) throws IOException {
        return Files.newInputStream(Paths.get("src", "test", "resources", "providers", "yaml", name));
    }

    @Test
    public void testSubstitutions() throws EnvFileErrorException, IOException {
        Map<String, String> context = new HashMap<String, String>() {{
            put("FOO", "BAR");
        }};

        Map<String, String> result;

        try (val content = getFile("substitutions.yaml")) {
            result = PARSER.process(
                    Collections.emptyMap(),
                    context,
                    content,
                    null
            );
        }

        Assert.assertEquals("", result.get("A"));
        Assert.assertEquals("default", result.get("B"));
        Assert.assertEquals("BAR", result.get("C"));
        Assert.assertEquals("BAR default", result.get("D"));
        Assert.assertEquals("BAR", result.get("E"));
    }

    @Test
    public void testOrder() throws EnvFileErrorException, IOException {
        Map<String, String> result;
        try (val content = getFile("order.yaml")) {
            result = PARSER.process(Collections.emptyMap(), Collections.emptyMap(), content, null);
        }

        Assert.assertEquals("A(B(C))", result.get("A"));
    }

}
