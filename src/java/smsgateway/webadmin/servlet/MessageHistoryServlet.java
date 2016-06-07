package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.txqueuereport.TxQueueReportSortByCarrier;
import hippoping.smsgw.api.comparator.txqueuereport.TxQueueReportSortByDeliver;
import hippoping.smsgw.api.comparator.txqueuereport.TxQueueReportSortByServiceName;
import hippoping.smsgw.api.comparator.txqueuereport.TxQueueReportSortByShortcode;
import hippoping.smsgw.api.comparator.txqueuereport.TxQueueReportSortByStatus;
import hippoping.smsgw.api.comparator.txqueuereport.TxQueueReportSortByType;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.MessageMms;
import hippoping.smsgw.api.db.MessageSms;
import hippoping.smsgw.api.db.MessageWap;
import hippoping.smsgw.api.db.OperConfig.CARRIER;
import hippoping.smsgw.api.db.ServiceElement;
import hippoping.smsgw.api.db.TxQueue;
import hippoping.smsgw.api.db.User;
import hippoping.smsgw.api.db.report.TxQueueReport;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DatetimeUtil;

public class MessageHistoryServlet extends HttpServlet {

    protected int rows = 0;
    protected String old_orderby = "";
    protected int sort = 0;

    private void sort(List<TxQueueReport> tqrList, String field, int swap) {
        Comparator comparator = null;

        if ((this.old_orderby != null) && (this.old_orderby.equals(field))) {
            if (swap == 1) {
                this.sort = (++this.sort % 2);
            }
        } else {
            this.sort = 0;
            this.old_orderby = field;
        }

        if (field.equals("shortcode")) {
            comparator = new TxQueueReportSortByShortcode();
        } else if (field.equals("service")) {
            comparator = new TxQueueReportSortByServiceName();
        } else if (field.equals("carrier")) {
            comparator = new TxQueueReportSortByCarrier();
        } else if (field.equals("deliver")) {
            comparator = new TxQueueReportSortByDeliver();
        } else if (field.equals("status")) {
            comparator = new TxQueueReportSortByStatus();
        } else if (field.equals("type")) {
            comparator = new TxQueueReportSortByType();
        }
        if (comparator != null) {
            Collections.sort(tqrList, comparator);
            if (this.sort == 1) {
                Collections.reverse(tqrList);
            }
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
                String msisdn = "";
                String srvcid = "";
                String operid = "";
                String orderby = request.getParameter("orderby");
                String page = request.getParameter("page");
                String swap = request.getParameter("swap");
                String fdate = request.getParameter("fdate");
                String tdate = request.getParameter("tdate");

                String cmd = request.getParameter("cmd");

                if ((page == null) || (page.equals(""))) {
                    page = "1";
                }

                if ((cmd != null) && (cmd.equals("cancel"))) {
                    cmd = "refresh";
                    long txqid = 0;
                    try {
                        txqid = Long.parseLong(request.getParameter("txqid"));
                    } catch (Exception e) {
                    }
                    try {
                        TxQueue txQueue = new TxQueue(txqid);
                        if ((txQueue != null) && (txQueue.cancel() > 0)) {
                            out.println("<script>alert('Cancel queue successfully.');</script>");
                        } else {
                            out.println("<script>alert('Cancellation failed.');</script>");
                        }
                    } catch (Exception e) {
                    }
                }
                if ((cmd != null) && (cmd.equals("refresh"))) {
                    msisdn = request.getParameter("msisdn");
                    srvcid = request.getParameter("srvcid");
                    operid = request.getParameter("operid");
                    if (operid == null) {
                        operid = Integer.toString(CARRIER.ALL.getId());
                    }

                    fdate = request.getParameter("fdate");
                    tdate = request.getParameter("tdate");
                    this.rows = Integer.parseInt(request.getParameter("rows"));

                    if ((srvcid == null) || (srvcid.equals(""))) {
                        srvcid = "-1";
                    }
                    if ((operid == null) || (operid.equals(""))) {
                        operid = "-1";
                    }

                    request.getSession().setAttribute("messageHistory",
                            TxQueueReport.getMessageHistory(msisdn, Integer.parseInt(srvcid),
                            Integer.parseInt(operid), "", -1, -1, fdate, tdate, user));

                    LogEvent.log(LogEvent.EVENT_TYPE.MESSAGE_HISTORY,
                            LogEvent.EVENT_ACTION.SEARCH, "view message history",
                            (User) request.getSession().getAttribute("USER"), msisdn,
                            CARRIER.fromId(Integer.parseInt(operid)), Integer.parseInt(srvcid), 0, 0,
                            LogEvent.LOG_LEVEL.INFO);
                }

                List tqrList = (List) request.getSession().getAttribute("messageHistory");

                if ((tqrList != null) && (tqrList.size() > 0)) {
                    sort(tqrList, orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = tqrList.size() / this.rows + (tqrList.size() % this.rows != 0 ? 1 : 0);

                pg = pg == 0 ? 1 : pg;
                out.println("<html><head>"
                        + "    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>"
                        + "    <link href='./css/cv.css' rel='stylesheet' type='text/css'>"
                        + "    <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>"
                        + "    <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>"
                        + "    <style type='text/css'>"
                        + "        body{margin:0px; padding: 0px; background: white;"
                        + "            font: 100.01% 'Trebuchet MS',Verdana,Arial,sans-serif}"
                        + "        h1,h2,p{margin: 0 10px}"
                        + "        h1{font-size: 250%;color: #FFF}"
                        + "        h2{font-size: 200%;color: #f0f0f0}"
                        + "        p{padding-bottom:1em}"
                        + "        h2{padding-top: 0.3em}"
                        + "        div#memberViewContent {background: #377CB1;}"
                        + "    </style>"
                        + "    <script src='./js/nifty.js' type='text/javascript'></script>"
                        + "    <script src='./js/utils.js' type='text/javascript'></script>"
                        + "    <script src='./js/filter_input.js' type='text/javascript'></script>"
                        + "    <script>"
                        + "    function validate_page(page, maxpage) {"
                        + "        var frm = document.forms[\"reloadFrm\"];"
                        + "        if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}"
                        + "        else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}"
                        + "        else {frm.submit();}"
                        + "    }"
                        + "    function goto_page(page) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.page.value=page;"
                        + "       frm.submit();"
                        + "    }"
                        + "    function doEdit(link) {"
                        + "       window.parent.location=link;"
                        + "    }"
                        + "    function doCancel(id, msg) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='cancel';"
                        + "       frm.txqid.value=id;"
                        + "       if (!msg) msg='';"
                        + "       if (confirm('Click OK to confirm to cancel message \"' + msg + '\", otherwise click cancel.'))"
                        + "           frm.submit();"
                        + "    }"
                        + "    function expandMsg(id, msg) {"
                        + "       getElement(id).innerHTML=msg;"
                        + "    }"
                        + "    </script>"
                        + "</head>"
                        + "<body style='background-color:#FFF;'>"
                        + "   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;;'>"
                        + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, "
                        + pg
                        + ");'>"
                        + "       <input type=hidden name=cmd value=''>"
                        + "       <input type=hidden name=txqid value=''>"
                        + (msisdn != null ? "       <input type=hidden name=msisdn value='" + msisdn + "'>" : "")
                        + (srvcid != null ? "       <input type=hidden name=srvcid value='" + srvcid + "'>" : "")
                        + "       <input type=hidden name=fdate value='" + fdate + "'>"
                        + "       <input type=hidden name=tdate value='" + tdate + "'>"
                        + "       <input type=hidden name=orderby value='" + orderby + "'>"
                        + "       <input type=hidden name=rows value='" + this.rows + "'>"
                        + "       <input type=hidden name=swap value='0'>"
                        + "       <input type=hidden name=csv value='0'>"
                        + "       <div class='floatl' style='font-size:75%; padding-left:5px;'><b>Total " + tqrList.size() + " record(s) found."
                        + "           (Page " + page + " of " + pg + ")</b> "
                        + "       </div>"
                        + "       <div class='floatr'>"
                        + "         <span style='padding:0;'>"
                        + (Integer.parseInt(page) > 1
                        ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'>"
                        + "<img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>"
                        : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>")
                        + (Integer.parseInt(page) < pg
                        ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'>"
                        + "<img src='images/next.gif' border=0 style='vertical-align:middle;'></a>"
                        : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>")
                        + "         </span>"
                        + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span>"
                        + "           <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>"
                        + "           <input type=submit value=go>"
                        + "       </div>"
                        + "       </form>"
                        + "       <table class='table3' style='width:100%;padding:0;'>"
                        + "       <tr>"
                        + "           <th width='3%'>No.</th>"
                        + "           <th width='10%'>"
                        + (orderby.equals("service") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "<a href='javascript:frm=document.forms[\"reloadFrm\"];"
                        + "frm.orderby.value=\"service\";frm.swap.value=1;frm.submit();'>Service</a></th>"
                        + "           <th width='10%'>"
                        + (orderby.equals("shortcode") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "<a href='javascript:frm=document.forms[\"reloadFrm\"];"
                        + "frm.orderby.value=\"shortcode\";frm.swap.value=1;frm.submit();'>Shortcode</a></th>"
                        + "           <th width='7%'>"
                        + (orderby.equals("carrier") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "<a href='javascript:frm=document.forms[\"reloadFrm\"];"
                        + "frm.orderby.value=\"carrier\";frm.swap.value=1;frm.submit();'>Carrier</a></th>"
                        + "           <th width='35%'>Message</th>"
                        + "           <th width='5%'>"
                        + (orderby.equals("type") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "<a href='javascript:frm=document.forms[\"reloadFrm\"];"
                        + "frm.orderby.value=\"type\";frm.swap.value=1;frm.submit();'>Type</a></th>"
                        + "           <th width='15%'>"
                        + (orderby.equals("deliver") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "<a href='javascript:frm=document.forms[\"reloadFrm\"];"
                        + "frm.orderby.value=\"deliver\";frm.swap.value=1;frm.submit();'>Deliver</a></th>"
                        + "           <th width='5%'>"
                        + (orderby.equals("status") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "<a href='javascript:frm=document.forms[\"reloadFrm\"];"
                        + "frm.orderby.value=\"status\";frm.swap.value=1;frm.submit();'>Status</a></th>"
                        + "           <th width='10%'>Description</th>"
                        + "           <th width='10%'>Action</th>"
                        + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                for (int i = sindex; (i < tqrList.size()) && (i < eindex); i++) {
                    String style = i % 2 == 0 ? "" : " d0";
                    switch (((TxQueueReport) tqrList.get(i)).getTxQueue().getStatus()) {
                        case ERROR:
                            style += " d1";
                            break;
                        case SENT:
                            style += " d2";
                            break;
                        case SENDING:
                            style += " d3";
                            break;
                        case CANCEL:
                            style += " d4";
                            break;
                        case QUEUE:
                            style += " d5";
                            break;
                    }

                    style = " class='" + style + "'";
                    try {
                        ServiceElement se = new ServiceElement(((TxQueueReport) tqrList.get(i)).getTxQueue().srvc_main_id, ((TxQueueReport) tqrList.get(i)).getTxQueue().oper_id, ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ON.getId() | ServiceElement.SERVICE_STATUS.TEST.getId());
                        String message = "";
                        String full_message = "";
                        String title = "";
                        String link = "push_message_%s.jsp?txqid=%d";
                        boolean msg_ok = true;
                        long id = ((TxQueueReport) tqrList.get(i)).getId();
                        int ctnt_id = ((TxQueueReport) tqrList.get(i)).getMessage().getContent_id();
                        if ((((TxQueueReport) tqrList.get(i)).getMessage() instanceof MessageSms)) {
                            MessageSms sms = (MessageSms) ((TxQueueReport) tqrList.get(i)).getMessage();
                            switch (sms.getSmsType()) {
                                case TEXT:
                                    for (int j = 0; j < sms.getMessage_num(); j++) {
                                        full_message = full_message + sms.getContentSub()[j];
                                    }

                                    message = sms.getContentSub()[0];
                                    title = message;

                                    int maxchar = 40;
                                    if (message.length() > maxchar) {
                                        message = message.substring(0, maxchar);
                                        title = message;
                                        message += "<a class='simpletext'"
                                                + " href='javascript:expandMsg(\"dsp_" + id + "\",\"" + full_message + "\");'>...</a>";
                                    }

                                    if (sms.getMessage_num() > 1) {
                                        message = message + "[concatenated]";
                                    }

                                    link = String.format(link, new Object[]{"sms_text", Long.valueOf(((TxQueueReport) tqrList.get(i)).getTxQueue().getTx_queue_id())});
                                    break;
                                case PICTURE:
                                    message = "<img src='header?file=" + sms.getFilename() + "' border='0' title='" + sms.getMessageInfo().getTitle() + "'>";

                                    title = sms.getMessageInfo().getTitle();

                                    link = String.format(link, new Object[]{"sms_picture", Long.valueOf(((TxQueueReport) tqrList.get(i)).getTxQueue().getTx_queue_id())});
                                    break;
                                case RINGTONE:
                                    title = sms.getFilename().substring(sms.getFilename().lastIndexOf('/') + 1).trim();
                                    message = "<img src='images/music_note16.png' border='0' style='vertical-align: top'> " + title;
                                    link = String.format(link, new Object[]{"sms_ringtone", Long.valueOf(((TxQueueReport) tqrList.get(i)).getTxQueue().getTx_queue_id())});
                            }
                        } else if ((((TxQueueReport) tqrList.get(i)).getMessage() instanceof MessageWap)) {
                            MessageWap wap = (MessageWap) ((TxQueueReport) tqrList.get(i)).getMessage();
                            message = wap.title + " [" + wap.url + "]";
                            title = wap.url;

                            link = String.format(link, new Object[]{"sms_wap", Long.valueOf(((TxQueueReport) tqrList.get(i)).getTxQueue().getTx_queue_id())});
                        } else if ((((TxQueueReport) tqrList.get(i)).getMessage() instanceof MessageMms)) {
                            MessageMms mms = (MessageMms) ((TxQueueReport) tqrList.get(i)).getMessage();
                            message = new MessageMmsDetail().printDetails(mms.getContent_id());
                            title = mms.getSubject();

                            link = String.format(link, new Object[]{"mms", Long.valueOf(((TxQueueReport) tqrList.get(i)).getTxQueue().getTx_queue_id())});
                        } else {
                            message = "<font color='red'>Message Error!!</font>";
                            msg_ok = false;
                        }

                        if (!full_message.isEmpty()) {
                            out.print("<input type='hidden' name='msg_" + id + "' value='" + full_message + "'>");
                        }
                        out.print("<tr" + style + "><td title='id:" + id + "'>" + (i + 1) + "</td>");
                        out.print("<td title='id:" + se.srvc_main_id + "'>" + se.srvc_name + "</td>");
                        out.print("<td>" + se.srvc_id + "</td>");
                        out.print("<td>" + CARRIER.fromId(((TxQueueReport) tqrList.get(i)).getTxQueue().oper_id).name() + "</td>");
                        out.print("<td id='dsp_" + id + "'" + (!full_message.isEmpty() ? " title='" + full_message + "'" : "") + ">" + message + "</td>");
                        out.print("<td title='id:" + ctnt_id + "'>" + (((TxQueueReport) tqrList.get(i)).getContent_Type().equals("SMS") ? ((TxQueueReport) tqrList.get(i)).getMessage().getSmsType() : ((TxQueueReport) tqrList.get(i)).getMessage() == null ? ((TxQueueReport) tqrList.get(i)).getContent_Type() : ((TxQueueReport) tqrList.get(i)).getContent_Type()) + "</td>");

                        out.print("<td>" + DatetimeUtil.changeDateFormat(((TxQueueReport) tqrList.get(i)).getTxQueue().deliver_dt, "yyyy-MM-dd HH:mm:ss", "dd-MM-yy HH:mm:ss") + "</td>");
                        out.print("<td>" + ((TxQueueReport) tqrList.get(i)).getTxQueue().getStatus().toString() + "</td>");
                        out.print("<td>" + ((((TxQueueReport) tqrList.get(i)).getTxQueue().getStatus().getId() >= TxQueue.TX_STATUS.SENT.getId()) && (((TxQueueReport) tqrList.get(i)).getTxQueue().status_desc != null) ? ((TxQueueReport) tqrList.get(i)).getTxQueue().status_desc : "-") + "</td>");

                        out.print("<td>");
                        if ((((TxQueueReport) tqrList.get(i)).getTxQueue().getStatus() == TxQueue.TX_STATUS.QUEUE) || (((TxQueueReport) tqrList.get(i)).getTxQueue().getStatus() == TxQueue.TX_STATUS.ERROR)) {
                            out.print(" <a href='javascript:doEdit(\"" + link + "\")'>" + "<img src='./images/edit02.gif' border='0'>" + "</a>");

                            out.print(" <a href='javascript:doCancel(" + ((TxQueueReport) tqrList.get(i)).getTxQueue().getTx_queue_id()
                                    + (msg_ok ? ",\"" + title + "\"" : "") + ")'>"
                                    + "<img src='./images/trash.gif' border='0'>" + "</a>");
                        }

                        out.print("</td>");
                        out.print("</tr>");
                    } catch (Exception e) {
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