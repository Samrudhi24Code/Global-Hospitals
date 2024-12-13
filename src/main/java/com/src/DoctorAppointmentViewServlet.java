package com.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DoctorAppointmentViewServlet")
public class DoctorAppointmentViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String JDBC_URL = "jdbc:mysql://localhost:3307/hospital_management?useSSL=false&serverTimezone=UTC";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = ""; 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            // Load MySQL JDBC Driver
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                out.println("<p>JDBC Driver not found.</p>");
                return;
            }

            // Retrieve doctor name from request
            String doctorName = request.getParameter("doctor_name");

            out.println("<html><head>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #f3f4f6; color: #333; margin: 0; padding: 0; }");
            out.println("h2 { color: #2c3e50; text-align: center; margin-top: 20px; }");
            out.println(".centered-form { display: flex; justify-content: center; align-items: center; height: 100vh; flex-direction: column; }");
            out.println(".form-container { display: block; width: 400px; padding: 40px; border-radius: 8px; background-color: #ffffff; box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1); text-align: center; }");
            out.println(".hospital-name { font-size: 28px; font-weight: bold; color: #2c3e50; margin-bottom: 20px; }");
            out.println("input[type='text'], input[type='submit'] { width: 90%; padding: 12px; margin: 10px 0; border-radius: 4px; border: 1px solid #ddd; font-size: 16px; }");
            out.println("input[type='submit'] { background-color: #3498db; color: #fff; cursor: pointer; }");
            out.println("input[type='submit']:hover { background-color: #2980b9; }");
            out.println(".appointment { background-color: #ffffff; border: 1px solid #ddd; padding: 15px; border-radius: 8px; margin: 10px auto; width: 80%; box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1); text-align: left; }");
            out.println(".back-button { position: absolute; top: 20px; right: 20px; text-decoration: none; padding: 10px 15px; border-radius: 4px; background-color: #3498db; color: #fff; }");
            out.println(".back-button:hover { background-color: #2980b9; }");
            out.println("</style>");
            out.println("</head><body>");

            // Back button to navigate to doctorHome.html
            out.println("<a href=\"doctorHome.html\" class=\"back-button\">Back to Home</a>");

            // Display the form in the center of the page if no doctor name is provided
            if (doctorName == null || doctorName.trim().isEmpty()) {
                out.println("<div class=\"centered-form\">");

                // Display "Global Hospital" above the form
                out.println("<div class=\"form-container\">");
                out.println("<div class=\"hospital-name\">Global Hospital</div>");
                out.println("<form action=\"DoctorAppointmentViewServlet\" method=\"POST\">");
                out.println("<label for=\"doctor_name\">Enter Doctor's Name:</label><br>");
                out.println("<input type=\"text\" name=\"doctor_name\" id=\"doctor_name\" required><br>");
                out.println("<input type=\"submit\" value=\"View Appointments\">");
                out.println("</form>");
                out.println("</div>");
                out.println("</div>");
            } else {
                // Display appointments if doctor name is provided
                try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                    String sql = "SELECT user_name, user_contact, appointment_date, appointment_time " +
                                 "FROM appointments WHERE doctor_name = ?";
                    PreparedStatement statement = conn.prepareStatement(sql);
                    statement.setString(1, doctorName);
                    ResultSet rs = statement.executeQuery();

                    out.println("<h2>Appointments for Dr. " + doctorName + "</h2>");
                    boolean hasAppointments = false;

                    while (rs.next()) {
                        hasAppointments = true;
                        String userName = rs.getString("user_name");
                        String userContact = rs.getString("user_contact");
                        String appointmentDate = rs.getString("appointment_date");
                        String appointmentTime = rs.getString("appointment_time");

                        out.println("<div class=\"appointment\">");
                        out.println("<p><strong>User Name:</strong> " + userName + "<br>");
                        out.println("<strong>User Contact:</strong> " + userContact + "<br>");
                        out.println("<strong>Appointment Date:</strong> " + appointmentDate + "<br>");
                        out.println("<strong>Appointment Time:</strong> " + appointmentTime + "<br></p>");
                        out.println("</div>");
                    }

                    if (!hasAppointments) {
                        out.println("<p>No appointments found for Dr. " + doctorName + ".</p>");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    out.println("<p>Error retrieving appointment data: " + e.getMessage() + "</p>");
                }
            }

            out.println("</body></html>");
        }
    }
}
