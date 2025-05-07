package com.example.musicrental.data;

import java.io.Serializable;

public class MessageDto implements Serializable {
    public Long    id;
    public Long    fromId;
    public Long    toId;
    public String  text;
    public String  createdAt;
    public String  fromEmail;

    public MessageDto() {}

    public MessageDto(Long id, Long fromId, Long toId,
                      String text, String createdAt,
                      String fromEmail) {
        this.id        = id;
        this.fromId    = fromId;
        this.toId      = toId;
        this.text      = text;
        this.createdAt = createdAt;
        this.fromEmail = fromEmail;
    }
}
