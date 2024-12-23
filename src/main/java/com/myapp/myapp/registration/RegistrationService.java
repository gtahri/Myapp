package com.myapp.myapp.registration;

import com.myapp.myapp.appuser.AppUser;
import com.myapp.myapp.appuser.AppUserRepository;
import com.myapp.myapp.appuser.AppUserRole;
import com.myapp.myapp.appuser.AppUserService;
import com.myapp.myapp.email.EmailSender;
import com.myapp.myapp.registration.token.ConfirmationToken;
import com.myapp.myapp.registration.token.ConfirmationTokenRepository;
import com.myapp.myapp.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistrationService {

    private final EmailValidator emailValidator;
    private final AppUserRepository appUserRepository;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    public RegistrationService(EmailValidator emailValidator, AppUserRepository appUserRepository, AppUserService appUserService, ConfirmationTokenService confirmationTokenService, EmailSender emailSender) {
        this.emailValidator = emailValidator;
        this.appUserRepository = appUserRepository;
        this.appUserService = appUserService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSender = emailSender;
    }

    public String register(RegistrationRequest request) {
        boolean isValideEmail = emailValidator.test(request.getEmail());
        if(!isValideEmail) {
            throw new IllegalStateException("Email is not valid");
        }
        String token = appUserService.signUp(
                new AppUser(request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER
                )
        );
        String link = "http://localhost:8080/api/v1/registration/confirm?token="+token;
        emailSender.send(request.getEmail(),buildEmail(request.getFirstName(), link) );

        return token;
    }
    public String buildEmail(String name, String link) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <style>" +
                "        .email-body {" +
                "            font-family: Arial, sans-serif;" +
                "            line-height: 1.6;" +
                "            color: #333333;" +
                "        }" +
                "        .email-header {" +
                "            font-size: 24px;" +
                "            font-weight: bold;" +
                "            color: #555555;" +
                "        }" +
                "        .email-button {" +
                "            display: inline-block;" +
                "            padding: 10px 20px;" +
                "            font-size: 16px;" +
                "            color: #ffffff;" +
                "            background-color: #007BFF;" +
                "            text-decoration: none;" +
                "            border-radius: 5px;" +
                "        }" +
                "        .email-button:hover {" +
                "            background-color: #0056b3;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"email-body\">" +
                "        <p class=\"email-header\">Hello " + name + ",</p>" +
                "        <p>Thank you for registering with us! Please click the button below to activate your account:</p>" +
                "        <p><a href=\"" + link + "\" class=\"email-button\">Activate Account</a></p>" +
                "        <p>If you didn’t register with us, please ignore this email.</p>" +
                "        <p>Best regards,<br>The Team</p>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }


    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token);

        if(confirmationToken.getConfirmedAt() != null){
            throw new IllegalStateException("Email already confirmed");
        }
        LocalDateTime  expiredAt = confirmationToken.getExpiresAt();
        if(expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }
        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableUser(confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }
}
