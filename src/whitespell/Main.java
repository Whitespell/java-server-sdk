package whitespell;

import whitespell.logic.UnitHandler;
import whitespell.model.Unit;
import whitespell.model.WhitespellIntelligence;
import whitespell.sample.MyApplication.MyEndpoints;
import whitespell.sample.MyApplication.MyIntelligence;
import whitespell.model.WhitespellWebServer;

public class Main {

    public static void main(String[] args) throws Exception {

        UnitHandler.putUnit("a7k39gn3oajg9-gkr938g-guau483g", new Unit("a7k39gn3oajg9-gkr938g-guau483g"));
        UnitHandler.putUnit("b7k39gn3oajg9-gkr938g-guau483g", new Unit("b7k39gn3oajg9-gkr938g-guau483g"));

       WhitespellWebServer testApi = new MyEndpoints("test");
       testApi.startAPI();
       testApi.startWebsockets();

       WhitespellIntelligence intel = new MyIntelligence("test");
       intel.start();



    }

}
