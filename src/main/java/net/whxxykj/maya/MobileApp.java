package net.whxxykj.maya;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.whxxykj.maya.common.repository.BaseRepositoryFactoryBean;
import net.whxxykj.maya.common.util.DateUtil;

@EnableJpaRepositories(basePackages = {"net.whxxykj.maya"},repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages  = {"net.whxxykj.maya.**.feign"})
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class MobileApp {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(DateUtil.TIMEZONE));
		SpringApplication.run(MobileApp.class);
	}
}
