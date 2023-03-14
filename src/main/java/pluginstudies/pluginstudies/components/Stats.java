package pluginstudies.pluginstudies.components;

public class Stats {

    private int health;
    private int ward;
    private int armor;
    private int evasion;

    private int DPS;
    private int[] physicalDmg = new int[2];
    private int[] fireDmg = new int[2];
    private int[] coldDmg = new int[2];
    private int[] lightningDmg = new int[2];

    public Stats(int health, int ward, int dps){
        this.health = health;
        this.ward = ward;
        this.DPS = dps;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getWard() {
        return ward;
    }

    public void setWard(int ward) {
        this.ward = ward;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getEvasion() {
        return evasion;
    }

    public void setEvasion(int evasion) {
        this.evasion = evasion;
    }

    public int getDPS() {
        return DPS;
    }

    public void setDPS(int DPS) {
        this.DPS = DPS;
    }
}
