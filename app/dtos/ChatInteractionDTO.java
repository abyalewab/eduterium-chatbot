package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ChatInteraction;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatInteractionDTO {

    private String chatRole;
    private String message;
    private String response;
    private Timestamp submittedAt;
    private String username;

    public static ChatInteractionDTO toDTO(ChatInteraction chatInteraction) {
        ChatInteractionDTO dto = new ChatInteractionDTO();
        dto.setChatRole(chatInteraction.getChatRole());
        dto.setMessage(chatInteraction.getMessage());
        dto.setResponse(chatInteraction.getResponse());
        dto.setUsername(chatInteraction.getUsername());
        dto.setSubmittedAt(chatInteraction.getSubmittedAt());
        return dto;
    }
}
