package io.github.michelep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionAdapter<T extends EliteEntity> implements Collection<T>
{
    private final EntityController<T> controller;
    private final Class<T> entityClass;

    public CollectionAdapter(EntityController<T> controller, Class<T> entityClass)
    {
        this.controller = controller;
        this.entityClass = entityClass;
    }

    @Override
    public int size()
    {
        return controller.count();
    }

    @Override
    public boolean isEmpty()
    {
        return controller.count() == 0;
    }

    @Override
    public boolean contains(Object o)
    {
        if(!entityClass.isAssignableFrom(o.getClass()))
            return false;
        return controller.contains(entityClass.cast(o));
    }

    @Override
    public Iterator<T> iterator()
    {
        return controller.getAll().iterator();
    }

    @Override
    public Object[] toArray()
    {
        return controller.getAll().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a)
    {
        return controller.getAll().toArray(a);
    }

    @Override
    public boolean add(T t)
    {
        controller.put(t);
        return true;
    }

    @Override
    public boolean remove(Object o)
    {
        if(!entityClass.isAssignableFrom(o.getClass()))
            return false;
        int count = controller.count();
        controller.remove(entityClass.cast(o));
        return controller.count() != count;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        for(Object o : c)
            if(!entityClass.isAssignableFrom(o.getClass()) || !controller.contains(entityClass.cast(o)))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        Collection<T> toAdd = new ArrayList<>(c);
        controller.putAll(toAdd);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        Collection<T> toRemove = new ArrayList<>();
        for(Object o : c)
            if(entityClass.isAssignableFrom(o.getClass()))
                toRemove.add(entityClass.cast(o));
        int count = controller.count();
        controller.removeAll(toRemove);
        return controller.count() != count;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        Collection<T> toRetain = new ArrayList<>();
        for(Object o : c)
            if(entityClass.isAssignableFrom(o.getClass()))
                toRetain.add(entityClass.cast(o));

        Collection<T> toRemove = controller.getAll();
        for(T e : toRetain)
            toRemove.remove(e);

        int count = controller.count();
        controller.removeAll(toRemove);
        return controller.count() != count;
    }

    @Override
    public void clear()
    {
        Collection<T> toRemove = controller.getAll();
        controller.removeAll(toRemove);
    }
}
