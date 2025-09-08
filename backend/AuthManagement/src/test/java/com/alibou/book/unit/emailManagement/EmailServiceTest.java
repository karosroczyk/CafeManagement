package com.alibou.book.unit.emailManagement;

import com.alibou.book.email.EmailService.EmailService;
import com.alibou.book.email.EmailService.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private SpringTemplateEngine templateEngine;
    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmailtest() throws MessagingException {
        MimeMessage mimeMessage = mock(MimeMessage.class);

        // Stub dependencies
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("ACTIVATE_ACCOUNT"), any(Context.class)))
                .thenReturn("<html>Email Content</html>");

        // Call service
        emailService.sendEmail(
                "test@example.com",
                "Alice",
                EmailTemplateName.ACTIVATE_ACCOUNT,
                "http://localhost/confirm",
                "67890",
                "Hello!"
        );

        // Verify interactions
        verify(mailSender, times(1)).createMimeMessage();
        verify(templateEngine, times(1)).process(eq("ACTIVATE_ACCOUNT"), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

}
