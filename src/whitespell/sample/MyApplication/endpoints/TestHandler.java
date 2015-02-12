package whitespell.sample.MyApplication.endpoints;

import whitespell.logic.ApiInterface;
import whitespell.logic.RequestContext;

import java.io.IOException;

/**
 * ListSightingsHandler lists all tag sightings.
 * // TODO(akalachman): Testing only!
 */
public class TestHandler implements ApiInterface {

    @Override
    public void call(RequestContext context) throws IOException {
        context.getResponse().getWriter().write("test: " + context.getUrlVariables().get("id"));
    }
}
