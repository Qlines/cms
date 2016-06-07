package smsgateway.webadmin.servlet;

import com.ais.legacy.dlvrmsg.response.XML;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.RxMoQueue;
import hippoping.smsgw.api.db.RxQueue;
import hippoping.smsgw.api.db.RxQueue.RX_TYPE;
import hippoping.smsgw.api.db.ServiceElement;
import hippoping.smsgw.api.db.User;
import hippoping.smsgw.api.hybrid.HybridwsdlStub;
import hippoping.smsgw.api.operator.ais.HybridDispatcherRemote;
import hippoping.smsgw.api.operator.ais.TypeLDispatcherRemote;
import hippoping.smsgw.api.operator.ais.TypeLMessageHeaderType;
import hippoping.smsgw.api.subscription.AisLegacySubscriptionService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {

    @EJB(name = "DtacSdpHandleRequestRef")
    private hippoping.smsgw.api.operator.dtac_sdp.DtacMoEventEJBRemote dtacSdpMoEventBean;
//    @EJB(name = "DtacHandleRequestRef")
//    private hippoping.smsgw.api.operator.dtac.DtacMoEventEJBRemote dtacMoEventEJBBean;
    @EJB(name = "TruemoveHandleRequestRef")
    private hippoping.smsgw.api.operator.true_css.MoEventBeanRemote moEventBean;
    @EJB(name = "TruehHandleRequestRef")
    private hippoping.smsgw.api.operator.trueh_css.MoEventBeanRemote truehMoEventBean;
    @EJB(name = "HybridDispatcherRef")
    private HybridDispatcherRemote hybridDispatcherBean;
    @EJB(name = "TypeLDispatcherRef")
    private TypeLDispatcherRemote typeLDispatcherBean;
    private static final Logger log = Logger.getLogger(RegisterServlet.class.getName());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String srvc_main_id = "0";
            String[] msisdn = null;
            String msisdn_tmp = "";
            OperConfig.CARRIER oper = null;
            String forward = "";
            String showResult = "off";
            String action = "";
            String ignore_numplan = "0";

            if ((request.getContentType() != null) && (request.getContentType().contains("multipart/form-data"))) {
                MultipartRequest multi = new MultipartRequest(request, ".", 5242880);

                Enumeration params = multi.getParameterNames();
                while (params.hasMoreElements()) {
                    String name = (String) params.nextElement();
                    String value = multi.getParameter(name);
                    if (name.equals("srvcid")) {
                        srvc_main_id = value;
                    } else if (name.equals("operid")) {
                        oper = OperConfig.CARRIER.fromId(Integer.parseInt(value));
                    } else if ((name.equals("msisdn")) && (value != null) && (value.trim().length() > 0)) {
                        msisdn = value.split(";");
                        msisdn_tmp = value;
                    } else if (name.equals("result")) {
                        showResult = value;
                    } else if (name.equals("forward")) {
                        forward = value;
                    } else if (name.equals("action")) {
                        action = value;
                    } else if (name.equals("ignore_numplan")) {
                        ignore_numplan = value;
                    }

                }

                Enumeration files = multi.getFileNames();
                while (files.hasMoreElements()) {
                    String name = (String) files.nextElement();
                    File f = multi.getFile(name);
                    if (f == null) {
                        break;
                    }

                    List msisdn_list = new ArrayList();

                    String line;
                    FileReader fr = new FileReader(f);
                    BufferedReader br = new BufferedReader(fr);
                    while ((line = br.readLine()) != null) {
                        if (line.contains(";")) {
                            for (String m : line.split(";")) {
                                if (m.trim().matches("^66[689][0-9]{8}$")) {
                                    msisdn_list.add(m.trim());
                                }
                            }
                        } else if (line.trim().matches("^66[689][0-9]{8}$")) {
                            msisdn_list.add(line.trim());
                        }
                    }

                    msisdn = (String[]) msisdn_list.toArray(new String[0]);
                }
            } else {
                try {
                    srvc_main_id = request.getParameter("srvcid");
                    oper = OperConfig.CARRIER.fromId(Integer.parseInt(request.getParameter("operid")));
                    msisdn = request.getParameter("msisdn").split(";");
                    msisdn_tmp = request.getParameter("msisdn");
                    showResult = request.getParameter("result");
                    action = request.getParameter("action");
                    ignore_numplan = request.getParameter("ignore_numplan");

                    String qstring = request.getQueryString();
                    String fwdParam = "forward=";
                    if (qstring.contains(fwdParam)) {
                        forward = qstring.substring(qstring.indexOf(fwdParam) + fwdParam.length());
                    }
                } catch (Exception e) {
                    throw new Exception("invalid parameters");
                }
            }

            if ((action == null) || ((!action.equals("register")) && (!action.equals("unregister")))) {
                throw new Exception("action not supported");
            }

            ServiceElement se = new ServiceElement(Integer.parseInt(srvc_main_id), oper.getId(), ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ON.getId() | ServiceElement.SERVICE_STATUS.TEST.getId());

            out.print("<h1>Information:</h1><br>Service : " + se.srvc_id + " - " + se.srvc_name + "<hr><h3>Result:</h3>");

            if (!se.isAble2ManageSub()) {
                throw new Exception("service is not able to manage!!");
            }

            for (String n : msisdn) {
                if (!n.isEmpty()) {
                    n = n.trim();

                    String status = "SUCCESS";
                    String detail = "Operation completed.";
                    try {
                        if (ignore_numplan == null) {
                            ignore_numplan = "0";
                        }
                        OperConfig.CARRIER tmpoper = OperConfig.whichOper(n);
                        if ((action.equals("register")) && (ignore_numplan.equals("0")) && ((tmpoper == null) || (!tmpoper.toString().equals(oper.toString())))) {
                            throw new Exception("MSISDN is mismatched with operator");
                        }

                        if (!n.matches("^66[689][0-9]{8}$")) {
                            throw new Exception("MSISDN is incorrect!!");
                        }

                        LogEvent.log(LogEvent.EVENT_TYPE.SUBSCRIBER, action.equals("register") ? LogEvent.EVENT_ACTION.ADD : LogEvent.EVENT_ACTION.DELETE, action.equals("register") ? "subscript MSISDN" : "unsubscript MSISDN", (User) request.getSession().getAttribute("USER"), n, oper, se.srvc_main_id, 0, 0, LogEvent.LOG_LEVEL.INFO);

                        long rx_id = RxQueue.add(oper, RX_TYPE.HTTP, action);

                        switch (oper) {
                            case TRUE:
                                this.moEventBean.processKeyword(se.srvc_id_mo, n, action.equals("register") ? se.sms_register : se.sms_unregister, RX_TYPE.SMS, null, rx_id);

                                break;
                            case TRUEH:
                                this.truehMoEventBean.processKeyword(se.srvc_id_mo, n, action.equals("register") ? se.sms_register : se.sms_unregister, RX_TYPE.SMS, null, rx_id);

                                break;
//                            case DTAC:
//                                hippoping.smsgw.api.operator.dtac.CpaResponseFactory cpareply = this.dtacMoEventEJBBean.processKeyword(se.srvc_id, n, null, action.equals("register") ? se.sms_register : se.sms_unregister, null, null, rx_id);
//
//                                log.info(String.format("Result of calling processKeyword: %d(%s)", new Object[]{Integer.valueOf(cpareply.getStatus()), cpareply.getStatus_desc()}));
//
//                                status = cpareply.getStatus() == 200 ? "OK" : "ERROR";
//                                detail = cpareply.getStatus_desc();
//                                break;
                            case DTAC_SDP:
                                hippoping.smsgw.api.operator.dtac_sdp.CpaResponseFactory cpareply2 = 
                                        dtacSdpMoEventBean.processKeyword(se.srvc_id, n, null, action.equals("register") ? se.sms_register : se.sms_unregister, null, null, rx_id, RX_TYPE.HTTP);

                                log.info(String.format("Result of calling processKeyword: %d(%s)", new Object[]{Integer.valueOf(cpareply2.getStatus()), cpareply2.getStatus_desc()}));

                                status = cpareply2.getStatus() == 200 ? "OK" : "ERROR";
                                detail = cpareply2.getStatus_desc();
                                break;
                            case AIS_LEGACY:
                                XML xml = null;
                                switch (action) {
                                    case "register":
                                        xml = new AisLegacySubscriptionService().sendRegister(se, n);
                                        break;
                                    case "unregister":
                                        xml = new AisLegacySubscriptionService().sendUnregister(se, n);
                                        break;
                                }

                                status = xml.getSTATUS();
                                detail = xml.getDETAIL();
                                break;
                            case AIS:
                                HybridwsdlStub.SendMOResponse rsp;
                                switch (action) {
                                    case "register":
                                        new RxQueue(rx_id).addType(RX_TYPE.SUB);
                                        RxMoQueue.add(rx_id, se.srvc_main_id, n, null, null, se.thrd_prty_register);
                                        rsp = this.hybridDispatcherBean.sendRegister(se, n);
                                        status = rsp.get_return().getSTATUS();
                                        detail = rsp.get_return().getSID();
                                        break;
                                    case "unregister":
                                        new RxQueue(rx_id).addType(RX_TYPE.UNSUB);
                                        RxMoQueue.add(rx_id, se.srvc_main_id, n, null, null, se.thrd_prty_unregister);
                                        if (se.oper_config.hybrid.getName() != null) {
                                            rsp = this.hybridDispatcherBean.sendUnregister(se, n);

                                            status = rsp.get_return().getSTATUS();
                                            detail = rsp.get_return().getSID();
                                        } else {
                                            this.typeLDispatcherBean.putFile(se, msisdn, TypeLMessageHeaderType.SSS_MSG_TYPE.UNREGISTER);

                                            status = "success";
                                            detail = "Put SFTP UNREGISTER file";
                                        }
                                        break;
                                }
                                break;
                        }
                    } catch (Exception e) {
                        status = "ERROR";
                        detail = e.getMessage() != null ? e.getMessage() : "Unknown error.";
                        log.log(Level.SEVERE, "Register/Unregister process failed!!", e);
                    }

                    out.print(n + " - " + status + ": " + detail + "<br>");
                }
            }

            if ((showResult != null) && (showResult.equals("on"))) {
                out.println("<script>window.open('member_view.jsp?operid=" + oper.getId() + "&srvcid=" + srvc_main_id + "&msisdn=" + msisdn_tmp + "',null,'height=394,width=732,status=yes,toolbar=no,menubar=no,location=no');</script>");
            }

            out.println("<a href='javascript:history.back(-1);'>Back</a>");
        } catch (Exception e) {
            log.severe(e.getMessage());
            String status = "ERROR";
            out.print(status + ": " + e.getMessage() + "<br>");
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
