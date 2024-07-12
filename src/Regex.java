public class Regex {

    static class Empty extends Regex{

        public String toString(){
            return "ε";
        }

        @Override
        public boolean equals(Object o){
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

        @Override
        public boolean equals(Object o){
            return (o instanceof Cat otherCat) && (left.equals(otherCat.left) && right.equals(otherCat.right));
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
        public boolean equals(Object o){
            return (o instanceof Star otherStar) && r.equals(otherStar.r);
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
        public boolean equals(Object o){
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
        public boolean equals(Object o){
            return (o instanceof Grouping otherGrouping) && r.equals(otherGrouping.r);
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
        public boolean equals(Object o){
            return (o instanceof Or otherOr) && (left.equals(otherOr.left) && right.equals(otherOr.right));
        }
    }
}
