package com.teamtreehouse.giflib.dao;

import com.teamtreehouse.giflib.model.Category;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.HibernateIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
public class CategoryDaoImpl extends HibernateDao implements CategoryDao {

    public CategoryDaoImpl() {
        typeParameterClass = Category.class;
    }

    @Override
    public Category findById(Long id) {
        Session session = sessionFactory.openSession();
        Category category = (Category) session.get("com.teamtreehouse.giflib.model.Category", id);
        Hibernate.initialize(category.getGifs());
        session.close();
        return category;
    }

    @Override
    public void save(Category category) {
        super.save(category);
    }

    @Override
    public void delete(Category category) {
        super.delete(category);
    }

}
