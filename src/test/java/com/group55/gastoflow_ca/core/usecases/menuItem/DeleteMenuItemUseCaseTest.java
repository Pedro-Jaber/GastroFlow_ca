package com.group55.gastoflow_ca.core.usecases.menuItem;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.interfaces.gateway.IMenuItemGateway;

@ExtendWith(MockitoExtension.class)
class DeleteMenuItemUseCaseTest {

    @Mock
    private IMenuItemGateway menuItemGateway;

    private DeleteMenuItemUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = DeleteMenuItemUseCase.create(menuItemGateway);
    }

    @Test
    void shouldDeleteMenuItemById() {
        var id = UUID.randomUUID();

        useCase.run(id);

        verify(menuItemGateway, times(1)).deleteById(id);
    }

    @Test
    void shouldOnlyCallDeleteAndNothingElse() {
        var id = UUID.randomUUID();

        useCase.run(id);

        verify(menuItemGateway, times(1)).deleteById(id);
        verifyNoMoreInteractions(menuItemGateway);
    }
}
