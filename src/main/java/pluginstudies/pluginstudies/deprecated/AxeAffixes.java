package pluginstudies.pluginstudies.deprecated;

import pluginstudies.pluginstudies.Crafting.Interfaces.AffixTableAcessInterface;
import pluginstudies.pluginstudies.utils.PairDT;

import java.util.*;

import static pluginstudies.pluginstudies.Crafting.CraftingUtils.*;
@Deprecated
public enum AxeAffixes implements AffixTableAcessInterface {
    PREFIXES(                 new PairDT<>("Added physical DMG",
                    mapRanges(new PairDT<>(2, generateRange(1,1,2,3)),
                              new PairDT<>(13, generateRange(4,5,8,9)),
                              new PairDT<>(21, generateRange(6,8,13,15)),
                              new PairDT<>(29, generateRange(7,11,16,19)),
                              new PairDT<>(36, generateRange(9,13,20,24)),
                              new PairDT<>(46, generateRange(13,17,26,30)),
                              new PairDT<>(54, generateRange(14,19,29,35)),
                              new PairDT<>(65, generateRange(17,24,36,41)),
                              new PairDT<>(77, generateRange(20,27,41,49)) ) ),

                              new PairDT<>("Physical DMG%",
                    mapRanges(new PairDT<>(1, generateRange(40,49)),
                              new PairDT<>(11, generateRange(50,64)),
                              new PairDT<>(23, generateRange(65,84)),
                              new PairDT<>(35, generateRange(85,109)),
                              new PairDT<>(46, generateRange(110,134)),
                              new PairDT<>(60, generateRange(135,154)),
                              new PairDT<>(73, generateRange(155,169)),
                              new PairDT<>(83, generateRange(170,179)) ) ),

                            new PairDT<>("Added fire DMG",
                    mapRanges(new PairDT<>(1, generateRange(1,2,3,4)),
                            new PairDT<>(12, generateRange(8,10,15,18)),
                            new PairDT<>(18, generateRange(12,16,24,28)),
                            new PairDT<>(26, generateRange(17,22,33,39)),
                            new PairDT<>(33, generateRange(21,28,42,49)),
                            new PairDT<>(42, generateRange(26,35,53,61)),
                            new PairDT<>(51, generateRange(32,42,63,74)),
                            new PairDT<>(62, generateRange(38,51,77,89)),
                            new PairDT<>(74, generateRange(45,61,91,106)) ) ),

                            new PairDT<>("Elemental DMG%",
                    mapRanges(new PairDT<>(4, generateRange(5,10)),
                            new PairDT<>(15, generateRange(11,20)),
                            new PairDT<>(30, generateRange(21,30)),
                            new PairDT<>(60, generateRange(31,37)),
                            new PairDT<>(81, generateRange(38,42)) ) ) ),



    SUFFIXES(               new PairDT<>("Accuracy",
                    mapRanges(new PairDT<>(1, generateRange(5,15)),
                            new PairDT<>(12, generateRange(16,60)),
                            new PairDT<>(20, generateRange(61,100)),
                            new PairDT<>(26, generateRange(101,130)),
                            new PairDT<>(33, generateRange(131,165)),
                            new PairDT<>(41, generateRange(166, 200)),
                            new PairDT<>(50, generateRange(201,250)),
                            new PairDT<>(63, generateRange(251, 320))) ),

                            new PairDT<>("Life on kill",
                    mapRanges(new PairDT<>(1, generateRange(3,6)),
                            new PairDT<>(23, generateRange(7,10)),
                            new PairDT<>(40, generateRange(11,14)) ) ),

                            new PairDT<>("STR",
                    mapRanges(new PairDT<>(1, generateRange(8,12)),
                            new PairDT<>(11, generateRange(13,17)),
                            new PairDT<>(22, generateRange(18,22)),
                            new PairDT<>(33, generateRange(23,27)),
                            new PairDT<>(44, generateRange(28,32)),
                            new PairDT<>(55, generateRange(33,37)),
                            new PairDT<>(66, generateRange(38,42)),
                            new PairDT<>(74, generateRange(43,50)),
                            new PairDT<>(85, generateRange(51,55))) ),

                            new PairDT<>("Bleed on hit%",
                    mapRanges(new PairDT<>(15, generateRange(10,10)),
                            new PairDT<>(55, generateRange(15,15)),
                            new PairDT<>(85, generateRange(20,20)) ) ) );

    private final Map<String, Map<Integer, List<int[]>>> affixes = new HashMap<>();
    private List<String> affixList = new ArrayList<>();

    AxeAffixes(PairDT<String, Map<Integer, List<int[]>>>... affixMapEntries){
        List<PairDT<String, Map<Integer, List<int[]>>>> affixList = Arrays.asList(affixMapEntries);
        for (PairDT<String, Map<Integer, List<int[]>>> affixEntry : affixList){
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
