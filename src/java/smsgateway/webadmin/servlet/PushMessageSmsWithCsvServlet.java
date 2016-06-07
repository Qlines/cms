/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.LogEvent.EVENT_ACTION;
import hippoping.smsgw.api.db.MessageSms;
import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceContentAction;
import hippoping.smsgw.api.db.TxQueue;
import hippoping.smsgw.api.db.User;
import hippoping.smsgw.api.ies.IesReceiver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DBPoolManager;
import lib.common.DatetimeUtil;
import lib.common.StringUtil;

/**
 *
 * @author nacks_mcair
 */
public class PushMessageSmsWithCsvServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(PushMessageSmsWithCsvServlet.class.getClass().getName());

    private int addTxqueue(String content, String deliver_dt, Map param, HttpServletRequest request) throws Exception {
        int res = 0;

        // check Date format
        if (!deliver_dt.trim().matches("^2\\d{3}[-/](0[1-9]|1[012]|[1-9])[-/](0[1-9]|[1-9]|[12][0-9]|3[01]) ((0|1)\\d{1}|2[0-3]|[0-9]):([0-5]\\d{1}|[0-9])(:([0-5]\\d{1})$|$)")) {
            throw new Exception("DateTime conversion error, 'yyyy-mm-dd HH:MM' is recommend.");
        }

        // Reformat datetime
        deliver_dt = deliver_dt.replace('/', '-'); // use / as date seperated
        if (deliver_dt.split(":").length == 2) {
            deliver_dt += ":00"; // Second is omited
        }
        String operid = (String) param.get("operid");
        String serviceid = (String) param.get("srvc_main_id");
        String type = (String) param.get("type");

        // check duplicate
        String date_format = "yyyy-MM-dd HH:mm:ss";
        Date deliver = DatetimeUtil.toDate(deliver_dt, date_format);
        int number = Integer.parseInt(operid);
        for (int i = OperConfig.CARRIER.length(); i > 0; i--) {
            if (number - Math.pow(2.0D, i) >= 0.0D) {
                number = (int) (number - Math.pow(2.0D, i));

                if (TxQueue.checkTxQueueDuplicate(Integer.parseInt(serviceid), i, null, content, deliver) > 0) {
                    throw new Exception("Duplicated message");
                }
            }
        }

        int content_id = new MessageSms().add(content, 1);

        try {
            DBPoolManager cp = new DBPoolManager();
            try {
                String whereoper = "";
                if (operid != null) {
                    whereoper = " AND ( 0";
                    number = Integer.parseInt(operid);
                    for (int i = OperConfig.CARRIER.length(); i > 0; i--) {
                        if (number - Math.pow(2.0D, i) >= 0.0D) {
                            number = (int) (number - Math.pow(2.0D, i));
                            whereoper = whereoper + " OR oper_id=" + i;
                        }
                    }
                    whereoper = whereoper + " )";
                }

                String sql
                        = "   SELECT db_code, oper_id"
                        + "   FROM ctnt_mngr_map"
                        + "  WHERE 1"
                        + "    AND srvc_main_id=?"
                        + whereoper;

                cp.prepareStatement(sql);
                cp.getPreparedStatement().setInt(1, Integer.parseInt(serviceid));
                ResultSet rs = cp.execQueryPrepareStatement();

                while (rs.next()) {
                    ServiceContentAction sca = new ServiceContentAction();
                    sca.action_type = ServiceContentAction.ACTION_TYPE.valueOf(type.toUpperCase());
                    sca.contentId = content_id;
                    try {
                        new IesReceiver().serviceMap(rs.getString(1), sca, deliver_dt);
                        res = 1;

                        LogEvent.log(LogEvent.EVENT_TYPE.valueOf(type.toUpperCase()), EVENT_ACTION.ADD,
                                rs.getString(1) + "|" + type + "|text message: " + content_id,
                                (User) request.getSession().getAttribute("USER"), null,
                                OperConfig.CARRIER.fromId(rs.getInt(2)), Integer.parseInt(serviceid), 0, 0, LogEvent.LOG_LEVEL.INFO);
                    } catch (Exception e) {
                        throw e;
                    }
                }

            } catch (SQLException e) {
                log.log(Level.SEVERE, "SQL Error!!", e);
                throw e;
            } finally {
                cp.release();
            }
        } catch (Exception e) {
            throw e;
        }

        return res;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            String forward = "";
            try {
                MultipartRequest multi = new MultipartRequest(request, ".", 5242880);

                out.println("<HTML>");
                out.println("<HEAD><TITLE>Push messages with CSV</TITLE>");
                out.println("<script src=\"http://code.jquery.com/jquery-latest.min.js\" type=\"text/javascript\"></script>"
                        + "<script>"
                        + "function nWin() {"
                        + "  var w = window.open();"
                        + "  var html = $(\"#toNewWindow\").html();"
                        + "    $(w.document.body).html(html);"
                        + "}"
                        + "</script>"
                        + "</HEAD>");
                out.println("<BODY>");
                Enumeration params = multi.getParameterNames();
                /*
                 Params:

                 field_delimiter = 0
                 operid = 4
                 remove_header = 1
                 csv_header = on
                 type = sms
                 line_delimiter = 1
                 srvc_main_id = 56
                 oper = on
                 */

                StringBuilder html = new StringBuilder();
                html.append("<div id='toNewWindow' style='display:none'><H1>Information</H1>");

                html.append("<H3>Params:</H3><PRE>");
                Map param = new HashMap();
                while (params.hasMoreElements()) {
                    String name = (String) params.nextElement();
                    String value = multi.getParameter(name);
                    html.append(name).append(" = ").append(value).append("<br/>");
                    param.put(name, value);
                }
                html.append("</PRE>");

                forward = "<script>window.location='push_message_sms.jsp?srvc_main_id=" + param.get("srvc_main_id") + "'</script>";

                String fd = ",";
                String ec = "";
                boolean remove_header = param.get("remove_header").equals("1");
                switch ((String) param.get("field_delimiter")) {
                    case "0":
                        fd = ",";
                        break;
                    case "1":
                        fd = "\t";
                        break;
                    case "2":
                        fd = (String) param.get("fdx_value");
                        break;
                }
                switch ((String) param.get("field_encap")) {
                    case "0":
                        ec = "";
                        break;
                    case "1":
                        ec = "'";
                        break;
                    case "2":
                        ec = "\"";
                        break;
                }

                html.append("<H3>Files:</H3><PRE>");
                Enumeration files = multi.getFileNames();
                while (files.hasMoreElements()) {
                    String name = (String) files.nextElement();
                    String filename = multi.getFilesystemName(name);
                    String type = multi.getContentType(name);
                    File f = multi.getFile(name);
                    html.append("filename: ").append(filename).append("<br/>");
                    html.append("type: ").append(type).append("<br/>");
                    if (f != null) {
                        html.append("length: ").append(f.length()).append("<br/>");
                    }
                    html.append("content:<br/>");
                    FileReader fr = new FileReader(f);
                    BufferedReader br = new BufferedReader(fr);

                    int exact_line = 0;
                    int success_line = 0;
                    int l = 0;
                    String line;
                    String log_pattern = "%s --> [%s] %s";

                    // BOM marker will only appear on the very beginning
                    br.mark(4);
                    if ('\ufeff' != br.read()) {
                        br.reset(); // not the BOM marker
                    }
                    while ((line = br.readLine()) != null) {

                        String err_status = "ERROR";
                        String err_desc = "Internal server error";

                        if (line.trim().isEmpty()) {
                            err_status = "SKIP";
                            err_desc = "Empty line";
                            html.append(String.format(log_pattern, line, err_status, err_desc)).append("<br/>");
                            log.info(String.format(log_pattern, line, err_status, err_desc));
                            continue;
                        }

                        String[] cols = (String [])StringUtil.splitText(line, fd, ec).toArray(new String[0]);
                        if (remove_header && l++ == 0) {
                            err_status = "SKIP";
                            err_desc = "Remove header";
                            html.append(String.format(log_pattern, line, err_status, err_desc)).append("<br/>");
                            log.info(String.format(log_pattern, line, err_status, err_desc));
                            continue;
                        }
                        if (cols.length == 0 || cols[0].trim().isEmpty()) {
                            err_status = "SKIP";
                            err_desc = "Blank column";
                            html.append(String.format(log_pattern, line, err_status, err_desc)).append("<br/>");
                            log.info(String.format(log_pattern, line, err_status, err_desc));
                            continue;
                        }

                        exact_line++;
                        log.info("read line ->" + line);
                        StringUtil.dump(line);
                        
                        log.info("amount of column:" + cols.length);

                        String deliver_dt = cols[0].trim();
                        String content = cols[1].trim();

                        err_status = "ERROR";
                        err_desc = "Internal server error";
                        int res = 0;
                        try {
                            res = addTxqueue(content, deliver_dt, param, request);
                            if (res == 1) {
                                err_status = "OK";
                                err_desc = "";
                            }
                        } catch (Exception e) {
                            err_desc = e.getMessage();
                        }

                        html.append(String.format(log_pattern, line, err_status, err_desc)).append("<br/>");
                        log.info(String.format(log_pattern, line, err_status, err_desc));

                        success_line += res;
                    }

                    html.append("<H3>Summary:</H3>");
                    html.append("Process ").append(exact_line).append(" line(s).<br/>");
                    html.append("Success ").append(success_line).append(" line(s).<br/>");

                    out.println(html);

                    out.println("</PRE>");
                }

                out.println("</div>");
                out.println("<script>nWin();</script>");
                out.println(forward);
            } catch (Exception e) {
                log.log(Level.SEVERE, "File upload error!!", e);
                out.println("<script>alert('File conversion error!');window.history.back();</script>");
            }
            out.println("</BODY></HTML>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
