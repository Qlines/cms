package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.report.SummaryReport;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReportSummarizeServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ReportSummarizeServlet.class.getName());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String dt_fmt = "dd-MM-yyyy";
            String d = request.getParameter("date");
            String cmd = request.getParameter("cmd");
            String scmd = request.getParameter("scmd");

            if (d == null) {
                out.println("Error due to required parameter for date " + dt_fmt.toUpperCase());
                throw new ServletException("Parameter error!!");
            }

            if (!d.matches("^(0[1-9]|[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012]|[1-9])-(19|20)\\d{2}$")) {
                out.println("Date format error");
                throw new ServletException("Date format error!!");
            }

            Date date = new SimpleDateFormat(dt_fmt).parse(d);

            double ncmd = Math.pow(2.0D, SummaryReport.COMMAND.ALL.getId()) - 1.0D;

            if ((cmd != null) && (!cmd.isEmpty())) {
                ncmd = Double.parseDouble(cmd);
            }

            if ((scmd != null) && (!scmd.isEmpty())) {
                ncmd = Math.pow(2.0D, SummaryReport.COMMAND.valueOf(scmd.toUpperCase()).getId());
            }

            for (int i = 0; i < SummaryReport.COMMAND.ALL.getId(); i++) {
                SummaryReport.COMMAND command = SummaryReport.COMMAND.fromId(i);

                out.print(command + ":");

                if (((int) ncmd & (int) Math.pow(2.0D, command.getId())) > 0) {
                    try {
                        SummaryReport.summarize(date, command);
                        out.print("OK");
                    } catch (Exception e) {
                        out.println("ERROR");
                    }
                }
                
                out.println("");
            }
        } catch (ParseException e) {
            logger.severe(e.getMessage());
        } finally {
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public String getServletInfo() {
        return "Short description";
    }
}