package pluginstudies.pluginstudies.components;

public class Profile {

    private Skills skills;
    public Profile(Skills skills){
        this.skills = skills;
    }
    public Skills getSkills(){
        return this.skills;
    }
    public void setSkills(Skills skills){
        this.skills = skills;
    }
}
