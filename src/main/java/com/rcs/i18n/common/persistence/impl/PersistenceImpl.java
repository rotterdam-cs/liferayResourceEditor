package com.rcs.i18n.common.persistence.impl;

import com.rcs.i18n.common.model.HibernateModel;
import com.rcs.i18n.common.persistence.Persistence;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class PersistenceImpl<E> extends HibernateDaoSupport implements Persistence<E>{
    @Override
    public <E extends HibernateModel> E get(Class<E> clazz, Serializable id) throws DataAccessException {
        return getHibernateTemplate().get(clazz, id);
    }

    @Override
    public <E extends HibernateModel> List getAll(Class<E> clazz) {
        return getHibernateTemplate().loadAll(clazz);
    }

    @Override
    public void insert(HibernateModel object) throws DataAccessException {
        getHibernateTemplate().save(object);
    }

    @Override
    public void update(HibernateModel object) throws DataAccessException {
        getHibernateTemplate().update(object);
    }

    @Override
    public void merge(HibernateModel object) throws DataAccessException {
        getHibernateTemplate().merge(object);
    }

    @Override
    public void delete(HibernateModel object) throws DataAccessException {
        getHibernateTemplate().delete(object);
    }

    @Override
    public <E extends HibernateModel> void deleteById(Class<E> clazz, Serializable id) throws DataAccessException {
        E obj = get(clazz, id);
        getHibernateTemplate().delete(obj);
    }

    @Override
    public <E extends HibernateModel> void deleteAll(List<E> objects) throws DataAccessException {
        getHibernateTemplate().deleteAll(objects);
    }
}
