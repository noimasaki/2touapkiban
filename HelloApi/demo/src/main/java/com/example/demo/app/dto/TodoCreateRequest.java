package com.example.demo.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TodoCreateRequest (
    @NotBlank(message="タイトルは必須です")
    @Size(max=100, message="タイトルは100文字以内で入力してください")
    String title
){}
