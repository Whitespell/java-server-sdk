package whitespell.model;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         1/20/15
 *         whitespell.model
 */
public interface Endpoint {

    /**
     * Endpoint is placed on the Whitespell Yoda server and handles incoming HTTP requests. Endpoints are dispatched using the API Dispatcher
     */
    public abstract void call();
    public abstract void websocket();
    public abstract void process();
}
