package de.legoshi.lccore.listener;

import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.util.GUIUtil;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import org.bukkit.Bukkit;
import team.unnamed.inject.Inject;

public class DiscordSRVListener {

    @Inject private ChatManager chatManager;

    public void init() {
        DiscordSRV discordSRV = (DiscordSRV)Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if(discordSRV == null) {
            MessageUtil.log(Message.SERVICE_ERROR, true, "discord");
            return;
        }
        DiscordSRV.api.subscribe(this);
    }

    @Subscribe
    public void onDiscordMessagePostProcess(DiscordGuildMessagePostProcessEvent event) {
        event.setCancelled(true);

        String name = event.getAuthor().getName();
        Member member = event.getGuild().getMember(event.getAuthor());
        if(member != null) {
            name = member.getEffectiveName();
        }
        String message = GUIUtil.colorize("&f[&b&lLC-Discord&f] &f" + name + " » &7" + event.getMessage().getContentStripped());
        chatManager.receiveDiscordMessage(message);
    }
}
