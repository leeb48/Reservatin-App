package com.project.bookapp.repositories;

import com.project.bookapp.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}
