package com.amorabot.inscripted.components.Items.modifiers;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.inscripted.components.Player.stats.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Items.modifiers.data.*;
import com.amorabot.inscripted.components.Items.relic.enums.Effects;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Arrays;

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
    LOCAL_FIRE_PERCENT("1 PREFIX FIRE_DAMAGE %+ - +", 5, false),
    ADDED_COLD("1 PREFIX COLD_DAMAGE ++ -/- +", 10, false),
    LOCAL_COLD_PERCENT("1 PREFIX COLD_DAMAGE %+ - +", 5, false),
    ADDED_LIGHTNING("1 PREFIX LIGHTNING_DAMAGE ++ -/- +", 10, false),
    LOCAL_LIGHTNING_PERCENT("1 PREFIX LIGHTNING_DAMAGE %+ - +", 5, false),
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
    @Meta(convertedStat = PlayerStats.STRENGTH, convertedValueType = ValueTypes.FLAT, rate = 10)
    STRENGTH_TO_FIRE_DMG("1 PREFIX FIRE_DAMAGE ++ -/- +", 3, true),



    //=====SUFFIXES=====
    //Regular suffixes
    STRENGTH("1 SUFFIX STRENGTH ++ - +", 8,true),
    DEXTERITY("1 SUFFIX DEXTERITY ++ - +", 8,true),
    INTELLIGENCE("1 SUFFIX INTELLIGENCE ++ - +", 8,true),
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
    COOLDOWN_REDUCTION("1 SUFFIX COOLDOWN_REDUCTION +% - +", 4, true),
    //Hybrid suffixes

    //=====IMPLICITS=====
    MARAUDER_AXE("1 IMPLICIT SHRED +% x +", 5, true),
    GLADIATOR_SWORD("1 IMPLICIT ACCURACY %+ x +", 5, true),
    MERCENARY_BOW("1 IMPLICIT DODGE ++ x +", 5, true),
    ROGUE_DAGGER("1 IMPLICIT CRITICAL_DAMAGE +% x +", 5, true),
    SORCERER_WAND("1 IMPLICIT MAELSTROM +% x +", 5, true),
    TEMPLAR_MACE("1 IMPLICIT ELEMENTAL_DAMAGE %+ x +", 5, true),

    MARAUDER_HEAVY_PLATING("1 IMPLICIT STRENGTH ++ - +", 5, true),
    GLADIATOR_CARVED_PLATING("2 IMPLICIT STRENGTH&DEXTERITY ++&++ -&- +", 5, true),
    MERCENARY_LIGHT_CLOTH("1 IMPLICIT DEXTERITY ++ - +", 5, true),
    ROGUE_RUNIC_LEATHER("2 IMPLICIT DEXTERITY&INTELLIGENCE ++&++ -&- +", 5, true),
    SORCERER_ENCHANTED_SILK("1 IMPLICIT INTELLIGENCE ++ - +", 5, true),
    TEMPLAR_RUNIC_STEEL("2 IMPLICIT INTELLIGENCE&STRENGTH ++&++ -&- +", 5, true),

    //=====RELIC INSCRIPTIONS=====
    //BLEEDING HEART INSCRIPTIONS
    BLHA_BLEED_CHANCE("1 RELIC BLEED +% x +", 1, true),
    BLHA_BLD_DMG("1 RELIC BLEED_DAMAGE +% - +", 1, true),

    //OMINOUS TWIG INSCRIPTIONS
    OMTW_ABYSSAL("1 RELIC ABYSSAL_DAMAGE ++ -/- +", 1, false),

    //CORRUPTORS WRAPPINGS INSCRIPTIONS
    COWR_WARD("1 RELIC WARD %+ - +", 1, false),
    COWW_STRENGTH("1 RELIC STRENGTH ++ - -", 1, true),
    COWR_MORE_WARD("1 RELIC WARD %* - +", 1, true),

    //APPROACHING WINTER INSCRIPTIONS
    APWI_WALK_SPEED("1 RELIC WALK_SPEED ++ - -", 1, true),
    APWI_COLD_RES("1 RELIC COLD_RESISTANCE +% - +", 1, true),
    APWI_FIRE_RES("1 RELIC FIRE_RESISTANCE +% - -", 1, true),
    APWI_COLD_DMG("1 RELIC COLD_DAMAGE %+ - +", 1, true),
    APWI_FLAT_COLD("1 RELIC COLD_DAMAGE ++ -/- +", 1, true),

    //BLIND RAGE INSCRIPTIONS
    BLRA_ACCURACY("1 RELIC ACCURACY ++ - -", 1, true),
    BLRA_LESS_HEALTH("1 RELIC HEALTH %* x -", 1, true),

    //EYE OF THE STORM INSCRIPTIONS
    EOTS_LIGHTNING_RES("1 RELIC LIGHTNING_RESISTANCE +% - -", 1, true),
    EOTS_MAX_LIGHTNING_RES("1 RELIC MAX_LIGHTNING_RESISTANCE +% - +", 1, true),

    //TIN FOIL HELMET INSCRIPTIONS
    TIFO_HELMET_INTELLIGENCE("1 RELIC INTELLIGENCE ++ - -", 1, true),

    //QUEEN OF THE FOREST INSCRIPTIONS
    QOTF_REDU_WALK_SPEED("1 RELIC WALK_SPEED %+ - -", 1, true),

    //SCARLET DANCER INSCRIPTIONS
    SCDA_PHYS("1 RELIC PHYSICAL_DAMAGE %+ - +", 1, false),
    SCDA_LESS_ARMOR("1 RELIC ARMOR %* x -", 1, true),
    SCDA_LIFE_ON_HIT("1 RELIC LIFE_ON_HIT ++ - +", 1, true),

    //IMMORTAL FLESH INSCRIPTIONS
    IMFL_INC_REGEN("1 RELIC HEALTH_REGEN %+ - +", 1, true),
    IMFL_FLAT_REGEN("1 RELIC HEALTH_REGEN ++ - +", 1, true),

    //GODHEAD SET INSCRIPTIONS
    GODHEAD_LESS_FIRE_RES("1 RELIC FIRE_RESISTANCE %* x -", 1, true),
    GODHEAD_LESS_LIGHTNING_RES("1 RELIC LIGHTNING_RESISTANCE %* x -", 1, true),
    GODHEAD_LESS_COLD_RES("1 RELIC COLD_RESISTANCE %* x -", 1, true),

    //TRINITY INSCRIPTIONS
    TRINITY_LESS_HEALTH("1 RELIC HEALTH %* x -", 1, true),
    TRINITY_LESS_WARD("1 RELIC WARD %* x -", 1, true),

    //BODY INSCRIPTIONS
    THE_BODY_INC_ARMOR("1 RELIC ARMOR %+ - +", 1, false),

    //MIND INSCRIPTIONS
    THE_MIND_INC_WARD("1 RELIC WARD %+ - +", 1, false),

    //SOUL INSCRIPTIONS
    THE_SOUL_INC_DODGE("1 RELIC DODGE ++ - +", 1, false),

    //MAD BUTCHER INSCRIPTIONS
    MABU_BLEED_CHANCE("1 RELIC BLEED +% - +", 1, true),

   //HEADSMAN BLADE INSCRIPTIONS
    HEBL_BLEED_DMG("1 RELIC BLEED_DAMAGE +% - +", 1, true),
    HEBL_SHRED("1 RELIC SHRED +% x +", 1, true),

    //EXECUTIONERS MASK INSCRIPTIONS
    EXMA_FLAT_PHYS("1 RELIC PHYSICAL_DAMAGE ++ -/- +", 1, true),
    EXMA_SHRED("1 RELIC SHRED +% - +", 1, true),
    EXMA_CRIT_DMG("1 RELIC CRITICAL_DAMAGE +% - +", 1, true),

    //ELUSIVE SHADOW INSCRIPTIONS
    ELSH_CRIT_CHANCE("1 RELIC CRITICAL_CHANCE +% - +", 1, true),

    //BROKEN FAITH INSCRIPTIONS
    BRFA_ABYSS_FLAT("1 RELIC ABYSSAL_DAMAGE ++ -/- +", 1, false),
    BRFA_ABYSS_PERCENT("1 RELIC ABYSSAL_DAMAGE %+ - +", 1, true),
    BRFA_LESS_HPS("1 RELIC HEALTH_REGEN %* x -", 1, true),

    //DRUIDIC PELTS INSCRIPTIONS
    DRPE_HEALTH_REGEN("1 RELIC HEALTH_REGEN ++ - +", 1, true),

    //META RELIC MODS
    @Meta(convertedStat = PlayerStats.STRENGTH, convertedValueType = ValueTypes.FLAT, rate = 10)
    HELLFORGE_STRENGTH_TO_FIRE_DMG("1 RELIC FIRE_DAMAGE ++ -/- +", 1, true),
    @Meta(convertedStat = PlayerStats.DEXTERITY, convertedValueType = ValueTypes.FLAT, rate = 5)
    QOTF_DEX_TO_WS("1 RELIC WALK_SPEED %+ x +", 1, true),
    @Meta(convertedStat = PlayerStats.WARD, convertedValueType = ValueTypes.FLAT, rate = 600)
    BROKEN_FAITH_WARD_TO_ABYSS("1 RELIC ABYSSAL_DAMAGE ++ -/- +", 1, true),

    //=====KEYSTONES=====
    LETHAL_STRIKES("* RELIC KEYSTONE LETHAL_STRIKES", 0, true),
    FORBIDDEN_PACT("* RELIC KEYSTONE FORBIDDEN_PACT", 0, true),
    PERMAFROST("* RELIC KEYSTONE PERMAFROST", 0, true),
    BERSERK("* RELIC KEYSTONE BERSERK", 0, true),
    THUNDERSTRUCK("* RELIC KEYSTONE THUNDERSTRUCK", 0, true),
    BLOOD_PACT("* RELIC KEYSTONE BLOOD_PACT", 0, true),
    ORGAN_FAILURE("* RELIC KEYSTONE ORGAN_FAILURE", 0, true),
    FIRE_ATTUNEMENT("* RELIC KEYSTONE FIRE_ATTUNEMENT", 0, true),
    LIGHTNING_ATTUNEMENT("* RELIC KEYSTONE LIGHTNING_ATTUNEMENT", 0, true),
    COLD_ATTUNEMENT("* RELIC KEYSTONE COLD_ATTUNEMENT", 0, true),
    ELEMENTAL_BLESSING("* RELIC KEYSTONE ELEMENTAL_BLESSING", 0, true),
    AGNOSTIC("* RELIC KEYSTONE AGNOSTIC", 0, true),
    WINDS_OF_CHANGE("* RELIC KEYSTONE WINDS_OF_CHANGE", 0, true),

    //=====EFFECTS=====
    THRILL_OF_THE_HUNT("* RELIC EFFECT THRILL_OF_THE_HUNT", 0, true),
    ADRENALINE_RUSH("* RELIC EFFECT ADRENALINE_RUSH", 0, true),
    GRACEFUL_LANDING("* RELIC EFFECT GRACEFUL_LANDING", 0, true),
    SADISM("* RELIC EFFECT SADISM", 0, true),
    COUP_DE_GRACE("* RELIC EFFECT COUP_DE_GRACE", 0, true),
    OPPORTUNIST("* RELIC EFFECT OPPORTUNIST", 0, true),
    OVERDRIVE("* RELIC EFFECT OVERDRIVE", 0, true);


    private final String definitionString;
    private final String displayName;
    private final ModifierData data;
    private final int totalTiers;
    private final boolean global;
    private final boolean meta;
    private final boolean positive;


