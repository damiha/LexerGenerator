import java.util.ArrayList;
import java.util.List;

public class Utils {

    static List<Regex> flatten(Regex r){

        List<Regex> list = new ArrayList<>(List.of(r));

        switch (r) {
            case Regex.Cat cat -> {
                list.addAll(flatten(cat.left));
                list.addAll(flatten(cat.right));
            }
            case Regex.Or or -> {
                list.addAll(flatten(or.left));
                list.addAll(flatten(or.right));
            }
            case Regex.Star star -> list.addAll(flatten(star.r));
            case Regex.Grouping grouping -> list.addAll(flatten(grouping.r));
            default -> {
            }
        }

        return list;
    }
}
