package pluginstudies.pluginstudies.DEPRECATEDCLASSES;
@Deprecated
public class PairDT<T , V>{
    // para uso com affixes: <T extends java.lang.String, V extends Map<Integer, List<int[]>>>
    private final T name;
    private final V range;

    public PairDT(T value, V ranges){
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
        if (!(o instanceof PairDT)){
            return false;
        }
        PairDT genericPair = (PairDT) o;
        return (this.name.equals(genericPair.getName()) && this.range.equals(genericPair.getRange()));
    }
}
