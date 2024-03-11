package de.legoshi.lccore.database;

import com.avaje.ebean.annotation.Transactional;
import de.legoshi.lccore.Linkcraft;
import de.legoshi.lccore.manager.ConfigManager;
import de.legoshi.lccore.util.message.Message;
import de.legoshi.lccore.util.message.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.hibernate.Hibernate;
import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class DBManager {

    private final Plugin plugin;
    @Getter private EntityManagerFactory entityManagerFactory;

    public DBManager(Plugin plugin) {
        this.plugin = plugin;
        if(connectToDB()) {
            MessageUtil.log(Message.DATABASE_CONNECTED, true);
        } else {
            // Perhaps this is heavy-handed, but we ABSOLUTELY do not want to server to start if we cant connect to db
            MessageUtil.log(Message.DATABASE_FAILED, true);
            Bukkit.shutdown();
        }
    }

    public boolean connectToDB() {
        Properties props = getDbProperties();

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        HibernatePersistenceProvider provider = new HibernatePersistenceProvider();

        try {
            this.entityManagerFactory = provider.createEntityManagerFactory("serverpro_db", props);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return entityManagerFactory.isOpen();
    }

    @NotNull
    private static Properties getDbProperties() {
        Properties props = new Properties();
        String base = "jdbc:mysql://" + ConfigManager.host + ":"
                + ConfigManager.port + "/" + ConfigManager.database;
        String options = "?useSSL=false&autoReconnect=true&connectTimeout=0&socketTimeout=0&characterEncoding=UTF-8&useUnicode=yes&useConfigs=maxPerformance";
        String url = base + options;

        props.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        props.put(Environment.URL, url);
        props.put(Environment.USER, ConfigManager.username);
        props.put(Environment.PASS, ConfigManager.password);
        return props;
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    @Transactional
    public <T> T persist(Identifiable<T> entity) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            em.persist(entity);
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return entity.getId();
    }

    // Must be a DataModel annotated with '@Entity' or an exception will be thrown
    // Perhaps em.flush if necessary
    @Transactional
    public <T> void persist(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            em.persist(entity);
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /* Use only when all of these apply: - Batch amount of persists are required
    //                                   - Return id value is not needed
    //                                   - Order of execution is irrelevant */
    public <T> void persistAsync(T entity) {
        Linkcraft.async(() -> persist(entity));
    }

    @Transactional
    public <T> void update(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            em.merge(entity);
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @Transactional
    public <T> boolean delete(Identifiable<T> entity) {

        Object result = find(entity.getId(), entity.getClass());

        if(result == null) {
            return false;
        }

        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();
            em.remove(em.merge(entity));
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }

        return true;
    }

    public <T> T find(Object id, Class<T> entityClass) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        T result = null;

        try {
            et.begin();
            result = em.find(entityClass, id);
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return result;
    }

    public <T> T find(Object id, Class<T> entityClass, Consumer<T> initializeProxy) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        T result = null;

        try {
            et.begin();
            result = em.find(entityClass, id);
            initializeProxy.accept(result);
            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return result;
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        EntityManager em = getEntityManager();
        EntityTransaction et = em.getTransaction();
        List<T> result = null;
        try {
            et.begin();

            CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(entityClass);
            query.select(query.from(entityClass));
            result = em.createQuery(query).getResultList();

            et.commit();
        } catch (Exception e) {
            et.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        return result;
    }

    public boolean hasResult(Query query) {
        boolean result = true;
        try {
            query.getSingleResult();
        } catch (NoResultException e) {
            result = false;
        }
        return result;
    }

    public <T> T getSingleResult(TypedQuery<T> query) {
        T result = null;

        try {
            result = query.getSingleResult();
        } catch (NoResultException ignored) {}

        return result;
    }

    public <T> void uncache(Object id, Class<T> entityClass) {
        entityManagerFactory.getCache().evict(entityClass, id);
    }

    public <T> void uncacheAll(Class<T> entityClass) {
        entityManagerFactory.getCache().evict(entityClass);
    }

    public void uncacheAll() {
        entityManagerFactory.getCache().evictAll();
    }

    public <T> void initialize(T obj) {
        Hibernate.initialize(obj);
    }
}
