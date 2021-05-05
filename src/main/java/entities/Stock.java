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
@Table(name = "stocks")
@NamedQuery(name = "Stock.deleteAllRows", query = "DELETE from Stock")
public class Stock implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "stock_ticker", length = 25)
    private String stockTicker;


    @ManyToMany(mappedBy = "stockList")
    private List<User> userList;

    public Stock() {
    }

    public Stock(String stockTicker) {
        this.stockTicker = stockTicker;
    }

    public String getStockTicker() {
        return stockTicker;
    }

    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stockTicker=\'" + stockTicker + '\'' +
                '}';
    }
//TODO Change when password is hashed

}
