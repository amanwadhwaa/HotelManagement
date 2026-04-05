package com.hotel;

public class Customer {
    private String name;
    private String contactNumber;
    private int roomNumber;

    public Customer(String name, String contactNumber, int roomNumber) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.roomNumber = roomNumber;
    }

    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }
    public int getRoomNumber() { return roomNumber; }

    @Override
    public String toString() {
        return name + "," + contactNumber + "," + roomNumber;
    }

    public static Customer fromString(String line) {
        String[] parts = line.split(",");
        return new Customer(parts[0], parts[1], Integer.parseInt(parts[2]));
    }
}