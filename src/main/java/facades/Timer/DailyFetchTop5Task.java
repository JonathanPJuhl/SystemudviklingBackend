package facades.Timer;

import entities.DailyStockRating;
import entities.Stock;
import entities.StockSymbol;
import facades.StockFacade;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.EMF_Creator;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

public class DailyFetchTop5Task extends TimerTask {
    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    StockFacade facade = StockFacade.getFacadeExample(emf);
    @Override
    public void run(){

        String accessKeyMarketstack = "80f90dbc8de86858f292e8e8ff76293f";
        String symbols = "";
        List<DailyStockRating> dR = facade.findFiveHighestGainsOrDropsFromDB("ASC", "first");
        List<StockSymbol> tickers = facade.getAllStockTickers();

        for (int i = 0; i < tickers.size(); i++) {
            if (i != tickers.size() - 1) {
                symbols += tickers.get(i) + ",";
            } else {
                symbols += tickers.get(i);
            }

        }

        String data = "";
        try {
            data = facade.fetchData("https://api.marketstack.com/v1/eod/latest?access_key=" + accessKeyMarketstack + "&symbols=" + symbols);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // String data = timeCheckForOnline();
        org.json.JSONObject json = new org.json.JSONObject(data);
        JSONArray jsonArray = json.getJSONArray("data");

        ArrayList<DailyStockRating> jsonArrayTimes = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            String symb = (String) item.get("symbol");
            String date = (String) item.get("date");
            double close = (double) item.get("close");
            if (symb.contains(".")) {
                jsonArray.remove(i);
            }
            if (!symb.contains(".")) {
                jsonArrayTimes.add(new DailyStockRating(symb, date, close));
            }
        }
        //sorting the two arrays to get them in same order, before claculating the daily rate
        Collections.sort(dR, DailyStockRating.stockNameComparator);
        Collections.sort(jsonArrayTimes, DailyStockRating.stockNameComparator);
        ArrayList<DailyStockRating> jsonArrayTimesFittedForCompare = new ArrayList<>();
        ArrayList<DailyStockRating> finishedArrayForDB = new ArrayList<>();
        if (dR.size() == 0) {
            facade.addDailyStockRatingsToDB(jsonArrayTimes);
        }


        for (int i = 0; i < dR.size(); i++) {
            for (int j = 0; j < dR.size(); j++) {
                if (jsonArrayTimes.get(i).getStockTicker().equals(dR.get(j).getStockTicker())) {
                    jsonArrayTimesFittedForCompare.add(jsonArrayTimes.get(i));
                }
            }
        }

        if (dR.size() == 0) {
            for (int i = 0; i < jsonArrayTimesFittedForCompare.size(); i++) {
                double closeDB = jsonArrayTimesFittedForCompare.get(i).getClose();
                double closeToday = jsonArrayTimesFittedForCompare.get(i).getClose();
                double rate = 100 - ((closeToday / closeDB) * 100.00);
                DailyStockRating forAdding = jsonArrayTimesFittedForCompare.get(i);
                finishedArrayForDB.add(new DailyStockRating(forAdding.getStockTicker(), forAdding.getDate(), forAdding.getClose(), rate));
            }
        } else {
            for (int i = 0; i < dR.size(); i++) {

                double closeDB = dR.get(i).getClose();
                double closeToday = jsonArrayTimesFittedForCompare.get(i).getClose();
                double rate = 100 - ((closeToday / closeDB) * 100.00);
                DailyStockRating forAdding = jsonArrayTimesFittedForCompare.get(i);
                finishedArrayForDB.add(new DailyStockRating(forAdding.getStockTicker(), forAdding.getDate(), forAdding.getClose(), rate));
            }
        }
        facade.addDailyStockRatingsToDB(finishedArrayForDB);
    }
}
