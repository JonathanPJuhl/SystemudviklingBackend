package entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "stocks_symbols")
@NamedQuery(name = "StockSymbol.deleteAllRows", query = "DELETE from StockSymbol")
public class StockSymbol implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_ticker", length = 25)
    private String symbol;


    public StockSymbol() {
    }

    public StockSymbol(@NotNull String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "StockSymbol{" +
                "symbol='" + symbol + '\'' +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

}
