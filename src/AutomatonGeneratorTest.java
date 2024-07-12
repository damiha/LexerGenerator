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

        List<Regex> regexInTree = Utils.flatten(r);

        // result for the root node?
        assertFalse(generator.isEmpty(r));

        for(Regex regex : regexInTree){

            if(regex.isSemanticallyEqual(a) || regex.isSemanticallyEqual(b) || regex.isSemanticallyEqual(aOrb)){
                assertFalse(generator.isEmpty(regex));
            }

            else if(regex.isSemanticallyEqual(aOrBStar)) {
                assertTrue(generator.isEmpty(regex));
            }
        }
    }
}