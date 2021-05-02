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
    @Column(name = "stock_id", length = 25)
    private int stockId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "stock_name")
    private String stockName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "stock_link")
    private String stockLink;

    @ManyToMany(mappedBy = "stockList")
    private List<User> userList;

    public Stock() {
    }

    public Stock( String stockName, String stockLink) {
        this.stockName = stockName;
        this.stockLink = stockLink;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockLink() {
        return stockLink;
    }

    public void setStockLink(String stockLink) {
        this.stockLink = stockLink;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    //TODO Change when password is hashed

}
