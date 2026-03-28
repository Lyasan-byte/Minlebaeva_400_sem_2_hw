package com.lays.service;

import com.lays.model.Role;
import com.lays.model.User;
import com.lays.properties.MailProperties;
import com.lays.repository.RoleRepository;
import com.lays.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    public RegistrationService(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder,
                               JavaMailSender javaMailSender,
                               MailProperties mailProperties) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.mailProperties = mailProperties;
    }

    public String register(String username,
                           String email,
                           String password,
                           String confirmPassword,
                           String baseUrl) {

        if (username == null || username.isBlank()) {
            return "Имя пользователя обязательно";
        }

        if (email == null || email.isBlank()) {
            return "Email обязателен";
        }

        if (password == null || password.isBlank()) {
            return "Пароль обязателен";
        }

        if (!password.equals(confirmPassword)) {
            return "Пароли не совпадают";
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return "Пользователь с таким именем уже существует";
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return "Пользователь с таким email уже существует";
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена"));

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(password));
        user.setVerified(false);
        user.setVerificationCode(UUID.randomUUID().toString());
        user.setRoles(List.of(userRole));

        userRepository.save(user);
        sendVerificationEmail(user, baseUrl);

        return null;
    }

    public void verify(String code) {
        User user = userRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Неверный или устаревший код подтверждения"));

        user.setVerified(true);
        user.setVerificationCode(null);
        userRepository.save(user);
    }

    private void sendVerificationEmail(User user, String baseUrl) {
        try {
            String verifyUrl = baseUrl + "/verification?code=" + user.getVerificationCode();

            String content = mailProperties.getContent()
                    .replace("${name}", user.getUsername())
                    .replace("${url}", verifyUrl);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setFrom(mailProperties.getFrom(), mailProperties.getSender());
            helper.setTo(user.getEmail());
            helper.setSubject(mailProperties.getSubject());
            helper.setText(content, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось отправить письмо подтверждения", e);
        }
    }
}