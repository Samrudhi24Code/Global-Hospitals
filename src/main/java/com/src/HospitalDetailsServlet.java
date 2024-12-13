package com.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/HospitalDetailsServlet")
public class HospitalDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String jdbcUrl = "jdbc:mysql://localhost:3307/hospital_management"; 
        String dbUser = "root";
        String dbPassword = "";

        String hospitalName = request.getParameter("hospitalName");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PreparedStatement reviewStatement = null;
        ResultSet reviewResultSet = null;
        PreparedStatement doctorStatement = null;
        ResultSet doctorResultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            String sqlQuery = "SELECT * FROM hospitals WHERE name = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, hospitalName);
            resultSet = preparedStatement.executeQuery();

            out.println("<html><head>");
            out.println("<div style='position: absolute; top: 10px; right: 10px;'>");
            out.println("<form action='HospitalSearchServlet' method='get'>");
            out.println("<button type='submit' style='padding: 10px 20px; background-color:#08C2FF ; color: white; border: none; border-radius: 4px; cursor: pointer;'>Back</button>");
            out.println("</form>");
            out.println("</div>");

            out.println("<style>");
            
            out.println("body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; margin: 0; padding: 0; }");
            out.println(".container { max-width: 1200px; margin: 0 auto; padding: 20px; }");
            out.println("h2, h3 { color: #198ab6; text-align: center; }");
            out.println(".details, .doctors, .reviews, .appointment { background-color: #ddeff1; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); padding: 20px; margin-bottom: 30px; }");
            out.println(".details { border-left: 4px solid #1f4e79; }");
            out.println(".doctors, .reviews { border-left: 4px solid #3b5998; }");
            out.println(".appointment { border-left: 4px solid #1f4e79; }");
            out.println(".details img { display: flex; margin: 20px auto; border-radius: 8px; max-width: 500px; height: auto; }");
            out.println(".doctors ul, .reviews ul { list-style-type: none; padding: 0; }");
            out.println(".doctors li, .reviews li { background-color: #f7f9fc; margin: 10px 0; padding: 15px; border-radius: 6px; border: 1px solid #d1d9e6; }");
            out.println(".rating { color: #ffb300; font-size: 1.2rem; font-weight: bold; }");
            out.println(".appointment form { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);color:blue }");
            out.println(".appointment button { grid-column: span 2; padding: 12px; background-color: #3b5998; color: white; font-size: 1.1rem; border: none; border-radius: 4px; cursor: pointer; transition: background-color 0.3s ease; }");
            out.println(".appointment button:hover { background-color: #1f4e79; }");
            out.println("strong {color:blue;font-size:1.3rem;  }");
            out.println("input { width:300px; height:30px}");
            
            out.println("</style>");
            out.println("</head><body>");
            out.println("<div class='container'>");

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specializations");
                String facilities = resultSet.getString("facilities");
                String address = resultSet.getString("address");
                String contactInfo = resultSet.getString("contact_info");
                String operatingHours = resultSet.getString("operating_hours");
                byte[] image = resultSet.getBytes("image");
                int hospitalId = resultSet.getInt("id");

                out.println("<div class='details'>");
                out.println("<h2>" + name + "</h2>");
                out.println("<p><strong>Specialization:</strong> " + specialization + "</p>");
                out.println("<p><strong>Facilities:</strong> " + facilities + "</p>");
                out.println("<p><strong>Address:</strong> " + address + "</p>");
                out.println("<p><strong>Contact:</strong> " + contactInfo + "</p>");
                out.println("<p><strong>Operating Hours:</strong> " + operatingHours + "</p>");

                if (image != null) {
                    String imageBase64 = java.util.Base64.getEncoder().encodeToString(image);
                    out.println("<img src='data:image/jpeg;base64," + imageBase64 + "' alt='" + name + "' width='500' />");
                } else {
                    out.println("<img src='placeholder.jpg' alt='No image available' width='300' />");
                }

                String doctorQuery = "SELECT doctor_name, specialization, qualification FROM doctors WHERE hospital_id = ?";
                doctorStatement = connection.prepareStatement(doctorQuery);
                doctorStatement.setInt(1, hospitalId);
                doctorResultSet = doctorStatement.executeQuery();

                out.println("<div class='doctors'><h3>Doctors Information</h3><ul>");
                while (doctorResultSet.next()) {
                    out.println("<li><strong>Doctor Name:</strong> " + doctorResultSet.getString("doctor_name") + "<br>");
                    out.println("<strong>Specialization:</strong> " + doctorResultSet.getString("specialization") + "<br>");
                    out.println("<strong>Qualification:</strong> " + doctorResultSet.getString("qualification") + "</li><br>");
                }
                out.println("</ul></div>");

                String reviewQuery = "SELECT * FROM reviews WHERE hospital_name = ?";
                reviewStatement = connection.prepareStatement(reviewQuery);
                reviewStatement.setString(1, hospitalName);
                reviewResultSet = reviewStatement.executeQuery();

                out.println("<div class='reviews'><h3>Reviews</h3><ul>");
                while (reviewResultSet.next()) {
                    out.println("<li><p>" + reviewResultSet.getString("review") + "</p>");
                    out.println("<p class='rating'>Rating: " + reviewResultSet.getInt("rating") + " stars</p></li>");
                }
                out.println("</ul></div>");

                out.println("<div class='appointment'><h3>Book an Appointment</h3>");
                out.println("<form action='BookingAppointmentServlet' method='post'>");
                out.println("<label for='doctorName'>Enter the Doctor Name:</label>");
                out.println("<input type='text' id='doctorName' name='doctorName' required>");
                out.println("<label for='patientName'>Patient Name:</label>");
                out.println("<input type='text' id='patientName' name='patientName' required>");
                out.println("<label for='appointmentDate'>Appointment Date:</label>");
                out.println("<input type='date' id='appointmentDate' name='appointmentDate' required>");
                out.println("<label for='patientphone'>Contact No:</label>");
                out.println("<input type='number' id='patientphone' name='patientphone' required>");
                out.println("<label for='appointmentTime'>Appointment Time:</label>");
                out.println("<input type='time' id='appointmentTime' name='appointmentTime' required>");
                out.println("<button type='submit'>Book Appointment</button>");
                out.println("</form></div>");
            } else {
                out.println("<p>No details found for the specified hospital name.</p>");
            }

            out.println("</div>");
            out.println("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error occurred while retrieving hospital details.</p>");
        } finally {
            try { if (reviewResultSet != null) reviewResultSet.close(); } catch (Exception e) { }
            try { if (doctorResultSet != null) doctorResultSet.close(); } catch (Exception e) { }
            try { if (resultSet != null) resultSet.close(); } catch (Exception e) { }
            try { if (reviewStatement != null) reviewStatement.close(); } catch (Exception e) { }
            try { if (doctorStatement != null) doctorStatement.close(); } catch (Exception e) { }
            try { if (preparedStatement != null) preparedStatement.close(); } catch (Exception e) { }
            try { if (connection != null) connection.close(); } catch (Exception e) { }
        }
    }
}
