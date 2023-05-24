package pluginstudies.pluginstudies.handlers;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pluginstudies.pluginstudies.RPGElements;
import pluginstudies.pluginstudies.managers.ProfileManager;

import java.util.List;

public class SkillsUIHandler implements Listener {

    private ProfileManager profileManager;


    public SkillsUIHandler(RPGElements plugin){
        this.profileManager = plugin.getProfileManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onPlayerClick(InventoryClickEvent event){
/*
        Inventory inventory = event.getInventory();
        if (!(event.getView().getTitle().equalsIgnoreCase(color("&e&lBasic skills")))){
            //excluímos o caso onde o player não abriu a GUI desejada
//            event.getWhoClicked().sendMessage("Click funcionando");
            return;
        }
        //é necessário cancelar a tentativa de mover quaisquer itens do lugar, portanto, cancelamos com:
        event.setCancelled(true);
        //agora é necessário checar se o player clicou em seu inventário ou na GUI
        if (event.getClickedInventory() == null){
            log("invalid click.");
            return;
        }
        if (!event.getClickedInventory().equals(inventory)){
            //se o player clicou em um inventário que não seja o da GUI, retorne
            return;
        }

        Player player = (Player) event.getWhoClicked(); //getWhoClicked() pode retornar HumanEntity e outras
//        ItemStack clickedItem = event.getCurrentItem(); //retorna o item no slot clickado
        int slot = event.getSlot(); //retorna o slot do item clickado

        //aqui testaremos o tipo do click, uma vez que só queremos permitir clicks com o M1 e M2 (left e right)
        ClickType click = event.getClick();
        if (click != ClickType.LEFT && click != ClickType.RIGHT){
            // só serão aceitos LEFT e RIGHT clicks, qualquer outro click acionado resultará false e o código segue
            // tipos de click ->  dropping, triple clicking, shift clicking
            log("testando L/R");
            return;
        }

        Skills skills = profileManager.getPlayerProfile(player.getUniqueId()).getSkills();
        int points = skills.getPoints(); int intelligence = skills.getIntelligence();
        int agility = skills.getAgility(); int strength = skills.getStrength();

        switch (slot){ //só tem 3 slots desejados, vamos checar qual foi e então agir de acordo
            case(19):
                if ((click == ClickType.RIGHT && intelligence == 0) || (click == ClickType.LEFT && intelligence == 10)){
                    //se tentarmos passar do minimo ou do máximo de pontos, nada ocorre
                    return;
                }

                //agora podemos considerar apenas os clicks, sem se preocupar com os casos extremos
                // LEFT -> adicionar pontos, RIGHT -> retirar
                if (click == ClickType.LEFT){
                    //clicou com o left, ainda tem pontos?
                    if (points == 0){
                        // se não, retorne
                        return;
                    } else { // o player tem pontos, então vamos atualizá-los
                        skills.setPoints(points-1);
                        skills.setIntelligence(intelligence+1);
                    }
                }else{ //se não é left, é right
                    //se queremos desalocar pontos, basta alterar os stats, já que não podemos descer abaixo de 0
                    skills.setPoints(points+1);
                    skills.setIntelligence(intelligence-1);
                }
                break;
            case(20):
                if ((click == ClickType.RIGHT && agility == 0) || (click == ClickType.LEFT && agility == 10)){
                    return;
                }

                if(click == ClickType.LEFT){
                    if(points ==0){
                        return;
                    }else {
                        skills.setPoints(points-1);
                        skills.setAgility(agility+1);
                    }
                }else{
                    skills.setPoints(points+1);
                    skills.setAgility(agility-1);
                }
                break;
            case(21):
                if ((click == ClickType.RIGHT && strength == 0) || (click == ClickType.LEFT && strength == 10)){
                    return;
                }

                if(click == ClickType.LEFT){
                    if(points ==0){
                        return;
                    }else {
                        skills.setPoints(points-1);
                        skills.setStrength(strength+1);
                    }
                }else{
                    skills.setPoints(points+1);
                    skills.setStrength(strength-1);
                }
                break;
        }

        //vamos pegar os itens do inventário e atualizálos ao fim dos clicks, mesmo que não haja nada
        ItemStack pointsItem = inventory.getItem(4);
        ItemStack intItem = inventory.getItem(19);
        ItemStack agiItem = inventory.getItem(20);
        ItemStack strItem = inventory.getItem(21);

        inventory.setItem(4, editItem(pointsItem.clone(), skills.getPoints(), Arrays.asList(
                color("&fYou have " + skills.getPoints() + " points left"),
                color("&7"),
                color("Allocate points to enhance your abilities") )));
        inventory.setItem(19, editItem(intItem.clone(), skills.getIntelligence(), Arrays.asList(
                color("&7You have " + "&9" + skills.getIntelligence() + " &7points allocated"),
                color("&7"),
                color("&7Click here to allocate more"))));
        inventory.setItem(20, editItem(agiItem.clone(), skills.getAgility(), Arrays.asList(
                color("&7You have " + "&a" + skills.getAgility() + " &7points allocated"),
                color("&7"),
                color("&7Click here to allocate more"))));
        inventory.setItem(21, editItem(strItem.clone(), skills.getStrength(), Arrays.asList(
                color("&7You have " + "&c" + skills.getStrength() + " &7points allocated"),
                color("&7"),
                color("&7Click here to allocate more"))));

        //aplicar os efeitos das mudanças de status no player:

        //1- Aplicar mudança de vida com base na Strength (valor base é 20 (double) )
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20D + skills.getStrength());
        //2- Aplicar mudança de speed baseado na Agility  (valor base é 0.2 (float) )
//        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue((float) (0.2 + (skills.getAgility()) / 10)*0.2);
        player.setWalkSpeed((float) (0.2 + ((skills.getAgility()) / 10)*0.2));
        //3- TODO Aplicar mudanças da Intelligence
*/
    }

    public ItemStack editItem(ItemStack item, int amount, List<String> lore){
        if (amount == 0){
            //se tentarmos colocar 0 items, resetamos para 1, que é o mínimo
            amount = 1;
        }

        item.setAmount(amount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
