package com.massivecraft.factions.listeners;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class EssentialsHomeHandler implements Listener {

    private IEssentials ess;

    public EssentialsHomeHandler(IEssentials essentials) {
        this.ess = essentials;
    }

    @EventHandler
    public void onLeave(FPlayerLeaveEvent event) throws Exception {
        Faction faction = event.getFaction();
        User user = ess.getUser(UUID.fromString(event.getfPlayer().getId()));
        List<String> homes = user.getHomes();
        if (homes == null || homes.isEmpty()) {
            return;
        }
        for (String homeName : user.getHomes()) {
            Location loc = user.getHome(homeName);
            FLocation floc = new FLocation(loc);
            Faction factionAt = Board.getInstance().getFactionAt(floc);
            if (factionAt.equals(faction) && factionAt.isNormal()) {
                user.delHome(homeName);
                P.p.log(Level.INFO, "FactionLeaveEvent: Removing home %s, player %s, in territory of %s", homeName, event.getfPlayer().getName(), faction.getTag());
            }
        }
    }
}
