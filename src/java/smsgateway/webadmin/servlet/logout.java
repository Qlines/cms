package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.LogEvent.EVENT_ACTION;
import hippoping.smsgw.api.db.LogEvent.EVENT_TYPE;
import hippoping.smsgw.api.db.LogEvent.LOG_LEVEL;
import hippoping.smsgw.api.db.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class logout extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            // log event
            LogEvent.log(EVENT_TYPE.LOG_OUT, EVENT_ACTION.NONE, "bye.",
                    (User) request.getSession().getAttribute("USER"),
                    null,
                    null,
                    0, 0, 0, LOG_LEVEL.INFO);

            request.getSession().invalidate();
            response.sendRedirect("index.jsp");
        } finally {
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}