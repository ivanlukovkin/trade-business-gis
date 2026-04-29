package org.lkvkn.gistrade.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.lkvkn.gistrade.users.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(Long id);

    @Override
    boolean existsById(Long id); 

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Override
    void deleteById(Long id); 
}
