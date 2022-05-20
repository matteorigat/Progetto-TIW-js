package it.polimi.tiw.projects.beans;

import java.sql.Time;
import java.sql.Timestamp;

public class Conference {

    private int id;
    private int hostId;
    private String title;
    private Timestamp date;
    private Time duration;
    private int guests;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    public int getHostId() {
        return hostId;
    }
    public void setHostId(int hostId) {
        this.hostId = hostId;
    }


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }


    public String getStringDate() {
        String[] s = date.toLocaleString().split(", ");
        return s[0] + " " + s[1].substring(0,5);
    }
    public Timestamp getDate() {
        return date;
    }
    public void setDate(Timestamp date) {
        this.date = date;
    }


    public String getStringDuration() {
        return duration.toString().substring(0,5);
    }
    public Time getDuration() {
        return duration;
    }
    public void setDuration(Time duration) {
        this.duration = duration;
    }


    public int getGuests() {
        return guests;
    }
    public void setGuests(int guests) {
        this.guests = guests;
    }
}
