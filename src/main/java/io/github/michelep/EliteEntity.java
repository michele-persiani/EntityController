package io.github.michelep;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
public abstract class EliteEntity implements Serializable, Comparable<EliteEntity>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "created_on", updatable = false)
    private String createdOn;

    @Column(name = "updated_on")
    private String lastUpdatedOn;


    @PrePersist
    public void prePersist()
    {
        createdOn = LocalDateTime.now().toString();
        lastUpdatedOn = createdOn;
    }

    @PreUpdate
    public void preUpdate()
    {
        lastUpdatedOn = LocalDateTime.now().toString();
    }


    public Long getId()
    {
        return id;
    }


    public void setId(Long id)
    {
        this.id = id;
    }


    public LocalDateTime getLastUpdatedOn()
    {
        if (Objects.isNull(lastUpdatedOn))
            return null;
        return LocalDateTime.parse(lastUpdatedOn);
    }


    public LocalDateTime getCreatedOn()
    {
        if (Objects.isNull(createdOn))
            return null;
        return LocalDateTime.parse(createdOn);
    }


    public boolean isNew()
    {
        return this.id == null || this.id < 0;
    }


    public boolean isValid()
    {
        return true;
    }


    public void put(EntityManager entityManager)
    {
        if(isNew())
            entityManager.persist(this);
        else
            entityManager.merge(this);
    }

    public void remove(EntityManager entityManager)
    {
        entityManager.remove(this);
    }


    @Override
    public boolean equals(Object obj)
    {
        if(!getClass().equals(obj.getClass()))
            return false;
        EliteEntity other = (EliteEntity) obj;
        if(id == null || other.id == null)
            return false;
        return Objects.equals(id, other.id);
    }


    public int hashCode()
    {
        return Objects.hash(getClass(), isNew()? -1 : id);
    }


    @Override
    public int compareTo(EliteEntity o)
    {
        if (createdOn == null && o.createdOn == null)
            return 0;
        else if (createdOn == null)
            return -1;
        else if (o.createdOn == null)
            return 1;
        else
            return getCreatedOn().compareTo(o.getCreatedOn());
    }

    @Override
    public String toString()
    {
        return String.format("%s [id = %s]", getClass().getSimpleName(), id);
    }
}
