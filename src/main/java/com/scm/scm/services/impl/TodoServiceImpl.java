package com.scm.scm.services.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scm.scm.entities.Todo;
import com.scm.scm.entities.User;
import com.scm.scm.repository.TodoRepository;
import com.scm.scm.services.TodoService;
import com.scm.scm.services.UserService;

@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoRepository todoRepo;

    @Autowired
    private UserService userService;

    public List<Todo> getAllTodos() {
        return todoRepo.findAll();
    }

    public Todo addTodo(Todo todo) {
        return todoRepo.save(todo);
    }

    public void deleteTodo(Long id) {
        todoRepo.deleteById(id);
    }

    @Override
    public List<Todo> getTodosByUserId(String userId) {
        return todoRepo.findByUserId(userId);
    }

    @Override
    public Map<String, Integer> getTodoCountPerUser() {
        List<User> users = userService.getAllUsers(); // Inject UserRepository if not already
        Map<String, Integer> map = new LinkedHashMap<>();

        for (User user : users) {
            if (!user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
                int count = todoRepo.countByUserId(user.getUserId());
                map.put(user.getEmail(), count);
            }
        }
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

}
