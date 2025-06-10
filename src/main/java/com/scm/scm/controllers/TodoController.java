package com.scm.scm.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scm.scm.entities.Todo;
import com.scm.scm.entities.User;
import com.scm.scm.helpers.Helper;
import com.scm.scm.services.TodoService;
import com.scm.scm.services.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/dashboard")
public class TodoController {
    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    // @RequestMapping
    // public String showTodos(Model model) {
    // model.addAttribute("todo", new Todo()); // for the form
    // return "user/dashboard"; // name of your HTML file
    // }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, String>> addTodoAjax(@RequestBody Map<String, String> payload,
            Authentication authentication) {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        String task = payload.get("task");

        // Validate task input
        if (task == null || task.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Task cannot be empty"));
        }

        Todo todo = new Todo();
        todo.setUserId(user.getUserId());
        todo.setCreatedAt(LocalDateTime.now());
        todo.setTask(task);

        todoService.addTodo(todo);

        // Format createdAt to be more user-friendly
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm");
        String createdAtFormatted = todo.getCreatedAt().format(formatter);
        int updatedUpcomingReminders = todoService.getTodosByUserId(user.getUserId()).size();

        Map<String, String> response = new HashMap<>();
        response.put("task", todo.getTask());
        response.put("id", todo.getId().toString());
        response.put("upcomingReminders", String.valueOf(updatedUpcomingReminders));
        response.put("createdAtFormatted", createdAtFormatted);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteTodo(@PathVariable("id") Long id, Authentication authentication) {
        todoService.deleteTodo(id); // delete the task
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);
        int updatedReminderCount = todoService.getTodosByUserId(user.getUserId()).size();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("upcomingReminders", updatedReminderCount);

        return ResponseEntity.ok(response);
    }

}