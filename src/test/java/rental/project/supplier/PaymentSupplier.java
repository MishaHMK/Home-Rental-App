package rental.project.supplier;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import rental.project.dto.payment.CreatePaymentDto;
import rental.project.dto.payment.PaymentDto;
import rental.project.model.Payment;
import rental.project.model.Payment.PaymentStatus;

public class PaymentSupplier {
    public static Payment getPayment() {
        try {
            return new Payment()
                    .setId(1L)
                    .setStatus(PaymentStatus.PENDING)
                    .setBooking(BookingSupplier.getBooking())
                    .setSessionUrl(new URL("https://checkout.stripe.com/c/pay/cs_test_a1h5"))
                    .setAmount(BigDecimal.valueOf(125.5))
                    .setSessionId("cs_test_a1h5");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL", e);
        }
    }

    public static Payment getCancelledPayment() {
        try {
            return new Payment()
                    .setId(1L)
                    .setStatus(PaymentStatus.CANCELED)
                    .setBooking(BookingSupplier.getBooking())
                    .setSessionUrl(new URL("https://checkout.stripe.com/c/pay/cs_test_a1h5"))
                    .setAmount(BigDecimal.valueOf(125.5))
                    .setSessionId("cs_test_a1h5");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL", e);
        }
    }

    public static List<Payment> getPaymentList() {
        return List.of(getPayment());
    }

    public static PaymentDto getPaymentDto() {
        try {
            return new PaymentDto()
                    .setId(1L)
                    .setStatus("PENDING")
                    .setBookingId(BookingSupplier.getBooking().getId())
                    .setSessionUrl(new URL("https://checkout.stripe.com/c/pay/cs_test_a1h5"))
                    .setAmount(BigDecimal.valueOf(125.5))
                    .setSessionId("cs_test_a1h5");
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL", e);
        }
    }

    public static List<PaymentDto> getPaymentDtoList() {
        return List.of(getPaymentDto());
    }

    public static CreatePaymentDto getCreatePaymentDto() {
        return new CreatePaymentDto().setBookingId(1L);
    }

    public static Session getSession() {
        Session session = new Session();
        session.setId("cs_test_a1h5");
        session.setUrl("https://checkout.stripe.com/c/pay/cs_test_a1h5");
        session.setStatus("complete");
        return session;
    }

    public static Session getOpenSession() {
        Session session = new Session();
        session.setId("cs_test_a1h5");
        session.setUrl("https://checkout.stripe.com/c/pay/cs_test_a1h5");
        session.setStatus("open");
        return session;
    }

    public static Session getWrongUrlSession() {
        Session session = new Session();
        session.setId("cs_test_a1h5");
        session.setUrl("ht@tp://bad-url");
        return session;
    }
}
