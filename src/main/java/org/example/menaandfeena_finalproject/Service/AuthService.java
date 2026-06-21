package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.AuthResponseDTO;
import org.example.menaandfeena_finalproject.DTO.In.LoginRequestDTO;
import org.example.menaandfeena_finalproject.DTO.In.UserRegisterRequestDto;
import org.example.menaandfeena_finalproject.DTO.Out.UserRegisterResponseDto;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDTO register(UserRegisterRequestDto dto) {

        dto.setPassword(
                passwordEncoder.encode(dto.getPassword())
        );

        UserRegisterResponseDto response =
                userService.registerUser(dto);

        User user =
                userRepository.findUserById(response.getId());

        String token =
                jwtService.generateToken(user);

        return new AuthResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                token
        );
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {

        User user =
                userRepository.findUserByEmail(dto.getEmail());

        if (user == null) {
            throw new ApiException("wrong email or password");
        }

        boolean matches =
                passwordEncoder.matches(
                        dto.getPassword(),
                        user.getPassword()
                );

        if (!matches) {
            throw new ApiException("wrong email or password");
        }

        String token =
                jwtService.generateToken(user);

        return new AuthResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                token
        );
    }
}