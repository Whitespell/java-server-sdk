package whitespell.sample.MyApplication;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import whitespell.sample.MyApplication.api.TestHandler;
import whitespell.model.WhitespellWebServer;
import whitespell.logic.ApiDispatcher;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         2/4/15
 *         whitespell.sample.MyApplication
 */
public class MyAPI extends WhitespellWebServer {

    public MyAPI(String apiKey) {
        super(apiKey);
    }

    @Override
    public void start() throws Exception{

        Server server = new Server(8080);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        ApiDispatcher dispatcher = new ApiDispatcher();
        dispatcher.addHandler(ApiDispatcher.RequestType.GET, new TestHandler(), "/test/?", "id");

        handler.addServletWithMapping(new ServletHolder(dispatcher), "/*");

        server.start();
        server.join();
    }

}
