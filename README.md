Trend Hive E-Commerce Platform
Trend Hive is a modern, full-stack e-commerce application designed to provide a seamless shopping experience. It features separate dashboards for customers and retailers, allowing for a comprehensive online marketplace. This project is built with a powerful Java Spring Boot backend and a dynamic, responsive vanilla JavaScript frontend.

Key Features
Dynamic Product Catalog: Browse a wide range of products loaded directly from the backend.

Product Search & Filtering: Easily find products by name or filter through different categories like Mobiles, Beauty, and Menswear.

Detailed Product View: Click on any product to see a dedicated page with detailed information, pricing, and images.

User Authentication: Secure registration and login system for both customers and sellers.

Personalized Experience: Logged-in users are greeted by name in the header.

Shopping Cart: A fully functional cart to add, update, and remove items.

Simple Checkout: A streamlined payment page to complete orders using "Cash On Delivery."

Separate User Roles: Distinct application flows for customers (shopping) and sellers (managing products).

Tech Stack
This project uses a modern technology stack for a robust and scalable application.

Backend:

Java & Spring Boot

Spring Security (for authentication and authorization)

Spring Data JPA (Hibernate for database interaction)

MySQL Database

Frontend:

HTML5

CSS3 (with custom properties for theming)

Vanilla JavaScript (ES6+ with async/await for API calls)

Project Structure
The project is organized into a clean, modular structure to separate concerns between the frontend and backend.

/
├── backend/ (Your Spring Boot Project)
│   └── src/
│       └── main/
│           ├── java/com/project/trendhive/
│           │   ├── Controller/
│           │   ├── Service/
│           │   ├── Model/
│           │   └── ...
│           └── resources/
│               └── application.properties
│
└── frontend
    ├── CustomerDashboard/
    │   ├── index.html          (Homepage)
    │   ├── index.js
    │   ├── index.css
    │   ├── product-detail.html
    │   ├── product-detail.js
    │   ├── ...
    ├── Login/
    │   ├── customer-login.html
    │   └── seller-login.html
    ├── Register/
    └── RetailerDashboard/
        └── seller.html

Getting Started: How to Run Locally
To run this project, you need to run the backend and frontend servers separately.

1. Backend Setup (Spring Boot)
Prerequisites:

Java JDK (Version 17 or higher)

Apache Maven

MySQL Server

Instructions:

Clone the repository.

Open the backend project in your preferred Java IDE (like IntelliJ IDEA or Eclipse).

In src/main/resources/application.properties, update the database URL, username, and password to match your local MySQL setup.

Run the main Spring Boot application file. The server will start on http://localhost:8080.

2. Frontend Setup (Vanilla JS)
Prerequisites:

A simple web server. The Live Server extension for Visual Studio Code is highly recommended.

Instructions:

Open the frontend folder in Visual Studio Code.

Right-click on CustomerDashboard/index.html and select "Open with Live Server".

Your browser will open the application, likely at http://localhost:5500 or a similar address.

Important: CORS Configuration
Because the frontend and backend are running on different ports (5500 and 8080), you must enable CORS on the backend. In your Spring Boot application, add the @CrossOrigin annotation to your controllers to allow requests from your frontend's origin.

Example for a Spring Boot Controller:

@RestController
@CrossOrigin(origins = "http://localhost:5500") // Allow requests from your frontend server
public class ProductController {
    // ... your controller methods
}

Author
Vedant Morey
