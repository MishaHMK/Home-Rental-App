package rental.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Test controller", description = "test endpoint")
@RestController
@RequestMapping("/sample")
public class SampleController {
    @GetMapping
    @Operation(summary = "Test get request", description = "smth...")
    public String getRequest() {
        System.out.println("Request received on /api/test");
        return "Success! ^_^";
    }
}
