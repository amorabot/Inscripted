package pluginstudies.pluginstudies.utils;

public class ItemLevelRange {

    private int ilvl;
    private int[] range;

    public ItemLevelRange(int ilvl, int[] range){
        this.ilvl = ilvl;
        this.range = range;
    }

    public int getIlvl() {
        return ilvl;
    }

    public int[] getRange() {
        return range;
    }
}
