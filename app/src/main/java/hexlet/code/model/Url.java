package hexlet.code.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public final class Url {
    private long id;
    private String name;
    private Timestamp createdAt;
    private int lastStatusCode;
    private Timestamp lastCheckAt;

    public Url(String name) {
        createdAt = Timestamp.valueOf(LocalDateTime.now());
        this.name = name;
    }

    public Url(String name, Timestamp createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(createdAt);
        } else {
            return "";
        }
    }

    public int getLastStatusCode() {
        return lastStatusCode;
    }

    public void setLastStatusCode(int lastStatusCode) {
        this.lastStatusCode = lastStatusCode;
    }

    public Timestamp getLastCheckAt() {
        return lastCheckAt;
    }
    public String getFormattedLastCheckAt() {
        if (lastCheckAt != null) {
            return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(lastCheckAt);
        } else {
            return "";
        }
    }

    public void setLastCheckAt(Timestamp lastCheckAt) {
        this.lastCheckAt = lastCheckAt;
    }
}
