package com.example.demo.infra;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;

import com.example.demo.domain.Todo;


@Mapper
public interface TodoMapper {
    // @Select("SELECT id, title, done FROM todo ORDER BY id")
    // List<Todo> findAll();
    List<Todo> findAll();

    // @Select("SELECT id, title, done FROM todo WHERE id = #{id}")
    // Optional<Todo> findById(Long id);
    Optional<Todo> findById(Long id);

    // @Insert("INSERT INTO todo(title, done) VALUES(#{title}, false)")
    // @Options(useGeneratedKeys = true, keyProperty = "id")
    // void insert(Todo todo);
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Todo todo);


    // @Update("UPDATE todo SET done = #{done} WHERE id = #{id}")
    // int update(Long id, Boolean done);
    int update(@Param("id") Long id, @Param("done") boolean done);
}