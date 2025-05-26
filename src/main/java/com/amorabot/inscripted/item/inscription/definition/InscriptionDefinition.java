package com.amorabot.inscripted.item.inscription.definition;

import com.amorabot.inscripted.item.inscription.language.AffixType;
import com.amorabot.inscripted.item.inscription.language.RollType;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import lombok.Getter;


import java.io.Serializable;

@Getter
public abstract class InscriptionDefinition implements Serializable {

    private String displayName;
    private final AffixType affix;

    public abstract <R> R accept(InscriptionDefinitionVisitor<R> visitor);
    public abstract boolean isGlobal();
    public abstract boolean isConstant();
    private void setDisplayName(String displayName){
        this.displayName = displayName;
    }

    public InscriptionDefinition(AffixType affixType){
        this.affix = affixType;
    }

    public interface InscriptionDefinitionVisitor<R>{
        R visitRegularInsc(InscriptionDefinition.Regular inscription);
        R visitHybridInsc(InscriptionDefinition.Hybrid inscription);
        R visitMetaInsc(InscriptionDefinition.Meta inscription);
        R visitEffect(InscriptionDefinition.Effect inscription);
        R visitKeystone(InscriptionDefinition.Keystone inscription);
    }

    public record BaseInscription(ValueType type, RollType roll, Stats stat) { }

    @Getter
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
            super.setDisplayName(accept(new InscriptionTemplateBuilder()));
        }

        @Override
        public <R> R accept(InscriptionDefinitionVisitor<R> visitor) {
            return visitor.visitRegularInsc(this);
        }
        @Override
        public boolean isGlobal() {
            return this.global;
        }
        @Override
        public boolean isConstant() {
            return this.baseData.roll.equals(RollType.CONSTANT);
        }
    }

    @Getter
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
            super.setDisplayName(accept(new InscriptionTemplateBuilder()));
        }


        @Override
        public <R> R accept(InscriptionDefinitionVisitor<R> visitor) {
            return visitor.visitHybridInsc(this);
        }
        @Override
        public boolean isGlobal() {
            return global;
        }

        @Override
        public boolean isConstant() {
            return (this.primaryData.roll.equals(RollType.CONSTANT) && this.secondaryData.roll.equals(RollType.CONSTANT));
        }
    }

    @Getter
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

            super.setDisplayName(accept(new InscriptionTemplateBuilder()));
        }

        @Override
        public <R> R accept(InscriptionDefinitionVisitor<R> visitor) {
            return visitor.visitMetaInsc(this);
        }
        @Override
        public boolean isGlobal() {return true;}

        @Override
        public boolean isConstant() {
            return this.baseData.roll.equals(RollType.CONSTANT);
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

    @Getter
    public static class Effect extends InscriptionDefinition{
        private final EffectIDs effectID;

        public Effect(EffectIDs effect) {
            super(AffixType.UNIQUE);
            this.effectID = effect;

            super.setDisplayName(accept(new InscriptionTemplateBuilder()));
        }

        @Override
        public <R> R accept(InscriptionDefinitionVisitor<R> visitor) {
            return visitor.visitEffect(this);
        }
        @Override
        public boolean isGlobal() {return true;}
        @Override
        public boolean isConstant() {
            return true;
        }
    }
    @Getter
    public static class Keystone extends InscriptionDefinition{
        private final KeystoneIDs keystoneID;

        public Keystone(KeystoneIDs keystone) {
            super(AffixType.UNIQUE);
            this.keystoneID = keystone;

            super.setDisplayName(accept(new InscriptionTemplateBuilder()));
        }

        @Override
        public <R> R accept(InscriptionDefinitionVisitor<R> visitor) {
            return visitor.visitKeystone(this);
        }
        @Override
        public boolean isGlobal() {return true;}
        @Override
        public boolean isConstant() {
            return true;
        }
    }
}
