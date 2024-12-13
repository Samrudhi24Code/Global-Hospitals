package com.src;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/loginServlet")
public class loginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public loginServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve user inputs
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");  // Get the selected role

        // JDBC Connection setup
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/userdatabase", "root", "")) {
                
                // SQL query based on user role
                String query = "";
                switch (role) {
                    case "admin":
                        query = "SELECT * FROM admin_table WHERE username = ? AND password = ?";
                        break;
                    case "doctor":
                        query = "SELECT * FROM doctor_table WHERE username = ? AND password = ?";
                        break;
                    case "user":
                        query = "SELECT * FROM users WHERE username = ? AND password = ?";
                        break;
                    default:
                        response.sendRedirect("login.html?error=true");
                        return;
                }

                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, username); // Use username here
                    ps.setString(2, password);  // Use password here

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            // User has logged in successfully, create a session
                            HttpSession session = request.getSession();
                            session.setAttribute("username", username); // Store the username in the session

                            // Redirect based on role
                            switch (role) {
                                case "admin":
                                    response.sendRedirect("homeadmin.html");
                                    break;
                                case "doctor":
                                    response.sendRedirect("doctorHome.html");
                                    break;
                                case "user":
                                	response.sendRedirect("index.html");

                                    break;
                                default:
                                    out.print("<h1>Invalid role!</h1>");
                                    break;
                            }
                        } else {
                            response.sendRedirect("login.html?error=true");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            out.print("<h1>Login Failed! JDBC Driver not found.</h1><br>");
            e.printStackTrace(out);
        } catch (SQLException e) {
            out.print("<h1>Login Failed! Database exception occurred.</h1><br>");
            e.printStackTrace(out);
        } finally {
            out.close();
        }
    }
}
