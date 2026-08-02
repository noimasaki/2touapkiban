package com.example.demo.infra;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.demo.domain.Todo;


@Mapper
public interface TodoMapper {
    @Select("SELECT id, title, done FROM todo ORDER BY id")
    List<Todo> findAll();

    @Select("SELECT id, title, done FROM todo WHERE id = #{id}")
    Optional<Todo> findById(Long id);
}