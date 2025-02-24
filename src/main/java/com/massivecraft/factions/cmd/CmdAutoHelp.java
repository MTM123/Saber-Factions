package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.CommandVisibility;
import com.massivecraft.factions.zcore.MCommand;
import com.massivecraft.factions.zcore.util.TL;

import java.util.ArrayList;

public class CmdAutoHelp extends MCommand<P> {

    public CmdAutoHelp() {
        super(P.p);
        this.aliases.add("?");
        this.aliases.add("h");
        this.aliases.add("help");

        this.setHelpShort("");

        this.optionalArgs.put("page", "1");
    }

    @Override
    public void perform() {

        if (this.commandChain.size() == 0) {
            return;
        }
        MCommand<?> pcmd = this.commandChain.get(this.commandChain.size() - 1);

        ArrayList<String> lines = new ArrayList<>(pcmd.helpLong);

        for (MCommand<?> scmd : pcmd.subCommands) {
            if (scmd.visibility == CommandVisibility.VISIBLE || (scmd.visibility == CommandVisibility.SECRET && scmd.validSenderPermissions(sender, false))) {
                lines.add(scmd.getUseageTemplate(this.commandChain, true));
            }
        }

        sendMessage(p.txt.getPage(lines, this.argAsInt(0, 1), TL.COMMAND_AUTOHELP_HELPFOR.toString() + pcmd.aliases.get(0) + "\""));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_HELP_DESCRIPTION;
    }
}
