package com.example.musicrental.data;

import java.io.Serializable;

public class ChatRoomDto implements Serializable {
    public Long    otherId;
    public String otherEmail;
    public String  lastText;
    public String  lastAt;

    public ChatRoomDto() {}

    public ChatRoomDto(Long otherId, String otherEmail, String lastText, String lastAt) {
        this.otherId   = otherId;
        this.otherEmail = otherEmail;
        this.lastText  = lastText;
        this.lastAt    = lastAt;
    }
}
