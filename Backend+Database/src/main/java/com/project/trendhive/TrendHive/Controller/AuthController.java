package com.project.trendhive.TrendHive.Controller;

import com.project.trendhive.TrendHive.Dto.LoginDto;
import com.project.trendhive.TrendHive.Dto.RegistrationDto;
import com.project.trendhive.TrendHive.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    ResponseEntity<RegistrationDto> registerUserandRetailer(@RequestBody RegistrationDto registrationDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUserandRetailer(registrationDto));
    }
    @PostMapping("/login")
    ResponseEntity<LoginDto> checkLogin(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok().body(userService.login(loginDto));
    }

}
