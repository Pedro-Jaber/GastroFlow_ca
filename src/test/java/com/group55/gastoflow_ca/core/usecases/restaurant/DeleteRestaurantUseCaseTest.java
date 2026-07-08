package com.group55.gastoflow_ca.core.usecases.restaurant;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.interfaces.gateway.IRestaurantGateway;

@ExtendWith(MockitoExtension.class)
class DeleteRestaurantUseCaseTest {

    @Mock
    private IRestaurantGateway restaurantGateway;

    private DeleteRestaurantUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = DeleteRestaurantUseCase.create(restaurantGateway);
    }

    @Test
    void shouldDeleteRestaurantById() {
        var id = UUID.randomUUID();

        useCase.run(id);

        verify(restaurantGateway, times(1)).deleteById(id);
    }

    @Test
    void shouldOnlyCallDeleteAndNothingElse() {
        var id = UUID.randomUUID();

        useCase.run(id);

        verify(restaurantGateway, times(1)).deleteById(id);
        verifyNoMoreInteractions(restaurantGateway);
    }
}
