package controllers;

import dtos.ChatRequestDTO;
import lombok.extern.slf4j.Slf4j;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.libs.Json;
import services.ChatGPTService;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * ChatGPTController handles HTTP requests for chat interactions, providing
 * endpoints to add messages and retrieve chat history for users.
 */
@Slf4j
public class ChatGPTController extends Controller {

    private final ChatGPTService chatGPTService;
    private final FormFactory formFactory;


    /**
     * Constructs a ChatGPTController with the given ChatGPTService and FormFactory.
     *
     * @param chatGPTService the service for chat operations
     * @param formFactory the factory for form instances
     */
    @Inject
    public ChatGPTController(ChatGPTService chatGPTService, FormFactory formFactory) {
        this.chatGPTService = chatGPTService;
        this.formFactory = formFactory;
    }

    /**
     * Adds a chat message from the request, linked to a username.
     *
     * @param request the HTTP request containing the chat message
     * @return a CompletionStage with the operation result:
     *         - success with added chat data, or
     *         - bad request with validation errors.
     */
    public CompletionStage<Result> addChat(Http.Request request) {
        log.info("Received request to add chat message.");

        Form<ChatRequestDTO> form = formFactory.form(ChatRequestDTO.class).bindFromRequest(request);

        if (form.hasErrors()) {
            log.warn("Chat message form contains errors: {}", form.errorsAsJson());
            return CompletableFuture.completedFuture(badRequest(form.errorsAsJson()));
        }

        String username = request.queryString("username").orElse(null);
        if (username == null) {
            log.warn("Username is missing from the request.");
        } else {
            log.info("Adding chat message for user: {}", username);
        }

        ChatRequestDTO dto = form.get();

        return chatGPTService.addChat(dto, username)
                .thenApplyAsync(chatDTO -> {
                    log.info("Successfully added chat message for user: {}", username);
                    return ok(Json.toJson(chatDTO));
                });
    }

    /**
     * Retrieves all chat interactions for the specified user.
     *
     * @param request The HTTP request containing the 'username' query parameter.
     * @return A CompletionStage containing the Result, either the list of chat interactions
     *         in JSON format or a bad request if the username is not provided.
     */
    public CompletionStage<Result> getChatsByUser(Http.Request request) {
        log.info("Received request to get chat history for user.");

        String username = request.queryString("username").orElse(null);

        if (username == null || username.trim().isEmpty()) {
            log.warn("Username is missing or empty in the request.");
            return CompletableFuture.completedFuture(badRequest("Username is required"));
        }

        log.info("Fetching chat history for user: {}", username);

        return chatGPTService.getAllChatsByUsername(username)
                .thenApplyAsync(chatInteractions -> {
                    log.info("Successfully retrieved chat history for user: {}", username);
                    return ok(Json.toJson(chatInteractions.stream().toList()));
                });
    }
}
