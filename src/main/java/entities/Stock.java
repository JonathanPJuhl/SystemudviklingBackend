package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
        this.userList = new ArrayList<>();

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
                "stockTicker=\'" + stockTicker + '\'' +"value=\'"  + '\'' +
                '}';
    }

}
