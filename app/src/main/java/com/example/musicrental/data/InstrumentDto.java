package com.example.musicrental.data;
import java.io.Serializable;
public class InstrumentDto implements Serializable {   // ← implements Serializable
    private static final long serialVersionUID = 1L;

    public Long   id;
    public Long   ownerId;
    public String title;
    public String description;
    public double pricePerDay;
    public String category;     // ← УЖЕ было
    public String imageUrl;     // ← новинка

    public InstrumentDto() { }

    public InstrumentDto(Long id, Long ownerId,
                         String title, String description,
                         double pricePerDay, String category,
                         String imageUrl) {
        this.id          = id;
        this.ownerId     = ownerId;
        this.title       = title;
        this.description = description;
        this.pricePerDay = pricePerDay;
        this.category    = category;
        this.imageUrl    = imageUrl;
    }
}
