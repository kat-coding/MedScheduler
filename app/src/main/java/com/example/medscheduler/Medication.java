package com.example.medscheduler;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Medication {
    private String id;
    private String name;
    private LocalDateTime lastTaken;
    private LocalDateTime nextAvailable;
    private long hoursBetween;

    public Medication(String id, String name, LocalDateTime lastTaken, LocalDateTime nextAvailable, int hoursBetween) {
        this.id = id;
        this.name = name;
        this.lastTaken = lastTaken;
        this.hoursBetween = hoursBetween;
        this.nextAvailable = nextAvailable;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastTaken() {
        return lastTaken;
    }

    public void setLastTaken(LocalDateTime lastTaken) {
        this.lastTaken = lastTaken;
    }

    public LocalDateTime getNextAvailable() {
        return nextAvailable;
    }

    public void setNextAvailable(LocalDateTime nextAvailable) {
        this.nextAvailable = nextAvailable;
    }

    public long getHoursBetween() {
        return hoursBetween;
    }

    public void setHoursBetween(long hoursBetween) {
        this.hoursBetween = hoursBetween;
    }

    @Override
    public String toString() {
        String formattedDateTime;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (nextAvailable.isBefore(LocalDateTime.now())) {
                formattedDateTime = "Now";
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
                formattedDateTime = nextAvailable.format(formatter);
            }
        }else{
            formattedDateTime = nextAvailable.toString();
        }
        return  name + '\n' +
                "Next dose available: " + formattedDateTime;
    }
}
