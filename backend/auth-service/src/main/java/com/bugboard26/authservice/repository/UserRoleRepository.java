package com.bugboard26.authservice.repository;

import com.bugboard26.authservice.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> 
{
    Optional<UserRole> findByNome(String nome);
}