package com.src;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject; // Ensure you have the json.org library
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/HospitalRatingServlet")
public class HospitalRatingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public HospitalRatingServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();

        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Log the received raw request data
        System.out.println("Raw Request Body: " + sb.toString());

        try {
            // Parse JSON request
            JSONObject jsonRequest = new JSONObject(sb.toString());
            String hospitalName = jsonRequest.getString("hospitalName");
            int rating = jsonRequest.getInt("rating");
            String review = jsonRequest.getString("review");

            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver"); // For MySQL 8.0 and above

            // Establish Connection
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/hospital_management", "root", "")) {
                // Check if the hospital exists
                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM hospitals WHERE name = ?")) {
                    checkStmt.setString(1, hospitalName);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        JSONObject jsonResponse = new JSONObject();

                        if (rs.next()) {
                            // Insert the review
                            try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO reviews (hospital_name, rating, review) VALUES (?, ?, ?)")) {
                                insertStmt.setString(1, hospitalName);
                                insertStmt.setInt(2, rating);
                                insertStmt.setString(3, review);
                                insertStmt.executeUpdate();
                            }

                            jsonResponse.put("success", true);
                            jsonResponse.put("message", "Review submitted successfully.");
                        } else {
                            jsonResponse.put("success", false);
                            jsonResponse.put("message", "Hospital not found.");
                        }

                        out.print(jsonResponse.toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error processing request: " + e.getMessage());
            out.print(jsonResponse.toString());
        } finally {
            out.flush();
            out.close();
        }
    }
}
