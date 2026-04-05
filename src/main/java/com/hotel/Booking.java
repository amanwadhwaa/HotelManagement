package com.hotel;

import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private String customerName;
    private int roomNumber;
    private String roomType;
    private double pricePerDay;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private boolean active;

    public Booking(int bookingId, String customerName, int roomNumber, String roomType, double pricePerDay, LocalDate checkIn, LocalDate checkOut) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerDay = pricePerDay;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.active = true;
    }

    public int getBookingId() { return bookingId; }
    public String getCustomerName() { return customerName; }
    public int getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getPricePerDay() { return pricePerDay; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public double getTotalBill() {
        long days = checkIn.until(checkOut).getDays();
        return days * pricePerDay;
    }

    @Override
    public String toString() {
        return bookingId + "," + customerName + "," + roomNumber + "," + roomType + "," + pricePerDay + "," + checkIn + "," + checkOut + "," + active;
    }

    public static Booking fromString(String line) {
        String[] parts = line.split(",");
        Booking b = new Booking(
            Integer.parseInt(parts[0]),
            parts[1],
            Integer.parseInt(parts[2]),
            parts[3],
            Double.parseDouble(parts[4]),
            LocalDate.parse(parts[5]),
            LocalDate.parse(parts[6])
        );
        b.setActive(Boolean.parseBoolean(parts[7]));
        return b;
    }
}
