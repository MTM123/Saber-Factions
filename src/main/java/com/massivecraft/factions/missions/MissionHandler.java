package com.massivecraft.factions.missions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.List;
import java.util.stream.Collectors;

public class MissionHandler implements Listener {

    private P plugin;

    public MissionHandler(P plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() == null || event.getEntity().getKiller() == null) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getEntity().getKiller());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType().equalsIgnoreCase("kill")).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            if (!event.getEntityType().toString().equals(section.getConfigurationSection("Mission").getString("EntityType"))) {
                continue;
            }
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType().equalsIgnoreCase("mine")).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            if (!event.getBlock().getType().toString().equals(section.getConfigurationSection("Mission").getString("Material"))) {
                continue;
            }
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType().equalsIgnoreCase("place")).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            if (!event.getBlock().getType().toString().equals(section.getConfigurationSection("Mission").getString("Material"))) {
                continue;
            }
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer == null) {
            return;
        }
        List<Mission> missions = fPlayer.getFaction().getMissions().values().stream().filter(mission -> mission.getType().equalsIgnoreCase("fish")).collect(Collectors.toList());
        for (Mission mission2 : missions) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("Missions").getConfigurationSection(mission2.getName());
            mission2.incrementProgress();
            checkIfDone(fPlayer, mission2, section);
        }
    }

    private void checkIfDone(FPlayer fPlayer, Mission mission, ConfigurationSection section) {
        if (mission.getProgress() < section.getConfigurationSection("Mission").getLong("Amount")) {
            return;
        }
        for (String command : section.getConfigurationSection("Reward").getStringList("Commands")) {
            P.p.getServer().dispatchCommand(P.p.getServer().getConsoleSender(), command.replace("%faction%", fPlayer.getFaction().getTag()));
        }
        fPlayer.getFaction().getMissions().remove(mission.getName());
        fPlayer.getFaction().msg(TL.MISSION_MISSION_FINISHED, plugin.color(section.getString("Name")));
    }
}
