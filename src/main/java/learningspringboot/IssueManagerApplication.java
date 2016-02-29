package learningspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Indicates a configuration class that declares one or more @Bean methods and also triggers auto-configuration and component scanning. This is a convenience annotation that is equivalent to declaring @Configuration, @EnableAutoConfiguration and @ComponentScan.
@SpringBootApplication
public class IssueManagerApplication {

    public static void main (String[] args) {
        SpringApplication.run(IssueManagerApplication.class, args);
    }
}
