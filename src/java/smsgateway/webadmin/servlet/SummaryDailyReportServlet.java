package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByDate;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByMtChrgBalance;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByMtChrgError;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByMtChrgTotal;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByMtChrgTotalAis;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByMtChrgTotalDtac;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByMtChrgTotalTrue;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByMtChrgTotalTrueh;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByPrice;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByService;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByServiceId;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortBySubBalanceAis;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortBySubBalanceDtac;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortBySubBalanceTrue;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortBySubBalanceTrueh;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortBySubTotalAis;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortBySubTotalDtac;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortBySubTotalTrue;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortBySubTotalTrueh;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByUnsubTotalAis;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByUnsubTotalDtac;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByUnsubTotalTrue;
import hippoping.smsgw.api.comparator.subscriptionsummaryreport.SubscriptionSummaryReportSortByUnsubTotalTrueh;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceCharge;
import hippoping.smsgw.api.db.ServiceElement;
import hippoping.smsgw.api.db.User;
import hippoping.smsgw.api.db.report.SubscriptionTrackerFactory;
import hippoping.smsgw.api.db.report.SummaryDailyReport;
import hippoping.smsgw.api.db.report.SummaryDailyReportFactory;
import hippoping.smsgw.api.db.report.SummaryReport;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DatetimeUtil;
import lib.common.StringConvert;

