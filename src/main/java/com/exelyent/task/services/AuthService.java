package com.exelyent.task.services;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.AuthRequest;

public interface AuthService {

	ApiResponse.AuthResponse registerAdmin(AuthRequest.Register request);
	
    ApiResponse.AuthResponse register(AuthRequest.Register request);

    ApiResponse.AuthResponse login(AuthRequest.Login request);

    ApiResponse.AuthResponse refreshToken(String refreshToken);

    ApiResponse.UserResponse getCurrentUser(String username);
}