package com.massivecraft.factions.cmd.relational;

import com.massivecraft.factions.struct.Relation;

public class CmdRelationEnemy extends FRelationCommand {

    public CmdRelationEnemy() {
        aliases.add("enemy");
        targetRelation = Relation.ENEMY;
    }
}
