package de.legoshi.lccore.command;

import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.database.DBManager;
import de.legoshi.lccore.database.models.LCPlayerDB;
import de.legoshi.lccore.util.Register;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Register
@Command(names = {"test3"}, permission = "test3", desc = "")
public class Test3Command implements CommandClass {

    @Inject private DBManager db;

    @Command(names = "")
    public void test3(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(Linkcraft.getPlugin(), () -> {
            if (!(sender instanceof Player)) {
                MessageUtil.send(Message.NOT_A_PLAYER, sender);
                return;
            }
            Player player = (Player)sender;
            if(player.getUniqueId().toString().equals("57344e2d-488a-428c-8537-0f22dfca2ec7")) {
                String sql = "SELECT DISTINCT player_id FROM lc_player_tags pt";
                EntityManager em = db.getEntityManager();
                Query query = em.createNativeQuery(sql);
                List<String> data = (List<String>)query.getResultList();

                for(String pId : data) {
                    String hql2 = "SELECT p FROM LCPlayerDB p WHERE p.id=:player";
                    TypedQuery<LCPlayerDB> query2 = em.createQuery(hql2, LCPlayerDB.class);
                    query2.setParameter("player", pId);
                    try {
                        query2.getSingleResult();
                    } catch (NoResultException ignored) {
                        OfflinePlayer oPlayer = Bukkit.getOfflinePlayer(UUID.fromString(pId));
                        LCPlayerDB dbPlayer = new LCPlayerDB(pId, oPlayer.getName());
                        db.persist(dbPlayer);
                    }
                }
                em.close();
            }
            player.sendMessage("complete");
        });
    }
}
