package com.example.musicrental.data;

import java.io.Serializable;
import java.time.Instant;

public class ReviewDto implements Serializable {
    public Long    id;
    public Long    bookingId;
    public int     rating;          // 1..5
    public String  comment;
    public String createdAt;

    public ReviewDto() {}

    public ReviewDto(Long id, Long bookingId,
                     int rating, String comment, String createdAt){
        this.id = id; this.bookingId = bookingId;
        this.rating = rating; this.comment = comment; this.createdAt = createdAt;
    }
}
