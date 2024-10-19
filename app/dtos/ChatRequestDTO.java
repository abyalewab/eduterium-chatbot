package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ChatInteraction;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatRequestDTO {

    private String chatRole;
    private String message;

    public static ChatInteraction toEntity(ChatRequestDTO dto, String username) {
        ChatInteraction chatInteraction = new ChatInteraction();
        chatInteraction.setChatRole(dto.getChatRole());
        chatInteraction.setMessage(dto.getMessage());
        chatInteraction.setResponse("Pending");
        chatInteraction.setSubmittedAt(new Timestamp(System.currentTimeMillis()));
        chatInteraction.setUsername(username);
        return chatInteraction;
    }
}
