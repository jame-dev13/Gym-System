package com.jame.dev.gymApp.user.usecases.mutation;

import com.jame.dev.gymApp.features.user.application.service.mutation.SoftDeleteUserByIdUseCaseService;
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
class SoftDeleteUserByIdUseCaseServiceTest {

    @Mock
    private UserMutationRepository userMutationRepository;

    @InjectMocks
    private SoftDeleteUserByIdUseCaseService service;

    @Test
    @DisplayName("Should call repository deleteById")
    void softDeleteById_callsRepositoryDeleteById() {
        service.softDeleteById(1L);

        verify(userMutationRepository).deleteById(anyLong());
        verifyNoMoreInteractions(userMutationRepository);
    }
}
