package controllers;

import lombok.extern.slf4j.Slf4j;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.*;
import javax.inject.Inject;

import static play.mvc.Results.*;

/**
 * Controller for handling user login functionalities.
 */
@Slf4j
public class LoginController {

    private final FormFactory formFactory;

    @Inject
    public LoginController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    /**
     * Renders the login page.
     *
     * @return Result with the login page.
     */
    public Result index() {
        log.info("Rendering login page.");
        return ok(views.html.login.render(null));
    }

    /**
     * Processes user login, validating credentials.
     *
     * @param request The HTTP request with user credentials.
     * @return Result indicating login outcome.
     */
    public Result login(Http.Request request) {
        log.info("Processing login request.");

        DynamicForm requestData = formFactory.form().bindFromRequest(request);
        String usernameField = requestData.get("username");
        String passwordField = requestData.get("password");

        log.debug("Login attempt with username: {}", usernameField);

        if (isNullOrEmpty(usernameField)) {
            log.warn("Login attempt failed: Username is missing.");
            return badRequest(views.html.login.render("Username is required."));
        }

        if (isNullOrEmpty(passwordField)) {
            log.warn("Login attempt failed: Password is missing.");
            return badRequest(views.html.login.render("Password is required."));
        }

        log.info("Login successful for user: {}", usernameField);
        return redirect("/eduterium-chatbot");
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param str The string to check.
     * @return true if null or empty; false otherwise.
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Logs out the user and redirects to the login page.
     *
     * @return Result redirecting to the login page.
     */
    public Result logout() {
        log.info("User logged out, redirecting to login page.");
        return redirect(routes.LoginController.index()).withNewSession();
    }
}
