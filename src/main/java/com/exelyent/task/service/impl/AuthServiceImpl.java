package com.exelyent.task.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.AuthRequest;
import com.exelyent.task.entity.Cart;
import com.exelyent.task.entity.User;
import com.exelyent.task.exception.BusinessException;
import com.exelyent.task.exception.DuplicateResourceException;
import com.exelyent.task.exception.ResourceNotFoundException;
import com.exelyent.task.repository.CartRepo;
import com.exelyent.task.repository.UserRepo;
import com.exelyent.task.security.CustomUserDetails;
import com.exelyent.task.security.JwtTokenProvider;
import com.exelyent.task.services.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

	private final UserRepo userRepository;
	private final CartRepo cartRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider tokenProvider;

	public AuthServiceImpl(UserRepo userRepository, CartRepo cartRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
		this.userRepository = userRepository;
		this.cartRepository = cartRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.tokenProvider = tokenProvider;
	}

	@Override
	@Transactional
	public ApiResponse.AuthResponse registerAdmin(AuthRequest.Register request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateResourceException("Email already registered: " + request.getEmail());
		}
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new DuplicateResourceException("Username already taken: " + request.getUsername());
		}

		User admin = new User();
		admin.setUsername(request.getUsername());
		admin.setEmail(request.getEmail());
		admin.setPassword(passwordEncoder.encode(request.getPassword()));
		admin.setFirstName(request.getFirstName());
		admin.setLastName(request.getLastName());
		admin.setPhone(request.getPhone());
		admin.setRole(User.Role.ROLE_ADMIN);
		admin.setEnabled(true);

		admin = userRepository.save(admin);

		Cart cart = new Cart();
		cart.setUser(admin);
		cartRepository.save(cart);

		log.info("Admin registered: {}", admin.getEmail());

		CustomUserDetails userDetails = new CustomUserDetails(admin);
		String accessToken = tokenProvider.generateAccessToken(userDetails);
		String refreshToken = tokenProvider.generateRefreshToken(userDetails);

		return buildAuthResponse(accessToken, refreshToken, admin);
	}

	@Override
	@Transactional
	public ApiResponse.AuthResponse register(AuthRequest.Register request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateResourceException("Email already registered: " + request.getEmail());
		}
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new DuplicateResourceException("Username already taken: " + request.getUsername());
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user.setPhone(request.getPhone());
		user.setRole(User.Role.ROLE_USER);
		user.setEnabled(true);

		user = userRepository.save(user);

		Cart cart = new Cart();
		cart.setUser(user);
		cartRepository.save(cart);

		log.info("New user registered: {}", user.getEmail());

		CustomUserDetails userDetails = new CustomUserDetails(user);
		String accessToken = tokenProvider.generateAccessToken(userDetails);
		String refreshToken = tokenProvider.generateRefreshToken(userDetails);

		return buildAuthResponse(accessToken, refreshToken, user);
	}

	@Override
	public ApiResponse.AuthResponse login(AuthRequest.Login request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String accessToken = tokenProvider.generateAccessToken(userDetails);
		String refreshToken = tokenProvider.generateRefreshToken(userDetails);

		User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		log.info("User logged in: {}", user.getEmail());
		return buildAuthResponse(accessToken, refreshToken, user);
	}

	@Override
	public ApiResponse.AuthResponse refreshToken(String refreshToken) {
		if (!tokenProvider.validateToken(refreshToken)) {
			throw new BusinessException("Invalid or expired refresh token");
		}
		String username = tokenProvider.extractUsername(refreshToken);
		User user = userRepository.findByUsernameOrEmail(username, username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		CustomUserDetails userDetails = new CustomUserDetails(user);
		String newAccessToken = tokenProvider.generateAccessToken(userDetails);
		String newRefreshToken = tokenProvider.generateRefreshToken(userDetails);

		return buildAuthResponse(newAccessToken, newRefreshToken, user);
	}

	@Override
	@Transactional(readOnly = true)
	public ApiResponse.UserResponse getCurrentUser(String username) {
		User user = userRepository.findByUsernameOrEmail(username, username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
		return mapToUserResponse(user);
	}

	// ─── Helpers ────────────────────────────────────────────────────────────

	private ApiResponse.AuthResponse buildAuthResponse(String access, String refresh, User user) {
		ApiResponse.AuthResponse response = new ApiResponse.AuthResponse();
		response.setAccessToken(access);
		response.setRefreshToken(refresh);
		response.setTokenType("Bearer");
		response.setExpiresIn(tokenProvider.getExpirationMs() / 1000);
		response.setUser(mapToUserResponse(user));
		return response;
	}

	private ApiResponse.UserResponse mapToUserResponse(User user) {
		ApiResponse.UserResponse response = new ApiResponse.UserResponse();
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setEmail(user.getEmail());
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setPhone(user.getPhone());
		response.setRole(user.getRole().name());
		response.setEnabled(user.isEnabled());
		response.setCreatedAt(user.getCreatedAt());
		return response;
	}
}