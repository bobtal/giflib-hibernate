package com.teamtreehouse.giflib.dao;

import com.teamtreehouse.giflib.model.Gif;

import java.util.List;

public interface GifDao<T> {
    List<T> findAll();
    T findById(Long id);
    void save(T t);
    void delete(T t);
}
