package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.MessageWap.MessageWapSortByTitle;
import hippoping.smsgw.api.comparator.MessageWap.MessageWapSortByUrl;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.Message;
import hippoping.smsgw.api.db.MessageWap;
import hippoping.smsgw.api.db.MessageWapFactory;
import hippoping.smsgw.api.db.ServiceContentAction;
import hippoping.smsgw.api.db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SmsWapListServlet extends HttpServlet {

    protected int rows = 0;
    protected String old_orderby = "";
    protected int sort = 0;

    private void sort(List<MessageWap> list, String field, int swap) {
        Comparator comparator = null;

        if ((this.old_orderby != null) && (this.old_orderby.equals(field))) {
            if (swap == 1) {
                this.sort = (++this.sort % 2);
            }
        } else {
            this.sort = 0;
            this.old_orderby = field;
        }

        if (field.equals("title")) {
            comparator = new MessageWapSortByTitle();
        } else if (field.equals("url")) {
            comparator = new MessageWapSortByUrl();
        }
        if (comparator != null) {
            Collections.sort(list, comparator);
            if (this.sort == 1) {
                Collections.reverse(list);
            }
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String encoding = "UTF-8";
            if (request.getCharacterEncoding() != null) {
                encoding = request.getCharacterEncoding();
            }
            User user = (User) request.getSession().getAttribute("USER");
            if (user == null) {
                out.print("<script>window.location='logout?msg=Your session has been expired! please relogin the page.'</script>");
            } else {
                String orderby = request.getParameter("orderby");
                String page = request.getParameter("page");
                String swap = request.getParameter("swap");

                String cmd = request.getParameter("cmd");

                if ((page == null) || (page.equals(""))) {
                    page = "1";
                }
                if ((orderby == null) || (orderby.equals(""))) {
                    orderby = "title";
                }

                String sid = request.getParameter("id");

                int id = 0;
                try {
                    id = Integer.parseInt(sid);
                } catch (Exception e) {
                }
                if ((cmd != null) && (cmd.equals("remove"))) {
                    try {
                        MessageWap sms = new MessageWap(id);
                        System.out.println("Remove sms text " + sms.remove() + " row(s)");
                    } catch (Exception e) {
                    }

                    LogEvent.log(LogEvent.EVENT_TYPE.WAP, LogEvent.EVENT_ACTION.DELETE, "delete SMS[" + id + "]", user, null, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);

                    id = 0;
                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("add"))) {
                    String title = new String(request.getParameter("title").getBytes("ISO8859_1"), encoding);
                    String url = new String(request.getParameter("url").getBytes("ISO8859_1"), encoding);
                    try {
                        new MessageWap().add(title, url, null, 0, user.getUid());
                    } catch (Exception e) {
                    }

                    LogEvent.log(LogEvent.EVENT_TYPE.WAP, LogEvent.EVENT_ACTION.ADD, "create WAP[" + id + "] -> " + url, user, null, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);

                    id = 0;
                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("edit")) && (id > 0)) {
                    String title = new String(request.getParameter("title").getBytes("ISO8859_1"), encoding);
                    String url = new String(request.getParameter("url").getBytes("ISO8859_1"), encoding);
                    try {
                        MessageWap wap = new MessageWap(id);
                        wap.title = title;
                        wap.url = url;
                        wap.setType(Message.SMS_TYPE.WAP);
                        wap.setContentType(ServiceContentAction.ACTION_TYPE.WAP);

                        wap.sync();

                        MessageWapFactory.remove(id);
                    } catch (Exception e) {
                    }

                    LogEvent.log(LogEvent.EVENT_TYPE.WAP, LogEvent.EVENT_ACTION.MODIFY, "modify WAP[" + id + "] -> " + url, user, null, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);

                    id = 0;
                    cmd = "refresh";
                }

                if ((cmd != null) && (cmd.equals("refresh"))) {
                    this.rows = 20;
                    try {
                        this.rows = Integer.parseInt(request.getParameter("rows"));
                    } catch (Exception e) {
                    }
                    String keyword = request.getParameter("keyword");
                    if (keyword != null) {
                        keyword = new String(request.getParameter("keyword").getBytes("ISO8859_1"), encoding);
                    }

                    request.getSession().setAttribute("smsWapList", new MessageWapFactory(keyword, 0, 0).getMessageWapList());

                    LogEvent.log(LogEvent.EVENT_TYPE.WAP, LogEvent.EVENT_ACTION.SEARCH, "search WAP with keyword[" + keyword + "]", user, null, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);
                }

                List list = (List) request.getSession().getAttribute("smsWapList");

                if ((list != null) && (orderby != null) && (list.size() > 0)) {
                    sort(list, orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = list.size() / this.rows + (list.size() % this.rows != 0 ? 1 : 0);

                pg = pg == 0 ? 1 : pg;
                out.println("<html><head>    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>    <link href='./css/cv.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>    <style type='text/css'>        body{margin:0px; padding: 0px; background: white;            font: 100.01% 'Trebuchet MS',Verdana,Arial,sans-serif}        h1,h2,p{margin: 0 10px}        h1{font-size: 250%;color: #FFF}        h2{font-size: 200%;color: #f0f0f0}        p{padding-bottom:1em}        h2{padding-top: 0.3em}        div#memberViewContent {background: #377CB1;}    </style>    <script src='./js/nifty.js' type='text/javascript'></script>    <script src='./js/utils.js' type='text/javascript'></script>    <script src='./js/filter_input.js' type='text/javascript'></script>    <script>    function validate_page(page, maxpage) {        var frm = document.forms[\"reloadFrm\"];       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}" + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}" + "       else {frm.submit();}" + "    }" + "    function goto_page(page) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.page.value=page;" + "       frm.submit();" + "    }" + "    function doEdit(id) {" + "       window.showModalDialog('sms_wap_new.jsp?cmd=edit&id=' + id,'', 'dialogWidth:640px;dialogHeight:470px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doCopy(id) {" + "       window.showModalDialog('sms_wap_new.jsp?cmd=copy&id=' + id,'', 'dialogWidth:640px;dialogHeight:470px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doAdd() {" + "       window.showModalDialog('sms_wap_new.jsp?cmd=add','', 'dialogWidth:640px;dialogHeight:470px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doRemove(id, content) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='remove';" + "       frm.id.value=id;" + "       frm.action='SmsWapListServlet';" + "       var msg = 'Click OK to confirm to delete wap push \"' + content + '\", otherwise click cancel.';" + "       if (confirm(msg)) {" + "           frm.submit();" + "       }" + "    }" + "    window.onload=function() {" + "    }" + "    </script>" + "</head>" + "<body>" + "   <div id='data' style='padding: 0 10px 0 10px;width:100%;'>" + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>" + "       <input type=hidden name=cmd value=''>" + "       <input type=hidden name=id value=''>" + "       <input type=hidden name=orderby value='" + orderby + "'>" + "       <input type=hidden name=rows value='" + this.rows + "'>" + "       <input type=hidden name=swap value='0'>" + "       <input type=hidden name=csv value='0'>" + "       <input type=hidden name=type value='" + (request.getParameter("type") != null ? request.getParameter("type") : "") + "'>" + "       <div id='content' class='floatl' style='font-size:75%; padding-left:5px; background:#FFF;'>" + "         <b>Total " + list.size() + " record(s) found. (Page " + page + " of " + pg + ")</b>" + "             | <a href='javascript:doAdd();' style='color:#333;'>" + "             <img src='./images/new2.gif' border='0' style='vertical-align:middle'> " + "             Creat new WAP Push" + "             </a>" + "       </div>" + "       <div class='floatr'>" + "         <span style='padding:0;'>" + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>") + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>") + "         </span>" + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>" + "           <input type=submit value=go>" + "       </div>" + "       <table class='table3' width='100%' style='padding:20px;'>" + "       <tr>" + "           <th width='3%'>No.</th>" + "           <th width='35%'>" + (orderby.equals("title") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"title\";frm.swap.value=1;frm.submit();'>Title</a></th>" + "           <th width='35%'>" + (orderby.equals("url") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"url\";frm.swap.value=1;frm.submit();'>URL</a></th>" + "           <th width='28%'>Action</th>" + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                for (int i = sindex; (i < list.size()) && (i < eindex); i++) {
                    String style = i % 2 == 0 ? "" : " d0";
                    style = " class='" + style + "'";
                    try {
                        out.print("<tr" + style + ((request.getParameter("type") != null) && (!request.getParameter("type").isEmpty()) && (request.getParameter("type").equals("popup")) ? " onclick='javascript:window.returnValue=" + ((MessageWap) list.get(i)).getContent_id() + ";window.close();'" : "") + "><td>" + (i + 1) + "</td>");

                        out.print("<td>" + ((MessageWap) list.get(i)).title + "</td>");
                        out.print("<td>" + ((MessageWap) list.get(i)).url + "</td>");
                        out.print("<td> <a href='javascript:doCopy(" + ((MessageWap) list.get(i)).getContent_id() + ")'>" + "<img title='copy' src='./images/copy16.gif' border='0'>" + "</a> " + " <a href='javascript:doEdit(" + ((MessageWap) list.get(i)).getContent_id() + ")'>" + "<img title='edit' src='./images/edit02.gif' border='0'>" + "</a> " + "<a href='javascript:doRemove(" + ((MessageWap) list.get(i)).getContent_id() + ", \"" + ((MessageWap) list.get(i)).url + "\")'>" + "<img title='delete' src='./images/trash.gif' border='0'>" + "</a>" + "</td>");

                        out.print("</tr>");
                    } catch (Exception e) {
                    }
                }
                out.println("</table>");
                out.println("</div></body></html>");
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