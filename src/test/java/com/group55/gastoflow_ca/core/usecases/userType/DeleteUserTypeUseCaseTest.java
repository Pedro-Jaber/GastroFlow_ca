package com.group55.gastoflow_ca.core.usecases.userType;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.interfaces.gateway.IUserTypeGateway;

@ExtendWith(MockitoExtension.class)
class DeleteUserTypeUseCaseTest {

    @Mock
    private IUserTypeGateway userTypeGateway;

    private DeleteUserTypeUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = DeleteUserTypeUseCase.create(userTypeGateway);
    }

    @Test
    void shouldDeleteUserTypeById() {
        var id = UUID.randomUUID();

        useCase.run(id);

        verify(userTypeGateway, times(1)).deleteById(id);
    }

    @Test
    void shouldOnlyCallDeleteAndNothingElse() {
        var id = UUID.randomUUID();

        useCase.run(id);

        verify(userTypeGateway, times(1)).deleteById(id);
        verifyNoMoreInteractions(userTypeGateway);
    }
}
