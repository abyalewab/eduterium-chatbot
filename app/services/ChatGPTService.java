package services;

import com.google.inject.ImplementedBy;
import dtos.ChatInteractionDTO;
import dtos.ChatRequestDTO;
import services.impl.ChatGPTServiceImpl;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Interface for chat services, providing methods to handle chat interactions.
 */
@ImplementedBy(ChatGPTServiceImpl.class)
public interface ChatGPTService {

    /**
     * Adds a new chat message for the specified user.
     *
     * @param dto the chat request data transfer object containing the chat message
     * @param username the username of the user sending the chat message
     * @return a CompletionStage containing the added chat interaction data
     */
    CompletionStage<ChatInteractionDTO> addChat(ChatRequestDTO dto, String username);

    /**
     * Retrieves all chat messages associated with a specific username.
     *
     * @param userId the username for which to retrieve chat messages
     * @return a CompletionStage containing a list of chat interaction data transfer objects
     */
    CompletionStage<List<ChatInteractionDTO>> getAllChatsByUsername(String userId);
}