public class SummaryDailyReportServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(SummaryDailyReportServlet.class.getName());
    protected int rows = 0;
    protected String old_orderby = "";
    protected int sort = 0;

    private void sort(List<SummaryDailyReport> dailyReportList, String field, int swap) {
        if (dailyReportList == null) {
            return;
        }
        if (dailyReportList.size() <= 0) {
            return;
        }

        Comparator comparator = null;

        if (field == null) {
            field = "service";
        }

        if ((this.old_orderby != null) && (this.old_orderby.equals(field))) {
            if (swap == 1) {
                this.sort = (++this.sort % 2);
            }
        } else {
            this.sort = 0;
            this.old_orderby = field;
        }

        if (field.equals("service")) {
            comparator = new SubscriptionSummaryReportSortByService();
        } else if (field.equals("srvcid")) {
            comparator = new SubscriptionSummaryReportSortByServiceId();
        } else if (field.equals("price")) {
            comparator = new SubscriptionSummaryReportSortByPrice();
        } else if (field.equals("date")) {
            comparator = new SubscriptionSummaryReportSortByDate();
        } else if (field.equals("sub_total_ais")) {
            comparator = new SubscriptionSummaryReportSortBySubTotalAis();
        } else if (field.equals("sub_total_dtac")) {
            comparator = new SubscriptionSummaryReportSortBySubTotalDtac();
        } else if (field.equals("sub_total_tmv")) {
            comparator = new SubscriptionSummaryReportSortBySubTotalTrue();
        } else if (field.equals("sub_total_tmh")) {
            comparator = new SubscriptionSummaryReportSortBySubTotalTrueh();
        } else if (field.equals("unsub_total_ais")) {
            comparator = new SubscriptionSummaryReportSortByUnsubTotalAis();
        } else if (field.equals("unsub_total_dtac")) {
            comparator = new SubscriptionSummaryReportSortByUnsubTotalDtac();
        } else if (field.equals("unsub_total_tmv")) {
            comparator = new SubscriptionSummaryReportSortByUnsubTotalTrue();
        } else if (field.equals("unsub_total_tmh")) {
            comparator = new SubscriptionSummaryReportSortByUnsubTotalTrueh();
        } else if (field.equals("sub_balance_ais")) {
            comparator = new SubscriptionSummaryReportSortBySubBalanceAis();
        } else if (field.equals("sub_balance_dtac")) {
            comparator = new SubscriptionSummaryReportSortBySubBalanceDtac();
        } else if (field.equals("sub_balance_tmv")) {
            comparator = new SubscriptionSummaryReportSortBySubBalanceTrue();
        } else if (field.equals("sub_balance_tmh")) {
            comparator = new SubscriptionSummaryReportSortBySubBalanceTrueh();
        } else if (field.equals("mt_chrg_total_ais")) {
            comparator = new SubscriptionSummaryReportSortByMtChrgTotalAis();
        } else if (field.equals("mt_chrg_total_dtac")) {
            comparator = new SubscriptionSummaryReportSortByMtChrgTotalDtac();
        } else if (field.equals("mt_chrg_total_tmv")) {
            comparator = new SubscriptionSummaryReportSortByMtChrgTotalTrue();
        } else if (field.equals("mt_chrg_total_tmh")) {
            comparator = new SubscriptionSummaryReportSortByMtChrgTotalTrueh();
        } else if (field.equals("mt_chrg_balance")) {
            comparator = new SubscriptionSummaryReportSortByMtChrgBalance();
        } else if (field.equals("mt_chrg_error")) {
            comparator = new SubscriptionSummaryReportSortByMtChrgError();
        } else if (field.equals("mt_chrg_total")) {
            comparator = new SubscriptionSummaryReportSortByMtChrgTotal();
        }

        if (comparator == null) {
            return;
        }

        Collections.sort(dailyReportList, comparator);
        if (this.sort == 1) {
            Collections.reverse(dailyReportList);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            User user = (User) request.getSession().getAttribute("USER");
            if (user == null) {
                out.print("<script>window.location='logout?msg=Your session has been expired! please relogin the page.'</script>");
            } else {
                String srvcid = "";
                String operid = "";
                String fdate = "";
                String tdate = "";
                String stype = request.getParameter("stype");
                String orderby = request.getParameter("orderby");
                String page = request.getParameter("page");
                String swap = request.getParameter("swap");

                String cmd = request.getParameter("cmd");

                if ((page == null) || (page.equals(""))) {
                    page = "1";
                }

                ServiceElement.SERVICE_TYPE type = ServiceElement.SERVICE_TYPE.ALL;
                if ((stype != null) && (StringConvert.isDigit(stype))) {
                    type = ServiceElement.SERVICE_TYPE.fromId(Integer.parseInt(stype));
                }

                if ((cmd != null) && (cmd.equals("refresh"))) {
                    srvcid = request.getParameter("srvcid");
                    operid = request.getParameter("operid");
                    fdate = request.getParameter("fdate");
                    tdate = request.getParameter("tdate");
                    this.rows = Integer.parseInt(request.getParameter("rows"));

                    if ((srvcid == null) || (srvcid.equals(""))) {
                        srvcid = "-1";
                    }

                    OperConfig.CARRIER oper = null;
                    if ((operid != null) && (!operid.equals(""))) {
                        oper = OperConfig.CARRIER.fromId(Integer.parseInt(operid));
                    }

                    String dt_fmt = "dd-MM-yyyy";

                    if ((!fdate.matches("^(0[1-9]|[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012]|[1-9])-(19|20)\\d{2}$")) || (!tdate.matches("^(0[1-9]|[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012]|[1-9])-(19|20)\\d{2}$"))) {
                        out.println("Date format error");
                        throw new ServletException("Date format error!!");
                    }
                    try {
                        Date from = new SimpleDateFormat(dt_fmt).parse(fdate);
                        Date to = new SimpleDateFormat(dt_fmt).parse(tdate);

                        request.getSession().setAttribute("dailyReportList", new SummaryDailyReportFactory(from, to, oper, Integer.parseInt(srvcid), type, user).getSubscriptionSummaryDailyReportList());

                        LogEvent.log(LogEvent.EVENT_TYPE.REPORT_SUMMARY, LogEvent.EVENT_ACTION.SEARCH, "", (User) request.getSession().getAttribute("USER"), null, oper, Integer.parseInt(srvcid), 0, 0, LogEvent.LOG_LEVEL.INFO);
                    } catch (ParseException e) {
                    } catch (Exception e) {
                    }

                }

                List dailyReportList = (List) request.getSession().getAttribute("dailyReportList");

                String sstracker = "ssTrackerDaily";
                request.getSession().setAttribute(sstracker, null);
                List trackBuffer = new ArrayList();

                if ((dailyReportList != null) && (dailyReportList.size() > 0)) {
                    sort(dailyReportList, orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = 0;
                int total = 0;
                if (dailyReportList != null) {
                    pg = dailyReportList.size() / this.rows + (dailyReportList.size() % this.rows != 0 ? 1 : 0);

                    total = dailyReportList.size();
                }
                pg = pg == 0 ? 1 : pg;
                out.println("<html><head>"
                        + "   <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>"
                        + "   <link href='./css/cv.css' rel='stylesheet' type='text/css'>"
                        + "   <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>"
                        + "   <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>"
                        + "   <style type='text/css'>"
                        + "       body{margin:0px; padding: 0px; background: white;"
                        + "           font: 100.01% 'Trebuchet MS',Verdana,Arial,sans-serif}"
                        + "       h1,h2,p{margin: 0 10px}"
                        + "       h1{font-size: 250%;color: #FFF}"
                        + "       h2{font-size: 200%;color: #f0f0f0}"
                        + "       p{padding-bottom:1em}"
                        + "       h2{padding-top: 0.3em}"
                        + "       div#memberViewContent {background: #377CB1;}"
                        + "   </style>"
                        + "   <script src='./js/nifty.js' type='text/javascript'></script>"
                        + "   <script src='./js/utils.js' type='text/javascript'></script>"
                        + "   <script src='./js/filter_input.js' type='text/javascript'></script>"
                        + "   <script>"
                        + "   function validate_page(page, maxpage) {"
                        + "        var frm = document.forms[\"reloadFrm\"];"
                        + "       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}"
                        + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}"
                        + "       else {frm.submit();}"
                        + "   }"
                        + "   function reload_page(order) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.orderby.value=order;"
                        + "       frm.swap.value=1;"
                        + "       frm.submit();"
                        + "   } "
                        + "   function goto_page(page) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.page.value=page;"
                        + "       frm.submit();"
                        + "   } "
                        + "   </script>"
                        + "</head>"
                        + "<body style='background-color:#FFF;'>"
                        + "   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;;'>"
                        + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>"
                        + "       <input type=hidden name=orderby value='" + orderby + "'>"
                        + "       <input type=hidden name=stype value='" + stype + "'>"
                        + "       <input type=hidden name=swap value='0'>"
                        + "       <input type=hidden name=csv value='0'>"
                        + "       <div class='floatl' style='font-size:75%; padding-left:5px;'><b>Total " + total + " record(s) found."
                        + "           (Page " + page + " of " + pg + ")</b> "
                        + "           | Export <a href='javascript:window.location=\"./SubscriptionSummaryDailyReportServlet?csv=1&page=" + page + "&stype=" + stype + "\";'><img src='./images/csv_2.gif'></a>"
                        + "       </div>"
                        + "       <div class='floatr'>"
                        + "         <span style='padding:0;'>"
                        + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>") + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>")
                        + "         </span>"
                        + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>"
                        + "           <input type=submit value=go>"
                        + "       </div>"
                        + "       </form>"
                        + "       <table class='table3' style='width:100%;padding:0;'>"
                        + "       <tr>"
                        + "           <th class='d1' width='5%'>No.</th>"
                        + "           <th class='d1'><a href='javascript:reload_page(\"srvcid\");'>"
                        + (orderby.equals("srvcid") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               Media Code</a></th>"
                        + "           <th class='d1'><a href='javascript:reload_page(\"service\");'>"
                        + (orderby.equals("service") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               Description</a></th>"
                        + "           <th class='d1'><a href='javascript:reload_page(\"price\");'>"
                        + (orderby.equals("price") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               Price</a></th>"
                        + "           <th class='d1'><a href='javascript:reload_page(\"date\");'>"
                        + (orderby.equals("date") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               Date</a></th>"
                        + ((type != null) && ((type.getDbId() & ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getDbId()) > 0)
                        ? "           <th colspan=4 class='orange'>Sub</th>"
                        + "           <th colspan=4>Unsub</th>"
                        + "           <th colspan=4 class='orange'>Balance</th>"
                        : "")
                        + "           <th colspan=7>Charging</th>"
                        + "       </tr><tr>"
                        + "           <td colspan='5'></td>"
                        + ((type != null) && ((type.getId() & ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getId()) > 0)
                        ? "           <th class='d0 d1'><a href='javascript:reload_page(\"sub_ais\");'>"
                        + (orderby.equals("sub_ais") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               AIS</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"sub_dtac\");'>"
                        + (orderby.equals("sub_dtac") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               DTAC</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"sub_tmv\");'>"
                        + (orderby.equals("sub_tmv") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               TMV</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"sub_tmh\");'>"
                        + (orderby.equals("sub_tmh") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               TMH</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"unsub_ais\");'>"
                        + (orderby.equals("unsub_ais") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               AIS</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"unsub_dtac\");'>"
                        + (orderby.equals("unsub_dtac") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               DTAC</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"unsub_tmv\");'>"
                        + (orderby.equals("unsub_tmv") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               TMV</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"unsub_tmh\");'>"
                        + (orderby.equals("unsub_tmh") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               TMH</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"balance_ais\");'>"
                        + (orderby.equals("balance_ais") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               AIS</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"balance_dtac\");'>"
                        + (orderby.equals("balance_dtac") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               DTAC</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"balance_tmv\");'>"
                        + (orderby.equals("balance_tmv") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               TMV</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"balance_tmh\");'>"
                        + (orderby.equals("balance_tmh") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               TMH</a></th>"
                        : "")
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"charge_ais\");'>"
                        + (orderby.equals("charge_ais") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               AIS</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"charge_dtac\");'>"
                        + (orderby.equals("charge_dtac") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               DTAC</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"charge_tmv\");'>"
                        + (orderby.equals("charge_tmv") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               TMV</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"charge_tmh\");'>"
                        + (orderby.equals("charge_tmh") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               TMH</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"charge_success\");'>"
                        + (orderby.equals("charge_success") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               Success</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"charge_error\");'>"
                        + (orderby.equals("charge_error") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               NoCredit /Error</a></th>"
                        + "           <th class='d0 d1'><a href='javascript:reload_page(\"charge_total\");'>"
                        + (orderby.equals("charge_total") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "               Total</a></th>"
                        + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                if (dailyReportList != null) {
                    int total_sub_ais = 0;
                    int total_sub_dtac = 0;
                    int total_sub_tmv = 0;
                    int total_sub_tmh = 0;
                    int total_unsub_ais = 0;
                    int total_unsub_dtac = 0;
                    int total_unsub_tmv = 0;
                    int total_unsub_tmh = 0;
                    int total_charge_ais = 0;
                    int total_charge_dtac = 0;
                    int total_charge_tmv = 0;
                    int total_charge_tmh = 0;
                    int total_charge_success = 0;
                    int total_charge_error = 0;
                    int total_charge_net = 0;

                    for (int i = sindex; (i < dailyReportList.size()) && (i < eindex); i++) {
                        String style = i % 2 == 0 ? "" : " class='d0'";
                        out.print("<tr" + style + "><td>" + (i + 1) + "</td>");
                        out.print("<td>" + ((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_id + "</td>");
                        out.print("<td>" + ((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_name + "</td>");
                        out.print("<td>" + ((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().price + "</td>");
                        out.print("<td>" + new SimpleDateFormat("dd/MM/yy").format(((SummaryDailyReport) dailyReportList.get(i)).getDate()) + "</td>");

                        if ((type != null) && ((type.getDbId() & ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getDbId()) > 0)) {
                            out.print("<td onclick='javascript:window.location=\"SubscriberViewDetailReportServlet"
                                    + "?id=" + SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id, OperConfig.CARRIER.AIS, ((SummaryDailyReport) dailyReportList.get(i)).getDate()) 
                                    + "&type=" + SummaryReport.COMMAND.SUB_TOTAL.getId() + "\"'>"
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS_LEGACY))
                                    + "</td>");

                            out.print("<td onclick='javascript:window.location=\"SubscriberViewDetailReportServlet"
                                    + "?id=" + SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id, 
                                    OperConfig.CARRIER.DTAC, ((SummaryDailyReport) dailyReportList.get(i)).getDate())
                                    + "&type=" + SummaryReport.COMMAND.SUB_TOTAL.getId() + "\"'>"
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC_SDP))
                                    + "</td>");

                            out.print("<td onclick='javascript:window.location=\"SubscriberViewDetailReportServlet"
                                    + "?id=" + SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id, 
                                    OperConfig.CARRIER.TRUE, ((SummaryDailyReport) dailyReportList.get(i)).getDate())
                                    + "&type=" + SummaryReport.COMMAND.SUB_TOTAL.getId() + "\"' class='d1'>" 
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUE) + "</td>");
                            out.print("<td onclick='javascript:window.location=\"SubscriberViewDetailReportServlet"
                                    + "?id=" + SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id, 
                                    OperConfig.CARRIER.TRUEH, ((SummaryDailyReport) dailyReportList.get(i)).getDate())
                                    + "&type=" + SummaryReport.COMMAND.SUB_TOTAL.getId() + "\"' class='d1'>"
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUEH) + "</td>");

                            out.print("<td onclick='javascript:window.location=\"SubscriberViewDetailReportServlet"
                                    + "?id=" + SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id, 
                                    OperConfig.CARRIER.AIS, ((SummaryDailyReport) dailyReportList.get(i)).getDate())
                                    + "&type=" + SummaryReport.COMMAND.UNSUB_TOTAL.getId() + "\"'>" 
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS) 
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS_LEGACY)) + "</td>");

                            out.print("<td onclick='javascript:window.location=\"SubscriberViewDetailReportServlet"
                                    + "?id=" + SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id, 
                                    OperConfig.CARRIER.DTAC, ((SummaryDailyReport) dailyReportList.get(i)).getDate()) 
                                    + "&type=" + SummaryReport.COMMAND.UNSUB_TOTAL.getId() + "\"'>"
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC) 
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC_SDP)) + "</td>");

                            out.print("<td onclick='javascript:window.location=\"SubscriberViewDetailReportServlet"
                                    + "?id=" + SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id, 
                                    OperConfig.CARRIER.TRUE, ((SummaryDailyReport) dailyReportList.get(i)).getDate())
                                    + "&type=" + SummaryReport.COMMAND.UNSUB_TOTAL.getId() + "\"' class='d2'>"
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUE) + "</td>");
                            out.print("<td onclick='javascript:window.location=\"SubscriberViewDetailReportServlet"
                                    + "?id=" + SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id, 
                                    OperConfig.CARRIER.TRUEH, ((SummaryDailyReport) dailyReportList.get(i)).getDate())
                                    + "&type=" + SummaryReport.COMMAND.UNSUB_TOTAL.getId() + "\"' class='d2'>"
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUEH) + "</td>");

                            out.print("<td>" 
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.AIS) 
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.AIS_LEGACY)) 
                                    + "</td>");

                            out.print("<td>" 
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.DTAC) 
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.DTAC_SDP)) 
                                    + "</td>");
                            out.print("<td>" 
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.TRUE) 
                                    + "</td>");
                            out.print("<td class='d3'>" 
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.TRUEH) 
                                    + "</td>");

                            total_sub_ais += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS);
                            total_sub_ais += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS_LEGACY);
                            total_sub_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC);
                            total_sub_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC_SDP);
                            total_sub_tmv += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUE);
                            total_sub_tmh += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUEH);
                            total_unsub_ais += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS);
                            total_unsub_ais += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS_LEGACY);
                            total_unsub_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC);
                            total_unsub_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC_SDP);
                            total_unsub_tmv += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUE);
                            total_unsub_tmh += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUEH);
                            
                            try {
                                int id;

                                if (((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS)
                                        + ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS_LEGACY) > 0) {
                                    id = SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id,
                                            OperConfig.CARRIER.AIS, ((SummaryDailyReport) dailyReportList.get(i)).getDate());

                                    trackBuffer.addAll(SubscriptionTrackerFactory.get(id, SummaryReport.COMMAND.SUB_TOTAL));
                                }
                                if (((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS)
                                        + ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS_LEGACY) > 0) {
                                    id = SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id,
                                            OperConfig.CARRIER.AIS, ((SummaryDailyReport) dailyReportList.get(i)).getDate());

                                    trackBuffer.addAll(SubscriptionTrackerFactory.get(id, SummaryReport.COMMAND.UNSUB_TOTAL));
                                }

                                if (((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC)
                                        + ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC_SDP) > 0) {
                                    id = SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id,
                                            OperConfig.CARRIER.DTAC, ((SummaryDailyReport) dailyReportList.get(i)).getDate());

                                    trackBuffer.addAll(SubscriptionTrackerFactory.get(id, SummaryReport.COMMAND.SUB_TOTAL));
                                }
                                if (((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC)
                                        + ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC_SDP) > 0) {
                                    id = SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id,
                                            OperConfig.CARRIER.DTAC, ((SummaryDailyReport) dailyReportList.get(i)).getDate());

                                    trackBuffer.addAll(SubscriptionTrackerFactory.get(id, SummaryReport.COMMAND.UNSUB_TOTAL));
                                }

                                if (((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUE) > 0) {
                                    id = SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id,
                                            OperConfig.CARRIER.TRUE, ((SummaryDailyReport) dailyReportList.get(i)).getDate());

                                    trackBuffer.addAll(SubscriptionTrackerFactory.get(id, SummaryReport.COMMAND.SUB_TOTAL));
                                }
                                if (((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUE) > 0) {
                                    id = SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id,
                                            OperConfig.CARRIER.TRUE, ((SummaryDailyReport) dailyReportList.get(i)).getDate());

                                    trackBuffer.addAll(SubscriptionTrackerFactory.get(id, SummaryReport.COMMAND.UNSUB_TOTAL));
                                }

                                if (((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUEH) > 0) {
                                    id = SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id,
                                            OperConfig.CARRIER.TRUEH, ((SummaryDailyReport) dailyReportList.get(i)).getDate());

                                    trackBuffer.addAll(SubscriptionTrackerFactory.get(id, SummaryReport.COMMAND.SUB_TOTAL));
                                }
                                if (((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUEH) > 0) {
                                    id = SummaryReport.getId(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_main_id,
                                            OperConfig.CARRIER.TRUEH, ((SummaryDailyReport) dailyReportList.get(i)).getDate());

                                    trackBuffer.addAll(SubscriptionTrackerFactory.get(id, SummaryReport.COMMAND.UNSUB_TOTAL));
                                }
                            } catch (Exception e) {
                            }
                        }
                        if (((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_chrg_type_id
                                == ServiceCharge.SRVC_CHRG.PER_MESSAGE.getId()) {
                            out.print("<td title='Total "
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(OperConfig.CARRIER.AIS)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(OperConfig.CARRIER.AIS_LEGACY))
                                    + "'>"
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS_LEGACY))
                                    + "</td>");

                            out.print("<td title='Total "
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(OperConfig.CARRIER.DTAC)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(OperConfig.CARRIER.DTAC_SDP))
                                    + "'>"
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC_SDP))
                                    + "</td>");

                            out.print("<td title='Total "
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(OperConfig.CARRIER.TRUE)
                                    + "'>"
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUE)
                                    + "</td>");

                            out.print("<td title='Total "
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(OperConfig.CARRIER.TRUEH)
                                    + "'>"
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUEH)
                                    + "</td>");

                            out.print("<td>" + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(null) + "</td>");
                            int sub_total_error = ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(null)
                                    - (((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS_LEGACY)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC_SDP)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUE)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUEH));

                            out.print("<td>" + sub_total_error + "</td>");

                            out.print("<td class='total'>" + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(null) + "</td>");

                            total_charge_ais += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS);
                            total_charge_ais += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS_LEGACY);
                            total_charge_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC);
                            total_charge_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC_SDP);
                            total_charge_tmv += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUE);
                            total_charge_tmh += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUEH);
                            total_charge_success += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(null);
                            total_charge_error += sub_total_error;
                            total_charge_net += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(null);
                        } else {
                            out.print("<td title='Total "
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(OperConfig.CARRIER.AIS)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(OperConfig.CARRIER.AIS_LEGACY))
                                    + "'>"
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS_LEGACY)) 
                                    + "</td>");

                            out.print("<td title='Total "
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(OperConfig.CARRIER.DTAC)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(OperConfig.CARRIER.DTAC_SDP))
                                    + "'>"
                                    + (((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC_SDP))
                                    + "</td>");

                            out.print("<td title='Total "
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(OperConfig.CARRIER.TRUE)
                                    + "'>"
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUE)
                                    + "</td>");

                            out.print("<td title='Total "
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(OperConfig.CARRIER.TRUEH)
                                    + "'>"
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUEH) 
                                    + "</td>");
                            
                            int sub_total_error = ((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(null)
                                    - (((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS_LEGACY)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC_SDP)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUE)
                                    + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUEH));

                            out.print("<td>" + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(null) + "</td>");
                            out.print("<td>" + sub_total_error + "</td>");
                            out.print("<td class='total'>" + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(null) + "</td>");

                            total_charge_ais += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS);
                            total_charge_ais += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS_LEGACY);
                            total_charge_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC);
                            total_charge_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC_SDP);
                            total_charge_tmv += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUE);
                            total_charge_tmh += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUEH);
                            total_charge_success += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(null);
                            total_charge_error += sub_total_error;
                            total_charge_net += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(null);
                        }

                        out.print("</tr>");
                    }
                    if (dailyReportList.size() > 0) {
                        out.println("<tr><td colspan='5' class='total border_top' style='text-align:left;'>Total</th>"
                                + ((type != null) && ((type.getId() & ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getId()) > 0)
                                ? "<td class='total border_top'"
                                + " onclick='javascript:window.location=\"SubscriberViewDetailReportServlet?ssname=" + sstracker
                                + "&oper_id=" + OperConfig.CARRIER.AIS.getId()
                                + "&type=" + SummaryReport.COMMAND.SUB_TOTAL.getId()
                                + "\"'>" + total_sub_ais + "</td>"
                                + "<td class='total border_top' onclick='javascript:window.location=\""
                                + "SubscriberViewDetailReportServlet" + "?ssname=" + sstracker
                                + "&oper_id=" + OperConfig.CARRIER.DTAC.getId()
                                + "&type=" + SummaryReport.COMMAND.SUB_TOTAL.getId()
                                + "\"'>" + total_sub_dtac + "</td>"
                                + "<td class='total border_top' onclick='javascript:window.location=\""
                                + "SubscriberViewDetailReportServlet" + "?ssname=" + sstracker
                                + "&oper_id=" + OperConfig.CARRIER.TRUE.getId()
                                + "&type=" + SummaryReport.COMMAND.SUB_TOTAL.getId()
                                + "\"'>" + total_sub_tmv + "</td>"
                                + "<td class='total border_top' onclick='javascript:window.location=\""
                                + "SubscriberViewDetailReportServlet" + "?ssname=" + sstracker
                                + "&oper_id=" + OperConfig.CARRIER.TRUEH.getId()
                                + "&type=" + SummaryReport.COMMAND.SUB_TOTAL.getId()
                                + "\"'>" + total_sub_tmh + "</td>"
                                + "<td class='total border_top' onclick='javascript:window.location=\""
                                + "SubscriberViewDetailReportServlet"
                                + "?ssname=" + sstracker
                                + "&oper_id=" + OperConfig.CARRIER.AIS.getId()
                                + "&type=" + SummaryReport.COMMAND.UNSUB_TOTAL.getId()
                                + "\"'>" + total_unsub_ais + "</td>"
                                + "<td class='total border_top' onclick='javascript:window.location=\""
                                + "SubscriberViewDetailReportServlet"
                                + "?ssname=" + sstracker
                                + "&oper_id=" + OperConfig.CARRIER.DTAC.getId()
                                + "&type=" + SummaryReport.COMMAND.UNSUB_TOTAL.getId()
                                + "\"'>" + total_unsub_dtac + "</td>"
                                + "<td class='total border_top' onclick='javascript:window.location=\""
                                + "SubscriberViewDetailReportServlet"
                                + "?ssname=" + sstracker
                                + "&oper_id=" + OperConfig.CARRIER.TRUE.getId()
                                + "&type=" + SummaryReport.COMMAND.UNSUB_TOTAL.getId()
                                + "\"'>" + total_unsub_tmv + "</td>"
                                + "<td class='total border_top' onclick='javascript:window.location=\""
                                + "SubscriberViewDetailReportServlet"
                                + "?ssname=" + sstracker
                                + "&oper_id=" + OperConfig.CARRIER.TRUEH.getId()
                                + "&type=" + SummaryReport.COMMAND.UNSUB_TOTAL.getId()
                                + "\"'>" + total_unsub_tmh + "</td>"
                                + "<td colspan='4' class='total border_top' style='text-align:left;'>&nbsp;</td>" : "")
                                + "<td class='total border_top'>" + total_charge_ais + "</td>"
                                + "<td class='total border_top'>" + total_charge_dtac + "</td>"
                                + "<td class='total border_top'>" + total_charge_tmv + "</td>"
                                + "<td class='total border_top'>" + total_charge_tmh + "</td>"
                                + "<td class='total border_top'>" + total_charge_success + "</td>"
                                + "<td class='total border_top'>" + total_charge_error + "</td>"
                                + "<td class='total border_top'>" + total_charge_net + "</td>"
                                + "</tr>");

                        request.getSession().setAttribute(sstracker, trackBuffer);
                    }
                }
                out.println("</table></div></body></html>");
            }
        } finally {
            out.close();
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "ISO8859_1");
        String csv = request.getParameter("csv");
        String stype = request.getParameter("stype");
        ServiceElement.SERVICE_TYPE type = ServiceElement.SERVICE_TYPE.ALL;
        if ((stype != null) && (StringConvert.isDigit(stype))) {
            type = ServiceElement.SERVICE_TYPE.fromId(Integer.parseInt(stype));
        }

        MultipartResponse multi = new MultipartResponse(response);
        try {
            if ((csv != null) && (csv.equals("1"))) {
                int total_sub_ais = 0;
                int total_sub_dtac = 0;
                int total_sub_tmv = 0;
                int total_sub_tmh = 0;
                int total_unsub_ais = 0;
                int total_unsub_dtac = 0;
                int total_unsub_tmv = 0;
                int total_unsub_tmh = 0;
                int total_charge_ais = 0;
                int total_charge_dtac = 0;
                int total_charge_tmv = 0;
                int total_charge_tmh = 0;
                int total_charge_success = 0;
                int total_charge_error = 0;
                int total_charge_net = 0;

                List dailyReportList = (List) request.getSession().getAttribute("dailyReportList");

                if (dailyReportList == null) {
                    response.sendError(204);
                }

                multi.startResponse("text/csv;charset=tis-620");
                response.setHeader("Content-disposition", "attachment; filename=Summary_Daily_Report_" + DatetimeUtil.getDateTime("yyyyMMddHHmmss") + ".csv");

                out.append("No.,Shortcode,Service,Price,Date," 
                        + ((type != null) && ((type.getId() & ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getId()) > 0) 
                        ? "sub_total_ais,sub_total_dtac,sub_total_tmv,sub_total_tmh,"
                        + "unsub_total_ais,unsub_total_dtac,unsub_total_tmv,unsub_total_tmh,"
                        + "sub_balance_ais,sub_balance_dtac,sub_balance_tmv,sub_balance_tmh," 
                        : "") 
                        + "charging_success_ais,charging_success_dtac,charging_success_tmv,charging_success_tmh,"
                        + "charging_success_total,charging_error,charging_total" 
                        + ",\r\n");

                for (int i = 0; i < dailyReportList.size(); i++) {
                    out.append(i + 1 + ",");
                    out.append(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_id + ",");
                    out.append(StringConvert.Unicode2ASCII2(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_name) + ",");
                    out.append(((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().price + ",");
                    out.append(new SimpleDateFormat("MM/dd/yy").format(((SummaryDailyReport) dailyReportList.get(i)).getDate()) + ",");

                    if ((type != null) && ((type.getId() & ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getId()) > 0)) {
                        out.append((((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS_LEGACY)) + ",");
                        out.append((((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC_SDP)) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUE) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUEH) + ",");
                        
                        out.append((((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS_LEGACY)) + ",");
                        out.append((((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC_SDP)) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUE) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUEH) + ",");
                        
                        out.append((((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.AIS)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.AIS_LEGACY)) + ",");
                        out.append((((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.DTAC)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.DTAC_SDP)) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.TRUE) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getSub_balance(OperConfig.CARRIER.TRUEH) + ",");
                        
                        total_sub_ais += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS);
                        total_sub_ais += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.AIS_LEGACY);
                        total_sub_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC);
                        total_sub_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.DTAC_SDP);
                        total_sub_tmv += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUE);
                        total_sub_tmh += ((SummaryDailyReport) dailyReportList.get(i)).getSub_total(OperConfig.CARRIER.TRUEH);
                        total_unsub_ais += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS);
                        total_unsub_ais += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.AIS_LEGACY);
                        total_unsub_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC);
                        total_unsub_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.DTAC_SDP);
                        total_unsub_tmv += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUE);
                        total_unsub_tmh += ((SummaryDailyReport) dailyReportList.get(i)).getUnsub_total(OperConfig.CARRIER.TRUEH);
                    }

                    if (((SummaryDailyReport) dailyReportList.get(i)).getServiceElement().srvc_chrg_type_id 
                            == ServiceCharge.SRVC_CHRG.PER_MESSAGE.getId()) {
                        
                        out.append(( ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS_LEGACY)) + ",");
                        out.append(( ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC_SDP)) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUE) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUEH) + ",");
                        
                        int sub_total_error = ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(null) 
                                - (((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS) 
                                + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS_LEGACY)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC_SDP)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUE)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUEH));
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(null) + ",");
                        out.append(sub_total_error + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(null) + ",");


                        total_charge_ais += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS);
                        total_charge_ais += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.AIS_LEGACY);
                        total_charge_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC);
                        total_charge_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.DTAC_SDP);
                        total_charge_tmv += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUE);
                        total_charge_tmh += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(OperConfig.CARRIER.TRUEH);
                        total_charge_success += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_balance(null);
                        total_charge_error += sub_total_error;
                        total_charge_net += ((SummaryDailyReport) dailyReportList.get(i)).getMt_chrg_total(null);
                    } else {
                        
                        out.append(( ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS_LEGACY)) + ",");
                        out.append(( ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC_SDP)) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUE) + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUEH) + ",");
                        
                        int sub_total_error = ((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(null) 
                                - (((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS_LEGACY)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC_SDP)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUE)
                                + ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUEH));
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(null) + ",");
                        out.append(sub_total_error + ",");
                        out.append(((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(null) + ",");

                        total_charge_ais += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS);
                        total_charge_ais += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.AIS_LEGACY);
                        total_charge_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC);
                        total_charge_dtac += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.DTAC_SDP);
                        total_charge_tmv += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUE);
                        total_charge_tmh += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(OperConfig.CARRIER.TRUEH);
                        total_charge_success += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_balance(null);
                        total_charge_error += sub_total_error;
                        total_charge_net += ((SummaryDailyReport) dailyReportList.get(i)).getRcur_total(null);
                    }
                    out.append("\r\n");
                }

                out.append("Total,,,,," 
                        + ((type != null) && ((type.getId() & ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getId()) > 0) 
                        ? total_sub_ais + "," + total_sub_dtac + "," + total_sub_tmv + "," + total_sub_tmh + "," 
                        + total_unsub_ais + "," + total_unsub_dtac + "," + total_unsub_tmv + "," + total_unsub_tmh + "," 
                        + ",,," 
                        : "") 
                        + total_charge_ais + "," + total_charge_dtac + "," + total_charge_tmv + "," + total_charge_tmh + "," 
                        + total_charge_success + "," + total_charge_error + "," + total_charge_net + "," 
                        + "\r\n");

                out.flush();
                multi.endResponse();
            }
        } finally {
            multi.finish();
            out.close();
        }
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