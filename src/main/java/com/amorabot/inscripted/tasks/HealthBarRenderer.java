package com.amorabot.inscripted.tasks;

import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.renderer.HealthBarGenerator;
import com.amorabot.inscripted.tasks.base.PlayerboundTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.UUID;

public class HealthBarRenderer extends PlayerboundTask {
    private final TextDisplay playerHealthBar;

    public HealthBarRenderer(UUID playerID) {
        super(playerID);
        this.playerHealthBar = createHPDisplayFor(getPlayer());
    }

    @Override
    protected void taskRoutine(Player player) {
        playerHealthBar.text(HealthBarGenerator.getHealthBarSegmentsFor(player));
    }

    @Override
    public void unregister() {
        if (PlayerDataContainer.hasPlayerData(getPlayerID())){
            PlayerDataContainer.getDataContainerFor(getPlayerID()).removeTask(getTaskId());
        }
        playerHealthBar.remove();
    }

    private TextDisplay createHPDisplayFor(Player player){
        TextDisplay display = player.getWorld().spawn(player.getLocation().clone().add(0,2.5,0), TextDisplay.class, textDisplay -> {
            textDisplay.setBillboard(Display.Billboard.CENTER);
            textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
            textDisplay.setTextOpacity((byte) (255*0.70));
            textDisplay.setLineWidth(1000);
            textDisplay.setBackgroundColor(Color.fromARGB(10,30,10,10));
            textDisplay.setPersistent(false);
            textDisplay.text(Component.text("Not initialized!"));
        });
        player.addPassenger(display);
//        player.hideEntity(Inscripted.getPlugin(),display);
        return display;
    }
}
