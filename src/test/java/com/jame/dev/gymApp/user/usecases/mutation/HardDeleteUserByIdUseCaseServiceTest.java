package com.jame.dev.gymApp.user.usecases.mutation;

import com.jame.dev.gymApp.features.user.application.service.mutation.HardDeleteUserByIdUseCaseService;
import com.jame.dev.gymApp.features.user.domain.repository.UserMutationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class HardDeleteUserByIdUseCaseServiceTest {

    @Mock
    private UserMutationRepository userMutationRepository;

    @InjectMocks
    private HardDeleteUserByIdUseCaseService service;

    @Test
    @DisplayName("Should call repository hardDeleteById")
    void hardDeleteById_callsRepositoryHardDeleteById() {
        service.hardDeleteById(1L);

        verify(userMutationRepository).hardDeleteById(anyLong());
        verifyNoMoreInteractions(userMutationRepository);
    }
}
