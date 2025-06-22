package com.adventure.adventurebooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Sport sport;

    private LocalDate bookingDate;
    private int numberOfPeople;
    private double totalPrice;
    private String status; // e.g. "CONFIRMED"
    private String slotTime; // e.g. "10:00 AM - 11:00 AM"

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    public static class BookingBuilder {
        private Long id;
        private User user;
        private Sport sport;
        private LocalDate bookingDate;
        private int numberOfPeople;
        private double totalPrice;
        private String status;
        private String slotTime;

        BookingBuilder() {
        }

        public BookingBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookingBuilder user(User user) {
            this.user = user;
            return this;
        }

        public BookingBuilder sport(Sport sport) {
            this.sport = sport;
            return this;
        }

        public BookingBuilder bookingDate(LocalDate bookingDate) {
            this.bookingDate = bookingDate;
            return this;
        }

        public BookingBuilder numberOfPeople(int numberOfPeople) {
            this.numberOfPeople = numberOfPeople;
            return this;
        }

        public BookingBuilder totalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public BookingBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BookingBuilder slotTime(String slotTime) {
            this.slotTime = slotTime;
            return this;
        }

        public Booking build() {
            return new Booking(this.id, this.user, this.sport, this.bookingDate, this.numberOfPeople, this.totalPrice, this.status, this.slotTime);
        }

        public String toString() {
            return "Booking.BookingBuilder(id=" + this.id + ", user=" + this.user + ", sport=" + this.sport + ", bookingDate=" + this.bookingDate + ", numberOfPeople=" + this.numberOfPeople + ", totalPrice=" + this.totalPrice + ", status=" + this.status + ", slotTime=" + this.slotTime + ")";
        }
    }
}
