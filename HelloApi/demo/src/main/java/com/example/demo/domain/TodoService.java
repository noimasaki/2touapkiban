package com.example.demo.domain;

import org.springframework.stereotype.Service;

import com.example.demo.infra.TodoMapper;

@Service
public class TodoService {
    private final TodoMapper mapper;

    public List<Todo> findAll() {
        return mapper.findAll();
    }
}
