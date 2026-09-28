package com.example.demo.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import com.example.demo.domain.Todo;
import com.example.demo.domain.TodoService;
import com.example.demo.app.dto.TodoCreateRequest;
import com.example.demo.app.dto.TodoResponse;

@RestController
@RequestMapping("/todos")
public class TodoController{
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    // @GetMapping
    // public List<Todo> list() {
    //     return service.findAll();
    // }
    @GetMapping
    public List<TodoResponse> list() {
        return service.findAll()
                .stream()
                .map(TodoResponse::from)
                .toList();
    }

    // 指定IDのtodoを取得
    // @GetMapping("/{id}")
    // public Optional<Todo> findById(@PathVariable("id") Long id) {
    //     return service.findById(id);
    // }
    @GetMapping("/{id}")
    public Optional<TodoResponse> findById(@PathVariable("id") Long id) {
        return service.findById(id).map(TodoResponse::from);
    }
    
    // @PostMapping
    // public Todo create(@Valid @RequestBody TodoCreateRequest request) {
    //     return service.create(request.title());
    // }
    @PostMapping
    public TodoResponse create(@Valid @RequestBody TodoCreateRequest request) {
        Todo todo = service.create(request.title());
        return TodoResponse.from(todo);
    }

    // public record CreateTodoRequest(String title) {}


    // 指定IDのtodoのdoneを更新
    @PostMapping("/{id}/done")
    public int updateDone(@PathVariable("id") Long id, @RequestBody UpdateDoneRequest req) {
        return service.updateDone(id, req.done);
    }

    public record UpdateDoneRequest(Boolean done) {}

}