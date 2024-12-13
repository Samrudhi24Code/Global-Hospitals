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

@WebServlet("/usersignServlet")
public class usersignServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public usersignServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Debugging output
        System.out.println("Received parameters: ");
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        // Validation: Check for null or empty parameters
        if (username == null || email == null || password == null || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            out.println("<script type='text/javascript'>");
            out.println("alert('All fields are required. Please fill in all fields.');");
            out.println("window.history.back();");
            out.println("</script>");
            return;
        }

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish database connection
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/userdatabase", "root", "")) {
                String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setString(1, username);
                    pst.setString(2, email);
                    pst.setString(3, password);

                    int rowsInserted = pst.executeUpdate();

                    if (rowsInserted > 0) {
                        out.println("<script type='text/javascript'>");
                        out.println("alert('User registered successfully!');");
                        out.println("window.location.href = 'index.html';");
                        out.println("</script>");
                    } else {
                        out.println("Failed to register user.");
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
