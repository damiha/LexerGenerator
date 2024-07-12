import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegexEngineTest {

    @Test
    public void test1(){
        RegexEngine regexEngine = new RegexEngine("a | b*");

        assertTrue(regexEngine.isAccepted("a"));
        assertTrue(regexEngine.isAccepted("bbbbbbb"));
        assertTrue(regexEngine.isAccepted(""));

        assertFalse(regexEngine.isAccepted("aa"));
        assertFalse(regexEngine.isAccepted("bbbba"));
    }

    @Test
    public void test2(){

        // numbers in the ternary system?
        RegexEngine regexEngine = new RegexEngine("0 | (1 | 2)(0 | 1 | 2)*");

        assertTrue(regexEngine.isAccepted("0"));
        assertTrue(regexEngine.isAccepted("1201"));
        assertTrue(regexEngine.isAccepted("11122221"));

        assertFalse(regexEngine.isAccepted(""));
        assertFalse(regexEngine.isAccepted("00"));
        assertFalse(regexEngine.isAccepted("01"));
        assertFalse(regexEngine.isAccepted("123"));
    }
}