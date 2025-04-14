package rental.project.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeUtil {
    public static final Long DEFAULT_QUANTITY = 1L;
    public static final Long CENT_TO_DOLLAR_RATIO = 100L;
    private static final String DEFAULT_CURRENCY = "usd";

    @Value("${stripe.success.link}")
    private String successUrl;

    @Value("${stripe.cancel.link}")
    private String cancelUrl;

    public StripeUtil(@Value("${stripe.api.key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    public Session createSession(BigDecimal amount, String name) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(DEFAULT_QUANTITY)
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency(DEFAULT_CURRENCY)
                                        .setUnitAmountDecimal(convertToCents(amount))
                                        .setProductData(SessionCreateParams.LineItem
                                                .PriceData.ProductData.builder()
                                                .setName(name)
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                )
                .build();

        return Session.create(params);
    }

    public Session receiveSession(String sessionId) throws StripeException {
        return Session.retrieve(sessionId);
    }

    public BigDecimal convertToCents(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(CENT_TO_DOLLAR_RATIO));
    }
}
