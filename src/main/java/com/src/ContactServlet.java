package com.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ContactServlet")
public class ContactServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String jdbcURL = "jdbc:mysql://localhost:3307/hospital_management"; // Update your database URL
    private String jdbcUsername = "root"; // Update your database username
    private String jdbcPassword = ""; // Update your database password

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String profession = request.getParameter("profession");
        String message = request.getParameter("message");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
                String sql = "INSERT INTO contacts (name, email, phone, profession, message) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, phone);
                statement.setString(4, profession);
                statement.setString(5, message);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    // Return a success message as JSON
                	response.sendRedirect("contact.html");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error occurred.");
        }
    }
}