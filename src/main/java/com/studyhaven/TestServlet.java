package com.studyhaven;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Setting the content type of response
        response.setContentType("text/html");

        // Get the PrintWriter to write the response
        PrintWriter out = response.getWriter();
        
        // Write the HTML response
        out.println("<html>");
        out.println("<head><title>Test Servlet</title></head>");
        out.println("<body>");
        out.println("<h1>Hello from TestServlet!</h1>");
        out.println("</body>");
        out.println("</html>");
    }
}
