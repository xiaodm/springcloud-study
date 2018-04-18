package test.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class EurekaClient1Application {
    Logger log = LoggerFactory.getLogger(EurekaClient1Application.class);

    @Value("${server.port}")
    String port;

    @Value("${db-config.spring.jpa.pro2}")
    String dbConfigPro1;

    @RequestMapping("/hi")
    public String home(@RequestParam String name) {
        log.info("service1 call hi");
        return "hi " + name + ",i am from port:" + port + " dbConfigPro1:" + dbConfigPro1;
    }

    public static void main(String[] args) {
        SpringApplication.run(EurekaClient1Application.class, args);
    }

}
