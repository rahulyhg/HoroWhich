package ie.moses.horowhich;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CollectionUtils {

    public static <T> List<T> list(T... ts) {
        List<T> list = new ArrayList<>(ts.length);
        list.addAll(Arrays.asList(ts));
        return list;
    }

}
