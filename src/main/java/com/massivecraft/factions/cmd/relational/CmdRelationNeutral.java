package com.massivecraft.factions.cmd.relational;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationNeutral extends FRelationCommand {

    public CmdRelationNeutral() {
        aliases.add("neutral");
        targetRelation = Relation.NEUTRAL;
    }
}
