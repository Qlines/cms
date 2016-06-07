package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.SubscriptionServices;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.StringConvert;

public class SubscriptionServicesInterface extends HttpServlet {

    private static final Logger log = Logger.getLogger(SubscriptionServicesInterface.class.getName());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String msisdns = request.getParameter("msisdn");
            String _srvc_main_id = request.getParameter("srvc_main_id");
            String _oper_id = request.getParameter("oper_id");
            String cmd = request.getParameter("cmd");

            if ((msisdns == null) || (msisdns.isEmpty()) || (_srvc_main_id == null) || (_srvc_main_id.isEmpty()) || (!StringConvert.isDigit(_srvc_main_id)) || (_oper_id == null) || (_oper_id.isEmpty()) || (!StringConvert.isDigit(_oper_id)) || (cmd == null) || (cmd.isEmpty())) {
                throw new Exception("parameter error!!");
            }

            int srvc_main_id = Integer.parseInt(_srvc_main_id);
            int oper_id = Integer.parseInt(_oper_id);

            String[] msisdn = msisdns.split("[;,:|]");
            log.info(cmd + " function called for srvc_main_id:" + _srvc_main_id + ", oper_id:" + _oper_id);
            SubscriptionServices.SUB_RESULT result = SubscriptionServices.SUB_RESULT.INVALID;
            for (String m : msisdn) {
                if (cmd.equals("sub")) {
                    result = new SubscriptionServices().doSub(m, srvc_main_id, OperConfig.CARRIER.fromId(oper_id));
                } else if (cmd.equals("unsub")) {
                    result = new SubscriptionServices().doUnsub(m, srvc_main_id, OperConfig.CARRIER.fromId(oper_id));
                }
                log.info(m + "->" + result);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, request.getQueryString(), e);
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