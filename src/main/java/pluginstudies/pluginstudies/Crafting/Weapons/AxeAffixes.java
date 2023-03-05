package pluginstudies.pluginstudies.Crafting.Weapons;

import java.util.*;

import static pluginstudies.pluginstudies.utils.Utils.*;

public enum AxeAffixes {

    PREFIXES( new String[]{
            "FLAT_PHYS",
            "INCREASED_PHYS",
            "FIRE_DMG",
            "INCREASED_ELE"
    },      mapRanges(new int[]
                    {2,13,21,29,36,46,54,65,77},
                    generateRange(1,1,2,3),
                    generateRange(4,5,8,9),
                    generateRange(6,8,13,15),
                    generateRange(7,11,16,19),
                    generateRange(9,13,20,24),
                    generateRange(13,17,26,30),
                    generateRange(14,19,29,35),
                    generateRange(17,24,36,41),
                    generateRange(20,27,41,49)),
            mapRanges(new int[]
                    {1,11,23,35,46,60,73,83},
                    generateRange(40,49),
                    generateRange(50,64),
                    generateRange(65,84),
                    generateRange(85,109),
                    generateRange(110,134),
                    generateRange(135,154),
                    generateRange(155,169),
                    generateRange(170,179)),
            mapRanges(new int[]
                    {1, 12, 18, 26, 33, 42, 51, 62, 74},
                    generateRange(1,2,3,4),
                    generateRange(8,10,15,18),
                    generateRange(12,16,24,28),
                    generateRange(17,22,33,39),
                    generateRange(21,28,42,49),
                    generateRange(26,35,53,61),
                    generateRange(32,42,63,74),
                    generateRange(38,51,77,89),
                    generateRange(45,61,91,106)),
            mapRanges(new int[]
                    {4, 15, 30, 60, 81},
                    generateRange(5,10),
                    generateRange(11,20),
                    generateRange(21,30),
                    generateRange(31,37),
                    generateRange(38,42)) ),
    SUFFIXES( new String[]{
            "FLAT_ACCURACY",
            "LIFE_ON_KILL",
            "STR",
            "BLEED_ON_HIT"
    },      mapRanges(new int[]
                    {1, 12, 20, 26, 33, 41, 50, 63},
                    generateRange(5,15),
                    generateRange(16,60),
                    generateRange(61,100),
                    generateRange(101,130),
                    generateRange(131,165),
                    generateRange(166,200),
                    generateRange(201,250),
                    generateRange(251,320)),
            mapRanges(new int[]
                    {1, 23, 40},
                    generateRange(3,6),
                    generateRange(7,10),
                    generateRange(11,14)),
            mapRanges(new int[]
                    {1, 11, 22, 33, 44, 55, 66, 74, 85},
                    generateRange(8,12),
                    generateRange(13,17),
                    generateRange(18,22),
                    generateRange(23,27),
                    generateRange(28,32),
                    generateRange(33,37),
                    generateRange(38,42),
                    generateRange(43, 50),
                    generateRange(51,55)),
            mapRanges(new int[]
                    {15, 55, 85},
                    generateRange(10,10),
                    generateRange(15,15),
                    generateRange(20,20)) );

    private final Map<String, Map<Integer, List<int[]>>> affixes = new HashMap<>();
    private String[] affixList = null;


    AxeAffixes(String[] affixName, Map<Integer, List<int[]>>... affix){
        affixList = affixName;
        List<Map<Integer, List<int[]>>> affixList = Arrays.asList(affix);
        if (affixList.size() == affixName.length){
            for (int i = 0; i< affixName.length; i++){
                this.affixes.put(affixName[i], affixList.get(i));
            }
        }
    }
    public Map<Integer, List<int[]>> getAffix(String affixName){
        return affixes.get(affixName);
    }
    public String[] getAffixList(){
        return affixList;
    }
}
