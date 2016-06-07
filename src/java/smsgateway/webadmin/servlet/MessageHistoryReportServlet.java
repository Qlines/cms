package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.MessageDetailReport.MessageDetailSortByDate;
import hippoping.smsgw.api.content.manage.MessageDetail;
import hippoping.smsgw.api.db.DeliveryReport;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.MessageMms;
import hippoping.smsgw.api.db.MessageSms;
import hippoping.smsgw.api.db.MessageWap;
import hippoping.smsgw.api.db.OperConfig.CARRIER;
import hippoping.smsgw.api.db.RxMoQueue;
import hippoping.smsgw.api.db.RxQueue;
import hippoping.smsgw.api.db.ServiceElement;
import hippoping.smsgw.api.db.Subscriber;
import hippoping.smsgw.api.db.SubscriberGroup;
import hippoping.smsgw.api.db.TxQueue;
import hippoping.smsgw.api.db.User;
import hippoping.smsgw.api.db.report.MessageDetailReport;
import hippoping.smsgw.api.db.report.TxQueueReport;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DatetimeUtil;
import lib.common.StringConvert;

public class MessageHistoryReportServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(MessageHistoryReportServlet.class.getName());
    protected int rows = 0;
    protected String old_orderby = "";
    protected int sort = 0;
    private static String[] chrg_img = {"warning16.gif", "delete16.gif", "accept16.gif", "blank.gif"};

    private void sort(List<MessageDetail> mdList, String field, int swap) {
        Comparator comparator = null;

        if ((this.old_orderby != null) && (this.old_orderby.equals(field))) {
            if (swap == 1) {
                this.sort = (++this.sort % 2);
            }
        } else {
            this.sort = 0;
            this.old_orderby = field;
        }

        if (field.equals("deliver")) {
            comparator = new MessageDetailSortByDate();
        }
        if (comparator != null) {
            Collections.sort(mdList, comparator);
            if (this.sort == 1) {
                Collections.reverse(mdList);
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
                out.print("<script>window.location='logout?msg=Your session has been expired! Please relogin the page.'</script>");
            } else {
                String msisdn = request.getParameter("msisdn");
                String srvcid = "";
                String operid = "";
                String orderby = request.getParameter("orderby");
                String page = request.getParameter("page");
                String swap = request.getParameter("swap");

                String cmd = request.getParameter("cmd");

                if ((page == null) || (page.equals(""))) {
                    page = "1";
                }

                if ((cmd != null) && (cmd.equals("refresh"))) {
                    srvcid = request.getParameter("srvcid");
                    operid = request.getParameter("operid");
                    this.rows = Integer.parseInt(request.getParameter("rows"));

                    if ((srvcid == null) || (srvcid.equals(""))) {
                        srvcid = "-1";
                    }
                    if ((operid == null) || (operid.equals(""))) {
                        operid = "-1";
                    }

                    String begindate = null;
                    String enddate = null;
                    try {
                        Subscriber sub = new Subscriber(msisdn, Integer.parseInt(srvcid), CARRIER.fromId(Integer.parseInt(operid)));
                        if (sub.getState() != SubscriberGroup.SUB_STATUS.REGISTER.getId()) {
                            enddate = sub.getExpired_date("yyyy-MM-dd");
                        } else {
                            enddate = DatetimeUtil.getDateTime("yyyy-MM-dd");
                        }
                    } catch (Exception e) {
                    }

                    request.getSession().setAttribute("messageHistoryReport",
                            MessageDetailReport.getMessageHistory(msisdn, Integer.parseInt(srvcid),
                                    Integer.parseInt(operid), -1, -1, begindate, enddate));

                    LogEvent.log(LogEvent.EVENT_TYPE.MESSAGE_HISTORY,
                            LogEvent.EVENT_ACTION.SEARCH, "view message history",
                            (User) request.getSession().getAttribute("USER"), msisdn,
                            CARRIER.fromId(Integer.parseInt(operid)), Integer.parseInt(srvcid), 0, 0,
                            LogEvent.LOG_LEVEL.INFO);
                }

                List mdList = (List) request.getSession().getAttribute("messageHistoryReport");

                if ((mdList != null) && (mdList.size() > 0)) {
                    sort(mdList, orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = mdList.size() / this.rows + (mdList.size() % this.rows != 0 ? 1 : 0);

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
                        + "        img{vertical-align:top}"
                        + "        h2{padding-top: 0.3em}"
                        + "        div#memberViewContent {background: #377CB1;}"
                        + "    </style>"
                        + "    <script src='./js/nifty.js' type='text/javascript'></script>"
                        + "    <script src='./js/utils.js' type='text/javascript'></script>"
                        + "    <script src='./js/filter_input.js' type='text/javascript'></script>"
                        + "    <script>"
                        + "    function doPlay(id, url) {"
                        + "       var pdiv = getElement(id);"
                        + "       if (active_player) active_player.innerHTML='';"
                        + "       if (pdiv) {"
                        + "           pdiv.innerHTML = \"<img title='stop' src='images/stop.gif'"
                        + " style='vertical-align:middle'"
                        + " onclick='doStop(\\\"\" + id + \"\\\");'> now playing"
                        + "                            <embed src='\" + url + \"' hidden=true autostart=true loop=false>\";"
                        + "           pdiv.style.display = 'block';"
                        + "           active_player = pdiv;"
                        + "       }"
                        + "    }"
                        + "    function doStop(id) {"
                        + "       var pdiv = getElement(id);"
                        + "       if (pdiv) {"
                        + "           pdiv.innerHTML = '';"
                        + "           pdiv.style.display = 'none';"
                        + "       }"
                        + "    }"
                        + "    function validate_page(page, maxpage) {"
                        + "        var frm = document.forms[\"reloadFrm\"];"
                        + "       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}"
                        + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}"
                        + "       else {frm.submit();}"
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
                        + "       if (confirm('Click OK to confirm to cancel message \"' + msg + '\", otherwise click cancel.'))"
                        + "           frm.submit();"
                        + "    }"
                        + "    </script>"
                        + "</head>"
                        + "<body style='background-color:#FFF;'>"
                        + "   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;;'>"
                        + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>"
                        + "       <input type=hidden name=cmd value=''>"
                        + "       <input type=hidden name=txqid value=''>"
                        + (msisdn != null
                        ? "       <input type=hidden name=msisdn value='" + msisdn + "'>" : "")
                        + (srvcid != null
                        ? "       <input type=hidden name=srvcid value='" + srvcid + "'>" : "")
                        + "       <input type=hidden name=orderby value='" + orderby + "'>"
                        + "       <input type=hidden name=rows value='" + this.rows + "'>"
                        + "       <input type=hidden name=swap value='0'>"
                        + "       <input type=hidden name=csv value='0'>"
                        + "       <div class='floatl' style='font-size:75%; padding-left:5px;'><b>Total " + mdList.size() + " record(s) found."
                        + "           (Page " + page + " of " + pg + ")</b> "
                        + "       </div>"
                        + "       <div class='floatr'>"
                        + "         <span style='padding:0;'>" + (Integer.parseInt(page) > 1
                        ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>"
                        : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>")
                        + (Integer.parseInt(page) < pg
                        ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>"
                        : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>")
                        + "         </span>"
                        + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span>"
                        + " <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>"
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
                        + "           <th width='10%'>Result</th>"
                        + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;

                ServiceElement se = null;
                for (int i = sindex; (i < mdList.size()) && (i < eindex); i++) {
                    try {
                        String charge = null;
                        int chrg_success = 0;
                        String style = i % 2 == 0 ? "" : " d0";
                        String message = "";
                        String full_message = "";
                        CARRIER oper = null;
                        String content_type = "";
                        String status = "";
                        String status_desc = "";
                        String dr_desc = "";
                        long id = 0;
                        int ctnt_id = 0;

                        MessageDetail md = (MessageDetail) mdList.get(i);
                        if ((md instanceof TxQueueReport)) {
                            TxQueueReport tqr = (TxQueueReport) md;
                            id = tqr.getId();
                            ctnt_id = tqr.getTxQueue().content_id;
                            charge = tqr.getTxQueue().chrg_flg;
                            oper = CARRIER.fromId(tqr.getTxQueue().oper_id);
                            content_type = tqr.getTxQueue().content_type.toString();
                            status = tqr.getTxQueue().getStatus().toString();
                            status_desc = (tqr.getTxQueue().getStatus().getId() >= TxQueue.TX_STATUS.SENT.getId())
                                    && (tqr.getTxQueue().status_desc != null)
                                    ? tqr.getTxQueue().status_desc
                                    : "-";

                            if ((se == null) || (se.srvc_main_id != tqr.getTxQueue().srvc_main_id)) {
                                se = new ServiceElement(tqr.getTxQueue().srvc_main_id, tqr.getTxQueue().oper_id, ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ON.getId() | ServiceElement.SERVICE_STATUS.TEST.getId());
                            }

                            if (tqr.getTxQueue().oper_id == CARRIER.AIS_LEGACY.getId()) {
                                chrg_success = (tqr.getTxQueue().status_code == 0)
                                        && (tqr.getTxQueue().status_desc != null)
                                        && (tqr.getTxQueue().status_desc.substring(0, 2).equals("OK")) ? 2 : 1;
                            } else {
                                if (tqr.getTxQueue().chrg_flg.equals("MO")) {
                                    chrg_success = 3; // no need to display charging result
                                } else {
                                    try {
                                        List drList = DeliveryReport.get(tqr.getTxQueue(), msisdn);
                                        if (drList == null) {
                                            chrg_success = 0;
                                        } else {
                                            for (int j = 0; j < drList.size(); j++) {
                                                dr_desc = ((DeliveryReport) drList.get(j)).getStatus_desc()
                                                        + ((((DeliveryReport) drList.get(j)).getStatus_text() != null)
                                                        && (!((DeliveryReport) drList.get(j)).getStatus_text().isEmpty())
                                                        ? "|" + ((DeliveryReport) drList.get(j)).getStatus_text()
                                                        : "");

                                                chrg_success = ((DeliveryReport) drList.get(j)).isChargeSuccess() ? 2 : 1;
                                                if (chrg_success == 2) {
                                                    break;
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        log.severe(e.getMessage());
                                    }
                                }
                            }
                            switch (tqr.getTxQueue().getStatus()) {
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

                            if ((tqr.getMessage() instanceof MessageSms)) {
                                MessageSms sms = (MessageSms) tqr.getMessage();
                                switch (sms.getSmsType()) {
                                    case TEXT:
                                        message = sms.getContentSub()[0];
                                        int maxchar = 40;
                                        if (message.length() > maxchar) {
                                            message = message.substring(0, maxchar) + "...";
                                        }

                                        if (sms.getMessage_num() > 1) {
                                            message = message + "[concatenated]";
                                        }

                                        for (int j = 0; j < sms.getMessage_num(); j++) {
                                            full_message = full_message + sms.getContentSub()[j];
                                        }

                                        break;
                                    case PICTURE:
                                        message = "<img src='header?file=" + sms.getFilename() + "' border='0'"
                                                + " title='" + StringConvert.replace(sms.getMessageInfo().getTitle(), "'", "\\\\&#39;", true)
                                                + "'>";

                                        break;
                                    case RINGTONE:
                                        String title = sms.getFilename().substring(sms.getFilename().lastIndexOf('/') + 1).trim();
                                        message = "<img src='images/music_note16.png' border='0' style='vertical-align: top'> " + title;
                                }
                            } else if ((tqr.getMessage() instanceof MessageWap)) {
                                MessageWap wap = (MessageWap) tqr.getMessage();
                                message = wap.title + " [" + wap.url + "]";
                            } else if ((tqr.getMessage() instanceof MessageMms)) {
                                MessageMms mms = (MessageMms) tqr.getMessage();
                                message = new MessageMmsDetail().printDetails(mms.getContent_id());
                            } else {
                                message = "<font color='red'>The message type cannot be display!!</font>";
                            }
                        } else if ((md instanceof RxMoQueue)) {
                            RxMoQueue rx = (RxMoQueue) md;
                            id = rx.getId();
                            oper = CARRIER.fromId(rx.oper_id);
                            chrg_success = 3;
                            style = style + " d3";

                            if ((se == null) || (se.srvc_main_id != rx.getSrvc_main_id())) {
                                se = new ServiceElement(rx.getSrvc_main_id(), rx.oper_id,
                                        ServiceElement.SERVICE_TYPE.ALL.getId(),
                                        ServiceElement.SERVICE_STATUS.ON.getId() | ServiceElement.SERVICE_STATUS.TEST.getId());
                            }

                            content_type = RxQueue.RX_TYPE.fromId(rx.type & ((RxQueue.RX_TYPE.SUB.getId() | RxQueue.RX_TYPE.UNSUB.getId()) ^ 0xFFFFFFFF)).toString();
                            if (rx.getContent_Type() == null) {
                                message = rx.content;
                            } else if (rx.getContent_Type().matches("(?i)(image)")) {
                                message = "<img src='header?file=" + rx.content + "' border=0>";
                            } else if (rx.getContent_Type().matches("(?i)(audio)")) {
                                message = "<img src='images/play.gif' border=0"
                                        + " onclick='doPlay(\"playerDiv_" + rx.getContent_id() + "\", \"header?file=" + rx.content + "\");'"
                                        + ">" + rx.content.substring(rx.content.lastIndexOf('/') + 1).trim() + "<br>"
                                        + "<div id='playerDiv_" + rx.getContent_id() + "'"
                                        + " style='width:100%;line-height:.8em;display:none;position:relative;left:0;top:0;"
                                        + "background:transparent;padding:0;margin:0;font-size:75%;'>"
                                        + "</div>";
                            } else {
                                message = "content format (" + rx.getContent_Type() + ") not supported";
                            }

                            if ((rx.type & RxQueue.RX_TYPE.SUB.getId()) > 0) {
                                status = RxQueue.RX_TYPE.SUB.toString();
                            } else if ((rx.type & RxQueue.RX_TYPE.UNSUB.getId()) > 0) {
                                status = RxQueue.RX_TYPE.UNSUB.toString();
                            }

                            if (se.chrg_flg.equals("MO")
                                    && (se.srvc_type & ServiceElement.SERVICE_TYPE.SMSDOWNLOAD.getId()) > 0) { // is MO charging
                                message += " <img src='./images/favorite16.gif' style='vertical-align:middle;'>";
                                chrg_success = 2;
                            }
                        }

                        style = " class='" + style + "'";

                        out.print("<tr" + style + "><td title='id:" + id + "'>" + (i + 1) + "</td>");
                        out.print("<td title='id:" + se.srvc_main_id + "'>" + se.srvc_name + "</td>");
                        out.print("<td>" + se.srvc_id + "</td>");
                        out.print("<td>" + oper.name() + "</td>");
                        out.print("<td"
                                + ((full_message != null) && (!full_message.isEmpty())
                                ? " title='" + StringConvert.replace(full_message, "'", "\\\\&#39;", true) + "'" : "")
                                + ">" + message + "</td>");

                        out.print("<td title='id:" + ctnt_id + "'>"
                                + content_type
                                + "<img style='vertical-align:top' src='./images/"
                                + ((md instanceof RxMoQueue) ? "download" : "up") + ".png'>" + "</td>");

                        out.print("<td>" + DatetimeUtil.print("dd-MM-yy HH:mm:ss", md.getTimestamp()) + "</td>");
                        out.print("<td>"
                                + status
                                + ((charge != null) && (charge.equals("MT")) ? "<img src='./images/favorite16.gif'>" : "") + "</td>");

                        out.print("<td>" + status_desc + "</td>");
                        out.print("<td><img title='" + StringConvert.replace(dr_desc, "'", "\\\\&#39;", true)
                                + "' src='./images/" + chrg_img[chrg_success] + "'>" + "</td>");

                        out.print("</tr>");
                    } catch (Exception e) {
                        log.severe(e.getMessage());
                    }
                }
                out.println("<tr>"
                        + "<td colspan='10' style='text-align:right;padding:10px 30px 10px 0;line-height:1.5em'>"
                        + "<img src='./images/favorite16.gif' style='vertical-align:middle;'>"
                        + " Charged message&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "<img src='./images/warning16.gif' style='vertical-align:middle;'>"
                        + " Unknown result<BR>"
                        + "<img src='./images/accept16.gif' style='vertical-align:middle;'>"
                        + " Received&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "<img src='./images/delete16.gif' style='vertical-align:middle;'>"
                        + " Charged fail</td>"
                        + "</tr>");

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
