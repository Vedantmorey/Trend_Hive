package com.project.trendhive.TrendHive.Service;

import com.project.trendhive.TrendHive.Dto.LoginDto;
import com.project.trendhive.TrendHive.Dto.RegistrationDto;

public interface UserService {
    RegistrationDto registerUserandRetailer(RegistrationDto registrationDto);

    LoginDto login(LoginDto loginDto);
}
