package pluginstudies.pluginstudies.components.PlayerComponents;

public class Profile {

    private int health;
    private int ward;
    private int armor;
    private int evasion;
    private Attributes attributes;
    private Stats stats;
    public Profile(Attributes attributes, Stats stats){
        this.attributes = attributes;
        this.stats = stats;
    }
    public Attributes getAttributes(){
        return this.attributes;
    }
    public void setAttributes(Attributes attributes){
        this.attributes = attributes;
    }

    public Stats getStats() {
        return this.stats;
    }
    public void setStats(Stats stats) {
        this.stats = stats;
    }
}
