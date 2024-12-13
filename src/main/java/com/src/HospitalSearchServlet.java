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

@WebServlet("/HospitalSearchServlet")
public class HospitalSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Database connection details
        String jdbcUrl = "jdbc:mysql://localhost:3307/hospital_management"; // Update with your DB info
        String dbUser = "root"; // Update with your DB username
        String dbPassword = ""; // Update with your DB password

        String searchQuery = request.getParameter("search"); // Get search query from request

        try {
            // Load and register MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish database connection
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
            Statement statement = connection.createStatement();

            // Build SQL query with optional search filter
            String sqlQuery = "SELECT * FROM hospitals WHERE status = 'approved'";
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                sqlQuery += " AND name LIKE '%" + searchQuery + "%'";
            }

            // Execute the query and retrieve results
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            // HTML and CSS structure
            out.println("<html><head>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; background-color: #e6f7ff; color: #333; margin: 0; padding: 0; }");
            out.println(".container { max-width: 1200px; margin: 30px auto; padding: 20px; box-sizing: border-box; }");
            out.println("h1 { text-align: center; color: #007bff; margin-bottom: 20px; font-size: 2.5em; font-weight: 600; }");
            out.println(".search-box { display: flex; justify-content: center; margin-bottom: 30px; }");
            out.println(".search-box input[type='text'] { width: 400px; padding: 12px; font-size: 16px; border: 2px solid #ccc; border-radius: 8px; transition: border 0.3s ease; }");
            out.println(".search-box input[type='text']:focus { border-color: #4CAF50; outline: none; }");
            out.println(".search-box button { padding: 12px 20px; font-size: 16px; background-color: #007bff; color: white; border: none; border-radius: 8px; cursor: pointer; margin-left: 10px; }");
            out.println(".search-box button:hover { background-color: #0056b3; }");
            out.println(".card { border: 1px solid #ccc; border-radius: 8px; box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); margin: 15px; padding: 20px; width: 300px; background-color: white; transition: transform 0.3s ease; text-decoration: none; color: inherit; display: flex; flex-direction: column; align-items: center; }");
            out.println(".card:hover { transform: translateY(-5px); box-shadow: 0px 6px 15px rgba(0, 0, 0, 0.15); }");
            out.println(".card h2 { color: #007bff; font-size: 22px; margin-top: 10px; }");
            out.println(".card p { font-size: 14px; color: #555; text-align: center; margin: 5px 0; }");
            out.println(".card img { width: 100%; height: auto; border-radius: 5px; margin-bottom: 15px; }");
            out.println(".grid { display: flex; flex-wrap: wrap; justify-content: center; gap: 20px; }");
            out.println(".logout-button { float: right; margin: 20px; padding: 10px 15px; background-color: #f44336; color: white; border: none; border-radius: 5px; cursor: pointer; }");
            out.println(".logout-button:hover { background-color: #d32f2f; }");
            out.println(".feedback-button { float: right; margin: 20px; padding: 10px 15px; background-color: #4CAF50; color: white; border: none; border-radius: 5px; cursor: pointer; margin-right: 100px; }");
            out.println(".feedback-button:hover { background-color: #45a049; }");
            out.println("</style>");
            out.println("</head><body>");

            out.println("<div class='container'>");
            
            // Feedback button
            out.println("<button class='feedback-button' onclick=\"window.location.href='rating.html';\">Give Feedback</button>");
            
            // Logout button
            out.println("<button class='logout-button' onclick=\"window.location.href='LogoutServlet';\">Logout</button>");
            
            out.println("<h1>Book Your Appointment with Global Hospitals</h1>");

            // Search box form
            out.println("<div class='search-box'>");
            out.println("<form action='HospitalSearchServlet' method='get'>");
            out.println("<input type='text' name='search' placeholder='Search by Hospital Name' value='" + (searchQuery != null ? searchQuery : "") + "' />");
            out.println("<button type='submit'>Search</button>");
            out.println("</form>");
            out.println("</div>");

            out.println("<div class='grid'>");

            // Display records
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specializations"); 
                String facilities = resultSet.getString("facilities"); 
                String address = resultSet.getString("address");
                String contactInfo = resultSet.getString("contact_info");
                String operatingHours = resultSet.getString("operating_hours");
                byte[] image = resultSet.getBytes("image");

                // Create a clickable card with hospital name
                out.println("<a href='HospitalDetailsServlet?hospitalName=" + name + "' class='card'>");
                out.println("<h2>" + name + "</h2>");
                out.println("<p><strong>Specialization:</strong> " + specialization + "</p>");
                out.println("<p><strong>Facilities:</strong> " + facilities + "</p>");
                out.println("<p><strong>Address:</strong> " + address + "</p>");
                out.println("<p><strong>Contact:</strong> " + contactInfo + "</p>");
                out.println("<p><strong>Operating Hours:</strong> " + operatingHours + "</p>");
                
                if (image != null) {
                    String imageBase64 = java.util.Base64.getEncoder().encodeToString(image);
                    out.println("<img src='data:image/jpeg;base64," + imageBase64 + "' alt='" + name + "' />");
                } else {
                    out.println("<img src='placeholder.jpg' alt='No image available' />");
                }
                
                out.println("</a>"); // Close the anchor tag
            }

            out.println("</div>"); // Close grid div
            out.println("</div>"); // Close container div

            out.println("</body></html>");

            // Closing resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        } finally {
            out.close();
        }
    }
}
