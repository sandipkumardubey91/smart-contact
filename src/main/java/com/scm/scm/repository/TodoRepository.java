package com.scm.scm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.scm.entities.Todo;
import com.scm.scm.entities.User;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserId(String userId);

    int countByUserId(String userId);

    // List<Todo> findAllByUser(User user);

}
