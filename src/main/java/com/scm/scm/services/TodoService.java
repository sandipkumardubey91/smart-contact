package com.scm.scm.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.scm.scm.entities.Todo;

public interface TodoService {
    public List<Todo> getAllTodos();

    public Todo addTodo(Todo todo);

    public void deleteTodo(Long id);

    List<Todo> getTodosByUserId(String userId);
    
    Map<String, Integer> getTodoCountPerUser();

}
