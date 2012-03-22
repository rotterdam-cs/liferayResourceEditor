package com.rcs.i18n.common.persistence;

import com.rcs.i18n.common.model.HibernateModel;
import org.springframework.dao.DataAccessException;

import java.io.Serializable;
import java.util.List;

public abstract interface Persistence<E> {

    <E extends HibernateModel> E get(Class<E> clazz, Serializable id) throws DataAccessException;

    <E extends HibernateModel> List getAll(Class<E> clazz) ;

    void insert(HibernateModel object) throws DataAccessException;

    void update(HibernateModel object) throws DataAccessException;

    void merge(HibernateModel object) throws DataAccessException;

    void delete(HibernateModel object) throws DataAccessException;

    public <E extends HibernateModel> void deleteById(Class<E> clazz, Serializable id) throws DataAccessException;

    <E extends HibernateModel> void deleteAll(List<E> objects) throws DataAccessException;

}