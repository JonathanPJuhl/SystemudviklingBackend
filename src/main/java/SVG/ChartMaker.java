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
        int x = 684;
        String dateLine = "<g class=\"labels x-labels\">";
        for(int i = 0; i<dates.size(); i++){
            dateLine+="<text x=\""+x+"\" y=\"400\">"+dates.get(i)+"</text>";
            x-=146;
        }
        dateLine+="<text x=\"400\" y=\"440\" class=\"label-title\">Date</text>\n</g>\n";
        return dateLine;
    }
    public String makeYAxis(ArrayList<Double> closePrices){
        double y = 10;
        int shownValue = 1000;
        String moneyLine = "<g class=\"labels y-labels\">\n";
        for(int i = 0; i<11; i++){
            moneyLine+="<text x=\"80\" y=\""+y+"\">"+shownValue+"</text>";
            y+=37.1;
            if(!(shownValue == 0)) {
                shownValue -= 100;
            }
        }
        moneyLine+="<text x=\"50\" y=\"200\" class=\"label-title\">Price</text>\n</g>\n";
        return moneyLine;
    }

public String makeDataSet(ArrayList<Double> closePrices){

        //Value for each step up (1000/371)
    double starting = 371;
        double cy = 0.371;
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
/*

  <svg version="1.2"  class="graph" aria-labelledby="title" role="img">
  <title id="title">A line chart showing some information</title>
<g class="grid x-grid" id="xGrid">
  <line x1="90" x2="90" y1="5" y2="371"></line>
</g>
<g class="grid y-grid" id="yGrid">
  <line x1="90" x2="705" y1="370" y2="370"></line>
</g>
  <g class="labels x-labels">
  <text x="100" y="400">2008</text>
  <text x="246" y="400">2009</text>
  <text x="392" y="400">2010</text>
  <text x="538" y="400">2011</text>
  <text x="684" y="400">2012</text>
  <text x="400" y="440" class="label-title">Year</text>
</g>
<g class="labels y-labels">
  <text x="80" y="15">15</text>
  <text x="80" y="131">10</text>
  <text x="80" y="248">5</text>
  <text x="80" y="373">0</text>
  <text x="50" y="200" class="label-title">Price</text>
</g>
<g class="data" data-setname="Our first data set">
  <circle cx="90" cy="192" data-value="7.2" r="4"></circle>
  <circle cx="240" cy="141" data-value="8.1" r="4"></circle>
  <circle cx="388" cy="179" data-value="7.7" r="4"></circle>
  <circle cx="531" cy="200" data-value="6.8" r="4"></circle>
  <circle cx="677" cy="104" data-value="6.7" r="4"></circle>
</g>
</svg>*/