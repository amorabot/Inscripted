package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

import com.amorabot.rpgelements.components.Items.Weapon.WeaponTypes;

import java.util.Arrays;
import java.util.List;

public enum ItemTypes {
    ARMOR {
        @Override
        public List<Enum<?>> getSubtypes() {
            return null;
        }
    },
    WEAPON {
        @Override
        public List<Enum<?>> getSubtypes() {
            return Arrays.asList(WeaponTypes.values());
        }
    };

    public abstract List<Enum<?>> getSubtypes();
}