//    private final GenericInscriptionDAO internalData;


    InscriptionID(String definitionString, int tiers, boolean isGlobal){
        //TODO: encapsulate definition string parsing
        this.definitionString = definitionString;
        this.totalTiers = tiers;
        this.global = isGlobal;

//        String displayName;
//        ModifierData data;

        Meta metaData = getMetaAnnotationData();
        this.meta = (metaData!=null);
//        boolean isMeta = (metaData!=null);

//        boolean isPositive;

        String[] tokens = definitionString.split(" ");
        if (tokens[0].equals("*")){//Special Unique inscription handling (Keystones and Effects)
            String uniqueRuneSuffix = " " + Affix.RELIC.getRuneIcon();
            this.positive = true;
//            isPositive = true;
            switch (tokens[2]){
                case "KEYSTONE":
                    this.displayName = Utils.convertToPrettyString(tokens[3].toLowerCase().replace("_"," "))
                            + uniqueRuneSuffix + "  ";
//                    displayName = Utils.convertToPrettyString("Passive - " + tokens[3].toLowerCase().replace("_"," "))
//                            + " " + uniqueRuneSuffix + "  ";
                    this.data = parseKeystone(tokens[3]);
//                    data = parseKeystone(tokens[3]);
                    break;
                case "EFFECT":
                    Effects mappedEffect;
                    try{
                        mappedEffect = Effects.valueOf(tokens[3]);
                    } catch (IllegalArgumentException exception){
                        Utils.error("Unable to parse " + tokens[3] + " unique effect argument (InscriptionID)");
                        this.displayName = "null effect momento";
                        this.data = null;
//                        this.internalData = new GenericInscriptionDAO(displayName,null,tiers,isGlobal,isMeta,false);
                        return;
                    }
                    this.displayName = mappedEffect.getDisplayName() + uniqueRuneSuffix;
//                    displayName = mappedEffect.getDisplayName() + uniqueRuneSuffix;
                    this.data = new UniqueEffectData(Affix.RELIC, mappedEffect);
//                    data = new UniqueEffectData(Affix.RELIC, mappedEffect);
                    break;
                default:
                    this.displayName = Utils.convertToPrettyString("sample unique mod");
                    this.data = null;
                    break;
            }
//            this.internalData = new GenericInscriptionDAO(displayName,data,tiers,isGlobal,isMeta,true);
            return;
        }
        /*
        Can still be a Unique value inscription, which will be properly initialized
        but not instantiated in the MODIFIER_TABLE (Instead, it's data will be used
        in a Uniques Enum)
        */

        this.positive = tokens[5].equals("+");
//        isPositive = tokens[5].equals("+");

        this.data = parseDefinitionString(tokens);
//        data = parseDefinitionString(tokens);
        this.displayName = parseDisplayName(data);
//        displayName = parseDisplayName(data);

//        this.internalData = new GenericInscriptionDAO(displayName,data,tiers,isGlobal,isMeta,isPositive);
    }

