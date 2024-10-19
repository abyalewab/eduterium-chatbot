package services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import dao.ChatInteractionDao;
import dtos.ChatInteractionDTO;
import dtos.ChatRequestDTO;
import lombok.extern.slf4j.Slf4j;
import models.ChatInteraction;
import play.libs.Json;
import play.libs.ws.WSClient;
import services.ChatGPTService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of the ChatGPTService interface, handling chat interactions
 * and communication with the ChatGPT API.
 */
@Singleton
@Slf4j
public class ChatGPTServiceImpl implements ChatGPTService {

    private final ChatInteractionDao chatInteractionDao;
    private final WSClient wsClient;
    private final String apiKey;
    private final String apiUrl;

    /**
     * Constructs a ChatGPTServiceImpl with the specified dependencies.
     *
     * @param chatInteractionDao the DAO for chat interactions
     * @param wsClient the WSClient for making HTTP requests
     * @param config the configuration object containing API settings
     */
    @Inject
    public ChatGPTServiceImpl(ChatInteractionDao chatInteractionDao, WSClient wsClient, Config config) {
        this.chatInteractionDao = chatInteractionDao;
        this.wsClient = wsClient;
        this.apiKey = config.getString("openai.apiKey");
        this.apiUrl = config.getString("openai.apiUrl");
        log.debug("ChatGPTServiceImpl initialized with API URL: {} and API Key: {}", apiUrl, "********");
    }

    /**
     * Adds a new chat message and retrieves a response from ChatGPT.
     *
     * @param dto the chat request data transfer object
     * @param username the username of the user sending the chat message
     * @return a CompletionStage containing the added chat interaction data
     */
    @Override
    public CompletionStage<ChatInteractionDTO> addChat(ChatRequestDTO dto, String username) {
        log.info("Adding chat message for user: {}", username);
        log.debug("Chat message: {}, Role: {}", dto.getMessage(), dto.getChatRole());

        return fetchChatGPTResponse(dto.getMessage(), dto.getChatRole())
                .exceptionally(error -> {
                    log.error("Error fetching response from ChatGPT: {}", error.getMessage(), error);
                    return "Please try again later to interact with the ChatBot. " +
                            "This is mock response for message \"" + dto.getMessage() + "\"";
                })
                .thenComposeAsync(gptResponse -> {
                    if (gptResponse == null || gptResponse.isEmpty()) {
                        log.warn("Received an empty response from ChatGPT.");
                        gptResponse = "Sorry, I couldn't generate a response.";
                    }

                    log.debug("ChatGPT response received: {}", gptResponse);
                    ChatInteraction chatInteraction = ChatRequestDTO.toEntity(dto, username);
                    chatInteraction.setResponse(gptResponse);

                    return chatInteractionDao.save(chatInteraction)
                            .thenApplyAsync(ignored -> {
                                log.info("Chat message saved successfully for user: {}", username);
                                return new ChatInteractionDTO(
                                        chatInteraction.getChatRole(),
                                        chatInteraction.getMessage(),
                                        chatInteraction.getResponse(),
                                        chatInteraction.getSubmittedAt(),
                                        chatInteraction.getUsername()
                                );
                            });
                });
    }

    /**
     * Retrieves all chat messages associated with a specific username.
     *
     * @param username the username for which to retrieve chat messages
     * @return a CompletionStage containing a list of chat interaction data transfer objects
     */
    @Override
    public CompletionStage<List<ChatInteractionDTO>> getAllChatsByUsername(String username) {
        log.info("Retrieving all chat messages for user: {}", username);
        return chatInteractionDao.getAllChatsByUsername(username)
                .thenApply(chatInteractions -> {
                    log.debug("Retrieved {} chat messages for user: {}", chatInteractions.size(), username);
                    return chatInteractions.stream()
                            .map(ChatInteractionDTO::toDTO)
                            .collect(Collectors.toList());
                });
    }

    /**
     * Fetches a response from the ChatGPT API based on the user's message.
     *
     * @param userMessage the message from the user
     * @param role the role of the message (e.g., user, assistant)
     * @return a CompletionStage containing the response from ChatGPT
     */
    private CompletionStage<String> fetchChatGPTResponse(String userMessage, String role) {
        log.debug("Fetching response from ChatGPT API for message: {} and role: {}", userMessage, role);

        JsonNode requestPayload = Json.newObject()
                .put("model", "gpt-3.5-turbo")
                .putArray("messages")
                .add(Json.newObject()
                        .put("role", role)
                        .put("content", userMessage)
                );

        return wsClient.url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(requestPayload)
                .thenApplyAsync(response -> {
                    log.debug("Received response from ChatGPT API with status: {}", response.getStatus());
                    if (response.getStatus() == 200) {
                        String gptResponse = response.asJson()
                                .get("choices").get(0).get("message").get("content").asText();
                        log.debug("ChatGPT response content: {}", gptResponse);
                        return gptResponse;
                    } else {
                        log.error("Failed to fetch ChatGPT response. Status: {}, Body: {}",
                                response.getStatus(), response.getBody());
                        throw new RuntimeException("Failed to fetch ChatGPT response: " + response.getBody());
                    }
                });
    }
}
