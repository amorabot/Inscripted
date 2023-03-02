package pluginstudies.pluginstudies.Crafting.Weapons;

import java.util.*;

import static pluginstudies.pluginstudies.utils.Utils.*;

public enum AxeAffixes {

    PREFIXES( new String[]{
            "FLAT_PHYS",
            "INCREASED_PHYS"
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
                    generateRange(170,179)) );
//    SUFFIXES();

    //Todos os afixos serão listas contendo um Map. Esse map mapeia o item level do item com uma lista de ranges
    //nessa lista de ranges teremos os valores dos atributos com base em um ilvl específico
    //esses ranges podem ser do tipo (##% to ##%) ou [(##% to ##%) to (@@% to @@%)]
//    private List<Map<Integer, List<int[]>>> affixes;
//    private List<Map<Integer, List<int[]>>> suffixes;
//    private Map<Integer, List<int[]> baseDmg;
    private final Map<String, Map<Integer, List<int[]>>> affixes = new HashMap<>();


    AxeAffixes(String[] affixName, Map<Integer, List<int[]>>... affix){
//        if (affixName.length == affix.length){ //só podemos relacionar <K, V> se o número de keys é igual ao de values
//            for (int i = 0; i < affixName.length; i++){ //para cada nome de affixo, adicione o afixo correspondente
//                affixes.put(affixName[i], affix[i]);
//            }
//        }
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
}
