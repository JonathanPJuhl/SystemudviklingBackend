package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Comparator;

@Entity
@Table(name = "stocks")
@NamedQuery(name = "Stock.deleteAllRows", query = "DELETE from Stock")
public class DailyStockRating implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_ticker", length = 25)
    private String stockTicker;

    @Basic(optional = false)
    @NotNull
    @Column(name="date", length = 25)
    private String date;

    @Basic(optional = false)
    @NotNull
    @Column(name="close", length = 25)
    private double close;

    @Basic(optional = false)
    @NotNull
    @Column(name="rate", length = 25)
    private double rate;

    public DailyStockRating(@NotNull String stockTicker, @NotNull String date, @NotNull double close, @NotNull double rate) {
        this.stockTicker = stockTicker;
        this.date = date;
        this.close = close;
        this.rate = rate;
    }
    public DailyStockRating(@NotNull String stockTicker, @NotNull String date, @NotNull double close) {
        this.stockTicker = stockTicker;
        this.date = date;
        this.close = close;
    }

    public DailyStockRating() {
    }

    public String getStockTicker() {
        return stockTicker;
    }

    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public static Comparator<DailyStockRating> stockNameComparator = new Comparator<DailyStockRating>() {

        public int compare(DailyStockRating db, DailyStockRating today) {
            String database = db.getStockTicker().toUpperCase();
            String newData = today.getStockTicker().toUpperCase();

            //ascending order
            return database.compareTo(newData);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};
}
