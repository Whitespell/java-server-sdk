package whitespell.sample.MyApplication.endpoints;

import whitespell.logic.RequestContext;
import whitespell.model.Unit;
import whitespell.model.UnitEndpoint;
import whitespell.model.Session;
import whitespell.sample.MyApplication.endpoints.ads.PersonalizedSoapAd;

/**
 * @author Pim de Witte(wwadewitte), Whitespell LLC
 *         2/15/15
 *         whitespell.sample.MyApplication.endpoints
 */

/**
 * The UnitDistributor distributed the ad to the proper handler based on the unique ad ID.
 */
public class UnitDistributor extends UnitEndpoint {
    @Override
    public void handleDistribution(RequestContext context, Unit ad, Session session) {

        switch(ad.getUUID()) {
            case "a7k39gn3oajg9-gkr938g-guau483g" : {
                PersonalizedSoapAd.call(context, session, ad);
                return;
            }
            case "b7k39gn3oajg9-gkr938g-guau483g" : {

                return;
            }
        }
    }
}