//    public static GenericInscriptionDAO parseDefinitionString(String defString,int tiers, boolean isGlobal){
//
//    }












    private ModifierData parseKeystone(String keystone) {
        try {
            Keystones mappedKeystone =  Keystones.valueOf(keystone);
            return new KeystoneData(Affix.RELIC, mappedKeystone);
        } catch (IllegalArgumentException exception){
            Utils.error("Unable to parse " + keystone + " keystone argument (InscriptionID)");
        }
        return null;
    }

    public int[] convert(int convertedValue, int[] metaStatValues){
        if(!isMeta()){return new int[]{69};}

        Meta metaData = getMetaAnnotationData();
        int conversionRate = metaData.rate();

        int stacks = Math.floorDiv(convertedValue,conversionRate); //568 DEX / 50 -> 11 stacks of metaStatValues
        if (stacks == 0){return new int[metaStatValues.length];}
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
            Utils.error("Invalid token for STAT on " + this + "'s definition string");
            return PlayerStats.HEALTH;
        }
    }
    private ValueTypes parseValueTypeToken(String valueTypeToken){ //Defaults to FLAT if invalid
        return ValueTypes.mapValueTypeToken(valueTypeToken);
    }
    private RangeTypes parseRangeTypeToken(String rangeTypeToken){ //Defaults to SINGLE_RANGE if invalid
        return RangeTypes.mapRangeTypeToken(rangeTypeToken);
    }


    //TODO: Make polymorphic call
    private String getDisplayName(InscriptionData data){
        return data.getDefinitionData().getDisplayName(getMetaAnnotationData(), isPositive(), isGlobal());
    }
    private String getDisplayName(HybridInscriptionData data){
        StatDefinition[] statDefinitions = data.getStatDefinitions();
        String firstDisplayName = statDefinitions[0].getDisplayName(getMetaAnnotationData(), isPositive(), isGlobal());
        String secondDisplayName = statDefinitions[1].getDisplayName(getMetaAnnotationData(), isPositive(), isGlobal());

        return firstDisplayName + HybridInscriptionData.HYBRID_SEPARATOR + secondDisplayName;
    }

//    public String getDisplayName(){
//        return internalData.displayName();
//    }
//    public ModifierData getData(){
//        return internalData.data();
//    }
//    public int getTotalTiers(){
//        return internalData.totalTiers();
//    }
//    public boolean isGlobal(){
//        return internalData.global();
//    }
//    public boolean isMeta(){
//        return internalData.meta();
//    }
//    public boolean isPositive(){
//        return internalData.positive();
//    }
}
