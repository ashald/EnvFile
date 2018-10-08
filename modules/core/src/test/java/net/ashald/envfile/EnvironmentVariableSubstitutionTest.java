package net.ashald.envfile;

import java.util.HashMap;
import java.util.Map;

import net.ashald.envfile.providers.dotenv.DotEnvFileParser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class EnvironmentVariableSubstitutionTest {

    private static final String SINGLE_VAR_NAME = "SINGLE";
    private static final String SNAKE_CASE_VAR_NAME = "SNAKE_CASE";
    private static final String DOT_SEP_VAR_NAME = "dot.separated.name";

    private static final String SINGLE_VAR_PLACE_HOLDER = "${"+ SINGLE_VAR_NAME +"}";
    private static final String SNAKE_CASE_VAR_PLACE_HOLDER = "${"+ SNAKE_CASE_VAR_NAME +"}";
    private static final String DOT_SEP_VAR_PLACE_HOLDER = "${"+ DOT_SEP_VAR_NAME +"}";

    private static final String SINGLE_VAR_VALUE = "SINGLE_VAR_VALUE";
    private static final String SNAKE_CASE_VAR_VALUE = "SNAKE_CASE_VAR_VALUE";
    private static final String DOT_SEP_VAR_VALUE = "DOT_SEP_VAR_VALUE";

    private static final String KEY = "TEST_VAR";

    private DotEnvFileParser parser = new DotEnvFileParser();

    @BeforeClass
    public static void beforeClass() {
        System.setProperty(SINGLE_VAR_NAME, SINGLE_VAR_VALUE);
        System.setProperty(SNAKE_CASE_VAR_NAME, SNAKE_CASE_VAR_VALUE);
        System.setProperty(DOT_SEP_VAR_NAME, DOT_SEP_VAR_VALUE);
    }

    @Test
    public void singleEnvVarTest() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, SINGLE_VAR_PLACE_HOLDER);
        map = ((AbstractEnvFileParser)parser).expandEnvironmentVariables(map);
        Assert.assertEquals("Placeholder was not replaced with single var value", SINGLE_VAR_VALUE, map.get(KEY));
    }

    @Test
    public void snakeCaseEnvironmentVariables() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, SNAKE_CASE_VAR_PLACE_HOLDER);
        map = ((AbstractEnvFileParser)parser).expandEnvironmentVariables(map);
        Assert.assertEquals("Placeholder was not replaced with snake case var value", SNAKE_CASE_VAR_VALUE, map.get(KEY));
    }

    @Test
    public void dotSeparatedEnvVar() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, DOT_SEP_VAR_PLACE_HOLDER);
        map = ((AbstractEnvFileParser)parser).expandEnvironmentVariables(map);
        Assert.assertEquals("Placeholder was not replaced with dot separated case var value", DOT_SEP_VAR_VALUE, map.get(KEY));
    }

    @Test
    public void forwardSlashPathTest() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, SINGLE_VAR_PLACE_HOLDER + "/" + SNAKE_CASE_VAR_PLACE_HOLDER + "/" + DOT_SEP_VAR_PLACE_HOLDER);
        map = ((AbstractEnvFileParser)parser).expandEnvironmentVariables(map);
        Assert.assertEquals(SINGLE_VAR_VALUE + "/" + SNAKE_CASE_VAR_VALUE + "/" + DOT_SEP_VAR_VALUE, map.get(KEY));
    }

    @Test
    public void backSlashPathTest() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, SINGLE_VAR_PLACE_HOLDER + "\\" + SNAKE_CASE_VAR_PLACE_HOLDER + "\\" + DOT_SEP_VAR_PLACE_HOLDER);
        map = ((AbstractEnvFileParser)parser).expandEnvironmentVariables(map);
        Assert.assertEquals(SINGLE_VAR_VALUE + "\\" + SNAKE_CASE_VAR_VALUE + "\\" + DOT_SEP_VAR_VALUE, map.get(KEY));
    }

    @Test
    public void prependEnvVarTest() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, "/prepend/path:" + SINGLE_VAR_PLACE_HOLDER);
        map = ((AbstractEnvFileParser)parser).expandEnvironmentVariables(map);
        Assert.assertEquals("Placeholder was not replaced with single var value", "/prepend/path:" + SINGLE_VAR_VALUE, map.get(KEY));
    }

    @Test
    public void appendEnvVarTest() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, SINGLE_VAR_PLACE_HOLDER + ":/append/path");
        map = ((AbstractEnvFileParser)parser).expandEnvironmentVariables(map);
        Assert.assertEquals("Placeholder was not replaced with single var value", SINGLE_VAR_VALUE + ":/append/path", map.get(KEY));
    }

    @Test
    public void precedenceEnvVarTest() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, "preferred");
        System.setProperty(KEY, "un-preferred");
        map = ((AbstractEnvFileParser)parser).expandEnvironmentVariables(map);
        Assert.assertEquals("Precedence was not followed", "preferred", map.get(KEY));
    }

    @Test
    public void unchangedEnvVarTest() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, "${UNCHANGED}");
        map = ((AbstractEnvFileParser)parser).expandEnvironmentVariables(map);
        Assert.assertEquals("Property should have been unchanged", "${UNCHANGED}", map.get(KEY));
    }

}