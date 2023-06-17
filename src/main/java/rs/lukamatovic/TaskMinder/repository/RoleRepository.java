package rs.lukamatovic.TaskMinder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.lukamatovic.TaskMinder.model.ERole;
import rs.lukamatovic.TaskMinder.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}