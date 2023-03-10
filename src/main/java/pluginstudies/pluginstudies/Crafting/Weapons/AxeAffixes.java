package pluginstudies.pluginstudies.Crafting.Weapons;

import pluginstudies.pluginstudies.Crafting.TableAcessInterface;
import pluginstudies.pluginstudies.utils.Pair;

import java.util.*;

import static pluginstudies.pluginstudies.Crafting.CraftingUtils.*;

public enum AxeAffixes implements TableAcessInterface {
    PREFIXES(                 new Pair<>("Added physical DMG",
                    mapRanges(new Pair<>(2, generateRange(1,1,2,3)),
                              new Pair<>(13, generateRange(4,5,8,9)),
                              new Pair<>(21, generateRange(6,8,13,15)),
                              new Pair<>(29, generateRange(7,11,16,19)),
                              new Pair<>(36, generateRange(9,13,20,24)),
                              new Pair<>(46, generateRange(13,17,26,30)),
                              new Pair<>(54, generateRange(14,19,29,35)),
                              new Pair<>(65, generateRange(17,24,36,41)),
                              new Pair<>(77, generateRange(20,27,41,49)) ) ),

                              new Pair<>("Physical DMG%",
                    mapRanges(new Pair<>(1, generateRange(40,49)),
                              new Pair<>(11, generateRange(50,64)),
                              new Pair<>(23, generateRange(65,84)),
                              new Pair<>(35, generateRange(85,109)),
                              new Pair<>(46, generateRange(110,134)),
                              new Pair<>(60, generateRange(135,154)),
                              new Pair<>(73, generateRange(155,169)),
                              new Pair<>(83, generateRange(170,179)) ) ),

                            new Pair<>("Added fire DMG",
                    mapRanges(new Pair<>(1, generateRange(1,2,3,4)),
                            new Pair<>(12, generateRange(8,10,15,18)),
                            new Pair<>(18, generateRange(12,16,24,28)),
                            new Pair<>(26, generateRange(17,22,33,39)),
                            new Pair<>(33, generateRange(21,28,42,49)),
                            new Pair<>(42, generateRange(26,35,53,61)),
                            new Pair<>(51, generateRange(32,42,63,74)),
                            new Pair<>(62, generateRange(38,51,77,89)),
                            new Pair<>(74, generateRange(45,61,91,106)) ) ),

                            new Pair<>("Elemental DMG%",
                    mapRanges(new Pair<>(4, generateRange(5,10)),
                            new Pair<>(15, generateRange(11,20)),
                            new Pair<>(30, generateRange(21,30)),
                            new Pair<>(60, generateRange(31,37)),
                            new Pair<>(81, generateRange(38,42)) ) ) ),



    SUFFIXES(               new Pair<>("Accuracy",
                    mapRanges(new Pair<>(1, generateRange(5,15)),
                            new Pair<>(12, generateRange(16,60)),
                            new Pair<>(20, generateRange(61,100)),
                            new Pair<>(26, generateRange(101,130)),
                            new Pair<>(33, generateRange(131,165)),
                            new Pair<>(41, generateRange(166, 200)),
                            new Pair<>(50, generateRange(201,250)),
                            new Pair<>(63, generateRange(251, 320))) ),

                            new Pair<>("Life on kill",
                    mapRanges(new Pair<>(1, generateRange(3,6)),
                            new Pair<>(23, generateRange(7,10)),
                            new Pair<>(40, generateRange(11,14)) ) ),

                            new Pair<>("STR",
                    mapRanges(new Pair<>(1, generateRange(8,12)),
                            new Pair<>(11, generateRange(13,17)),
                            new Pair<>(22, generateRange(18,22)),
                            new Pair<>(33, generateRange(23,27)),
                            new Pair<>(44, generateRange(28,32)),
                            new Pair<>(55, generateRange(33,37)),
                            new Pair<>(66, generateRange(38,42)),
                            new Pair<>(74, generateRange(43,50)),
                            new Pair<>(85, generateRange(51,55))) ),

                            new Pair<>("Bleed on hit%",
                    mapRanges(new Pair<>(15, generateRange(10,10)),
                            new Pair<>(55, generateRange(15,15)),
                            new Pair<>(85, generateRange(20,20)) ) ) );

    private final Map<String, Map<Integer, List<int[]>>> affixes = new HashMap<>();
    private List<String> affixList = new ArrayList<>();

    AxeAffixes(Pair<String, Map<Integer, List<int[]>>>... affixMapEntries){
        List<Pair<String, Map<Integer, List<int[]>>>> affixList = Arrays.asList(affixMapEntries);
        for (Pair<String, Map<Integer, List<int[]>>> affixEntry : affixList){
            this.affixes.put(affixEntry.getName(), affixEntry.getRange());
            this.affixList.add(affixEntry.getName());
        }
    }
    @Override
    public Map<Integer, List<int[]>> getAffix(String affixName){
        return affixes.get(affixName);
    }
    @Override
    public String[] getAffixNameArray(){
        String[] affixNames = new String[affixList.size()];
        return affixList.toArray(affixNames);
    }
}
