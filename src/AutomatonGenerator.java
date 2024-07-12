import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AutomatonGenerator {

    Set<Integer> empty;
    Map<Integer, Set<Integer>> first;
    Map<Integer, Set<Integer>> next;
    Map<Integer, Set<Integer>> last;

    Map<Regex, Integer> rToId;

    private void init(Regex r){
        empty = new HashSet<>();
        first = new HashMap<>();
        next = new HashMap<>();
        last = new HashMap<>();

        rToId = new HashMap<>();
        createRToId(r);
    }

    private void createRToId(Regex r){

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
        else if(r instanceof Regex.Star star){
            createRToId(star.r);
        }
    }

    // this is post order -> evaluate children first
    private void createEmpty(Regex r){

        if(r instanceof Regex.Empty){
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

            // TODO: what to do here
            throw new RuntimeException("Not implemented yet.");
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

            // TODO: what to do here
            throw new RuntimeException("Not implemented yet.");
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

            // TODO: what to do here
            throw new RuntimeException("Not implemented yet.");
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

    public Set<Integer> getLast(Regex r){
        return last.get(rToId.get(r));
    }

    public NFA generatorAutomaton(Regex r){

        init(r);

        createEmpty(r);

        createFirst(r);

        // we always assume that before the call to createNext(r)
        // its own next value has been set by its parent
        // start with the empty set because nothing comes after r
        next.put(rToId.get(r), new HashSet<>());
        createNext(r);

        createLast(r);

        return null;
    }
}
