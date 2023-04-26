package pluginstudies.pluginstudies.CustomDataTypes;

import org.javatuples.Triplet;
import pluginstudies.pluginstudies.Crafting.ItemModifierAccess;
import pluginstudies.pluginstudies.Crafting.ItemRarities;
import pluginstudies.pluginstudies.Crafting.ItemTypes;

import java.io.Serializable;
import java.util.List;

public class ItemInformation <E extends Enum<E> & ItemModifierAccess, T extends ItemTypes> implements Serializable {

    private static final long serialversionUID = 1000101L;

    private boolean identified;
    private int ilvl;
    private ItemRarities rarity;
    private String implicit;
    private T itemType;
    private Enum<?> modifierTable;
//    private List<Triplet<? extends ItemModifierAccess, Integer, List<int[]>>> PrefixList;
//    private ModifierInformation modInfo;
    //TODO: IMPLEMENTAR MÉTODOS PARA CADA VALOR NO ENUM ITEMTYPES -> RETORNA A TABELA DE MODIFICADORES CORRESPONDENTE
    //a tabela retornada fica como um enum genérico Enum<?> que só vai ser acessado pelo método de ItemTypes vindo do construtor, garantindo type-safety e sincronização
}
