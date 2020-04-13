package edu.gatech.cs6301.Backend3.Model;

import java.util.Objects;

public class ReportSession {
    private String startingTime;
    private String endingTime;
    private Double hoursWorked;

    public ReportSession() {
    }

    public ReportSession(String startingTime, String endingTime, Double hoursWorked) {
        this.startingTime = startingTime;
        this.endingTime = endingTime;
        this.hoursWorked = hoursWorked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportSession that = (ReportSession) o;
        return Objects.equals(startingTime, that.startingTime) &&
                Objects.equals(endingTime, that.endingTime) &&
                Objects.equals(hoursWorked, that.hoursWorked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingTime, endingTime, hoursWorked);
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
}
