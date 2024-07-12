import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// construct the states as you read in the input (otherwise, too much memory)
public class NFA {

    // every state of the NFA has a unique string representation
    Set<String> finalStates;

    String startState;

    Map<Pair<String, Character>, Set<String>> transitions;

    public NFA(String startState, Set<String> finalStates, Map<Pair<String, Character>, Set<String>> transitions){
        this.startState = startState;
        this.finalStates = finalStates;
        this.transitions = transitions;
    }

    // the main purpose of an NFA
    // realizes the language set
    public boolean isAccepted(String input){

        Set<String> currentStateSet = Set.of(startState);

        for(char c : input.toCharArray()){

            Set<String> nextStates = new HashSet<>();

            for(String state : currentStateSet){

                Pair<String, Character> stateC = new Pair<>(state, c);

                if(transitions.containsKey(stateC)){
                    nextStates.addAll(transitions.get(stateC));
                }
            }

            currentStateSet = nextStates;

            // special case, no possibilities left open
            if(currentStateSet.isEmpty()){
                break;
            }
        }

        // is a final state among them?
        // if so, there exists a path in the automaton that leads to acceptance

        // which one is unimportant
        for(String finalState : finalStates){
            if(currentStateSet.contains(finalState)){
                return true;
            }
        }

        return false;
    }
}
