package com.example.demo.domain;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.infra.TodoMapper;

@Service
public class TodoService {

    private final TodoMapper mapper;

    public TodoService(TodoMapper mapper) {
        this.mapper = mapper;
    }

    public List<Todo> findAll() {
        return mapper.findAll();
    }

    @Transactional
    public Todo create(String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        mapper.insert(todo);
        return todo;
    }
}
