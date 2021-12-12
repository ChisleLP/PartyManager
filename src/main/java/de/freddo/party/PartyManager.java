package de.freddo.party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PartyManager {

    private HashMap<UUID, Enums.Roles> memberList;
    private ArrayList<UUID> inviteList;

     public PartyManager () {
         memberList = new HashMap<>();
         inviteList = new ArrayList<>();
     }

    public HashMap<UUID, Enums.Roles> getMemberList() {
        return memberList;
    }

    public void setMemberList(HashMap<UUID, Enums.Roles> memberList) {
        this.memberList = memberList;
    }

    public ArrayList<UUID> getInviteList() {
        return inviteList;
    }

    public void setInviteList(ArrayList<UUID> inviteList) {
        this.inviteList = inviteList;
    }

}
