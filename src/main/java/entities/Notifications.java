package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

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


    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "date")
    private String date;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "status")
    private boolean status;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "message")
    private String message;
    @OneToOne
    @JoinColumn(name = "stock_ticker", referencedColumnName = "stock_ticker")
    private Stock stockTicker;

    public Notifications( String date,  boolean status, String message, Stock stockTicker) {
        this.date = date;
        this.status = status;
        this.message = message;
        this.stockTicker = stockTicker;
    }

    public Notifications() {
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
}