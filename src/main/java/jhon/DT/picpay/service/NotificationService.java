package jhon.DT.picpay.service;

import jhon.DT.picpay.dtos.NotificationDTO;
import jhon.DT.picpay.model.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NotificationService {

    private final WebClient webClient;


    public NotificationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://util.devi.tools/api/v1/notify").build();
    }

    public void sendNotification(User user, String message) throws Exception {
        String email = user.getEmail();
        NotificationDTO notificationRequest = new NotificationDTO(email, message);

        // Envio da requisição POST
        ResponseEntity<String> notificationResponse = webClient.post()
                .uri("")
                .bodyValue(notificationRequest)  // Corpo da requisição
                .retrieve()  // Realiza a requisição
                .toEntity(String.class)  // Mapeia a resposta para String
                .block();  // Bloqueia para obter a resposta de forma síncrona (caso queira uma resposta bloqueante)

        // Verificação do status da resposta
        if (notificationResponse == null || notificationResponse.getStatusCode() != HttpStatus.OK) {
            System.out.println("Erro ao enviar notificação");
            throw new Exception("Notification service is down");
        }
    }
}
