import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class NFATest {

    @Test
    public void test1(){

        // example taken from: https://www.javatpoint.com/non-deterministic-finite-automata

        String q0 = "q0";
        String q1 = "q1";
        String q2 = "q2";

        Set<String> finalStates = Set.of(q1);

        Map<Pair<String, Character>, Set<String>> transitionFunction = new HashMap<>();
        transitionFunction.put(new Pair<>(q0, 'a'), Set.of(q1, q2));
        transitionFunction.put(new Pair<>(q0, 'b'), Set.of(q0, q1));
        transitionFunction.put(new Pair<>(q1, 'a'), Set.of(q1));

        NFA nfa = new NFA(q0, finalStates, transitionFunction);

        assertTrue(nfa.isAccepted("aaaa"));
        assertTrue(nfa.isAccepted("a"));
        assertTrue(nfa.isAccepted("bbbba"));
        assertFalse(nfa.isAccepted("ab"));
    }
}