package com.partygo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.partygo.dao")
@EnableSwagger2
public class PartyGoProApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartyGoProApplication.class, args);
	}
}
