package rental.project.service.payment;

import java.util.List;
import org.springframework.data.domain.Pageable;
import rental.project.dto.payment.CreatePaymentDto;
import rental.project.dto.payment.PaymentDto;

public interface PaymentService {
    List<PaymentDto> getAllByUserId(Pageable pageable, Long userId);

    PaymentDto save(CreatePaymentDto createPaymentDto);

    PaymentDto success(String sessionId);

    PaymentDto cancel(String sessionId);
}
