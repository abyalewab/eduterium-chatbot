package controllers;

import play.mvc.*;

/**
 * HomeController manages HTTP requests for the home page.
 * It renders the home page directly without session checks.
 */
public class HomeController extends Controller {

    /**
     * Renders the home page.
     *
     * @return A Result object containing the rendered HTML page.
     */
    public Result index() {
        return ok(views.html.home.render());
    }
}
