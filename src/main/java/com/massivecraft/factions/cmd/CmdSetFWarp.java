package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

public class CmdSetFWarp extends FCommand {

    public CmdSetFWarp() {
        super();

        this.aliases.add("setwarp");
        this.aliases.add("sw");

        this.requiredArgs.add("warp name");
        this.optionalArgs.put("password", "password");

        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;

        this.senderMustBePlayer = true;

        this.permission = Permission.SETWARP.node;
    }

    @Override
    public void perform() {
        if (!(fme.getRelationToLocation() == Relation.MEMBER)) {
            fme.msg(TL.COMMAND_SETFWARP_NOTCLAIMED);
            return;
        }

        // This statement allows us to check if they've specifically denied it, or default to
        // the old setting of allowing moderators to set warps.
        if (!fme.isAdminBypassing()) {
            Access access = myFaction.getAccess(fme, PermissableAction.SETWARP);
            if (access != Access.ALLOW && fme.getRole() != Role.LEADER) {
                fme.msg(TL.GENERIC_FPERM_NOPERMISSION, "set warps");
                return;
            }
        }

        String warp = argAsString(0);

        // Checks if warp with same name already exists and ignores maxWarp check if it does.
        boolean warpExists = myFaction.isWarp(warp);

        int maxWarps = P.p.getConfig().getInt("max-warps", 5);
        boolean tooManyWarps = maxWarps <= myFaction.getWarps().size();
        if (tooManyWarps && !warpExists) {
            fme.msg(TL.COMMAND_SETFWARP_LIMIT, maxWarps);
            return;
        }

        if (!transact(fme)) {
            return;
        }

        String password = argAsString(1);

        LazyLocation loc = new LazyLocation(fme.getPlayer().getLocation());
        myFaction.setWarp(warp, loc);
        if (password != null) {
            myFaction.setWarpPassword(warp, password);
        }
        fme.msg(TL.COMMAND_SETFWARP_SET, warp, password != null ? password : "");
    }

    private boolean transact(FPlayer player) {
        return !P.p.getConfig().getBoolean("warp-cost.enabled", false) || player.isAdminBypassing() || payForCommand(P.p.getConfig().getDouble("warp-cost.setwarp", 5), TL.COMMAND_SETFWARP_TOSET.toString(), TL.COMMAND_SETFWARP_FORSET.toString());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETFWARP_DESCRIPTION;
    }
}
