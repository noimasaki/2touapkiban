package com.example.demo.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import com.example.demo.domain.Todo;
import com.example.demo.domain.TodoService;

@RestController
@RequestMapping("/todos")
public class TodoController{
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Todo> list() {
        return service.findAll();
    }
    
    @PostMapping
    public Todo create(@RequestBody CreateTodoRequest req) {
        return service.create(req.title());
    }

    public record CreateTodoRequest(String title) {}
}