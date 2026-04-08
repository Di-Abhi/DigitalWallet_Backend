package com.loyaltyService.user_service.controller;

import com.loyaltyService.user_service.exception.GlobalExceptionHandler;
import com.loyaltyService.user_service.service.AdminUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminUserController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void blockUserShouldRejectWhenAdminTargetsSelf() throws Exception {
        mockMvc.perform(patch("/api/admin/users/100/block")
                        .header("X-User-Role", "ADMIN")
                        .header("X-User-Id", "100"))
                .andExpect(status().isBadRequest());

        verify(adminUserService, never()).setStatus(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
