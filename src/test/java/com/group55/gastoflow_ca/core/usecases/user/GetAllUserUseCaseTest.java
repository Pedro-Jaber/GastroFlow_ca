package com.group55.gastoflow_ca.core.usecases.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.group55.gastoflow_ca.core.dtos.shared.PageInputDTO;
import com.group55.gastoflow_ca.core.dtos.shared.PageOutputDTO;
import com.group55.gastoflow_ca.core.entities.User;
import com.group55.gastoflow_ca.core.entities.UserType;
import com.group55.gastoflow_ca.core.interfaces.gateway.IUserGateway;

@ExtendWith(MockitoExtension.class)
class GetAllUserUseCaseTest {

    @Mock
    private IUserGateway userGateway;

    private GetAllUserUseCase useCase;

    @BeforeEach
    void setup() {
        useCase = GetAllUserUseCase.create(userGateway);
    }

    @Test
    void shouldReturnPageOfUsers() {
        var pageInput = new PageInputDTO(0, 10);
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);

        var user1 = User.create(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", "123",
                userType, LocalDateTime.now(), LocalDateTime.now());
        var user2 = User.create(UUID.randomUUID(), "Jane Doe", "jane@ex.com", "jane", "123",
                userType, LocalDateTime.now(), LocalDateTime.now());

        var expectedPage = new PageOutputDTO<>(List.of(user1, user2), 0, 10, 2L, 1);

        when(userGateway.findAll(pageInput)).thenReturn(expectedPage);

        var result = useCase.run(pageInput);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2L);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyPageWhenNoUsersExist() {
        var pageInput = new PageInputDTO(0, 10);
        var emptyPage = new PageOutputDTO<User>(List.of(), 0, 10, 0L, 0);

        when(userGateway.findAll(pageInput)).thenReturn(emptyPage);

        var result = useCase.run(pageInput);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0L);
    }

    @Test
    void shouldRespectPageAndSizeParameters() {
        var pageInput = new PageInputDTO(2, 5);
        var userType = UserType.create(UUID.randomUUID(), "Cliente", null);
        var user = User.create(UUID.randomUUID(), "John Doe", "jdoe@ex.com", "jdoe", "123",
                userType, LocalDateTime.now(), LocalDateTime.now());
        var page = new PageOutputDTO<>(List.of(user), 2, 5, 11L, 3);

        when(userGateway.findAll(pageInput)).thenReturn(page);

        var result = useCase.run(pageInput);

        assertThat(result.page()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.totalPages()).isEqualTo(3);
    }

    @Test
    void shouldDelegateToGatewayExactlyOnce() {
        var pageInput = new PageInputDTO(0, 10);
        when(userGateway.findAll(any())).thenReturn(new PageOutputDTO<>(List.of(), 0, 10, 0L, 0));

        useCase.run(pageInput);

        verify(userGateway, times(1)).findAll(pageInput);
    }
}
