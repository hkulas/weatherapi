package com.example.weatherapi.response;

public class ForecastDto {
    private double maxTempC;
    private double minTempC;
    private double totalPrecipMm;
    private double avgHumidity;
    private String condition;

    public double getMaxTempC() {
        return maxTempC;
    }

    public void setMaxTempC(double maxTempC) {
        this.maxTempC = maxTempC;
    }

    public double getMinTempC() {
        return minTempC;
    }

    public void setMinTempC(double minTempC) {
        this.minTempC = minTempC;
    }

    public double getTotalPrecipMm() {
        return totalPrecipMm;
    }

    public void setTotalPrecipMm(double totalPrecipMm) {
        this.totalPrecipMm = totalPrecipMm;
    }

    public double getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumidity(double avgHumidity) {
        this.avgHumidity = avgHumidity;
    }


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
