package com.scriptrunner.init;

import com.scriptrunner.model.User;
import com.scriptrunner.reporitory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(this.userRepository.findByUsername("admin").isEmpty()){
            User user = User.builder()
                    .username("admin")
                    .passwordHash(this.passwordEncoder.encode("admin"))
                    .build();
            this.userRepository.saveAndFlush(user);
            System.out.println("User created");
        }
    }
}
