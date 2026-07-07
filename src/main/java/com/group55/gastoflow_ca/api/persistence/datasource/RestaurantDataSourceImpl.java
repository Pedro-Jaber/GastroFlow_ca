package com.group55.gastoflow_ca.api.persistence.datasource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.group55.gastoflow_ca.api.persistence.entity.RestaurantJpaEntity;
import com.group55.gastoflow_ca.api.persistence.entity.UserJpaEntity;
import com.group55.gastoflow_ca.api.persistence.repository.RestaurantJpaRepository;
import com.group55.gastoflow_ca.api.persistence.repository.UserJpaRepository;
import com.group55.gastoflow_ca.core.dtos.restaurant.RestaurantDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.interfaces.dataSource.IRestaurantDataSource;

@Component
public class RestaurantDataSourceImpl implements IRestaurantDataSource {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public RestaurantDataSourceImpl(
            RestaurantJpaRepository restaurantJpaRepository,
            UserJpaRepository userJpaRepository) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public RestaurantDTO saveNewRestaurant(RestaurantDTO newRestaurantDTO) {
        RestaurantJpaEntity entity = toEntity(newRestaurantDTO);
        RestaurantJpaEntity saved = restaurantJpaRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public PageOutputDTO<RestaurantDTO> findAll(PageInputDTO pageInput) {
        Pageable pageable = PageRequest.of(pageInput.page(), pageInput.size());
        Page<RestaurantJpaEntity> page = restaurantJpaRepository.findAll(pageable);

        List<RestaurantDTO> content = page.getContent().stream()
                .map(this::toDTO)
                .toList();

        return new PageOutputDTO<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    public Optional<RestaurantDTO> findById(UUID id) {
        return restaurantJpaRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<RestaurantDTO> findByName(String name) {
        return restaurantJpaRepository.findByName(name).map(this::toDTO);
    }

    @Override
    public RestaurantDTO updateRestaurant(RestaurantDTO updatedRestaurantDTO) {
        RestaurantJpaEntity entity = toEntity(updatedRestaurantDTO);
        RestaurantJpaEntity updated = restaurantJpaRepository.save(entity);
        return toDTO(updated);
    }

    @Override
    public void deleteById(UUID id) {
        restaurantJpaRepository.deleteById(id);
    }

    private RestaurantJpaEntity toEntity(RestaurantDTO dto) {
        UserJpaEntity ownerEntity = userJpaRepository
                .findById(dto.ownerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Owner not found with id: " + dto.ownerId()));

        return new RestaurantJpaEntity(
                dto.id(),
                dto.name(),
                dto.address(),
                dto.cuisineType(),
                dto.openingHours(),
                ownerEntity,
                dto.createdAt(),
                dto.updatedAt());
    }

    private RestaurantDTO toDTO(RestaurantJpaEntity entity) {
        return new RestaurantDTO(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getCuisineType(),
                entity.getOpeningHours(),
                entity.getOwner().getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}