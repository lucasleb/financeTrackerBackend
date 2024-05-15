package com.lucaslebrun.authapi.repositories;

import com.lucaslebrun.authapi.entities.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.groups WHERE u.email = :email")
    Optional<User> findByEmailWithGroups(@Param("email") String email);
}