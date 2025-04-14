package rental.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rental.project.dto.payment.CreatePaymentDto;
import rental.project.dto.payment.PaymentDto;
import rental.project.service.payment.PaymentService;

import java.util.List;

@Tag(name = "Payment controller",
        description = "Payments management endpoint")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('ADMIN') or "
            + " #userId == @securityUtil.loggedInUserId")
    @GetMapping
    @Operation(summary = "Get all payments",
            description = "Get all payments by user")
    public List<PaymentDto> getPaymentsByUserId(@RequestParam(required = false) Long userId,
                                                @ParameterObject Pageable pageable) {
        return paymentService.getAllByUserId(pageable, userId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/success")
    @Operation(summary = "Confirm payment",
            description = "Confirm payment with given session")
    public PaymentDto confirmPayment(@RequestParam(required = false) String sessionId) {
        return paymentService.success(sessionId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping("/cancel")
    @Operation(summary = "Cancel payment",
            description = "Cancel payment with given session")
    public PaymentDto cancelPayment(@RequestParam(required = false) String sessionId) {
        return paymentService.cancel(sessionId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @PostMapping
    @Operation(summary = "Create payment",
            description = "Create payment entity with session based on booking data")
    public PaymentDto createPayment(@RequestBody CreatePaymentDto createPaymentDto) {
        return paymentService.save(createPaymentDto);
    }
}
