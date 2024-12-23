package com.myapp.myapp.appuser;

import com.myapp.myapp.registration.token.ConfirmationToken;
import com.myapp.myapp.registration.token.ConfirmationTokenRepository;
import com.myapp.myapp.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    public AppUserService(AppUserRepository appUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder,ConfirmationTokenService confirmationTokenService) {
        this.appUserRepository = appUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }
    public String signUp(AppUser appUser) {
        boolean userExists =  appUserRepository.findByEmail(appUser.getEmail()).isPresent();
        boolean userEnabled = appUser.isEnabled();
        if(userExists && userEnabled) {
            throw new IllegalArgumentException("Email already in use");
        }
        else if (userExists && !userEnabled) {
            System.out.println("here");

            AppUser oldAppuser = appUserRepository.findByEmail(appUser.getEmail()).orElseThrow(() -> new UsernameNotFoundException(appUser.getEmail()));
            appUserRepository.delete(oldAppuser);

            encodedPassword(appUser);
            return generateToken(appUser);
        }
        else{

            encodedPassword(appUser);
            return generateToken(appUser);
        }
    }

    private void encodedPassword(AppUser appUser) {
        String passwordEncoded = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(passwordEncoded);
        appUserRepository.save(appUser);
    }

    private String generateToken(AppUser appUser) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
                );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    public void enableUser(String email) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);
        appUser.ifPresent(user -> {
            user.setEnabled(true);
            appUserRepository.save(user);
        }
    );

    }
}
