package controllers;

import play.filters.csrf.CSRF;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * CsrfController manages CSRF token retrieval for secure HTTP requests.
 */
@Singleton
public class CsrfController extends Controller {

    /**
     * Constructs a CsrfController instance.
     */
    @Inject
    public CsrfController() {}

    /**
     * Retrieves the CSRF token for the current request.
     *
     * @param request The HTTP request containing session data.
     * @return A Result object containing the CSRF token in JSON format.
     */
    public Result getCsrfToken(Http.Request request) {
        String csrfToken = CSRF.getToken(request).map(CSRF.Token::value).orElse("No CSRF token present");

        return ok("{\"csrfToken\": \"" + csrfToken + "\"}").as("application/json");
    }
}
