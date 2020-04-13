package edu.gatech.cs6301.Backend3.Model;

import java.util.Objects;

public class Session {
    private int id;
    private String startTime;
    private String endTime;
    private int counter;

    public Session() {
    }

    public Session(String startTime, String endTime, int counter) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.counter = counter;
    }

    public Session(int id, String startTime, String endTime, int counter) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.counter = counter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return id == session.id &&
                counter == session.counter &&
                Objects.equals(startTime, session.startTime) &&
                Objects.equals(endTime, session.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, endTime, counter);
    }

    public boolean equalsExceptId(Session session) {
        return this.startTime.equals(session.startTime) &&
                this.endTime.equals(session.endTime) &&
                this.counter == session.counter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
