
// turns the regex into an AST
// needed for the berry sethi construction
public class RegexParser {

    // grammar

    // cat over or
    // grouping over cat

    // or -> and(r)*
    // and -> star(r)*
    // star -> primary"*"
    // primary -> letter | empty | (r)

    int current;

    String s;

    public Regex parse(String s){

        // delete all white spaces and add the EOF token
        this.s = s.replaceAll("\\s+","") + "$";
        current = 0;

        return or();
    }

    public Regex regex(){
        return or();
    }

    public Regex or(){
        Regex r = and();

        while(match('|')){
            Regex right = or();
            r = new Regex.Or(r, right);
        }

        return r;
    }

    public Regex and(){

        // concat as many as possible
        // they all need to be at star level
        Regex r = star();

        while(peek() != '|' && peek() != ')' && peek() != '$'){
            Regex right = and();
            r = new Regex.Cat(r, right);
        }

        return r;
    }

    public Regex star(){
        Regex r = opt();

        if(match('*')){
            return new Regex.Star(r);
        }

        return r;
    }

    // ? binds stronger than * because a?* = (a | eps)*
    public Regex opt(){
        Regex r = primary();

        if(match('?')){
            return new Regex.Opt(r);
        }

        return r;
    }

    public Regex primary(){

        if(match('(')){

            Regex r = regex();

            consume(')', "Closing parentheses expected");

            return new Regex.Grouping(r);
        }
        else if(match('ε')){
            return new Regex.Empty();
        }
        // it's a letter,
        Regex r = new Regex.Letter(peek());
        advance();
        return r;
    }

    private boolean match(char c){
        if(!atEnd() && s.charAt(current) == c){
            current++;
            return true;
        }
        return false;
    }

    private char peek(){
        return s.charAt(current);
    }

    private void advance(){
        current++;
    }

    private char previous(){
        return s.charAt(current - 1);
    }

    private char consume(char c, String message){
        if(match(c)){
            return previous();
        }
        throw new RuntimeException(message);
    }

    private boolean atEnd(){
        return current >= s.length();
    }
}
