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
        // TODO
    }

    public boolean isEmpty(Regex r){
        return empty.contains(rToId.get(r));
    }

    public Set<Integer> getFirst(Regex r){
        return null;
    }

    public NFA generatorAutomaton(Regex r){

        init(r);

        createEmpty(r);

        createFirst(r);

        return null;
    }
}
