package com.example.rdbrepository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttachRepository extends JpaRepository<Attach,Long> {
    Optional<Attach> findById(Long attachId);
    Optional<Attach> findByOriginFileName(String originFileName);
}
