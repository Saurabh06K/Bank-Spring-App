package com.aurionpro.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aurionpro.bank.entity.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Integer> {
	Optional<Role> findByRoleName(String roleName);
}
