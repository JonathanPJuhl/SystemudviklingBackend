package entities;

public class PinnedStockDto {
    String symbol;
    String date;
    double open;
    double low;
    double high;
    double close;

    public PinnedStockDto(String symbol, String date, double open, double low, double high, double close) {
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.low = low;
        this.high = high;
        this.close = close;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    @Override
    public String toString() {
        return "PinnedStockDto{" +
                "symbol='" + symbol + '\'' +
                ", date='" + date + '\'' +
                ", open=" + open +
                ", low=" + low +
                ", high=" + high +
                ", close=" + close +
                '}';
    }
}
