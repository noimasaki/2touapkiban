package com.example.demo.infra;

import com.example.demo.domain.Todo;

@Mapper
public interface TodoMapper {
    @Select("SELECT id, title, done FROM todo ORDER BY id")
    List<Todo> findAll();

    @Select("SELECT id, title, done FROM todo WHERE id = #{id}")
    Optional<Todo> findById(Long id);
}