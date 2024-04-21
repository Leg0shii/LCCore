package de.legoshi.lccore.service;

import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import org.bukkit.Bukkit;
import team.unnamed.inject.Inject;

public class DatabaseService implements Service {
    @Inject private DBManager db;

    @Override
    public void start() {
        if(!db.init()) {
            MessageUtil.log(Message.DATABASE_FAILED, true);
            // Perhaps this is heavy-handed, but we ABSOLUTELY do not want to server to start if we cant connect to db
            Bukkit.shutdown();
        }
    }
}
