# Wadhwa's & Wadhwa's Hotel Management System

A JavaFX-based Hotel Management System built as part of a university lab project.

## Features

### Core
- 🏨 **Room Management** — Add, view, filter, and delete rooms (Single, Double, Deluxe, Suite)
- 👤 **Customer Management** — Auto-populated guest registry linked to bookings
- 📅 **Booking & Checkout** — Book available rooms, prevent double booking, checkout to release rooms
- 💰 **Billing Management** — View total bills per booking, print invoices with full breakdown

### Extra
- 💾 **File Persistence** — All data saved to `.txt` files (rooms.txt, customers.txt, bookings.txt)
- 🎨 **Custom Dark Theme** — Styled with CSS, dark dashboard aesthetic
- 🔨 **Maven Build** — Managed with Apache Maven
- 🖼️ **Scene Builder / FXML** — UI built using FXML with Scene Builder
- 📊 **Stat Cards** — Live room stats (Total, Available, Occupied) on the dashboard

## Tech Stack
- Java 25
- JavaFX 21
- Apache Maven 3.9.14
- Scene Builder (FXML)
- File I/O for persistence

## Project Structure
```
src/
├── main/
│   ├── java/com/hotel/
│   │   ├── MainApp.java
│   │   ├── Room.java
│   │   ├── Customer.java
│   │   ├── Booking.java
│   │   ├── DataStore.java
│   │   ├── RoomViewController.java
│   │   ├── CustomerViewController.java
│   │   ├── BookingViewController.java
│   │   └── BillingViewController.java
│   └── resources/com/hotel/
│       ├── RoomView.fxml
│       ├── CustomerView.fxml
│       ├── BookingView.fxml
│       ├── BillingView.fxml
│       └── styles.css
```

## How to Run
1. Make sure Java and Maven are installed
2. Clone the repo
3. Run `mvn javafx:run`

## Marking Criteria Coverage
| Criteria | Status |
|---|---|
| Basic System (Room, Customer, Booking) | ✅ |
| GUI Design & Additional Features | ✅ |
| File Persistence | ✅ |
| Maven | ✅ |
| Scene Builder / FXML | ✅ |
| Billing Management | ✅ |
| Custom Styling | ✅ |
