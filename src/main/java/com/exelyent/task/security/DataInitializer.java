package com.exelyent.task.security;

import com.exelyent.task.entity.User;
import com.exelyent.task.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepo userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        if (userRepository.existsByEmail("admin@exelyent.com")) {
            log.info("Admin account already exists — skipping seed");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@exelyent.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setFirstName("Super");
        admin.setLastName("Admin");
        admin.setRole(User.Role.ROLE_ADMIN);
        admin.setEnabled(true);

        userRepository.save(admin);
        log.info("✅ Admin account created — email: admin@exelyent.com / password: Admin@123");
    }
}