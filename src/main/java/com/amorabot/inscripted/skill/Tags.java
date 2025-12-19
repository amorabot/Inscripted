package com.amorabot.inscripted.skill;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.Profile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum Tags {
    NONE {
        @Override
        public Map<Stats, Integer> getTagBonuses(UUID playerID) {
            return new HashMap<>();
        }

        @Override
        public int getDamageBonus(UUID playerID) {
            return 0;
        }
    },
    MELEE {
        @Override
        public Map<Stats, Integer> getTagBonuses(UUID playerID) {
            Map<Stats, Integer> statMap = new HashMap<>();
            Profile playerProfile = PlayerDataContainer.getProfile(playerID);

            statMap.put(Stats.MELEE_DAMAGE,playerProfile.getDamageComponent().getMeleeDamage());
            return statMap;
        }

        @Override
        public int getDamageBonus(UUID playerID) {
            Profile playerProfile = PlayerDataContainer.getProfile(playerID);
            return playerProfile.getDamageComponent().getMeleeDamage();
        }
    },
    SPELL {
        @Override
        public Map<Stats, Integer> getTagBonuses(UUID playerID) {
            return new HashMap<>();
        }

        @Override
        public int getDamageBonus(UUID playerID) {
            return 0;
        }
    },
    PROJECTILE {
        @Override
        public Map<Stats, Integer> getTagBonuses(UUID playerID) {
            Map<Stats, Integer> statMap = new HashMap<>();
            Profile playerProfile = PlayerDataContainer.getProfile(playerID);

            statMap.put(Stats.PROJECTILE_DAMAGE,playerProfile.getDamageComponent().getProjectileDamage());
            statMap.put(Stats.EXTRA_PROJECTILES,playerProfile.getDamageComponent().getExtraProjectiles());
            return statMap;
        }

        @Override
        public int getDamageBonus(UUID playerID) {
            Profile playerProfile = PlayerDataContainer.getProfile(playerID);
            return playerProfile.getDamageComponent().getProjectileDamage();
        }
    },
    AOE {
        @Override
        public Map<Stats, Integer> getTagBonuses(UUID playerID) {
            Map<Stats, Integer> statMap = new HashMap<>();
            Profile playerProfile = PlayerDataContainer.getProfile(playerID);

            statMap.put(Stats.AREA_DAMAGE,playerProfile.getDamageComponent().getAreaDamage());
            return statMap;
        }

        @Override
        public int getDamageBonus(UUID playerID) {
            Profile playerProfile = PlayerDataContainer.getProfile(playerID);
            return playerProfile.getDamageComponent().getAreaDamage();
        }
    },
    AURA {
        @Override
        public Map<Stats, Integer> getTagBonuses(UUID playerID) {
            return new HashMap<>();
        }

        @Override
        public int getDamageBonus(UUID playerID) {
            return 0;
        }
    };

    public abstract Map<Stats, Integer> getTagBonuses(UUID playerID);
    public abstract int getDamageBonus(UUID playerID);
}
