import org.junit.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AutomatonGeneratorTest {

    @Test
    public void testEmpty1(){

        RegexParser regexParser = new RegexParser();

        Regex r = regexParser.parse("(a | b)*a(a | b)");

        AutomatonGenerator generator = new AutomatonGenerator();

        NFA nfa = generator.generatorAutomaton(r);

        Regex a = new Regex.Letter('a');
        Regex b = new Regex.Letter('b');

        Regex aOrb = new Regex.Or(a, b);

        Regex aOrBStar = new Regex.Star(new Regex.Grouping(aOrb));
        Regex aCatAOrB = new Regex.Cat(a, new Regex.Grouping(aOrb));

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

        // at least the sizes of the first sets must be the same
        // we don't know what the ids mean
        Map<Regex, Integer> expectedFirstSetSize = new HashMap<>();
        expectedFirstSetSize.put(r, 3);
        expectedFirstSetSize.put(aOrb, 2);
        expectedFirstSetSize.put(aCatAOrB, 1);
        expectedFirstSetSize.put(a, 1);

        for(Regex expected : expectedFirstSetSize.keySet()){

            for(Regex in : regexInTree){
                if(expected.isSemanticallyEqual(in)){
                    assertEquals(expectedFirstSetSize.get(expected), generator.getFirst(in).size());
                }
            }
        }

        // for the next sets
        // we only print the stuff
        // seems correct
        printAllNext(generator, r, 0);

        Map<Regex, Integer> expectedLastSetSize = new HashMap<>();
        expectedLastSetSize.put(r, 2);
        expectedLastSetSize.put(aOrb, 2);
        expectedLastSetSize.put(aCatAOrB, 2);
        expectedLastSetSize.put(a, 1);

        for(Regex expected : expectedLastSetSize.keySet()){

            for(Regex in : regexInTree){
                if(expected.isSemanticallyEqual(in)){
                    assertEquals(expectedLastSetSize.get(expected), generator.getLast(in).size());
                }
            }
        }

        // test the behavior of the constructed NFA

        // must end either with aa or ab, rest is arbitrary
        //(a | b)*a(a | b)

        assertTrue(nfa.isAccepted("aa"));
        assertTrue(nfa.isAccepted("ab"));

        assertTrue(nfa.isAccepted("aaaa"));
        assertTrue(nfa.isAccepted("baab"));

        assertFalse(nfa.isAccepted("aabb"));
        assertFalse(nfa.isAccepted("baba"));
    }

    @Test
    public void testEpsilonReduction(){
        RegexParser regexParser = new RegexParser();

        AutomatonGenerator generator = new AutomatonGenerator();

        Regex reduced1 = generator.getEpislonReducedRegex(regexParser.parse("a | ε"));
        assertEquals("a?", reduced1.toString());

        Regex reduced2 = generator.getEpislonReducedRegex(regexParser.parse("a | εεεb | εc"));
        assertEquals("a | b | c", reduced2.toString());

        Regex reduced3 = generator.getEpislonReducedRegex(regexParser.parse("ε"));
        assertEquals("ε", reduced3.toString());

        Regex reduced4 = generator.getEpislonReducedRegex(regexParser.parse("ε | a? | ε"));
        assertEquals("a?", reduced4.toString());
    }

    private void printAllNext(AutomatonGenerator g, Regex r, int level){

        String indent = "\t".repeat(level);

        System.out.printf(indent + "For %s (id: %d): %s\n", r, g.rToId.get(r), g.getNext(r));

        if(r instanceof  Regex.Letter || r instanceof Regex.Empty){
            // no op, already done
        }
        else if(r instanceof Regex.Cat cat){

            System.out.println(indent + "Going left...");
            printAllNext(g, cat.left, level + 1);
            System.out.println(indent + "Going right...");
            printAllNext(g, cat.right, level + 1);
        }
        else if(r instanceof Regex.Or or){
            System.out.println(indent + "Going left...");
            printAllNext(g, or.left, level + 1);
            System.out.println(indent + "Going right...");
            printAllNext(g, or.right, level + 1);
        }
        else if(r instanceof Regex.Star star){
            System.out.println(indent + "Going down...");
            printAllNext(g, star.r, level + 1);
        }
        else if(r instanceof Regex.Grouping grouping){
            System.out.println(indent + "Going down...");
            printAllNext(g, grouping.r, level + 1);
        }
        else {
            throw new RuntimeException("Unknown regex type");
        }
    }
}