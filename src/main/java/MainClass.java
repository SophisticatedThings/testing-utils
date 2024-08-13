import java.util.ArrayList;
import java.util.List;

public class MainClass {
    public static void main(String[] args) {
        final List<Integer> list = new ArrayList<>(List.of(1,4,3,1));
        list.add(1);
        System.out.println(list.size());
    }
}
