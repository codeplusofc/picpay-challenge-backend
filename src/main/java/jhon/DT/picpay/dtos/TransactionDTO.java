package jhon.DT.picpay.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionDTO(BigDecimal value, UUID senderId, UUID receiverId) {
}
