package whitespell.sample.MyApplication.actions;

import whitespell.model.Action;
import whitespell.model.ActionType;
import whitespell.sample.MyApplication.MyIntelligence;

import java.io.IOException;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         2/4/15
 *         whitespell.sample.MyApplication.actions
 */
public class GenerateAdvertisementExample extends Action {

    public GenerateAdvertisementExample() {
        super("Generate Bathroom Ad", ActionType.Java, new String[]{
                "$userid"
        });
    }

    @Override
    protected void execute() {

        Session session = MyIntelligence.getSession(this.getSessionId());

        AdConnection adCon = session.getConnection("/my_ad?ses_id=abc");

        final int productClicks = 0;

        adCon.addEventHandler("product_clicked", new SessionEventHandler() {
            @Override
            private void handle() {
                productClicks++;
            }

        });
        // generate ad and

        String exampleGeneratedAd = "{ 'welcome_message' : 'Buenos Dias, Raquelle Gomez', 'products' : [ { 'product_name' : 'Soap', 'product_price' : '$1.50' }, { 'product_name' : 'Body Wash', 'product_price' : '$3.95' }, { 'product_name' : 'Shampoo', 'product_price' : '$15' }, { 'product_name' : 'Conditioner', 'product_price' : '$25' }, ]";

        try {
            this.getContext().getResponse().getWriter().write(exampleGeneratedAd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * Has the ad been open for more than 30 seconds? Let's show the new prices.
         */
        if (adCon.isOpen() && adCon.getOpenTimeMS() > 30_000 && productClicks == 0) {
            String newAd = "{ 'welcome_message' : 'Buenos Dias, Raquelle Gomez', 'products' : [ { 'product_name' : 'Soap', 'product_price' : '$1.20' }, { 'product_name' : 'Body Wash', 'product_price' : '$3.55' }, { 'product_name' : 'Shampoo', 'product_price' : '$12' }, { 'product_name' : 'Conditioner', 'product_price' : '$22' }, ]";
            adCon.sendEvent("VariableUpdates", newAd);
        }

    }

    @Override
    protected boolean doForceExecute() {
        return false;
    }

    @Override
    protected long forceExecuteCheckTime() {
        return 0;
    }

    @Override
    protected long cacheTime() {
        return 0;
    }
}
