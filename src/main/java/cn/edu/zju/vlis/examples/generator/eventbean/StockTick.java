
package cn.edu.zju.vlis.examples.generator.eventbean;

import cn.edu.zju.vlis.util.DateHelper;

public class StockTick {

    private String stockSymbol;
    private double price;
    private long time;

    public StockTick(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public StockTick(String stockSymbol, double price) {
        this.stockSymbol = stockSymbol;
        this.price = price;
    }

    public StockTick(String stockSymbol, double price, long time) {
        this.stockSymbol = stockSymbol;
        this.price = price;
        this.time = time;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public double getPrice() {
        return price;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String toString() {
        return "stockSymbol=" + stockSymbol +
                "  price=" + price + " time = " + DateHelper.getDateFromLong(time, "yyyy-MM-dd HH:mm:ss");
    }
}
