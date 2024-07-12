public abstract class Regex {

    // for comparing the AST,
    // but we can't use that in general
    // since different (a | b) in the syntax tree might have different first sets
    abstract boolean isSemanticallyEqual(Regex other);

    static class Empty extends Regex{

        public String toString(){
            return "ε";
        }

        public boolean isSemanticallyEqual(Regex o){
            return (o instanceof Empty);
        }
    }

    static class Cat extends Regex{
        Regex left;
        Regex right;

        public Cat(Regex left, Regex right){
            this.left = left;
            this.right = right;
        }

        public String toString(){
            return String.format("%s%s", left.toString(), right.toString());
        }

        
        public boolean isSemanticallyEqual(Regex o){
            return (o instanceof Cat otherCat) && (left.isSemanticallyEqual(otherCat.left) && right.isSemanticallyEqual(otherCat.right));
        }
    }

    static class Star extends Regex{
        Regex r;

        public Star(Regex r){
            this.r = r;
        }

        public String toString(){
            return String.format("%s*", r);
        }

        @Override
        public boolean isSemanticallyEqual(Regex o){
            return (o instanceof Star otherStar) && r.isSemanticallyEqual(otherStar.r);
        }
    }

    static class Letter extends Regex{

        char c;

        public Letter(char c){
            this.c = c;
        }

        public String toString(){
            return "" + c;
        }

        @Override
        public boolean isSemanticallyEqual(Regex o){
            return (o instanceof Letter otherLetter) && c == otherLetter.c;
        }
    }

    static class Grouping extends Regex{
        Regex r;

        public Grouping(Regex r){
            this.r = r;
        }

        public String toString(){
            return String.format("(%s)", r);
        }

        @Override
        public boolean isSemanticallyEqual(Regex o){
            return (o instanceof Grouping otherGrouping) && r.isSemanticallyEqual(otherGrouping.r);
        }
    }

    static class Or extends Regex{

        Regex left;
        Regex right;

        public Or(Regex left, Regex right){
            this.left = left;
            this.right = right;
        }

        public String toString(){
            return String.format("%s | %s", left, right);
        }

        @Override
        public boolean isSemanticallyEqual(Regex o){
            return (o instanceof Or otherOr) && (left.isSemanticallyEqual(otherOr.left) && right.isSemanticallyEqual(otherOr.right));
        }
    }
}
