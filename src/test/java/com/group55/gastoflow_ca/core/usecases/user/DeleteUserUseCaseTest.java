package com.group55.gastoflow_ca.core.usecases.user;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    private DeleteUserUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = DeleteUserUseCase.create(userGateway);
    }

    @Test
    void shouldDeleteUserById() {
        var id = UUID.randomUUID();

        useCase.run(id);

        verify(userGateway, times(1)).deleteById(id);
    }

    @Test
    void shouldOnlyCallDeleteAndNothingElse() {
        var id = UUID.randomUUID();

        useCase.run(id);

        verify(userGateway, times(1)).deleteById(id);
        verifyNoMoreInteractions(userGateway);
    }
}
