package rental.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HomeRentalApp {
    public static void main(String[] args) {
        SpringApplication.run(HomeRentalApp.class, args);
    }
}
