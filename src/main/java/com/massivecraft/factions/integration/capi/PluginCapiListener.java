package com.massivecraft.factions.integration.capi;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
import java.util.Set;

@Deprecated
public class PluginCapiListener implements Listener {
    P p;

    Set<String> myChannelIds = new LinkedHashSet<String>();

    public PluginCapiListener(P p) {
        this.p = p;

        myChannelIds.add("faction");
        myChannelIds.add("allies");
    }

    private String replacePlayerTags(String format, FPlayer me, FPlayer you) {
        String meFactionTag = me.getChatTag(you);
        format = format.replace("{ME_FACTIONTAG}", meFactionTag.length() == 0 ? "" : meFactionTag);
        format = format.replace("{ME_FACTIONTAG_PADR}", meFactionTag.length() == 0 ? "" : meFactionTag + " ");
        format = format.replace("{ME_FACTIONTAG_PADL}", meFactionTag.length() == 0 ? "" : " " + meFactionTag);
        format = format.replace("{ME_FACTIONTAG_PADB}", meFactionTag.length() == 0 ? "" : " " + meFactionTag + " ");

        String youFactionTag = you.getChatTag(me);
        format = format.replace("{YOU_FACTIONTAG}", youFactionTag.length() == 0 ? "" : youFactionTag);
        format = format.replace("{YOU_FACTIONTAG_PADR}", youFactionTag.length() == 0 ? "" : youFactionTag + " ");
        format = format.replace("{YOU_FACTIONTAG_PADL}", youFactionTag.length() == 0 ? "" : " " + youFactionTag);
        format = format.replace("{YOU_FACTIONTAG_PADB}", youFactionTag.length() == 0 ? "" : " " + youFactionTag + " ");

        return format;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onListChannelsEvent(CAPIListChannelsEvent event)
    {
        for (Channel c : Channels.i.getAll())
        {
            if (myChannelIds.contains(c.getId()))
            {
                event.getChannels().add(c);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMessageToChannel(CAPIMessageToChannelEvent event)
    {
        if (event.isCancelled()) return;
        if ( ! myChannelIds.contains(event.getChannel().getId())) return;

        Player me = event.getMe();
        FPlayer fme = FPlayers.i.get(me);
        Faction myFaction = fme.getFaction();

        if (event.getChannel().getId().equals("faction") && myFaction.isNormal())
        {
            event.getThem().addAll(myFaction.getOnlinePlayers());
        }
        else if (event.getChannel().getId().equals("allies"))
        {
            for (Player somePlayer : Bukkit.getServer().getOnlinePlayers())
            {
                FPlayer someFPlayer = FPlayers.i.get(somePlayer);
                if (someFPlayer.getRelationTo(fme).isAtLeast(Rel.ALLY))
                    event.getThem().add(somePlayer);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMessageToPlayer(CAPIMessageToPlayerEvent event)
    {
        if (event.isCancelled()) return;
        event.setFormat(this.replacePlayerTags(event.getFormat(), FPlayers.i.get(event.getMe()), FPlayers.i.get(event.getYou())));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSelectChannel(CAPISelectChannelEvent event)
    {
        if (event.isCancelled()) return;
        String channelId = event.getChannel().getId();
        if ( ! myChannelIds.contains(channelId)) return;

        Player me = event.getMe();
        FPlayer fme = FPlayers.i.get(me);

        if ( ! fme.hasFaction())
        {
            event.setFailMessage(p.txt.parse("<b>You must be member in a faction to use this channel."));
            event.setCancelled(true);
        }
    }
}
