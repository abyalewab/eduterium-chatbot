package dao;

import com.google.inject.ImplementedBy;
import dao.impl.ChatInteractionDaoImpl;
import models.ChatInteraction;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(ChatInteractionDaoImpl.class)
public interface ChatInteractionDao {

    /**
     * Saves a chat interaction to the database.
     *
     * @param chatInteraction the chat interaction to save
     * @return a CompletionStage indicating the completion of the save operation
     */
    CompletionStage<Void> save(ChatInteraction chatInteraction);

    /**
     * Retrieves all chat interactions for a specific username.
     *
     * @param userId the username for which to retrieve chat interactions
     * @return a CompletionStage containing a list of chat interactions associated with the user
     */
    CompletionStage<List<ChatInteraction>> getAllChatsByUsername(String userId);
}
