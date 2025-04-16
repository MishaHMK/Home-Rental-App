package rental.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rental.project.config.MapperConfig;
import rental.project.dto.payment.PaymentDto;
import rental.project.model.Payment;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "booking.id", target = "bookingId")
    PaymentDto toDto(Payment payment);
}
