package com.example.demo.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.domain.Todo;
import com.example.demo.domain.TodoService;

@RestController
@RequestMapping("/todos")
public class HelloController{
    private final TodoService service;

    @GetMapping
    public List<Todo> list() {
        return service.findAll();
    }
    
}