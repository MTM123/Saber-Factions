package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.FactionWarpsFrame;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CmdFWarp extends FCommand {

    public CmdFWarp() {
        super();
        this.aliases.add("warp");
        this.aliases.add("warps");
        this.optionalArgs.put("warpname", "warpname");
        this.optionalArgs.put("password", "password");


        this.permission = Permission.WARP.node;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        //TODO: check if in combat.
        if (!fme.isAdminBypassing()) {
            Access access = myFaction.getAccess(fme, PermissableAction.WARP);
            if (access != Access.ALLOW && fme.getRole() != Role.LEADER) {
                fme.msg(TL.GENERIC_FPERM_NOPERMISSION, "use warps");
                return;
            }
        }


        if (args.size() == 0) {
            new FactionWarpsFrame(fme.getFaction()).buildGUI(fme);
        } else if (args.size() > 2) {
            fme.msg(TL.COMMAND_FWARP_COMMANDFORMAT);
        } else {
            final String warpName = argAsString(0);
            final String passwordAttempt = argAsString(1);

            if (myFaction.isWarp(argAsString(0))) {

                // Check if requires password and if so, check if valid. CASE SENSITIVE
                if (myFaction.hasWarpPassword(warpName) && !myFaction.isWarpPassword(warpName, passwordAttempt)) {
                    fme.msg(TL.COMMAND_FWARP_INVALID_PASSWORD);
                    return;
                }

                // Check transaction AFTER password check.
                if (!transact(fme)) return;
                final FPlayer fPlayer = fme;
                final UUID uuid = fme.getPlayer().getUniqueId();
                this.doWarmUp(WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warpName, () -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.teleport(fPlayer.getFaction().getWarp(warpName).getLocation());
                        fPlayer.msg(TL.COMMAND_FWARP_WARPED, warpName);
                    }
                }, this.p.getConfig().getLong("warmups.f-warp", 0));
            } else {
                fme.msg(TL.COMMAND_FWARP_INVALID_WARP, warpName);
            }
        }
    }

    private boolean transact(FPlayer player) {
        return !P.p.getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing() || payForCommand(P.p.getConfig().getDouble("warp-cost.warp", 5), TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FWARP_DESCRIPTION;
    }
}
