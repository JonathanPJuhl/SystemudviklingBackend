package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "user_stock_noti")
@NamedQuery(name = "UserStockNoti.deleteAllRows", query = "DELETE from UserStockNoti")
public class UserStockNoti implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_stock_noti_id", length = 25)
    private String userStockID;


    @ManyToOne
    @JoinColumn(name = "user_name")
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_ticker")
    private Stock stock;

    @Column(name = "threshold_for_noti")
    private int threshold;

    @Column(name = "closing_at_time_of_setting_noti")
    private double close;

    public UserStockNoti(User user, Stock stock, int threshold, double close) {
        this.userStockID = user.getUsername()+stock.getStockTicker();
        this.user = user;
        this.stock = stock;
        this.threshold = threshold;
        this.close = close;
    }

    public UserStockNoti() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }
}
