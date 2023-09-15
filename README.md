# Hospital Management System

## Project Overview

The Hospital Management System is a comprehensive solution designed to streamline healthcare operations. This system empowers healthcare providers to efficiently manage patients, appointments, staff, lab tests, and more. Below, you'll find an overview of the essential components and functionalities.

### Staff Module

This module contains different endpoints that allow you to perform the following operations:

- Create a new Staff profile
- Assign a role to a staff
- Manage Staff
- Update staff information
- Delete staff information from the database

### Patient Module

This module provides you with various endpoints that allow you to perform the following:

- Create a new patient profile
- Manage patients' information
- Allow patients to request to see a doctor
- Add contact address/information for a patient
- Delete a patient's profile

### Appointment Module

This module provides you with different endpoints that allow you to do the following:

- Schedule an appointment between a patient and a doctor
- Reschedule an appointment
- Update appointment status when they expire
- Manage scheduled appointments
- Cancel or update an appointment

### Notification Service

This endpoint performs the following operations:

- Send a confirmation email to the patient/user when they request to see a doctor
- Send a notification to both the patient and doctor when an appointment is scheduled
- Send a notification to both the patient and doctor when an appointment is rescheduled
- Send a reminder notification to both the patient and doctor 24 hours before their scheduled appointments.

## Technologies Used

In the development of our advanced system, we harnessed the capabilities of a carefully curated technology stack, including:

- **Java: version 17** - The heart of our project, Java 17 provided the foundation for our application's logic and functionality.

- **Spring Boot: version 3.1.1** - Spring Boot served as the framework that streamlined our development process, allowing us to create robust and efficient applications with ease.

- **Spring Security and JWT Security** - Security is paramount, and Spring Security combined with JWT (JSON Web Tokens) ensured airtight user authentication and authorization based on different roles and permissions.

- **MySQL** - For data storage and management, MySQL was our trusted relational database management system.

- **Swagger UI and Postman** - We adopted Swagger UI for clear and interactive API documentation, while Postman played a crucial role in thorough API testing and validation.

This carefully selected technology stack empowered us to create a high-performing and secure system that meets the demands of modern healthcare management.

## API Documentation

You can explore our API documentation using Postman Documenter:

[API Documentation on Postman](https://documenter.getpostman.com/view/27352130/2s9YC4Uscw)
