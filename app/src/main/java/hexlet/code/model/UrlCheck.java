package hexlet.code.model;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class UrlCheck {
    private long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private long urlId;
    private Timestamp createdAt;
}
