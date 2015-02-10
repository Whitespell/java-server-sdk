package whitespell.model;

public abstract class WhitespellWebServer {
    private final String apiKey;

    public WhitespellWebServer(String apiKey) {
        this.apiKey = apiKey;
    }

    public abstract void start() throws Exception;
}
