package com.massivecraft.factions.cmd.points;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdPointsAdd extends FCommand {

    public CmdPointsAdd() {
        super();
        this.aliases.add("add");

        this.requiredArgs.add("faction/player");
        this.requiredArgs.add("# of points");


        this.errorOnToManyArgs = false;
        //this.optionalArgs

        this.permission = Permission.ADDPOINTS.node;

        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;
    }


    @Override
    public void perform() {
        Faction faction = Factions.getInstance().getByTag(args.get(0));

        if (faction == null) {
            FPlayer fPlayer = this.argAsFPlayer(0);
            if (fPlayer != null) {
                faction = fPlayer.getFaction();
            }
        }

        if (faction == null || faction.isWilderness()) {
            msg(TL.COMMAND_POINTS_FAILURE.toString().replace("{faction}", args.get(0)));
            return;
        }
        if (argAsInt(1) <= 0) {
            msg(TL.COMMAND_POINTS_INSUFFICIENT);
            return;
        }

        faction.setPoints(faction.getPoints() + argAsInt(1));

        msg(TL.COMMAND_POINTS_SUCCESSFUL, argAsInt(1), faction.getTag(), faction.getPoints());
    }


    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ADDPOINTS_DESCRIPTION;
    }


}
