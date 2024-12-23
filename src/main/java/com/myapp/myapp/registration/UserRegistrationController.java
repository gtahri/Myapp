package com.myapp.myapp.registration;

import jakarta.servlet.Registration;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
public class UserRegistrationController {

    private final RegistrationService registrationService;

    public UserRegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public String register(@RequestBody RegistrationRequest request){
        return registrationService.register(request);
    }
    @GetMapping("/confirm")
    public String confirm(@RequestParam("token") String token){
        try {
            return registrationService.confirmToken(token);
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}
