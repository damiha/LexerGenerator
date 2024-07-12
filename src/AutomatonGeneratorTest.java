import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AutomatonGeneratorTest {

    @Test
    public void testEmpty1(){

        RegexParser regexParser = new RegexParser();

        Regex r = regexParser.parse("(a | b)*a(a | b)");

        AutomatonGenerator generator = new AutomatonGenerator();

        generator.generatorAutomaton(r);

        Regex a = new Regex.Letter('a');
        Regex b = new Regex.Letter('b');

        Regex aOrb = new Regex.Or(a, b);

        Regex aOrBStar = new Regex.Star(new Regex.Grouping(aOrb));

        assertFalse(generator.isEmpty(a));
        assertFalse(generator.isEmpty(b));
        assertFalse(generator.isEmpty(aOrb));
        assertTrue(generator.isEmpty(aOrBStar));
        assertFalse(generator.isEmpty(r));
    }
}