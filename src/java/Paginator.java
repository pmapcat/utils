package utils.java.paginator;
import java.util.ArrayList;
import java.util.List;
public class Paginator {
    public static List<String>  Paginate(int current,int last){
        int delta = 2;
        int left  =  current - delta;
        int right = current + delta + 1;
        int l     = 0;
        List<Integer> items_range = new ArrayList<Integer>();
        List<String> items_range_with_dots = new ArrayList<String>();
        for (int i = 1;i<=last;i++) {
            if(i ==1 || i == last || i>= left && i < right){
                items_range.add(i);
            }
        }
        for (int k = 0; k < items_range.size(); k++) {
         Integer i = items_range.get(k);
            if(l != 0){
                if ((i-l) == 2) {
                    items_range_with_dots.add(Integer.toString(l+1));
                }else if(i-l != 1){
                    items_range_with_dots.add("...");
                }
            }
            items_range_with_dots.add(Integer.toString(i));
            l = i;
        }
        return items_range_with_dots;
    }
}

