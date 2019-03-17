package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdAdmin extends FCommand {
    public CmdAdmin() {
        super();
        this.aliases.add("admin");

        this.requiredArgs.add("");
        this.optionalArgs.put("on/off", "flip");

        this.permission = Permission.ADMIN.node;
        this.disableOnLock = false;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeOfficer = false;
        senderMustBeLeader = false;
    }

    @Override
    public void perform() {
        fme.setHasAdminMode(this.argAsBool(0, !fme.hasAdminMode()));

        if (fme.hasAdminMode()) {
            fme.msg("<i>You have enabled administrator bypass mode.");
            P.p.log(fme.getName() + " has ENABLED administrator bypass mode.");
        } else {
            fme.msg("<i>You have disabled administrator bypass mode.");
            P.p.log(fme.getName() + " DISABLED administrator bypass mode.");
        }
    }
}
