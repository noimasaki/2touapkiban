package com.example.demo.app.dto;

import com.example.demo.domain.Todo;

public record TodoResponse (
    Long id,
    String title,
    boolean done
){
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
            todo.getId(),
            todo.getTitle(),
            todo.getDone()
        );
    }
}
