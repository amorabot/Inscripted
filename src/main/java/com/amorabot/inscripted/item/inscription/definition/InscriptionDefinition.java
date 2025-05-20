package com.amorabot.inscripted.item.inscription.definition;

import com.amorabot.inscripted.item.inscription.language.AffixType;
import com.amorabot.inscripted.item.inscription.language.RollType;
import com.amorabot.inscripted.item.inscription.language.ValueType;


import java.io.Serializable;

public abstract class InscriptionDefinition implements Serializable {

    private String displayName;
    private final AffixType affix;

    public abstract <R> R accept(InscriptionVisitor<R> visitor);
    public abstract boolean isGlobal();
    private void setDisplayName(String displayName){
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }

    public AffixType getAffix() {
        return affix;
    }

    public InscriptionDefinition(AffixType affixType){
        this.affix = affixType;
    }

    public interface InscriptionVisitor<R>{
        R visitRegularInsc(InscriptionDefinition.Regular inscription);
        R visitHybridInsc(InscriptionDefinition.Hybrid inscription);
        R visitMetaInsc(InscriptionDefinition.Meta inscription);
        R visitEffect(InscriptionDefinition.Effect inscription);
        R visitKeystone(InscriptionDefinition.Keystone inscription);
    }

    public record BaseInscription(ValueType type, RollType roll, Stats stat) { }

    public static class Regular extends InscriptionDefinition{
        private final boolean positive;
        private final boolean global;
        private final BaseInscription baseData;

        public Regular(AffixType affix, BaseInscription data, boolean isPositive, boolean isGlobal){
            super(affix);
            this.positive = isPositive;
            this.global = isGlobal;
            this.baseData = data;
            //After data initialization, set the inscriptions display name based on that
            super.setDisplayName(accept(new InscriptionNameBuilder()));
        }

        @Override
        public <R> R accept(InscriptionVisitor<R> visitor) {
            return visitor.visitRegularInsc(this);
        }
        @Override
        public boolean isGlobal() {
            return this.global;
        }
        public boolean isPositive(){
            return positive;
        }
        public BaseInscription getBaseData() {
            return baseData;
        }
    }

    public static class Hybrid extends InscriptionDefinition{
        private final boolean positive;
        private final boolean global;
        private final BaseInscription primaryData;
        private final BaseInscription secondaryData;

        public Hybrid(AffixType affix, BaseInscription primaryData, boolean isPositive, boolean isGlobal, BaseInscription secondaryData) {
            super(affix);
            this.primaryData = primaryData;
            this.secondaryData = secondaryData;
            this.positive = isPositive;
            this.global = isGlobal;
            super.setDisplayName(accept(new InscriptionNameBuilder()));
        }


        @Override
        public <R> R accept(InscriptionVisitor<R> visitor) {
            return visitor.visitHybridInsc(this);
        }
        @Override
        public boolean isGlobal() {
            return false;
        }
        public BaseInscription getPrimaryData() {
            return this.primaryData;
        }
        public BaseInscription getSecondaryData(){
            return this.secondaryData;
        }
    }

    public static class Meta extends InscriptionDefinition{
        private final boolean positive;
        private final Stats convertedStat;
        private final ValueType valueType;
        private final int conversionRate;
        private final BaseInscription baseData;

        public Meta(AffixType affix, BaseInscription data, boolean isPositive, Stats convertedStat, ValueType valueType, int conversionRate){
            super(affix);
            this.baseData = data;
            this.positive = isPositive;

            this.convertedStat = convertedStat;
            this.valueType = valueType;
            this.conversionRate = conversionRate;

            super.setDisplayName(accept(new InscriptionNameBuilder()));
        }

        @Override
        public <R> R accept(InscriptionVisitor<R> visitor) {
            return visitor.visitMetaInsc(this);
        }
        @Override
        public boolean isGlobal() {return true;}
        public BaseInscription getBaseData(){
            return this.baseData;
        }
        public Stats getMetaStat(){
            return this.convertedStat;
        }
        public ValueType getMetaValueType(){
            return this.valueType;
        }
        public int getConversionRate(){
            return this.conversionRate;
        }
    }

    public static class Effect extends InscriptionDefinition{
        private final EffectIDs effectID;

        public Effect(EffectIDs effect) {
            super(AffixType.UNIQUE);
            this.effectID = effect;

            super.setDisplayName(accept(new InscriptionNameBuilder()));
        }

        @Override
        public <R> R accept(InscriptionVisitor<R> visitor) {
            return visitor.visitEffect(this);
        }
        @Override
        public boolean isGlobal() {return true;}
    }
    public static class Keystone extends InscriptionDefinition{
        private final KeystoneIDs keystoneID;

        public Keystone(KeystoneIDs keystone) {
            super(AffixType.UNIQUE);
            this.keystoneID = keystone;

            super.setDisplayName(accept(new InscriptionNameBuilder()));
        }

        @Override
        public <R> R accept(InscriptionVisitor<R> visitor) {
            return visitor.visitKeystone(this);
        }
        @Override
        public boolean isGlobal() {return true;}
    }
}
