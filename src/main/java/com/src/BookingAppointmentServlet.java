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

@WebServlet("/BookingAppointmentServlet")
public class BookingAppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3307/hospital_management"; 
        String dbUser = "root";
        String dbPassword = "";

        // Get form parameters
        String patientName = request.getParameter("patientName");
        String doctorName = request.getParameter("doctorName");
        String appointmentDate = request.getParameter("appointmentDate");
        String appointmentTime = request.getParameter("appointmentTime");
        String patientPhone = request.getParameter("patientphone");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            // Step 1: Check if the doctor exists in the doctors table
            String checkDoctorQuery = "SELECT COUNT(*) FROM doctors WHERE doctor_name = ?";
            preparedStatement = connection.prepareStatement(checkDoctorQuery);
            preparedStatement.setString(1, doctorName);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                // Doctor exists, proceed with booking the appointment

                // Step 2: Insert the appointment details into the appointments table
                String sqlQuery = "INSERT INTO appointments (user_name, user_contact, appointment_date, appointment_time, doctor_name) VALUES (?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sqlQuery);
                preparedStatement.setString(1, patientName);
                preparedStatement.setString(2, patientPhone);
                preparedStatement.setString(3, appointmentDate);
                preparedStatement.setString(4, appointmentTime);
                preparedStatement.setString(5, doctorName);

                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    // Success - Appointment booked
                    response.sendRedirect("SuccessBooking.html");  
                } else {
                    // Failed to book appointment
                    out.println("<html><body>");
                    out.println("<h2>Failed to book appointment. Please try again later.</h2>");
                    out.println("</body></html>");
                }
            } else {
                // Doctor not found
                out.println("<html><body>");
                out.println("<h2>Error: Doctor not found in the database.</h2>");
                out.println("<p>Please check the doctor's name and try again.</p>");
                out.println("</body></html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Closing the resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
