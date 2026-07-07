package com.group55.gastoflow_ca.api.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group55.gastoflow_ca.api.persistence.entity.MenuItemJpaEntity;

public interface MenuItemJpaRepository extends JpaRepository<MenuItemJpaEntity, UUID> {

}
