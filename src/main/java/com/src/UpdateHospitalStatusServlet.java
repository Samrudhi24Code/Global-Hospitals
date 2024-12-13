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

@WebServlet("/UpdateHospitalStatus")
public class UpdateHospitalStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String jdbcURL = "jdbc:mysql://localhost:3307/hospital_management";
        String dbUser = "root";
        String dbPassword = "";

        int hospitalId = Integer.parseInt(request.getParameter("hospital_id"));
        String action = request.getParameter("action");
        String newStatus = action.equals("approve") ? "Approved" : "Rejected";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {
                String sql = "UPDATE hospitals SET status = ? WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, newStatus);
                stmt.setInt(2, hospitalId);
                stmt.executeUpdate();

                out.println("<html><body>");
                out.println("<script type='text/javascript'>");
                out.println("alert('Hospital status updated to: " + newStatus + "');");
                out.println("window.location.href = 'PendingHospitalsServlet';"); // Redirect to pending hospitals
                out.println("</script>");
                out.println("</body></html>");
            }
        } catch (Exception e) {
            out.println("Error updating hospital status: " + e.getMessage());
        }
    }
}
