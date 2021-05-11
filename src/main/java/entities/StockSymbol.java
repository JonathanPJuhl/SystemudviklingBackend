package entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

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
    //TODO Change when password is hashed

}
