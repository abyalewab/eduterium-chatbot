package dao.impl;

import dao.ChatInteractionDao;
import lombok.extern.slf4j.Slf4j;
import models.ChatInteraction;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;


/**
 * Implementation of ChatInteractionDao for handling database operations
 * related to chat interactions, including saving and retrieving chats
 * by username.
 */
@Slf4j
public class ChatInteractionDaoImpl implements ChatInteractionDao {

    private final JPAApi jpaApi;
    private final Executor executor;

    @Inject
    public ChatInteractionDaoImpl(JPAApi jpaApi, Executor executor) {
        this.jpaApi = jpaApi;
        this.executor = executor;
    }

    /**
     * Saves a chat interaction to the database.
     *
     * @param chatInteraction the chat interaction to save
     * @return a CompletionStage indicating the completion of the save operation
     */
    @Override
    public CompletionStage<Void> save(ChatInteraction chatInteraction) {
        return runAsync(() -> {
            try {
                jpaApi.withTransaction(em -> {
                    em.persist(chatInteraction);
                });
            } catch (PersistenceException e) {
                handleDatabaseException(e);
            }
        }, executor);
    }

    /**
     * Retrieves all chat interactions for a specific username.
     *
     * @param username the username for which to retrieve chat interactions
     * @return a CompletionStage containing a list of chat interactions associated with the user
     */
    @Override
    public CompletionStage<List<ChatInteraction>> getAllChatsByUsername(String username) {
        return supplyAsync(() -> {
            try {
                return jpaApi.withTransaction(em -> {
                    return em.createQuery("SELECT c FROM ChatInteraction c WHERE c.username = :username",
                                    ChatInteraction.class).setParameter("username", username).getResultList();
                });
            } catch (PersistenceException e) {
                handleDatabaseException(e);
                return null;
            }
        }, executor);
    }

    /**
     * Handles database exceptions by logging the error and throwing a runtime exception.
     *
     * @param e the exception that occurred during a database operation
     */
    private void handleDatabaseException(Exception e) {
        log.error("Database operation failed: " + e.getMessage());
        throw new RuntimeException("Failed to perform database operation", e);
    }
}
