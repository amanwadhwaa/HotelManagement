package com.hotel;

public class Room {
    private int roomNumber;
    private String roomType; // Single, Double, Deluxe
    private double pricePerDay;
    private boolean available;

    public Room(int roomNumber, String roomType, double pricePerDay) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerDay = pricePerDay;
        this.available = true;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public double getPricePerDay() { return pricePerDay; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return roomNumber + "," + roomType + "," + pricePerDay + "," + available;
    }

    public static Room fromString(String line) {
        String[] parts = line.split(",");
        Room r = new Room(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]));
        r.setAvailable(Boolean.parseBoolean(parts[3]));
        return r;
    }
}