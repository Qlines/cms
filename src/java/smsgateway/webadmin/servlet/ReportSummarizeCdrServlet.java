package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.report.SummarizeDtacCdr;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DatetimeUtil;

public class ReportSummarizeCdrServlet extends HttpServlet {

    private void summarizeDtac(String fdate, String tdate, int srvc_main_id)
            throws Exception {
        Date from = null;
        Date to = null;
        if (fdate == null) {
            throw new Exception("require parameter from date!!");
        }

        from = DatetimeUtil.toDate(fdate, "yyyyMMdd");
        if (tdate != null) {
            to = DatetimeUtil.toDate(tdate, "yyyyMMdd");
        }
        try {
            new SummarizeDtacCdr().process(from, to, srvc_main_id);
            new SummarizeDtacCdr().process_new(from, to, srvc_main_id);
        } catch (Exception e) {
            throw e;
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String operSelect = request.getParameter("oper");
            String fdate = request.getParameter("from");
            String tdate = request.getParameter("to");
            String ssrvcid = request.getParameter("srvc_main_id");
            if ((ssrvcid == null) || (ssrvcid.isEmpty())) {
                ssrvcid = "0";
            }

            if (operSelect.trim().toLowerCase().equals("all")) {
                summarizeDtac(fdate, tdate, Integer.parseInt(ssrvcid));
            } else if (!operSelect.trim().toLowerCase().equals("ais")) {
                if (operSelect.trim().toLowerCase().equals("dtac")) {
                    summarizeDtac(fdate, tdate, Integer.parseInt(ssrvcid));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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