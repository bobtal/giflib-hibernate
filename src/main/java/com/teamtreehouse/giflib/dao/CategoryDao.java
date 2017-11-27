package com.teamtreehouse.giflib.dao;

import com.teamtreehouse.giflib.model.Category;

import java.util.List;

public interface CategoryDao<T> {
    List<T> findAll();
    T findById(Long id);
    void save(T t);
    void delete(T t);
}
