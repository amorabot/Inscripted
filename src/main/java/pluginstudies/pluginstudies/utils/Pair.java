package pluginstudies.pluginstudies.utils;

import java.util.List;
import java.util.Map;

public class Pair <T , V>{
    // para uso com affixes: <T extends java.lang.String, V extends Map<Integer, List<int[]>>>
    private final T name;
    private final V range;

    public Pair(T value, V ranges){
        assert value != null;
        assert ranges != null;

        this.name = value;
        this.range = ranges;
    }

    public T getName() {
        return name;
    }

    public V getRange() {
        return range;
    }
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Pair)){
            return false;
        }
        Pair genericPair = (Pair) o;
        return (this.name.equals(genericPair.getName()) && this.range.equals(genericPair.getRange()));
    }
}
