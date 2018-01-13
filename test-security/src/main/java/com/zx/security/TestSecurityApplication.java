package com.zx.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@MapperScan("com.zx.security.dao")
@SpringBootApplication
public class TestSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestSecurityApplication.class, args);
	}
}
