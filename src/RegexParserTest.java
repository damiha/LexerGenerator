import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegexParserTest {

    @Test
    public void testParser1(){

        RegexParser regexParser = new RegexParser();

        Regex r = regexParser.parse("a | bb*");

        Regex.Letter a = new Regex.Letter('a');
        Regex.Letter b = new Regex.Letter('b');

        Regex expected = new Regex.Or(
                a,
                new Regex.Cat(b, new Regex.Star(b))
        );

        Regex r2 = regexParser.parse("b(a | ε)*");

        Regex expected2 = new Regex.Cat(
                b,
                new Regex.Star(new Regex.Grouping(new Regex.Or(a, new Regex.Empty())))
        );

        assertEquals(expected2, r2);
    }

    @Test
    public void testParser3(){

        RegexParser regexParser = new RegexParser();

        Regex r = regexParser.parse("(a | b)*a(a | b)");

        Regex a = new Regex.Letter('a');
        Regex b = new Regex.Letter('b');

        Regex expected = new Regex.Cat(
                new Regex.Star(
                        new Regex.Grouping(new Regex.Or(a, b))
                ),
                new Regex.Cat(
                        a, new Regex.Grouping(new Regex.Or(a, b))
                )
        );

        assertEquals(expected, r);
    }
}