package com.cafe.auth;

import com.cafe.auth.roleManagement.entity.Role;
import com.cafe.auth.roleManagement.dao.RoleDAOJPA;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class AuthManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthManagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleDAOJPA roleDAOJPA){
		return args -> {
			if(roleDAOJPA.findByName("ROLE_CLIENT").isEmpty()){
				roleDAOJPA.save(Role.builder().name("ROLE_CLIENT").build());
			}
		};
	}
}
