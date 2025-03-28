package com.auth.repository;

import com.auth.models.TontineGroup;
import com.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TontineGroupRepository extends JpaRepository<TontineGroup, Long> {
    List<TontineGroup> findByCreator(User creator);
}