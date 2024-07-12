public class RegexEngine {

    NFA nfa;

    // one regex engine for one expression (NFA construction relatively expensive)
    public RegexEngine(String regularExpression){

        RegexParser parser = new RegexParser();
        AutomatonGenerator generator = new AutomatonGenerator();

        // this is an AST
        Regex regex = parser.parse(regularExpression);

        nfa = generator.generatorAutomaton(regex);
    }

    public boolean isAccepted(String s){
        return nfa.isAccepted(s);
    }
}
