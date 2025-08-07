package com.yongjincompany.devblind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan()
public class DevblindApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevblindApplication.class, args);
	}

}
