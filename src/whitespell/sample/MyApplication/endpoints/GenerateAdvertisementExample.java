package whitespell.sample.MyApplication.endpoints;

import whitespell.logic.RequestContext;
import whitespell.model.Action;
import whitespell.model.Endpoint;
import whitespell.model.ActionType;
import whitespell.sample.MyApplication.MyIntelligence;
import java.io.IOException;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         2/4/15
 *         whitespell.sample.MyApplication.actions
 */
public class GenerateAdvertisementExample extends Endpoint {

    private int productClicks;

    @Override
    protected void call(RequestContext context) {

        String exampleGeneratedAd = "{ 'welcome_message' : 'Buenos Dias, Raquelle Gomez', 'products' : [ { 'product_name' : 'Soap', 'product_price' : '$1.50' }, { 'product_name' : 'Body Wash', 'product_price' : '$3.95' }, { 'product_name' : 'Shampoo', 'product_price' : '$15' }, { 'product_name' : 'Conditioner', 'product_price' : '$25' }, ]";

        try {
            context.getResponse().getWriter().write(exampleGeneratedAd);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void websocket() {
        this.getWebsocket().addEventHandler("product_clicked", new SessionEventHandler() {
            @Override
            private void handle() {
                productClicks++;
            }

        });
    }

    @Override
    protected void process() {

        WebsocketConnection adCon = null;
        if(adcon == null) {
            adCon = this.getWebsocket();
        } else {
            if (adCon.isOpen() && adCon.getOpenTimeMS() > 30_000 && productClicks == 0) {
                String newAd = "{ 'welcome_message' : 'Buenos Dias, Raquelle Gomez', 'products' : [ { 'product_name' : 'Soap', 'product_price' : '$1.20' }, { 'product_name' : 'Body Wash', 'product_price' : '$3.55' }, { 'product_name' : 'Shampoo', 'product_price' : '$12' }, { 'product_name' : 'Conditioner', 'product_price' : '$22' }, ]";
                adCon.sendEvent("VariableUpdates", newAd);
            }
        }
    }

}
