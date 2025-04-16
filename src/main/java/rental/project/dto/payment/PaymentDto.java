package rental.project.dto.payment;

import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;

@Data
public class PaymentDto {
    private Long id;
    private Long bookingId;
    private String status;
    private String sessionId;
    private URL sessionUrl;
    private BigDecimal amount;
}
