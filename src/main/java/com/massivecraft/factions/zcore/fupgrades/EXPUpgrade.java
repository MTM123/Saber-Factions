package com.massivecraft.factions.zcore.fupgrades;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EXPUpgrade implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Entity killer = e.getEntity().getKiller();

        if (killer == null)
            return;

        FLocation floc = new FLocation(e.getEntity().getLocation());
        Faction faction = Board.getInstance().getFactionAt(floc);

        if (!faction.isWilderness()) {
            int level = faction.getUpgrade(UpgradeType.EXP);
            if (level != 0) {

                double multiplier = -1;

                switch (level) {
                    case 1:
                        multiplier = P.p.getConfig().getDouble("fupgrades.MainMenu.EXP.EXP-Boost.level-1");
                        break;
                    case 2:
                        multiplier = P.p.getConfig().getDouble("fupgrades.MainMenu.EXP.EXP-Boost.level-2");
                        break;
                    case 3:
                        multiplier = P.p.getConfig().getDouble("fupgrades.MainMenu.EXP.EXP-Boost.level-3");
                        break;
                }

                if (multiplier >= 0)
                    spawnMoreExp(e, multiplier);
            }
        }
    }

    private void spawnMoreExp(EntityDeathEvent e, double multiplier) {
        double newExp = e.getDroppedExp() * multiplier;
        e.setDroppedExp((int) newExp);
    }
}
