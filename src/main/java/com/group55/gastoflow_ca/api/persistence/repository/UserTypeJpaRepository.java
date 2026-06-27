package com.group55.gastoflow_ca.api.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group55.gastoflow_ca.api.persistence.entity.UserTypeJpaEntity;

public interface UserTypeJpaRepository extends JpaRepository<UserTypeJpaEntity, UUID> {

    Optional<UserTypeJpaEntity> findByName(String name);

}
