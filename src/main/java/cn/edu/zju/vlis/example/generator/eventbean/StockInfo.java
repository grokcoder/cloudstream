package cn.edu.zju.vlis.example.generator.eventbean;

/**
 * Created by wangxiaoyi on 16/5/9.
 */
public class StockInfo {

    private String symbol;
    private String industry;

    public StockInfo(String symbol, String industry){
        this.symbol = symbol;
        this.industry = industry;
    }

    @Override
    public String toString() {
        return "StockInfo{" +
                "symbol='" + symbol + '\'' +
                ", industry='" + industry + '\'' +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}