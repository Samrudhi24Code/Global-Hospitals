package com.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/PendingHospitalsServlet")
public class PendingHospitalsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; background-color: #f4f7fa; margin: 0; padding: 20px; }");
        out.println("h2 { text-align: center; color: #333; }");
        out.println("table { width: 90%; margin: 20px auto; border-collapse: collapse; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }");
        out.println("th, td { padding: 12px; text-align: center; border: 1px solid #ddd; color: #333; }");
        out.println("th { background-color: #6a1b9a; color: white; }");
        out.println("tr:nth-child(even) { background-color: #e0b7f3; }");
        out.println("tr:hover { background-color: #f2e6fa; }");

        out.println("input[type='submit'] { background-color: #6a1b9a; color: white; border: none; padding: 10px 15px; border-radius: 5px; cursor: pointer; transition: background-color 0.3s; margin: 5px; }");
        out.println("input[type='submit']:hover { background-color: #4a0f73; }");
        out.println("</style>");

        String jdbcURL = "jdbc:mysql://localhost:3307/hospital_management";
        String dbUser = "root";
        String dbPassword = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(jdbcURL, dbUser, dbPassword)) {
                Statement stmt = connection.createStatement();
                String sql = "SELECT * FROM hospitals WHERE status = 'Pending'";
                ResultSet rs = stmt.executeQuery(sql);

                out.println("<h2>Pending Hospitals</h2>");
                out.println("<table>");
                out.println("<tr><th>Name</th><th>Specializations</th><th>Facilities</th><th>Address</th><th>Contact Info</th><th>Operating Hours</th><th>Action</th></tr>");

                while (rs.next()) {
                    out.println("<tr>");
                    out.println("<td>" + rs.getString("name") + "</td>");
                    out.println("<td>" + rs.getString("specializations") + "</td>");
                    out.println("<td>" + rs.getString("facilities") + "</td>");
                    out.println("<td>" + rs.getString("address") + "</td>");
                    out.println("<td>" + rs.getString("contact_info") + "</td>");
                    out.println("<td>" + rs.getString("operating_hours") + "</td>");
                    out.println("<td>");
                    
                    out.println("<form action='UpdateHospitalStatus' method='post' style='display:inline;'>");
                    out.println("<input type='hidden' name='hospital_id' value='" + rs.getInt("id") + "'>");
                    out.println("<input type='hidden' name='action' value='approve'>");
                    out.println("<input type='submit' value='Approve'>");
                    out.println("</form>");
                    
                    out.println("<form action='UpdateHospitalStatus' method='post' style='display:inline;'>");
                    out.println("<input type='hidden' name='hospital_id' value='" + rs.getInt("id") + "'>");
                    out.println("<input type='hidden' name='action' value='reject'>");
                    out.println("<input type='submit' value='Reject'>");
                    out.println("</form>");
                    out.println("</td>");
                    out.println("</tr>");
                }

                out.println("</table>");
                rs.close();
                stmt.close();
            } catch (Exception e) {
                out.println("Error retrieving hospitals: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            out.println("Database Driver not found: " + e.getMessage());
        }
    }
}
