package jhon.DT.picpay.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransactionDTO(
        @NotNull(message = "Sender ID is required")
        @JsonProperty("senderId") Long senderId,

        @NotNull(message = "Receiver ID is required")
        @JsonProperty("receiverId") Long receiverId,

        @NotNull(message = "Transaction value is required")
        @Min(value = 1, message = "Transaction value must be greater than 0")
        @JsonProperty("value") BigDecimal value
) {}