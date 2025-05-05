package com.example.musicrental.data;

import java.io.Serializable;

public class MessageDto implements Serializable {
    public Long    id;
    public Long    fromId;
    public Long    toId;
    public String  text;
    public String  createdAt; // RFC-3339 строка
    public String  fromEmail; // <— новое поле для почты отправителя

    public MessageDto() {}

    // Конструктор (Retrofit заполнит все поля по именам JSON)
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
