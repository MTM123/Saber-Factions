package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.fperms.gui.PermissableActionFrame;
import com.massivecraft.factions.zcore.fperms.gui.PermissableRelationFrame;
import com.massivecraft.factions.zcore.util.TL;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CmdPerm extends FCommand {

    public CmdPerm() {
        super();
        this.aliases.add("perm");
        this.aliases.add("perms");
        this.aliases.add("permission");
        this.aliases.add("permissions");

        this.optionalArgs.put("relation", "relation");
        this.optionalArgs.put("action", "action");
        this.optionalArgs.put("access", "access");


        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = true;

    }

    @Override
    public void perform() {
        if (args.size() == 0) {
            new PermissableRelationFrame(fme.getFaction()).buildGUI(fme);
            return;
        } else if (args.size() == 1 && getPermissable(argAsString(0)) != null) {
            new PermissableActionFrame(fme.getFaction()).buildGUI(fme, getPermissable(argAsString(0)));
            return;
        }

        // If not opening GUI, then setting the permission manually.
        if (args.size() != 3) {
            fme.msg(TL.COMMAND_PERM_DESCRIPTION);
            return;
        }

        Set<Permissable> permissables = new HashSet<>();
        Set<PermissableAction> permissableActions = new HashSet<>();

        boolean allRelations = argAsString(0).equalsIgnoreCase("all");
        boolean allActions = argAsString(1).equalsIgnoreCase("all");

        if (allRelations) {
            permissables.addAll(myFaction.getPermissions().keySet());
        } else {
            Permissable permissable = getPermissable(argAsString(0));

            if (permissable == null) {
                fme.msg(TL.COMMAND_PERM_INVALID_RELATION);
                return;
            }

            permissables.add(permissable);
        }

        if (allActions) {
            permissableActions.addAll(Arrays.asList(PermissableAction.values()));
        } else {
            PermissableAction permissableAction = PermissableAction.fromString(argAsString(1));
            if (permissableAction == null) {
                fme.msg(TL.COMMAND_PERM_INVALID_ACTION);
                return;
            }

            permissableActions.add(permissableAction);
        }

        Access access = Access.fromString(argAsString(2));

        if (access == null) {
            fme.msg(TL.COMMAND_PERM_INVALID_ACCESS);
            return;
        }

        for (Permissable permissable : permissables) {
            for (PermissableAction permissableAction : permissableActions) {
                fme.getFaction().setPermission(permissable, permissableAction, access);
            }
        }
        fme.msg(TL.COMMAND_PERM_SET, argAsString(1), access.name(), argAsString(0));
        P.p.log(String.format(TL.COMMAND_PERM_SET.toString(), argAsString(1), access.name(), argAsString(0)) + " for faction " + fme.getTag());
    }

    private Permissable getPermissable(String name) {
        if (Role.fromString(name.toUpperCase()) != null) {
            return Role.fromString(name.toUpperCase());
        } else if (Relation.fromString(name.toUpperCase()) != null) {
            return Relation.fromString(name.toUpperCase());
        } else {
            return null;
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PERM_DESCRIPTION;
    }

}