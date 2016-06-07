package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceElement;
import hippoping.smsgw.api.db.Subscriber;
import hippoping.smsgw.api.operator.dtac.SubscriberSynchronize;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.StringConvert;

public class DtacSubscriberSyncInterface extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String msisdn = request.getParameter("msisdn");
            String sSrvc_main_id = request.getParameter("srvc_main_id");
            int srvc_main_id = 0;

            if ((msisdn == null) || (sSrvc_main_id == null) || (!StringConvert.isDigit(msisdn)) || (!StringConvert.isDigit(sSrvc_main_id))) {
                throw new Exception("invalid parameter error!!");
            }

            srvc_main_id = Integer.valueOf(sSrvc_main_id).intValue();

            ServiceElement se = new ServiceElement(srvc_main_id, OperConfig.CARRIER.DTAC.getId(), ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());
            if (se == null) {
                throw new Exception("service not found");
            }

            Subscriber subscriber = new Subscriber(msisdn, srvc_main_id, OperConfig.CARRIER.DTAC);
            if (subscriber == null) {
                throw new Exception("subscriber not found");
            }

            if ((se.srvc_type & ServiceElement.SERVICE_TYPE.NOCSS.getId()) > 0) {
                throw new Exception("no operation on NOCSS services");
            }

            Subscriber[] subscribers = {subscriber};
            new SubscriberSynchronize().sendSyncStatus(se, subscribers, null, null, null);
        } catch (Exception e) {
            out.print("<H1>" + e.getMessage() + "</H1>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
