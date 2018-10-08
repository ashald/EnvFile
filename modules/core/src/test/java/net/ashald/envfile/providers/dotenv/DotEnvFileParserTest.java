package net.ashald.envfile.providers.dotenv;
import net.ashald.envfile.EnvFileErrorException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class DotEnvFileParserTest {

    private DotEnvFileParser parser = new DotEnvFileParser();

    private String createFile(String content) throws IOException {
        File file = File.createTempFile("test", ".env");
        file.deleteOnExit();

        String filePath = file.getAbsolutePath();
        Files.write(Paths.get(filePath), content.getBytes());
        return filePath;
    }

    @Test
    public void testMalformedEncoding() throws IOException, EnvFileErrorException {
        parser.readFile(createFile("\\usr")); // should not fail
    }

    @Test
    public void testLinesStartingWithHasAreIgnored() throws IOException, EnvFileErrorException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter, true);
        writer.println("# ignored");
        writer.println(" # also ignored");
        writer.println("key=value");

        Map<String, String> result = parser.readFile(createFile(stringWriter.toString()));
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("value", result.get("key"));
    }

    @Test
    public void testInlineComments() throws IOException, EnvFileErrorException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter, true);
        writer.println("key1=foo#bar"); // not a comment
        writer.println("key2=foo #bar"); // a comment
        writer.println("key3='foo #bar'"); // a comment
        writer.println("key4=\"foo #bar\""); // a comment
        writer.println("key5=foo \\#bar"); // a comment

        Map<String, String> result = parser.readFile(createFile(stringWriter.toString()));
        Assert.assertEquals("foo#bar", result.get("key1"));
        Assert.assertEquals("foo", result.get("key2"));
        Assert.assertEquals("foo #bar", result.get("key3"));
        Assert.assertEquals("foo #bar", result.get("key4"));
        Assert.assertEquals("foo #bar", result.get("key5"));
    }

}
