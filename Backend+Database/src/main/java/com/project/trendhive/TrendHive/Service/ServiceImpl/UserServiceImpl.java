package com.project.trendhive.TrendHive.Service.ServiceImpl;

import com.project.trendhive.TrendHive.Config.JwtTokenProvider;
import com.project.trendhive.TrendHive.Dto.LoginDto;
import com.project.trendhive.TrendHive.Dto.RegistrationDto;
import com.project.trendhive.TrendHive.Entity.User;
import com.project.trendhive.TrendHive.Repository.UserRepository;
import com.project.trendhive.TrendHive.Service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) { // <-- ADD TO CONSTRUCTOR
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public RegistrationDto registerUserandRetailer(RegistrationDto registrationDto) {
        User user = modelMapper.map(registrationDto, User.class);
        if ("CUSTOMER".equalsIgnoreCase(registrationDto.getRole())) {
            user.setRole("CUSTOMER");
        } else if ("RETAILER".equalsIgnoreCase(registrationDto.getRole())) {
            user.setRole("RETAILER");
        } else {
            throw new IllegalArgumentException("Invalid role provided.");
        }
        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser,RegistrationDto.class);
    }

    @Override
    public LoginDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        System.out.println("This is working");
        return new LoginDto(token);
    }
}