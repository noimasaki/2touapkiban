package com.example.demo.domain;

import java.util.List;
import java.util.Optional;

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

    public Optional<Todo> findById(Long id) {
        return mapper.findById(id);
    }

    @Transactional
    public Todo create(String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        mapper.insert(todo);
        return todo;
    }

    @Transactional
    public int updateDone(Long id, boolean done) {
        int updateCount = mapper.update(id, done);
        return updateCount;
    }
}
