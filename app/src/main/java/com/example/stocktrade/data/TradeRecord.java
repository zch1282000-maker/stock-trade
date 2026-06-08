package com.example.stocktrade.data;

import java.io.Serializable;
import java.util.Date;

public class TradeRecord implements Serializable {
    private long id;
    private String type;
    private String stockName;
    private String content;
    private Date fillTime;
    private String status; // pending: 待生效, effective: 生效, voided: 作废
    private String conclusion;
    private String volatilityValue;

    public TradeRecord() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getFillTime() { return fillTime; }
    public void setFillTime(Date fillTime) { this.fillTime = fillTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }

    public String getVolatilityValue() { return volatilityValue; }
    public void setVolatilityValue(String volatilityValue) { this.volatilityValue = volatilityValue; }

    public boolean canReview() {
        if (fillTime == null) return false;
        if (!"pending".equals(status)) return false;
        Date now = new Date();
        long diff = now.getTime() - fillTime.getTime();
        return diff >= 3600000;
    }

    public long getRemainingMinutes() {
        if (fillTime == null) return 0;
        Date now = new Date();
        long diff = now.getTime() - fillTime.getTime();
        if (diff >= 3600000) return 0;
        return (3600000 - diff) / 60000;
    }
}
