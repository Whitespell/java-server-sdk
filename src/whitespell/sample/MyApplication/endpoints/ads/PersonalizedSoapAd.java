package whitespell.sample.MyApplication.endpoints.ads;

import whitespell.logic.RequestContext;
import whitespell.model.UnitProcess;
import whitespell.model.Unit;
import whitespell.model.Session;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         2/15/15
 *         whitespell.sample.MyApplication.endpoints
 */
public class PersonalizedSoapAd {

    public static void call(RequestContext context, Session session, Unit ad) {

        //rest request to API to get products


        ad.setProcess(new UnitProcess(ad, session) {
            private int productClicks = 0;
            @Override
            protected void bindHandlers() {
                addEventHandler("product clicked", new SessionEventHandler() {
                    @Override
                    protected void handle() {
                        productsClicked++;
                    }
                });
            }

            private int productsClicked
                    ,
                    adChanges = 0;

            @Override
            public void process() {
                if (ad.getClient().isChannelOpen() && (getParentSession().getStartTime() + 30_000 > System.currentTimeMillis()) && productClicks == 0 && adChanges == 0) {
                    String newAd = "{ 'welcome_message' : 'Buenos Dias, Raquelle Gomez', 'products' : [ { 'product_name' : 'Soap', 'product_price' : '$1.20' }, { 'product_name' : 'Body Wash', 'product_price' : '$3.55' }, { 'product_name' : 'Shampoo', 'product_price' : '$12' }, { 'product_name' : 'Conditioner', 'product_price' : '$22' }, ]";
                    ad.getClient().sendEvent("var", newAd);
                }
            }

        });

        //prepare future ads and add them to the Session's ad queue

    }
}
