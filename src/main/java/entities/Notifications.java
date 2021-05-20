package entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notifications")
@NamedQuery(name = "Notifications.deleteAllRows", query = "DELETE from Notifications")
public class Notifications implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "message_id", length = 25)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageID;

    @ManyToMany(mappedBy = "notiList")
    private List<User> userList;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "date")
    @Expose
    private String date;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "status")
    @Expose
    private boolean status;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "message")
    @Expose
    private String message;
    @OneToOne
    @JoinColumn(name = "stock_ticker", referencedColumnName = "stock_ticker")
    @Expose
    private Stock stockTicker;

    public Notifications( String date,  boolean status, String message, Stock stockTicker) {
        this.date = date;
        this.status = status;
        this.message = message;
        this.stockTicker = stockTicker;
        this.userList = new ArrayList<>();
    }

    public Notifications() {
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public int getMessageID() {
        return messageID;
    }
    public void addUser(User user){
        userList.add(user);
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Stock getStockTicker() {
        return stockTicker;
    }

    public void setStockTicker(Stock stockTicker) {
        this.stockTicker = stockTicker;
    }

    @Override
    public String toString() {
        return "Notifications{" +
                "messageID=" + messageID +
                ", userList=" + userList +
                ", date='" + date + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", stockTicker=" + stockTicker +
                '}';
    }
}