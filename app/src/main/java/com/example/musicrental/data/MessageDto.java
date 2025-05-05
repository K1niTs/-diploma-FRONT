// app/src/main/java/com/example/musicrental/data/MessageDto.java
package com.example.musicrental.data;

import java.io.Serializable;
import java.time.Instant;

public class MessageDto implements Serializable {
    public Long    id;
    public Long    fromId;
    public Long    toId;
    public String  text;
    public String  createdAt; // RFC-3339 строка

    public MessageDto() {}
    public MessageDto(Long id, Long fromId, Long toId, String text, String createdAt) {
        this.id = id;
        this.fromId = fromId;
        this.toId   = toId;
        this.text   = text;
        this.createdAt = createdAt;
    }
}
