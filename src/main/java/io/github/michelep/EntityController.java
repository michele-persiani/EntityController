package io.github.michelep;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntityController<T extends EliteEntity>
{
    private static String persistenceUnit = "defaultPersistenceUnit";
    private static EntityManager staticEntityManager;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final EntityManager entityManager;
    private final Class<T> entityClass;

    protected EntityController(Class<T> entityClass, EntityManager entityManager)
    {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    public void put(T entity)
    {
        if(!entity.isValid())
            throw new IllegalArgumentException("Entity is not valid");
        entityManager.getTransaction().begin();
        entity.put(entityManager);
        entityManager.getTransaction().commit();
    }

    public void putAll(Collection<T> entities)
    {
        if(!entities.stream().allMatch(EliteEntity::isValid))
            throw new IllegalArgumentException("One or more entities are not valid");
        entityManager.getTransaction().begin();
        entities.forEach(e -> e.put(entityManager));
        entityManager.getTransaction().commit();
    }

    public void remove(T entity)
    {
        entityManager.getTransaction().begin();
        entity.remove(entityManager);
        entityManager.getTransaction().commit();
    }

    public void removeAll(Collection<T> entities)
    {
        entityManager.getTransaction().begin();
        entities.forEach(e -> e.remove(entityManager));
        entityManager.getTransaction().commit();
    }

    private String tableName()
    {
        if(entityClass.isAnnotationPresent(Table.class))
        {
            Table table = entityClass.getAnnotation(Table.class);
            if(!table.name().isEmpty())
                return table.name();
        }
        return entityClass.getSimpleName();
    }

    public List<T> getAll()
    {
        String tableName = tableName();

        try
        {
            List<T> entities = new ArrayList<>(
                    entityManager.createQuery("SELECT e FROM " + tableName + " e", entityClass).getResultList()
            );
            logger.info(String.format("num entities %s", entities.size()));
            return entities;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public Optional<T> find(Object id)
    {
        logger.info(String.format("id %s", id));
        if (id == null)
            return Optional.empty();

        T entity = null;
        try
        {
            entity = entityManager.find(entityClass, id);
            logger.info("entity fetched");
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "exception %s", e.getMessage());
        }

        return Optional.ofNullable(entity);
    }

    public T get(Object id)
    {
        return find(id).orElseThrow(() -> new IllegalArgumentException("Entity with id " + id + " not found"));
    }

    public int count()
    {
        String tableName = tableName();
        Query q = entityManager.createQuery("SELECT COUNT(e) FROM " + tableName + " e");
        return ((Number)q.getSingleResult()).intValue();
    }

    public boolean contains(T entity)
    {
        return entityManager.contains(entity);
    }

    public static void setPersistenceUnit(String persistenceUnit)
    {
        EntityController.persistenceUnit = persistenceUnit;
        staticEntityManager = null;
    }

    public static String getPersistenceUnit()
    {
        return EntityController.persistenceUnit;
    }

    public static <T extends EliteEntity>  EntityController<T> create(Class<T> entityClass)
    {
        if(staticEntityManager == null)
        {
            EntityManagerFactory entitymanagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
            staticEntityManager = entitymanagerFactory.createEntityManager();
        }
        return new EntityController<>(entityClass, staticEntityManager);
    }
}
