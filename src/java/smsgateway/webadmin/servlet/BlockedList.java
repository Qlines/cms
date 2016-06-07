package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.subscriberblocked.SubscriberBlockedSortByBlockedFlag;
import hippoping.smsgw.api.comparator.subscriberblocked.SubscriberBlockedSortByCreateDate;
import hippoping.smsgw.api.comparator.subscriberblocked.SubscriberBlockedSortByMsisdn;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.SubscriberBlocked;
import hippoping.smsgw.api.db.User;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DatetimeUtil;

public class BlockedList extends HttpServlet {

    protected List<SubscriberBlocked> blockedList = null;
    protected int rows = 0;
    protected String old_orderby = "";
    protected int sort = 0;

    private void sort(String field, int swap) {
        Comparator comparator = null;

        if ((this.old_orderby != null) && (this.old_orderby.equals(field))) {
            if (swap == 1) {
                this.sort = (++this.sort % 2);
            }
        } else {
            this.sort = 0;
            this.old_orderby = field;
        }

        if (field.equals("msisdn")) {
            comparator = new SubscriberBlockedSortByMsisdn();
        } else if (field.equals("create_dt")) {
            comparator = new SubscriberBlockedSortByCreateDate();
        } else if (field.equals("block_flag")) {
            comparator = new SubscriberBlockedSortByBlockedFlag();
        }
        Collections.sort(this.blockedList, comparator);
        if (this.sort == 1) {
            Collections.reverse(this.blockedList);
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
                String blocked = "";
                //String fdate = "";
                //String tdate = "";
                String orderby = request.getParameter("orderby");
                String page = request.getParameter("page");
                String swap = request.getParameter("swap");

                String cmd = request.getParameter("cmd");

                if ((page == null) || (page.equals(""))) {
                    page = "1";
                }

                if ((cmd != null) && (cmd.equals("add"))) {
                    try {
                        msisdn = request.getParameter("msisdn");
                        SubscriberBlocked sub = null;
                        try {
                            sub = new SubscriberBlocked(msisdn);
                        } catch (Exception e) {
                        }
                        if (sub != null) {
                            out.print("<script>alert('Subscriber number " + msisdn + " has already been in blocked list.')</script>");
                        } else {
                            SubscriberBlocked.add(msisdn);
                            try {
                                sub = new SubscriberBlocked(msisdn);
                                if (sub != null) {
                                    this.blockedList.add(sub);
                                }
                            } catch (Exception e) {
                            }

                            LogEvent.log(LogEvent.EVENT_TYPE.BLOCK_LIST, LogEvent.EVENT_ACTION.ADD, "add blocked number", (User) request.getSession().getAttribute("USER"), msisdn, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);

                            out.print("<script>alert('Subscriber number " + msisdn + " has been blocked.')</script>");
                        }
                    } catch (Exception e) {
                    }
                }
                if ((cmd != null) && (cmd.equals("remove"))) {
                    try {
                        msisdn = request.getParameter("msisdn");
                        SubscriberBlocked sub = null;
                        Iterator iterator = blockedList.iterator();
                        while (iterator.hasNext()) {
                            sub = (SubscriberBlocked) iterator.next();
                            if (sub.getMsisdn().equals(msisdn)) {
                                // remove from list
                                if (blockedList.remove(sub)) {
                                    // remove from system
                                    if (sub.remove() > 0) {
                                        out.print("<script>alert('Removed successfully.')</script>");

                                        // log event
                                        LogEvent.log(LogEvent.EVENT_TYPE.BLOCK_LIST, LogEvent.EVENT_ACTION.DELETE,
                                                "remove blocked number",
                                                (User) request.getSession().getAttribute("USER"),
                                                msisdn,
                                                null,
                                                0, 0, 0, LogEvent.LOG_LEVEL.INFO);
                                    }
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }

                }

                if ((cmd != null) && ((cmd.equals("block")) || (cmd.equals("unblock")))) {
                    try {
                        msisdn = request.getParameter("msisdn");
                        SubscriberBlocked sub = null;
                        Iterator iterator = this.blockedList.iterator();
                        while (iterator.hasNext()) {
                            sub = (SubscriberBlocked) iterator.next();
                            if (sub.getMsisdn().equals(msisdn)) {
                                sub.setBlocked(cmd.equals("block"));

                                LogEvent.log(LogEvent.EVENT_TYPE.BLOCK_LIST, LogEvent.EVENT_ACTION.MODIFY, cmd + " number", (User) request.getSession().getAttribute("USER"), msisdn, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);
                            }

                        }

                    } catch (Exception e) {
                    }

                }

                if ((cmd != null) && (cmd.equals("refresh"))) {
                    msisdn = request.getParameter("msisdn");
                    blocked = request.getParameter("blocked");
                    //fdate = request.getParameter("fdate");
                    //tdate = request.getParameter("tdate");
                    this.rows = Integer.parseInt(request.getParameter("rows"));
                    try {
                        this.blockedList = SubscriberBlocked.getBlockedList(msisdn, (blocked != null) && (!blocked.equals("0")) ? Boolean.valueOf(blocked.equals("1")) : null, null, null);
                                //, fdate != null ? DatetimeUtil.toDate(fdate, "yyyy-MM-dd") : null
                        //, tdate != null ? DatetimeUtil.toDate(tdate, "yyyy-MM-dd") : null);

                        LogEvent.log(LogEvent.EVENT_TYPE.BLOCK_LIST, LogEvent.EVENT_ACTION.SEARCH, "search blocked list", (User) request.getSession().getAttribute("USER"), null, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);
                    } catch (Exception e) {
                    }

                }

                if ((this.blockedList != null) && (this.blockedList.size() > 0)) {
                    sort(orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = this.blockedList.size() / this.rows + (this.blockedList.size() % this.rows != 0 ? 1 : 0);

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
                        + "       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}"
                        + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}"
                        + "       else {frm.submit();}"
                        + "    }"
                        + "    function goto_page(page) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.page.value=page;"
                        + "       frm.submit();"
                        + "    }"
                        + "    function doAdd() {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='add';"
                        + "       frm.msisdn.value=getElement('msisdn_add').value;"
                        + "       var msisdn = frm.msisdn.value;"
                        + "       if ((msisdn.indexOf('08', 0) == 0)"
                        + "                        || (msisdn.indexOf('09', 0) == 0)"
                        + "                        || (msisdn.indexOf('06', 0) == 0)) {"
                        + "           frm.msisdn.value = msisdn.replace('0', '66');"
                        + "       }"
                        + "       if (!isMsisdn(frm.msisdn.value)) {"
                        + "         alert('Invalid format for mobile number [' + frm.msisdn.value + ']');"
                        + "         return;"
                        + "       }"
                        + "       frm.submit();"
                        + "    }"
                        + "    function doImport() {"
                        + "       window.open(\"block_sub.html\""
                        + "           ,null, \"left=300,top=300,height=150,width=332,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=no\");"
                        + "       return;"
                        + "    }"
                        + "    function doFilter() {"
                        + "       window.open(\"filter_sub.html\""
                        + "           ,null, \"left=300,top=300,height=150,width=332,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=no\");"
                        + "       return;"
                        + "    }"
                        + "    function doBlock(msisdn) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='block';"
                        + "       frm.msisdn.value=msisdn;"
                        + "       frm.submit();"
                        + "    }"
                        + "    function doUnblock(msisdn) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='unblock';"
                        + "       frm.msisdn.value=msisdn;"
                        + "       frm.submit();"
                        + "    }"
                        + "    function doRemove(msisdn) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='remove';"
                        + "       frm.msisdn.value=msisdn;"
                        + "       if (confirm('Click OK to confirm to remove blocked subscriber number \"' + msisdn + '\", otherwise click cancel.'))"
                        + "           frm.submit();"
                        + "    }"
                        + "    </script>"
                        + "</head>"
                        + "<body style='background-color:#FFF;'>"
                        + "   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;;'>"
                        + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>"
                        + "       <input type=hidden name=cmd value=''>" + "       <input type=hidden name=msisdn value=''>"
                        + "       <input type=hidden name=orderby value='" + orderby + "'>" + "       <input type=hidden name=rows value='" + this.rows + "'>"
                        + "       <input type=hidden name=swap value='0'>" + "       <input type=hidden name=csv value='0'>"
                        + "       <div class='floatl' style='font-size:75%; padding-left:5px;'><b>Total " + this.blockedList.size() + " record(s) found."
                        + "           (Page " + page + " of " + pg + ")</b> "
                        + "           | <a href='javascript:window.location=\"./BlockedList?csv=1&page=" + page + "\";'>"
                        + "           <img title='Export blocked list (.csv)' src='./images/download16.gif'></a>"
                        + "           <a href='javascript:doImport();'><img title='Import blocked list (.txt)' src='./images/upload16.gif'></a>"
                        + "           <a href='javascript:doFilter();'><img title='Filter blocked subscriber (.txt)' src='./images/filter16.gif'></a>"
                        + "       </div>"
                        + "       <div class='floatr'>"
                        + "         <span style='padding:0;'>"
                        + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>")
                        + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>")
                        + "         </span>"
                        + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>"
                        + "           <input type=submit value=go>"
                        + "       </div>"
                        + "       </form>"
                        + "       <table class='table3' style='width:100%;padding:0;font-size:.7em;'>"
                        + "       <tr>"
                        + "           <th width='5%'>No.</th>"
                        + "           <th width='30%'>" + (orderby.equals("msisdn") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "<a href='javascript:frm=document.forms[\"reloadFrm\"];"
                        + "frm.orderby.value=\"msisdn\";frm.swap.value=1;frm.submit();'>MSISDN</a></th>"
                        + "           <th width='30%'>" + (orderby.equals("creat_dt") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"creat_dt\";frm.swap.value=1;frm.submit();'>Create Date</a></th>"
                        + "           <th width='20%'>" + (orderby.equals("block_flag") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "")
                        + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"block_flag\";frm.swap.value=1;frm.submit();'>Status</a></th>"
                        + "           <th width='15%'>Action</th>"
                        + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                for (int i = sindex; (i < this.blockedList.size()) && (i < eindex); i++) {
                    String style = i % 2 == 0 ? "" : " d0";
                    style = style + (((SubscriberBlocked) this.blockedList.get(i)).isBlocked() ? " d1" : "");
                    style = " class='" + style + "'";
                    out.print("<tr" + style + "><td>" + (i + 1) + "</td>");
                    out.print("<td>" + ((SubscriberBlocked) this.blockedList.get(i)).getMsisdn() + "</td>");
                    out.print("<td>" + DatetimeUtil.print("yyyy-MM-dd", DatetimeUtil.toDate(((SubscriberBlocked) this.blockedList.get(i)).getCreate_dt())) + "</td>");
                    out.print("<td>" + (((SubscriberBlocked) this.blockedList.get(i)).isBlocked() ? "Blocked" : "Allow") + "</td>");
                    out.print("<td> <a href='javascript:" + (((SubscriberBlocked) this.blockedList.get(i)).isBlocked() ? "doUnblock" : "doBlock") + "(\"" + ((SubscriberBlocked) this.blockedList.get(i)).getMsisdn() + "\")'>");

                    out.print("<img src='./images/" + (((SubscriberBlocked) this.blockedList.get(i)).isBlocked() ? "unblock" : "block") + "01.gif' border='0'>");
                    out.print("</a> <a href='javascript:doRemove(\"" + ((SubscriberBlocked) this.blockedList.get(i)).getMsisdn() + "\")'>" + "<img src='./images/trash.gif' border='0'>" + "</a>");

                    out.print("</td>");
                    out.print("</tr>");
                }
                out.println("<div id=add style='display:block'><tr><td colspan='4' style='text-align:left'><input type=text id=msisdn_add onkeypress='return filter_digit_char(event)' maxlength='11'><input type=button value='Add' onclick='doAdd()'></td></tr></div>");

                out.println("</table></div></body></html>");
            }
        } finally {
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "ISO8859_1");
        String csv = request.getParameter("csv");

        MultipartResponse multi = new MultipartResponse(response);
        try {
            if ((csv != null) && (csv.equals("1"))) {
                multi.startResponse("text/csv;charset=tis-620");
                response.setHeader("Content-disposition", "attachment; filename=Blocked_list.csv");

                out.append("No.,MSISDN,DATE,STATUS,\r\n");
                for (int i = 0; i < this.blockedList.size(); i++) {
                    out.append(i + 1 + ",");
                    out.append(((SubscriberBlocked) this.blockedList.get(i)).getMsisdn() + ",");
                    out.append(new SimpleDateFormat("MM/dd/yy").format(DatetimeUtil.toDate(((SubscriberBlocked) this.blockedList.get(i)).getCreate_dt())) + ",");
                    out.append((((SubscriberBlocked) this.blockedList.get(i)).isBlocked() ? "blocked" : "unblocked") + ",");
                    out.append("\r\n");
                }
                out.flush();
                multi.endResponse();
            }
        } finally {
            multi.finish();
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public String getServletInfo() {
        return "Short description";
    }
}
