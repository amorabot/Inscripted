package com.amorabot.inscripted.components.Items.modifiers;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.Files.ModifierEditor;
import com.amorabot.inscripted.components.Items.modifiers.data.*;
import com.amorabot.inscripted.components.Items.modifiers.unique.Effects;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum InscriptionID {

    /*
    Definition string syntax:

        Num. of Mods     Affix     Stats     ValueTypeToken     RangeTypeToken     isPositiveToken

        GLOSSARY:
            ValueTypeToken
                ++  Flat value (FLAT)
                %+  Additive percentage (INCREASED)
                +%  Flat percentage value (PERCENT)
                %*  Multiplicative percentage (MULTI.)

            RangeTypeToken
                x  Single value to be deserialized (SINGLE_VALUE)
                -  Single range to be deserialized (SINGLE_RANGE)
                -/-  Double range to be deserialized (DOUBLE_RANGE)

            isPositiveToken
                +  The deserialized value will be treated as a positive integer
                -(or anything else)  The value will be treated as negative

        ==> When [num. of mods] is greater than 1 (Hybrid mods), Stats,ValueTypeToken... should be separated by &
    Examples:
        1.)  1 PREFIX STAMINA ++ - +                           ==>  Flat STAMINA (+)
        2.)  2 PREFIX INTELLIGENCE&STRENGTH ++&++ -&- +&+      ==>  Hybrid Flat INT(+) & STR(+)
        3.)  1 SUFFIX CRITICAL_DAMAGE +% - +                   ==>  Percent CRIT_DMG (+)

    If it's a meta/conversion mod, the @Meta annotation should be appended, containing the needed parameters

    Examples:
        1.) @Meta(..STR, 10)
            "1 PREFIX FIRE_DAMAGE ++ -/- +" 2, true, true
            Equates to => +1-2 fire dmg per 10 str

        2.) @Meta(..DEX, 50)
            "1 PREFIX WALK_SPEED %+ - +" 1, true, true
            Equates to => 1% ws per 50 dex

        3.) @Meta(..DEX, 100)
            "1 PREFIX PROJECTILES ++ x +" 1, true, true
            Equates to => +1 proj per 100 dex
    */

    //=====PREFIXES=====
    //Regular prefixes
    HEALTH("1 PREFIX HEALTH ++ - +", 12,false),
    HEALTH_PERCENT("1 PREFIX HEALTH %+ - +", 6, false),
    HEALTH_REGEN_PERCENT("1 PREFIX HEALTH_REGEN %+ - +", 6, true),
    ARMOR("1 PREFIX ARMOR ++ - +", 10, false),
    ARMOR_PERCENT("1 PREFIX ARMOR %+ - +", 6, false),
    DODGE("1 PREFIX DODGE ++ - +", 4, false),
    WARD("1 PREFIX WARD ++ - +", 12, false),
    WARD_PERCENT("1 PREFIX WARD %+ - +", 6, false),
    STRENGTH_PERCENT("1 PREFIX STRENGTH %+ - +", 3,true),
    DEXTERITY_PERCENT("1 PREFIX DEXTERITY %+ - +", 3,true),
    INTELLIGENCE_PERCENT("1 PREFIX INTELLIGENCE %+ - +", 3,true),
    WALK_SPEED("1 PREFIX WALK_SPEED ++ - +", 4,true),
    STAMINA("1 PREFIX STAMINA ++ - +", 6, true),
    PERCENT_PHYSICAL("1 PREFIX PHYSICAL_DAMAGE %+ - +", 8, false),
    PERCENT_ELEMENTAL("1 PREFIX ELEMENTAL_DAMAGE %+ - +", 8, false),
    ADDED_PHYSICAL("1 PREFIX PHYSICAL_DAMAGE ++ -/- +", 9, false),
    ADDED_FIRE("1 PREFIX FIRE_DAMAGE ++ -/- +", 10, false),
    ADDED_COLD("1 PREFIX COLD_DAMAGE ++ -/- +", 10, false),
    ADDED_LIGHTNING("1 PREFIX LIGHTNING_DAMAGE ++ -/- +", 10, false),
    ADDED_ABYSSAL("1 PREFIX ABYSSAL_DAMAGE ++ -/- +", 1, false),
    PHYSICAL_LIFESTEAL("1 PREFIX LIFESTEAL +% - +", 3, true),
    SHRED("1 PREFIX SHRED +% - +", 5, true),
    MAELSTROM("1 PREFIX MAELSTROM +% - +", 5, true),
    //Hybrid prefixes
    HYBRID_PHYS_ACC("2 PREFIX PHYSICAL_DAMAGE&ACCURACY ++&++ -/-&- +", 8, false),
    ARMOR_HEALTH("2 PREFIX ARMOR&HEALTH ++&++ -&- +", 4, false),
    WARD_HEALTH("2 PREFIX WARD&HEALTH ++&++ -&- +", 4, false),
    DODGE_HEALTH("2 PREFIX DODGE&HEALTH ++&++ -&- +", 4, false),
    ARMOR_WARD("2 PREFIX ARMOR&WARD ++&++ -&- +", 4, false),
    ARMOR_DODGE("2 PREFIX ARMOR&DODGE ++&++ -&- +", 4, false),
    DODGE_WARD("2 PREFIX DODGE&WARD ++&++ -&- +", 4, false),
    //Meta prefixes,
    @Meta(convertedStat = PlayerStats.STRENGTH, rate = 10)
    STRENGTH_TO_FIRE_DMG("1 PREFIX FIRE_DAMAGE ++ -/- +", 3, true),



    //=====SUFFIXES=====
    //Regular suffixes
    STRENGTH("1 SUFFIX STRENGTH ++ - +", 8,true),
    LESSER_STRENGTH("1 SUFFIX STRENGTH ++ - +", 8,true),
    DEXTERITY("1 SUFFIX DEXTERITY ++ - +", 8,true),
    LESSER_DEXTERITY("1 SUFFIX DEXTERITY ++ - +", 8,true),
    INTELLIGENCE("1 SUFFIX INTELLIGENCE ++ - +", 8,true),
    LESSER_INTELLIGENCE("1 SUFFIX INTELLIGENCE ++ - +", 8,true),
    HEALTH_REGEN("1 SUFFIX HEALTH_REGEN ++ - +", 8, true),
    FIRE_RESISTANCE("1 SUFFIX FIRE_RESISTANCE +% - +", 8, true),
    LIGHTNING_RESISTANCE("1 SUFFIX LIGHTNING_RESISTANCE +% - +", 8, true),
    COLD_RESISTANCE("1 SUFFIX COLD_RESISTANCE +% - +", 8, true),
    ABYSSAL_RESISTANCE("1 SUFFIX ABYSSAL_RESISTANCE +% - +", 8, true),
    LIFE_ON_HIT("1 SUFFIX LIFE_ON_HIT ++ - +", 5, true),
    ACCURACY("1 SUFFIX ACCURACY ++ - +", 5, true),
    STAMINA_REGEN("1 SUFFIX STAMINA %+ - +", 6, true),
    BLEEDING("1 SUFFIX BLEED +% - +", 4, true),
    CRITICAL_CHANCE("1 SUFFIX CRITICAL_CHANCE +% - +", 6, true),
    //Hybrid suffixes

    //=====IMPLICITS=====
    MARAUDER_AXE("1 IMPLICIT SHRED +% x +", 5, true),
    GLADIATOR_SWORD("1 IMPLICIT ACCURACY %+ x +", 5, true),
    MERCENARY_BOW("1 IMPLICIT DODGE ++ x +", 5, true),
    ROGUE_DAGGER("1 IMPLICIT CRITICAL_DAMAGE %+ x +", 5, true),
    SORCERER_WAND("1 IMPLICIT MAELSTROM +% x +", 5, true),
    TEMPLAR_MACE("1 IMPLICIT ELEMENTAL_DAMAGE %+ x +", 5, true),

    MARAUDER_HEAVY_PLATING("1 IMPLICIT STRENGTH ++ - +", 5, true),
    GLADIATOR_CARVED_PLATING("2 IMPLICIT STRENGTH&DEXTERITY ++&++ -&- +", 5, true),
    MERCENARY_LIGHT_CLOTH("1 IMPLICIT DEXTERITY ++ - +", 5, true),
    ROGUE_RUNIC_LEATHER("2 IMPLICIT DEXTERITY&INTELLIGENCE ++&++ -&- +", 5, true),
    SORCERER_ENCHANTED_SILK("1 IMPLICIT INTELLIGENCE ++ - +", 5, true),
    TEMPLAR_RUNIC_STEEL("2 IMPLICIT INTELLIGENCE&STRENGTH ++&++ -&- +", 5, true),

    //=====UNIQUE MODS=====
    BLEEDING_HEART_BLD_CHANCE("1 UNIQUE BLEED +% x +", 1, true),
    BLEEDING_HEART_BLD_DMG("1 UNIQUE BLEED_DAMAGE +% x +", 1, true),
    OMINOUS_TWIG_ABYSSAL("1 UNIQUE ABYSSAL_DAMAGE ++ -/- +", 1, false),
    CORRUPTORS_WRAPPINGS_WARD("1 UNIQUE WARD %+ - +", 1, false),
    CORRUPTORS_WRAPPINGS_STRENGTH("1 UNIQUE STRENGTH ++ - -", 1, true),
    CORRUPTORS_WRAPPINGS_MORE_WARD("1 UNIQUE WARD %* - +", 1, true),
    APPROACHING_WINTER_WALK_SPEED("1 UNIQUE WALK_SPEED ++ - -", 1, true),
    APPROACHING_WINTER_COLD_RES("1 UNIQUE COLD_RESISTANCE +% - +", 1, true),
    APPROACHING_WINTER_FIRE_RES("1 UNIQUE FIRE_RESISTANCE +% - -", 1, true),
    APPROACHING_WINTER_COLD_DMG("1 UNIQUE COLD_DAMAGE %+ - +", 1, true),
    BLIND_RAGE_ACCURACY("1 UNIQUE ACCURACY ++ - -", 1, true),
    BLIND_RAGE_LESS_HEALTH("1 UNIQUE HEALTH %* x -", 1, true),
    EYE_OF_THE_STORM_LIGHTNING_RES("1 UNIQUE LIGHTNING_RESISTANCE +% - -", 1, true),
    EYE_OF_THE_STORM_MAX_LIGHTNING_RES("1 UNIQUE MAX_LIGHTNING_RESISTANCE +% - +", 1, true),
    @Meta(convertedStat = PlayerStats.STRENGTH, rate = 10)
    HELLFORGE_STRENGTH_TO_FIRE_DMG("1 UNIQUE FIRE_DAMAGE ++ -/- +", 1, true),

    //=====KEYSTONES=====
    LETHAL_STRIKES("* UNIQUE KEYSTONE LETHAL_STRIKES", 0, true),
    FORBIDDEN_PACT("* UNIQUE KEYSTONE FORBIDDEN_PACT", 0, true),
    PERMAFROST("* UNIQUE KEYSTONE PERMAFROST", 0, true),
    BERSERK("* UNIQUE KEYSTONE BERSERK", 0, true),
    THUNDERSTRUCK("* UNIQUE KEYSTONE THUNDERSTRUCK", 0, true);

    private static final Map<Affix, Map<InscriptionID, Map<Integer, int[]>>> MODIFIER_TABLE = new HashMap<>();
    private static final Map<InscriptionID, Map<Integer, int[]>> IMPLICIT_TABLE = new HashMap<>();
    private static final Map<InscriptionID, int[]> UNIQUE_MOD_TABLE = new HashMap<>();

    private final String definitionString;
    private final String displayName;
    private final ModifierData data;
    private final int totalTiers;
    private final boolean global;
    private final boolean meta;
    private final boolean positive;


    InscriptionID(String definitionString, int tiers, boolean isGlobal){
        this.definitionString = definitionString;
        this.totalTiers = tiers;
        this.global = isGlobal;

        Meta metaData = getMetaAnnotationData();
        this.meta = (metaData!=null);

        String[] tokens = definitionString.split(" ");
        if (tokens[0].equals("*")){//Special Unique inscription handling (Keystones and Effects)
            this.positive = true;
            switch (tokens[2]){
                case "KEYSTONE":
                    this.displayName = Utils.convertToPrettyString(tokens[3].toLowerCase().replace("_"," ") + " " + Affix.UNIQUE.getRuneIcon());
                    this.data = parseKeystone(tokens[3]);
                    break;
                case "EFFECT":
                    this.displayName = "effect name :D";
                    this.data = parseUniqueEffect(tokens[3]);
                    break;
                default:
                    this.displayName = Utils.convertToPrettyString("sample unique mod");
                    this.data = null;
                    break;
            }
            return;
        }
        /*
        Can still be a Unique value inscription, which will be properly initialized
        but not instantiated in the MODIFIER_TABLE (Instead, it's data will be used
        in a Uniques Enum)
        */

        this.positive = tokens[5].equals("+");

        this.data = parseDefinitionString(tokens);
        this.displayName = parseDisplayName(data);
    }

    private ModifierData parseKeystone(String keystone) {
        try {
            Keystones mappedKeystone =  Keystones.valueOf(keystone);
            return new KeystoneData(Affix.UNIQUE, mappedKeystone);
        } catch (IllegalArgumentException exception){
            Utils.error("Unable to parse " + keystone + " keystone argument (InscriptionID)");
        }
        return null;
    }
    private ModifierData parseUniqueEffect(String uniqueEffect) {
        try {
            Effects mappedEffect =  Effects.valueOf(uniqueEffect);
            return new UniqueEffectData(Affix.UNIQUE, mappedEffect);
        } catch (IllegalArgumentException exception){
            Utils.error("Unable to parse " + uniqueEffect + " unique effect argument (InscriptionID)");
        }
        return null;
    }

    public int[] convert(int convertedValue, int[] metaStatValues){
        if(!isMeta()){return new int[]{69};}

        Meta metaData = getMetaAnnotationData();
        int conversionRate = metaData.rate();

        int stacks = convertedValue / conversionRate; //568 DEX / 50 -> 11 stacks of metaStatValues
        int[] convertedStatValues = metaStatValues.clone();

        return Arrays.stream(convertedStatValues).map(value ->{
            value = value * stacks;
            return value;
        }).toArray();
    }
    public Meta getMetaAnnotationData(){
        Meta annotation = null;
        try {
            Field enumConstant = InscriptionID.class.getField(this.name());
            if (enumConstant.isAnnotationPresent(Meta.class)){
                annotation = enumConstant.getAnnotation(Meta.class);
            }
        } catch (NoSuchFieldException e) {
            Utils.error("Unable to find enum constant field for: " + this.name() + " @"+this.getClass().getSimpleName());
        }
        return annotation;
        // https://stackoverflow.com/questions/40970830/get-annotation-value-from-enum-constant
    }

    private String parseDisplayName(ModifierData data){
        if (data instanceof InscriptionData inscriptionData){return getDisplayName(inscriptionData);}
        if (data instanceof HybridInscriptionData hybridInscriptionData){return getDisplayName(hybridInscriptionData);}

        return "INVALID STAT";
    }

    private ModifierData parseDefinitionString(String[] tokens){
        int stats = 0;
        try { stats = Integer.parseInt(tokens[0]); } catch (NumberFormatException exception){
            Utils.error("Invalid inscription definition string: " + this.getClass().getSimpleName());
        }
        return switch (stats) {
            case 0 -> null; //Invalid
            case 1 -> buildStandardInscriptionData(tokens);
            default -> buildSpecialInscriptionData(stats, tokens);
        };
    }
    private ModifierData buildStandardInscriptionData(String[] tokens){
        Affix affix = parseAffixToken(tokens[1]);
        PlayerStats stat = parseStat(tokens[2]);
        ValueTypes valueType = parseValueTypeToken(tokens[3]);
        RangeTypes rangeType = parseRangeTypeToken(tokens[4]);

        return new InscriptionData(affix, valueType, rangeType, stat);
    }
    private ModifierData buildSpecialInscriptionData(int subtokens, String[] tokens){

        Affix affix = parseAffixToken(tokens[1]);
        String[] statTokens = tokens[2].split("&");
        String[] valueTokens = tokens[3].split("&");
        String[] rangeTokens = tokens[4].split("&");

        PlayerStats[] stats = new PlayerStats[subtokens];
        ValueTypes[] values = new ValueTypes[subtokens];
        RangeTypes[] ranges = new RangeTypes[subtokens];

        for (int i = 0; i<subtokens; i++){
            stats[i] = parseStat(statTokens[i]);
            values[i] = parseValueTypeToken(valueTokens[i]);
            ranges[i] = parseRangeTypeToken(rangeTokens[i]);
        }

        return new HybridInscriptionData(affix, values, ranges, stats);
    }


    private Affix parseAffixToken(String affixToken){ //Defaults to PREFIX if invalid
        try {
            return Affix.valueOf(affixToken);
        } catch (IllegalArgumentException exception){
            Utils.error("Invalid token for AffixType on " + this.getClass().getSimpleName() + "'s definition string");
            return Affix.PREFIX;
        }
    }
    private PlayerStats parseStat(String statToken){ //Defaults to HEALTH if invalid
        try {
            return PlayerStats.valueOf(statToken);
        } catch (IllegalArgumentException exception){
            Utils.error("Invalid token for STAT on " + this.getClass().getSimpleName() + "'s definition string");
            return PlayerStats.HEALTH;
        }
    }
    private ValueTypes parseValueTypeToken(String valueTypeToken){ //Defaults to FLAT if invalid
        return ValueTypes.mapValueTypeToken(valueTypeToken);
    }
    private RangeTypes parseRangeTypeToken(String rangeTypeToken){ //Defaults to SINGLE_RANGE if invalid
        return RangeTypes.mapRangeTypeToken(rangeTypeToken);
    }


    private String getDisplayName(InscriptionData data){
        return data.getDefinitionData().getDisplayName(getMetaAnnotationData(), isPositive(), isGlobal());
    }
    private String getDisplayName(HybridInscriptionData data){
        StatDefinition[] statDefinitions = data.getStatDefinitions();
        String firstDisplayName = statDefinitions[0].getDisplayName(getMetaAnnotationData(), isPositive(), isGlobal());
        String secondDisplayName = statDefinitions[1].getDisplayName(getMetaAnnotationData(), isPositive(), isGlobal());

        return firstDisplayName + HybridInscriptionData.HYBRID_SEPARATOR + secondDisplayName;
    }




    /*
    Modifier table methods
    */
    public static Map<Affix, Map<InscriptionID, Map<Integer, int[]>>> getModifierTable(){
        return MODIFIER_TABLE;
    }

    public static Map<InscriptionID, Map<Integer, int[]>> getImplicitTable() {
        return IMPLICIT_TABLE;
    }

    public static Map<InscriptionID, int[]> getUniqueTable() {
        return UNIQUE_MOD_TABLE;
    }

    public static int[] fetchValuesFor(Inscription mod){
        InscriptionID modID = mod.getInscription();
        ModifierData data = modID.getData();
        if (data.isUnique()){
            return fetchUniqueValuesFor(modID);
        } //Change the data source when the mod is unique
        if (data.getAffixType().equals(Affix.IMPLICIT)){
            return fetchValuesForImplicit(modID, mod.getTier());
        }
        return fetchValuesFor(modID, mod.getTier());
    }
    public static int[] fetchValuesFor(InscriptionID mod, int tier){
        ModifierData modData = mod.getData();

        Affix affixType = modData.getAffixType();

        int[] values = getModifierTable().get(affixType).get(mod).get(tier);
        if (values == null){
            Utils.error("Invalid combination: " + mod + " & Tier " + tier);
            return new int[4];
        }
        return values;
    }
    public static int[] fetchValuesForImplicit(InscriptionID mod, int tier){
        int[] values = getImplicitTable().get(mod).get(tier);
        if (values == null){
            Utils.error("Invalid implicit combination: " + mod + " & Tier " + tier);
            return new int[4];
        }
        return values;
    }
    public static int[] fetchUniqueValuesFor(InscriptionID mod){
        int[] values = getUniqueTable().get(mod);
        if (values == null){
            Utils.error("Invalid modifier value for: " + mod);
            return new int[2];
        }
        return values;
    }

    public static void loadModifiers(){
        Map<InscriptionID, Map<Integer, int[]>> prefixModData = new HashMap<>();
        Map<InscriptionID, Map<Integer, int[]>> suffixModData = new HashMap<>();

        for (InscriptionID mod : InscriptionID.values()){
            ModifierData modData = mod.getData();
            Affix modAffixType = modData.getAffixType();

            switch (modAffixType){
                case PREFIX -> prefixModData.put(mod, mod.queryModifierValues());
                case SUFFIX -> suffixModData.put(mod, mod.queryModifierValues());
                case IMPLICIT -> IMPLICIT_TABLE.put(mod, mod.queryModifierValues());
                case UNIQUE -> UNIQUE_MOD_TABLE.put(mod, ModifierEditor.getTableValuesFor(mod,mod.getTotalTiers()));
            }
        }
        InscriptionID.MODIFIER_TABLE.put(Affix.PREFIX, prefixModData);
        InscriptionID.MODIFIER_TABLE.put(Affix.SUFFIX, suffixModData);
    }

    public Map<Integer, int[]> queryModifierValues(){
        ModifierData modData = getData();
        if (modData.getAffixType().equals(Affix.UNIQUE)){
            return null;
        }
        Map<Integer,int[]> tempTierValueMapping = new HashMap<>();
        for (int i = 0; i < getTotalTiers(); i++){
            int[] currValue = ModifierEditor.getTableValuesFor(this, i);
            tempTierValueMapping.put(i, currValue);
        }
        return tempTierValueMapping;
    }
}
