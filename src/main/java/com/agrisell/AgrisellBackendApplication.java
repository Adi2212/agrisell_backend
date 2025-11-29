package com.agrisell;

import com.agrisell.model.Role;
import com.agrisell.model.User;
import com.agrisell.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class AgrisellBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgrisellBackendApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();

		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT).setFieldMatchingEnabled(true)
				.setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE).setSkipNullEnabled(true);
		return mapper;
	}
	
//	@Bean
//	CommandLineRunner initAdmin(UserRepository repo, BCryptPasswordEncoder encoder) {
//	    return args -> {
//	        if (repo.findByEmail("admin@agrisell.com").isEmpty()) {
//	            User admin = new User();
//	            admin.setName("System Admin");
//	            admin.setEmail("admin@agrisell.com");
//	            admin.setPassword(encoder.encode("admin123"));
//	            admin.setRole(Role.ADMIN);
//	            repo.save(admin);
//	            System.out.println("✅ Admin user created: admin@agrisell.com / admin123");
//	        }
//	    };
//	}

}
