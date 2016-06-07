package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.LogEvent.LogEventSortByTimestamp;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DatetimeUtil;

public class LogEventReportServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(LogEventReportServlet.class.getClass().getName());
    protected int rows = 0;
    protected String old_orderby = "";
    protected int sort = 0;

    private void sort(List<LogEvent> LogEventReportList, String field, int swap) {
        if (LogEventReportList == null) {
            return;
        }
        if (LogEventReportList.size() <= 0) {
            return;
        }

        Comparator comparator = null;

        if (field == null) {
            field = "timestamp";
        }

        if ((this.old_orderby != null) && (this.old_orderby.equals(field))) {
            if (swap == 1) {
                this.sort = (++this.sort % 2);
            }
        } else {
            this.sort = 0;
            this.old_orderby = field;
        }

        if (field.equals("timestamp")) {
            comparator = new LogEventSortByTimestamp();
        }

        if (comparator == null) {
            return;
        }

        Collections.sort(LogEventReportList, comparator);
        if (this.sort == 1) {
            Collections.reverse(LogEventReportList);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            User user = (User) request.getSession().getAttribute("USER");
            if (user == null) {
                out.print("<script>window.location='logout?msg=Your session has been expired! Please relogin the page.'</script>");
            } else {
                int log_level = 0;
                User uid = null;
                Date fdate = new Date();
                Date tdate = fdate;
                LogEvent.EVENT_ACTION event_action = null;
                LogEvent.EVENT_TYPE event_type = null;
                String msisdn = null;
                String orderby = request.getParameter("orderby");
                String page = request.getParameter("page");
                String swap = request.getParameter("swap");

                String cmd = request.getParameter("cmd");

                if ((page == null) || (page.equals(""))) {
                    page = "1";
                }

                if ((cmd != null) && (cmd.equals("refresh"))) {
                    String _log_level = request.getParameter("log_level");
                    if (_log_level != null) {
                        log_level = Integer.parseInt(_log_level);
                    }

                    String _uid = request.getParameter("uid");
                    if (_uid != null) {
                        try {
                            uid = new User(Integer.parseInt(_uid));
                        } catch (Exception e) {
                        }
                    }
                    String _from_date = request.getParameter("fdate");
                    if (_from_date != null) {
                        try {
                            fdate = DatetimeUtil.toDate(_from_date, "yyyy-MM-dd");
                        } catch (Exception e) {
                            log.log(Level.SEVERE, e.getMessage());
                        }
                    }

                    String _to_date = request.getParameter("tdate");
                    if (_to_date != null) {
                        try {
                            tdate = DatetimeUtil.toDate(_to_date, "yyyy-MM-dd");
                        } catch (Exception e) {
                            log.log(Level.SEVERE, e.getMessage());
                        }
                    }

                    String _event_action = request.getParameter("event_action");
                    if (_event_action != null) {
                        event_action = LogEvent.EVENT_ACTION.fromId(Integer.parseInt(_event_action));
                    }

                    String _event_type = request.getParameter("event_type");
                    if (_event_type != null) {
                        event_type = LogEvent.EVENT_TYPE.fromId(Integer.parseInt(_event_type));
                    }

                    String _msisdn = request.getParameter("msisdn");
                    if (_msisdn != null) {
                        msisdn = _msisdn;
                    }

                    this.rows = Integer.parseInt(request.getParameter("rows"));
                    try {
                        request.getSession().setAttribute("logEventReportList", LogEvent.get(log_level, uid, msisdn, fdate, tdate, event_type, event_action, -1));
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "get log event error!!", e);
                    }

                }

                List logEventReportList = (List) request.getSession().getAttribute("logEventReportList");

                if ((logEventReportList != null) && (logEventReportList.size() > 0)) {
                    sort(logEventReportList, orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = 0;
                int total = 0;
                if (logEventReportList != null) {
                    pg = logEventReportList.size() / this.rows + (logEventReportList.size() % this.rows != 0 ? 1 : 0);

                    total = logEventReportList.size();
                }
                pg = pg == 0 ? 1 : pg;
                out.println("<html><head>   <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>   <link href='./css/cv.css' rel='stylesheet' type='text/css'>   <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>   <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>   <style type='text/css'>       body{margin:0px; padding: 0px; background: white;           font: 100.01% 'Trebuchet MS',Verdana,Arial,sans-serif}       h1,h2,p{margin: 0 10px}       h1{font-size: 250%;color: #FFF}       h2{font-size: 200%;color: #f0f0f0}       p{padding-bottom:1em}       h2{padding-top: 0.3em}       div#memberViewContent {background: #377CB1;}   </style>   <script src='./js/nifty.js' type='text/javascript'></script>   <script src='./js/utils.js' type='text/javascript'></script>   <script src='./js/filter_input.js' type='text/javascript'></script>   <script>   function validate_page(page, maxpage) {        var frm = document.forms[\"reloadFrm\"];       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}" + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}" + "       else {frm.submit();}" + "   }" + "   function reload_page(order) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.orderby.value=order;" + "       frm.swap.value=1;" + "       frm.submit();" + "   } " + "   function goto_page(page) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.page.value=page;" + "       frm.submit();" + "   } " + "   </script>" + "</head>" + "<body style='background-color:#FFF;'>" + "   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;;'>" + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>" + "       <input type=hidden name=orderby value='" + orderby + "'>" + "       <input type=hidden name=swap value='0'>" + "       <div class='floatl' style='font-size:75%; padding-left:5px;'><b>Total " + total + " record(s) found." + "           (Page " + page + " of " + pg + ")</b> " + "       </div>" + "       <div class='floatr'>" + "         <span style='padding:0;'>" + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>") + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>") + "         </span>" + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>" + "           <input type=submit value=go>" + "       </div>" + "       </form>" + "       <table class='table3' style='width:100%;padding:0;'>" + "       <tr>" + "           <th class='d1' width='3%'>No.</th>" + "           <th class='d1' width='5%'>Level</th>" + "           <th class='d1' width='20%'><a href='javascript:reload_page(\"timestamp\");'>" + (orderby.equals("timestamp") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "               Timestamp</a></th>" + "           <th class='d1' width='15%'>User</th>" + "           <th class='d1' width='15%'>Action</th>" + "           <th class='d1' width='15%'>Type</th>" + "           <th class='d1' width='25%'>Description</th>" + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                if (logEventReportList != null) {
                    for (int i = sindex; (i < logEventReportList.size()) && (i < eindex); i++) {
                        String style = i % 2 == 0 ? "" : " class='d0'";
                        LogEvent logEvent = (LogEvent) logEventReportList.get(i);
                        String ext_det = (logEvent.event_type != LogEvent.EVENT_TYPE.LOG_IN) && (logEvent.event_type != LogEvent.EVENT_TYPE.LOG_OUT) ? logEvent.getDetails() : "";

                        out.print("<tr" + style + "><td>" + (i + 1) + "</td>");
                        out.print("<td>" + logEvent.log_level + "</td>");
                        out.print("<td>" + DatetimeUtil.print("yyyy-MM-dd HH:mm:ss", logEvent.timestamp) + "</td>");
                        out.print("<td>" + logEvent.user.getName() + "</td>");
                        out.print("<td>" + (logEvent.event_action != LogEvent.EVENT_ACTION.NONE ? logEvent.event_action : "") + "</td>");

                        out.print("<td>" + logEvent.event_type + "</td>");
                        out.print("<td>" + logEvent.event_desc + (!ext_det.isEmpty() ? "|" : "") + ext_det + "</td>");

                        out.print("</tr>");
                    }
                }
                out.println("</table></div></body></html>");
            }
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