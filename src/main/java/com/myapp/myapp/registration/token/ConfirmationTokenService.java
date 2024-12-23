package com.myapp.myapp.registration.token;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }
    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not fount"));
    }

    public void setConfirmedAt(String token) {
        try {
            ConfirmationToken confirmationToken = getToken(token);
            confirmationToken.setConfirmedAt(LocalDateTime.now());
        }
        catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }


    }
}
