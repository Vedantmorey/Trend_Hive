package com.project.trendhive.TrendHive.Controller;

import com.project.trendhive.TrendHive.Config.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GetMapping("/api/greeting")
    public String userGreeting(@AuthenticationPrincipal UserPrincipal principal){
        String  name = principal.getFullName();
        return name;
    }
}