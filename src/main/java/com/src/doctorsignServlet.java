package com.src;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/doctorsignServlet")

public class doctorsignServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public doctorsignServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve form parameters (ensure names match the HTML form)
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish database connection
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/userdatabase", "root", "")) {
                // Prepare SQL query
                String query = "INSERT INTO doctor_table (username, email, password) VALUES (?, ?, ?)";
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setString(1, username);
                    pst.setString(2, email);
                    pst.setString(3, password); // Store password as plain text (consider hashing)

                    // Execute update and check the result
                    int rowsInserted = pst.executeUpdate();

                    if (rowsInserted > 0) {
                        out.println("<script type='text/javascript'>");
                        out.println("alert('Registration successful!');"); // Alert message
                        out.println("window.location.href = 'instructions.html';"); // Redirect after alert
                        out.println("</script>");
                    } else {
                        out.println("<h1>Failed to register user.</h1>");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            out.println("<h1>JDBC Driver not found!</h1>");
            e.printStackTrace(out);
        } catch (SQLException e) {
            out.println("<h1>SQL error occurred: " + e.getMessage() + "</h1>");
            e.printStackTrace(out);
        } finally {
            out.close();
        }
    }
}
