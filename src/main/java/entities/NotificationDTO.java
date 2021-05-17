package entities;



public class NotificationDTO {

    private String date;

    private boolean status;

    private String message;

    private String stockTicker;

    private int id;

    public NotificationDTO(String date, boolean status, String message, String stockTicker, int id) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.message = message;
        this.stockTicker = stockTicker;
    }

    public NotificationDTO() {
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

    public String getStockTicker() {
        return stockTicker;
    }

    public void setStockTicker(String stockTicker) {
        this.stockTicker = stockTicker;
    }
}
