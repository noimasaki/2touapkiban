package com.example.demo.domain;

import java.util.List;

import org.springframework.stereotype.Service;

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
}
