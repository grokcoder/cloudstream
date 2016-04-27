/**************************************************************************************
 * Copyright (C) 2006-2015 EsperTech Inc. All rights reserved.                        *
 * http://www.espertech.com/esper                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package cn.edu.zju.vlis.xiaoyi.esper.eventbean;

public class StockTick {
    private String stockSymbol;
    private double price;

    public StockTick(String stockSymbol, double price) {
        this.stockSymbol = stockSymbol;
        this.price = price;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {
        return "stockSymbol=" + stockSymbol +
                "  price=" + price;
    }
}