package com.src;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

@WebServlet("/HospitalServlet")
@MultipartConfig
public class HospitalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection details
    private static final String jdbcURL = "jdbc:mysql://localhost:3307/hospital_management?useSSL=false&serverTimezone=UTC"; 
    private static final String dbUser = "root";
    private static final String dbPassword = ""; 

    public HospitalServlet() {
        super();
    }

    // Register MySQL JDBC driver
    static {
        try {
            // Explicitly load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Simple response for GET requests
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Retrieve hospital details from the request
        String hospitalName = request.getParameter("hospital_name");
        String specializations = request.getParameter("specializations");
        String facilities = request.getParameter("facilities");
        String address = request.getParameter("address");
        String contactInfo = request.getParameter("contact_info");
        String operatingHours = request.getParameter("operating_hours");

        // Retrieve doctor details from the request (array)
        String[] doctorNames = request.getParameterValues("doctor_name[]");
        String[] doctorSpecializations = request.getParameterValues("doctor_specialization[]");
        String[] doctorContacts = request.getParameterValues("qualification[]");

        // Handle image upload
        Part filePart = request.getPart("hospital_image");
        InputStream imageInputStream = filePart.getInputStream();

        try (Connection conn = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {
            String hospitalQuery = "INSERT INTO hospitals (name, specializations, facilities, address, contact_info, operating_hours, image) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(hospitalQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, hospitalName);
                stmt.setString(2, specializations);
                stmt.setString(3, facilities);
                stmt.setString(4, address);
                stmt.setString(5, contactInfo);
                stmt.setString(6, operatingHours);
                stmt.setBlob(7, imageInputStream);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        long hospitalId = generatedKeys.getLong(1);

                        // Insert doctor details
                        String doctorQuery = "INSERT INTO doctors (hospital_id,doctor_name, specialization,qualification) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement doctorStmt = conn.prepareStatement(doctorQuery)) {
                            for (int i = 0; i < doctorNames.length; i++) {
                                doctorStmt.setLong(1, hospitalId);
                                doctorStmt.setString(2, doctorNames[i]);
                                doctorStmt.setString(3, doctorSpecializations[i]);
                                doctorStmt.setString(4, doctorContacts[i]);
                                doctorStmt.addBatch();
                            }
                            doctorStmt.executeBatch();
                        }
                    }

                    // Redirect to the Back.html page with a success message
                    response.sendRedirect("Formfillingback.html");

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
        }
    }
}
