package whitespell;

import whitespell.model.WhitespellIntelligence;
import whitespell.sample.MyApplication.MyAPI;
import whitespell.sample.MyApplication.MyIntelligence;
import whitespell.model.WhitespellWebServer;

public class Main {

    public static void main(String[] args) throws Exception {

       WhitespellWebServer testApi = new MyAPI("test");
       testApi.start();

       WhitespellIntelligence intel = new MyIntelligence("test");
       intel.start();

    }

}
