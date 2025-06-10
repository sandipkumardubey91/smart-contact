package com.scm.scm;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.scm.scm.entities.User;
import com.scm.scm.repository.UserRepo;

@SpringBootApplication
public class ScmApplication {

	@Autowired
	UserRepo userRepo;

	@Bean
	public CommandLineRunner createAdminUser(UserRepo userRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			String adminEmail = "admin@scm";
			if (!userRepo.findByEmail(adminEmail).isPresent()) {
				User admin = User.builder()
						.userId(UUID.randomUUID().toString())
						.name("Admin")
						.email(adminEmail)
						.password(passwordEncoder.encode("admin123")) // strong password
						.role("ROLE_ADMIN")
						.enabled(true)
						.emailVerified(true)
						.phoneVerified(true)
						.build();
				userRepo.save(admin);
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(ScmApplication.class, args);
	}

}
