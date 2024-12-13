package com.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AdminViewHospital")
public class AdminViewHospital extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3307/hospital_management?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

       
        out.println("<html><head><title>Admin View Hospital Bookings</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #daeef7; color: #333; }");
        out.println("h2 { color: #1badef; text-align: center; font-size: 32px; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; margin-top: 20px; }");
        out.println("table { width: 90%; margin: 30px auto; border-collapse: collapse; box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2); font-size: 18px; }");
        out.println("th, td { padding: 15px; text-align: left; }");
        out.println("th { background-color: #9986ef; color: white; font-size: 18px; text-transform: uppercase; }");
        out.println("td { background-color: white; color: #333; }");
        out.println("tr:nth-child(even) td { background-color: #f9f9f9; }");
        out.println("tr:hover td { background-color: #f1e8ff; }");
        out.println(".back-button { display: block; width: 150px; margin: 20px auto; text-align: center; padding: 12px 20px; background-color: #1badef; color: white; text-decoration: none; border-radius: 5px; transition: background-color 0.3s ease, transform 0.3s ease; font-size: 16px; }");
        out.println(".back-button:hover { background-color: #4a206a; transform: scale(1.05); }");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<h2>Booking Details</h2>");
        out.println("<table><tr><th>User Name</th><th>Contact</th><th>Appointment Date</th><th>Appointment Time</th><th>Doctor Name</th></tr>");

        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // SQL query to fetch appointment data
            String sql = "SELECT user_name, user_contact, appointment_date, appointment_time, doctor_name FROM appointments";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Iterate over the result set and display each record in the table
            while (rs.next()) {
                String userName = rs.getString("user_name");
                String userContact = rs.getString("user_contact");
                String appointmentDate = rs.getString("appointment_date");
                String appointmentTime = rs.getString("appointment_time");
                String doctorName = rs.getString("doctor_name");

                out.println("<tr><td>" + userName + "</td><td>" + userContact + "</td><td>" + appointmentDate + "</td><td>" + appointmentTime + "</td><td>" + doctorName + "</td></tr>");
            }

            // Close resources
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace(out);
            out.println("<p>Error retrieving booking details.</p>");
        }

        out.println("</table>");
        out.println("<a href='homeadmin.html' class='back-button'>Back to Home</a>");
        out.println("</body></html>");
    }
}
