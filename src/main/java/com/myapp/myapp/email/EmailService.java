package com.myapp.myapp.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Getter
@Service
public class EmailService implements EmailSender{

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void send(String to, String email) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,"utf-8");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom("myapp@myapp.com");
            mimeMessageHelper.setSubject("Confirm your email");
            mimeMessageHelper.setText(email, true);
            mailSender.send(mimeMessage);
        }
        catch (MessagingException e){
            LOGGER.error("failed to send email" ,e);
            throw new IllegalStateException("failed to send email");
        }

    }

}
