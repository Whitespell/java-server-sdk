package whitespell.sample.MyApplication.api;

import whitespell.model.Endpoint;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         2/3/15
 *         whitespell.api
 */
public class ListUser implements Endpoint {

    //@Override
    public void register() {
       // MyAPI.getWhitespell().bindTo("/users/?", "id");
        //MyAPI.getWhitespell().bindToKeys("$userid");
    }

    @Override
    public void handleGet() {

    }

    @Override
    public void handlePost() {

    }

    @Override
    public void handleUpdate() {

    }

    @Override
    public void handleDelete() {

    }

}
