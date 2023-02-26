package pluginstudies.pluginstudies.components;

public class Skills {
    private int points;
    private int health;
    private int intelligence;
    private int agility;
    private int strength;

    public Skills(int points, int health, int intelligence, int agility, int strength) {
        this.points = points;
        this.health = health;
        this.intelligence = intelligence;
        this.agility = agility;
        this.strength = strength;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getHealth() {
        return health;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getAgility() {
        return agility;
    }

    public int getStrength() {
        return strength;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
