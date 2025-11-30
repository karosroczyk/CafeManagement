package com.cafe.menumanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MenuManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenuManagementApplication.class, args);
	}

}
