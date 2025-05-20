package com.amorabot.inscripted.item.inscription.language;

public enum ValueType {
    FLAT {
        @Override
        public String getKeyword(boolean isPositive) {
            if (isPositive){return "+";}
            return "-";
        }
    },
    INCREASED {
        @Override
        public String getKeyword(boolean isPositive) {
            if (isPositive){return "Increased";}
            return "Reduced";
        }
    },
    PERCENTAGE{
        @Override
        public String getKeyword(boolean isPositive) {
            if (isPositive){return "+";}
            return "-";
        }
    },
    MULTIPLIER {
        @Override
        public String getKeyword(boolean isPositive) {
            if (isPositive){return "More";}
            return "Less";
        }
    };

    public abstract String getKeyword(boolean isPositive);
}
