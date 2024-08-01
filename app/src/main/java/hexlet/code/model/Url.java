package hexlet.code.model;

import java.sql.Timestamp;

public class Url {
    private int id;
    private String name;
    private Timestamp createdAt;

    public Url (String name) {
        this.name = name;
    }

    public void setId (int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
