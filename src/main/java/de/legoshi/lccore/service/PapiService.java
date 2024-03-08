package de.legoshi.lccore.service;


import de.legoshi.lccore.manager.ChatManager;
import de.legoshi.lccore.util.LCExpansion;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.Bukkit;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class PapiService implements Service {

    @Inject private Injector injector;
    @Inject private ChatManager chatManager;

    @Override
    public void start() {
        chatManager.initPapiMap();
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            injector.getInstance(LCExpansion.class).register();
            MessageUtil.log(Message.SERVICE_STARTED, true, "papi");
        } else {
            MessageUtil.log(Message.SERVICE_ERROR, true, "papi");
        }
    }}
