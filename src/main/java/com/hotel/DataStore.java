package com.hotel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataStore {

    private static final String ROOMS_FILE = "rooms.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String BOOKINGS_FILE = "bookings.txt";

    // ---- ROOMS ----
    public static List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ROOMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) rooms.add(Room.fromString(line));
            }
        } catch (IOException e) {
            // file doesn't exist yet, return empty list
        }
        return rooms;
    }

    public static void saveRooms(List<Room> rooms) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms) pw.println(r.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---- CUSTOMERS ----
    public static List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) customers.add(Customer.fromString(line));
            }
        } catch (IOException e) {
            // file doesn't exist yet
        }
        return customers;
    }

    public static void saveCustomers(List<Customer> customers) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CUSTOMERS_FILE))) {
            for (Customer c : customers) pw.println(c.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---- BOOKINGS ----
    public static List<Booking> loadBookings() {
        List<Booking> bookings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) bookings.add(Booking.fromString(line));
            }
        } catch (IOException e) {
            // file doesn't exist yet
        }
        return bookings;
    }

    public static void saveBookings(List<Booking> bookings) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking b : bookings) pw.println(b.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---- BOOKING ID ----
    public static int getNextBookingId(List<Booking> bookings) {
        return bookings.stream().mapToInt(Booking::getBookingId).max().orElse(0) + 1;
    }
}
