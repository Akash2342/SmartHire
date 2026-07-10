package com.backend.naukri.domain.user.repository;

import com.backend.naukri.common.enums.Role;
import com.backend.naukri.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    /** Paginated user list for admin dashboard — optionally filtered by role. */
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<User> findByRoleOrderByCreatedAtDesc(Role role, Pageable pageable);

    /** Used for dashboard metrics count. */
    long countByRole(Role role);
}
