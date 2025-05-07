package com.example.musicrental.data;

import java.io.Serializable;
import java.time.LocalDate;

public class BookingDto implements Serializable {

    public Long id;
    public Long userId;
    public Long instrumentId;
    public String instrumentTitle;
    public LocalDate startDate;
    public LocalDate endDate;
    public double totalCost;
    public String status;
    public String paymentUrl;

    public BookingDto() { }

    public BookingDto(Long id, Long userId, Long instrId,
                      String instrTitle,
                      LocalDate from, LocalDate to,
                      double cost, String status,
                      String paymentUrl){
        this.id = id; this.userId = userId; this.instrumentId = instrId;
        this.instrumentTitle= instrTitle;
        this.startDate = from; this.endDate = to;
        this.totalCost = cost; this.status = status;
        this.paymentUrl      = paymentUrl;
    }
}
