package SVG;

import entities.DailyStockRating;

import java.util.ArrayList;


public class ChartMaker implements Draw {

    @Override
    public String draw(ArrayList<DailyStockRating> datesAndClosingValues) {
        String chart = "<svg version=\"1.2\"  class=\"graph\" aria-labelledby=\"title\" role=\"img\">\n" +
                "<title id=\"title\">A line chart showing some information</title>\n" +
                "<g class=\"grid x-grid\" id=\"xGrid\">\n" +
                "<line x1=\"90\" x2=\"90\" y1=\"5\" y2=\"371\"></line>\n" +
                "</g>\n" +
                "<g class=\"grid y-grid\" id=\"yGrid\">\n" +
                "<line x1=\"90\" x2=\"705\" y1=\"370\" y2=\"370\"></line>\n" +
                "</g>\n";

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();
        int i = 0;
        while (i<5){


            dates.add(datesAndClosingValues.get(i).getDate().substring(0,10));
            prices.add(datesAndClosingValues.get(i).getClose());
            i+=1;
        }

        chart+=makeXAxis(dates);
        chart+=makeYAxis(prices);
        chart+=makeDataSet(prices);
        System.out.println(chart);
        return chart;
    }
    public String makeXAxis(ArrayList<String> dates){
        int x = 684; // start at the end of x-axis, since our arraylist is backwards
        String dateLine = "<g class=\"labels x-labels\">";
        for(int i = 0; i<dates.size(); i++){
            dateLine+="<text x=\""+x+"\" y=\"400\">"+dates.get(i)+"</text>";
            x-=146; //going back one day out of 5
        }
        dateLine+="<text x=\"400\" y=\"440\" class=\"label-title\">Date</text>\n</g>\n";
        return dateLine;
    }
    public String makeYAxis(ArrayList<Double> closePrices){
        double y = 10; //Startingpoint on y-axis
        int shownValue = 1000; // maxvalue at top of chart
        String moneyLine = "<g class=\"labels y-labels\">\n";
        for(int i = 0; i<11; i++){
            moneyLine+="<text x=\"80\" y=\""+y+"\">"+shownValue+"</text>";
            y+=37.1; //Moving "100" down the y-axis
            if(!(shownValue == 0)) {
                shownValue -= 100;
            }
        }
        moneyLine+="<text x=\"50\" y=\"200\" class=\"label-title\">Price</text>\n</g>\n";
        return moneyLine;
    }

public String makeDataSet(ArrayList<Double> closePrices){

        //Value for each step up (1000/371)
    double starting = 371; //represents 0 on the y-axis
        double cy = 0.371; //represents a single step (1) on the chart
        String dataLine = "<g class=\"data\" data-setname=\"Our first data set\">\n"+
                "<circle cx=\"90\" cy=\""+(starting-closePrices.get(0)*cy)+"\" data-value=\""+closePrices.get(0)+"\" r=\"4\"></circle>\n" +
                "<circle cx=\"240\" cy=\""+(starting-closePrices.get(1)*cy)+"\" data-value=\""+closePrices.get(1)+"\" r=\"4\"></circle>\n" +
                "<circle cx=\"388\" cy=\""+(starting-closePrices.get(2)*cy)+"\" data-value=\""+closePrices.get(2)+"\" r=\"4\"></circle>\n" +
                "<circle cx=\"531\" cy=\""+(starting-closePrices.get(3)*cy)+"\" data-value=\""+closePrices.get(3)+"\" r=\"4\"></circle>\n" +
                "<circle cx=\"677\" cy=\""+(starting-closePrices.get(4)*cy)+"\" data-value=\""+closePrices.get(4)+"\" r=\"4\"></circle>\n" +
                "</g>\n" +
                "</svg>";
        return dataLine;
    }
}
