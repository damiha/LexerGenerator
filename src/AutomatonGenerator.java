import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AutomatonGenerator {

    Set<Integer> empty;
    Map<Integer, Set<Integer>> first;
    Map<Integer, Set<Integer>> next;
    Map<Integer, Set<Integer>> last;

    Set<Integer> leaf;
    Map<Integer, Character> leafToCharacter;

    Map<Regex, Integer> rToId;

    private void init(Regex r){
        empty = new HashSet<>();
        leaf = new HashSet<>();
        first = new HashMap<>();
        next = new HashMap<>();
        last = new HashMap<>();

        rToId = new HashMap<>();
        leafToCharacter = new HashMap<>();
        createRToId(r);
    }

    public Regex getEpislonReducedRegex(Regex r){
        if(r instanceof Regex.Empty || r instanceof Regex.Letter || r instanceof Regex.Opt){
            return r;
        }
        else if(r instanceof Regex.Cat cat){

            // in the case, the whole expression is empty, we still get an epsilon
            Regex leftWithout = getEpislonReducedRegex(cat.left);
            Regex rightWithout = getEpislonReducedRegex(cat.right);

            if(leftWithout instanceof Regex.Empty){
                return rightWithout;
            }
            else if(rightWithout instanceof Regex.Empty){
                return leftWithout;
            }
            return new Regex.Cat(leftWithout, rightWithout);
        }
        else if(r instanceof Regex.Or or){
            Regex leftWithout = getEpislonReducedRegex(or.left);
            Regex rightWithout = getEpislonReducedRegex(or.right);

            if(leftWithout instanceof Regex.Empty && !(rightWithout instanceof Regex.Empty)){

                // don't allow ??
                if(rightWithout instanceof Regex.Opt){
                    return rightWithout;
                }
                return new Regex.Opt(rightWithout);
            }
            else if(rightWithout instanceof Regex.Empty && !(leftWithout instanceof Regex.Empty)){

                if(leftWithout instanceof Regex.Opt){
                    return leftWithout;
                }
                return new Regex.Opt(leftWithout);
            }
            else if(rightWithout instanceof Regex.Empty && leftWithout instanceof Regex.Empty){
                return new Regex.Empty();
            }
            // both non-empty
            return new Regex.Or(leftWithout, rightWithout);
        }
        else if(r instanceof Regex.Star star){

            Regex reducedChild = getEpislonReducedRegex(star.r);


            // (eps)* = eps
            if(reducedChild instanceof Regex.Empty){
                return new Regex.Empty();
            }

            return new Regex.Star(reducedChild);
        }
        else if(r instanceof Regex.Grouping grouping){

            Regex reducedChild = getEpislonReducedRegex(grouping.r);

            // (eps) = eps
            if(reducedChild instanceof Regex.Empty){
                return new Regex.Empty();
            }

            return new Regex.Grouping(reducedChild);
        }
        else {
            throw new RuntimeException("Unknown regex type");
        }
    }

    private void createRToId(Regex r){

        if(r instanceof Regex.Letter letter){
            leaf.add(rToId.size());
            leafToCharacter.put(rToId.size(), letter.c);
        }

        rToId.put(r, rToId.size());

        // now go to the children
        if(r instanceof Regex.Cat cat){
            createRToId(cat.left);
            createRToId(cat.right);
        }
        else if(r instanceof Regex.Or or){
            createRToId(or.left);
            createRToId(or.right);
        }
        else if(r instanceof Regex.Grouping grouping){
            createRToId(grouping.r);
        }
        else if(r instanceof Regex.Opt opt){
            createRToId(opt.r);
        }
        else if(r instanceof Regex.Star star){
            createRToId(star.r);
        }
    }

    // this is post order -> evaluate children first
    private void createEmpty(Regex r){

        if(r instanceof Regex.Empty){
           throw new RuntimeException("Should not contain epsilon anymore.");
        }
        else if(r instanceof Regex.Opt){
            empty.add(rToId.get(r));
        }
        else if(r instanceof Regex.Letter){
            // not empty
        }
        else if(r instanceof Regex.Cat cat){

            createEmpty(cat.left);
            createEmpty(cat.right);

            if(isEmpty(cat.left) && isEmpty(cat.right)) {
                empty.add(rToId.get(r));
            }
        }
        else if(r instanceof Regex.Or or){
            createEmpty(or.left);
            createEmpty(or.right);

            // empty = potentially empty
            // i.e. there exists one path to the empty word
            if(isEmpty(or.left) || isEmpty(or.right)) {
                empty.add(rToId.get(r));
            }
        }
        else if(r instanceof Regex.Star star){

            // this is just so the child gets updated
            // we already know empty == true
            createEmpty(star.r);
            empty.add(rToId.get(r));
        }
        else if(r instanceof Regex.Grouping grouping){
            createEmpty(grouping.r);

            if(isEmpty(grouping.r)){
                empty.add(rToId.get(r));
            }
        }
        else {
            throw new RuntimeException("Unknown regex type");
        }
    }

    // assumes that createEmpty has already been called
    private void createFirst(Regex r){

        if(r instanceof Regex.Empty){
            throw new RuntimeException("Should not contain epsilon anymore.");
        }
        else if(r instanceof Regex.Letter){

            int id = rToId.get(r);
            first.put(id, new HashSet<>(Set.of(id)));
        }
        else if(r instanceof Regex.Cat cat){

            createFirst(cat.left);
            createFirst(cat.right);

            int id = rToId.get(r);

            Set<Integer> firstForR = new HashSet<>(getFirst(cat.left));

            // add second if first one can be empty
            if(isEmpty(cat.left)) {
                firstForR.addAll(getFirst(cat.right));
            }

            first.put(id, firstForR);
        }
        else if(r instanceof Regex.Or or){

            createFirst(or.left);
            createFirst(or.right);

            // union the first sets
            Set<Integer> firstForR = new HashSet<>(getFirst(or.left));
            firstForR.addAll(getFirst(or.right));

            first.put(rToId.get(r), firstForR);
        }
        else if(r instanceof Regex.Star star){

            createFirst(star.r);

            // pass the first set to the parent
            Set<Integer> firstForR = new HashSet<>(getFirst(star.r));

            first.put(rToId.get(r), firstForR);
        }
        else if(r instanceof Regex.Opt opt){

            createFirst(opt.r);

            // pass the first set to the parent
            Set<Integer> firstForR = new HashSet<>(getFirst(opt.r));

            first.put(rToId.get(r), firstForR);
        }
        else if(r instanceof Regex.Grouping grouping){
            createFirst(grouping.r);

            // pass the first set to the parent
            Set<Integer> firstForR = new HashSet<>(getFirst(grouping.r));

            first.put(rToId.get(r), firstForR);
        }
        else {
            throw new RuntimeException("Unknown regex type");
        }
    }

    private void createLast(Regex r){
        if(r instanceof Regex.Empty){
            throw new RuntimeException("Should not contain epsilon anymore.");
        }
        else if(r instanceof Regex.Letter){

            int id = rToId.get(r);
            last.put(id, new HashSet<>(Set.of(id)));
        }
        else if(r instanceof Regex.Cat cat){

            createLast(cat.left);
            createLast(cat.right);

            int id = rToId.get(r);

            Set<Integer> lastForR = new HashSet<>(getLast(cat.right));

            // if second one can be empty, first one can be last
            if(isEmpty(cat.right)) {
                lastForR.addAll(getLast(cat.left));
            }

            last.put(id, lastForR);
        }
        else if(r instanceof Regex.Or or){

            createLast(or.left);
            createLast(or.right);

            // union the first sets
            Set<Integer> lastForR = new HashSet<>(getLast(or.left));
            lastForR.addAll(getLast(or.right));

            last.put(rToId.get(r), lastForR);
        }
        else if(r instanceof Regex.Star star){

            createLast(star.r);

            // pass the first set to the parent
            Set<Integer> lastForR = new HashSet<>(getLast(star.r));

            last.put(rToId.get(r), lastForR);
        }
        else if(r instanceof Regex.Opt opt){

            createLast(opt.r);

            // pass the first set to the parent
            Set<Integer> lastForR = new HashSet<>(getLast(opt.r));

            last.put(rToId.get(r), lastForR);
        }
        else if(r instanceof Regex.Grouping grouping){
            createLast(grouping.r);

            // pass the first set to the parent
            Set<Integer> lastForR = new HashSet<>(getLast(grouping.r));

            last.put(rToId.get(r), lastForR);
        }
        else {
            throw new RuntimeException("Unknown regex type");
        }
    }

    // needs first set and needs empty attribute
    // assumes that both have been called beforehand

    // the parent always sets attribute of his children
    // PRE order traversal
    private void createNext(Regex r){
        if(r instanceof Regex.Empty){
            throw new RuntimeException("Should not contain epsilon anymore.");
        }
        else if(r instanceof Regex.Letter){
            // parent has to set this
            // if this is the root,
            // next is initialized to the empty set
        }
        else if(r instanceof Regex.Cat cat){

            // next[r] is already initialized

            Set<Integer> nextForRight = new HashSet<>(getNext(r));
            Set<Integer> nextForLeft = new HashSet<>(getFirst(cat.right));

            // left can pass through right, so next could also be next of right
            if(isEmpty(cat.right)){
                nextForLeft.addAll(getNext(r));
            }

            // insert so that child calls can use them
            next.put(rToId.get(cat.left), nextForLeft);
            next.put(rToId.get(cat.right), nextForRight);

            createNext(cat.left);
            createNext(cat.right);
        }
        else if(r instanceof Regex.Or or){

            // next[r] is already initialized

            Set<Integer> nextForRight = new HashSet<>(getNext(r));
            Set<Integer> nextForLeft = new HashSet<>(getNext(r));

            // insert so that child calls can use them
            next.put(rToId.get(or.left), nextForLeft);
            next.put(rToId.get(or.right), nextForRight);

            createNext(or.left);
            createNext(or.right);
        }
        else if(r instanceof Regex.Star star){

            Set<Integer> nextForR = new HashSet<>(getNext(r));

            // could repeat it again so first set of itself?
            nextForR.addAll(getFirst(star.r));

            // child set so continue
            next.put(rToId.get(star.r), nextForR);

            createNext(star.r);
        }
        else if(r instanceof Regex.Opt opt){

            Set<Integer> nextForR = new HashSet<>(getNext(r));

            // child set so continue
            next.put(rToId.get(opt.r), nextForR);

            createNext(opt.r);
        }
        else if(r instanceof Regex.Grouping grouping){

            Set<Integer> nextForR = new HashSet<>(getNext(r));

            // child set so continue
            next.put(rToId.get(grouping.r), nextForR);

            createNext(grouping.r);
        }
        else {
            throw new RuntimeException("Unknown regex type");
        }
    }

    public boolean isEmpty(Regex r){
        return empty.contains(rToId.get(r));
    }

    public Set<Integer> getFirst(Regex r){
        return first.get(rToId.get(r));
    }

    public Set<Integer> getNext(Regex r){
        return next.get(rToId.get(r));
    }

    public Set<Integer> getNext(int i){
        return next.get(i);
    }

    public Set<Integer> getLast(Regex r){
        return last.get(rToId.get(r));
    }

    public NFA generatorAutomaton(Regex r){

        // a | eps -> a?
        // the slides only have generation rules for ?
        r = getEpislonReducedRegex(r);

        if(r instanceof Regex.Empty){
            // special case, the whole language is just the empty word
            // don't know if this is handled correctly by the Berry-Sethi construction
            String q0 = "q0";

            // only when no characters are read in (the empty word)
            // last state set is q0 so contained in final states
            // gets accepted
            return new NFA(q0, Set.of(q0), new HashMap<>());
        }

        init(r);

        createEmpty(r);

        createFirst(r);

        // we always assume that before the call to createNext(r)
        // its own next value has been set by its parent
        // start with the empty set because nothing comes after r
        next.put(rToId.get(r), new HashSet<>());
        createNext(r);

        createLast(r);

        // construction on slide 33 (lexer-b.pdf)
        String startState = "*S";

        Set<String> finalStates = new HashSet<>();

        for(Integer stateId : getLast(r)){
            finalStates.add(String.format("%d*", stateId));
        }

        // if r can be empty, make start state a final state
        if(isEmpty(r)){
            finalStates.add(startState);
        }

        Map<Pair<String, Character>, Set<String>> transitions = new HashMap<>();

        for(Integer firstFromStart : getFirst(r)){
            Pair<String, Character> p = new Pair<>("*S", leafToCharacter.get(firstFromStart));

            Set<String> nextAfterP = transitions.getOrDefault(p, new HashSet<>());
            nextAfterP.add(String.format("%d*", firstFromStart));

            transitions.put(p, nextAfterP);
        }

        for(Integer i : leaf){

            for(Integer afterI : getNext(i)){

                // *i, a, *i' where a is in i'
                Pair<String, Character> p = new Pair<>(String.format("%d*", i), leafToCharacter.get(afterI));

                Set<String> nextAfterP = transitions.getOrDefault(p, new HashSet<>());
                nextAfterP.add(String.format("%d*", afterI));

                transitions.put(p, nextAfterP);
            }
        }

        return new NFA(startState, finalStates, transitions);
    }
}
