package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.link.LinkConfigureSortByName;
import hippoping.smsgw.api.db.HybridConfig;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.OperConfig;
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
import lib.common.StringConvert;

public class LinkListServlet extends HttpServlet {

    protected List<OperConfig> linkList = null;
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

        if (field.equals("name")) {
            comparator = new LinkConfigureSortByName();
        }
        if (comparator != null) {
            Collections.sort(this.linkList, comparator);
            if (this.sort == 1) {
                Collections.reverse(this.linkList);
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
                    orderby = "name";
                }

                String confid = request.getParameter("conf_id");

                int conf_id = 0;
                try {
                    conf_id = Integer.parseInt(confid);
                } catch (Exception e) {
                }
                if ((cmd != null) && (cmd.equals("remove"))) {
                    try {
                        OperConfig lc = new OperConfig(conf_id);
                        System.out.println("Remove link " + lc.remove() + " row(s)");

                        LogEvent.log(LogEvent.EVENT_TYPE.LINK, LogEvent.EVENT_ACTION.DELETE, "delete link config", (User) request.getSession().getAttribute("USER"), null, null, 0, conf_id, 0, LogEvent.LOG_LEVEL.INFO);
                    } catch (Exception e) {
                    }

                    conf_id = 0;
                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("add"))) {
                    try {
                        OperConfig lc = new OperConfig();
                        lc.conf_name = new String(request.getParameter("conf_name").getBytes("ISO8859_1"), encoding);
                        lc.user = request.getParameter("user");
                        lc.password = request.getParameter("password");
                        lc.register_url = request.getParameter("register_url");
                        lc.unregister_url = request.getParameter("unregister_url");
                        lc.sub_stat_url = request.getParameter("sub_stat_url");
                        lc.sms_link_url = request.getParameter("sms_link_url");
                        lc.mms_link_url = request.getParameter("mms_link_url");
                        lc.ivr_link_url = request.getParameter("ivr_link_url");
                        lc.thrd_prty_url = request.getParameter("thrd_prty_url");
                        lc.thrd_prty_auth = request.getParameter("thrd_prty_auth");

                        lc.sftp_cust = request.getParameter("sftp_cust");
                        lc.sftp_host = request.getParameter("sftp_host");
                        lc.sftp_port = (request.getParameter("sftp_port").isEmpty() ? 22 : Integer.parseInt(request.getParameter("sftp_port")));

                        lc.sftp_user = request.getParameter("sftp_user");
                        lc.sftp_password = request.getParameter("sftp_password");
                        lc.sftp_remote_dir = request.getParameter("sftp_remote_dir");

                        lc.hybrid = (request.getParameter("link_hybd_id").equals("0") ? null : new HybridConfig(Integer.parseInt(request.getParameter("link_hybd_id"))));

                        OperConfig.add(lc);

                        LogEvent.log(LogEvent.EVENT_TYPE.LINK, LogEvent.EVENT_ACTION.ADD, "create link config", (User) request.getSession().getAttribute("USER"), null, null, 0, lc.conf_id, 0, LogEvent.LOG_LEVEL.INFO);
                    } catch (Exception e) {
                    }

                    conf_id = 0;
                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("edit")) && (conf_id > 0)) {
                    try {
                        OperConfig lc = new OperConfig(conf_id);
                        lc.conf_name = new String(request.getParameter("conf_name").getBytes("ISO8859_1"), encoding);
                        lc.user = request.getParameter("user");
                        lc.password = request.getParameter("password");
                        lc.register_url = request.getParameter("register_url");
                        lc.unregister_url = request.getParameter("unregister_url");
                        lc.sub_stat_url = request.getParameter("sub_stat_url");
                        lc.sms_link_url = request.getParameter("sms_link_url");
                        lc.mms_link_url = request.getParameter("mms_link_url");
                        lc.ivr_link_url = request.getParameter("ivr_link_url");
                        lc.thrd_prty_url = request.getParameter("thrd_prty_url");
                        lc.thrd_prty_auth = request.getParameter("thrd_prty_auth");

                        lc.sftp_cust = request.getParameter("sftp_cust");
                        lc.sftp_host = request.getParameter("sftp_host");
                        lc.sftp_port = (request.getParameter("sftp_port").isEmpty() ? 22 : Integer.parseInt(request.getParameter("sftp_port")));

                        lc.sftp_user = request.getParameter("sftp_user");
                        lc.sftp_password = request.getParameter("sftp_password");
                        lc.sftp_remote_dir = request.getParameter("sftp_remote_dir");

                        lc.hybrid = (request.getParameter("link_hybd_id").equals("0") ? null : new HybridConfig(Integer.parseInt(request.getParameter("link_hybd_id"))));

                        lc.sync();

                        LogEvent.log(LogEvent.EVENT_TYPE.LINK, LogEvent.EVENT_ACTION.MODIFY, "modify link config", (User) request.getSession().getAttribute("USER"), null, null, 0, conf_id, 0, LogEvent.LOG_LEVEL.INFO);
                    } catch (Exception e) {
                    }

                    conf_id = 0;
                    cmd = "refresh";
                }

                if ((cmd != null) && (cmd.equals("refresh"))) {
                    this.rows = 20;
                    try {
                        this.rows = Integer.parseInt(request.getParameter("rows"));
                    } catch (Exception e) {
                    }
                    String search = request.getParameter("search");
                    if (search != null) {
                        search = new String(search.getBytes("ISO8859_1"), encoding);
                        request.getSession().setAttribute(getServletName() + "_search", search);
                    } else {
                        search = (String) request.getSession().getAttribute(getServletName() + "_search");
                    }
                    this.linkList = OperConfig.getAll(search);

                    LogEvent.log(LogEvent.EVENT_TYPE.LINK, LogEvent.EVENT_ACTION.SEARCH, "search link config", (User) request.getSession().getAttribute("USER"), null, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);
                }

                if ((this.linkList != null) && (orderby != null) && (this.linkList.size() > 0)) {
                    sort(orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = this.linkList.size() / this.rows + (this.linkList.size() % this.rows != 0 ? 1 : 0);

                pg = pg == 0 ? 1 : pg;
                out.println("<html><head>    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>    <link href='./css/cv.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>    <style type='text/css'>        body{margin:0px; padding: 0px; background: white;            font: 100.01% 'Trebuchet MS',Verdana,Arial,sans-serif}        h1,h2,p{margin: 0 10px}        h1{font-size: 250%;color: #FFF}        h2{font-size: 200%;color: #f0f0f0}        p{padding-bottom:1em}        h2{padding-top: 0.3em}        div#memberViewContent {background: #377CB1;}    </style>    <script src='./js/nifty.js' type='text/javascript'></script>    <script src='./js/utils.js' type='text/javascript'></script>    <script src='./js/filter_input.js' type='text/javascript'></script>    <script>    function validate_page(page, maxpage) {        var frm = document.forms[\"reloadFrm\"];       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}" + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}" + "       else {frm.submit();}" + "    }" + "    function goto_page(page) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.page.value=page;" + "       frm.submit();" + "    }" + "    function doEdit(id) {" + "       window.showModalDialog('link_new.jsp?cmd=edit&conf_id=' + id,'', 'dialogWidth:640px;dialogHeight:670px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doCopy(id) {" + "       window.showModalDialog('link_new.jsp?cmd=copy&conf_id=' + id,'', 'dialogWidth:640px;dialogHeight:670px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doAdd() {" + "       window.showModalDialog('link_new.jsp?cmd=add','', 'dialogWidth:640px;dialogHeight:670px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doRemove(id, name) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='remove';" + "       frm.conf_id.value=id;" + "       frm.action='LinkListServlet';" + "       var msg = 'Click OK to confirm to delete link \"' + name + '\", otherwise click cancel.';" + "       if (confirm(msg)) {" + "           frm.submit();" + "       }" + "    }" + "    window.onload=function() {" + "    }" + "    </script>" + "</head>" + "<body>" + "   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;'>" + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>" + "       <input type=hidden name=cmd value=''>" + "       <input type=hidden name=conf_id value=''>" + "       <input type=hidden name=orderby value='" + orderby + "'>" + "       <input type=hidden name=rows value='" + this.rows + "'>" + "       <input type=hidden name=swap value='0'>" + "       <input type=hidden name=csv value='0'>" + "       <div id='content' class='floatl' style='font-size:75%; padding-left:5px; background:#FFF;'>" + "         <b>Total " + this.linkList.size() + " record(s) found. (Page " + page + " of " + pg + ")</b>" + "             | <a href='javascript:doAdd();' style='color:#333;'>" + "             <img src='./images/new2.gif' border='0' style='vertical-align:middle'> " + "             Creat new link" + "             </a>" + "       </div>" + "       <div class='floatr'>" + "         <span style='padding:0;'>" + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>") + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>") + "         </span>" + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>" + "           <input type=submit value=go>" + "       </div>" + "       <table class='table3' width='100%' style='padding:20px;'>" + "       <tr>" + "           <th width='3%'>No.</th>" + "           <th width='20%'>" + (orderby.equals("name") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"name\";frm.swap.value=1;frm.submit();'>Name</a></th>" + "           <th width='20%'>User</th>" + "           <th width='20%'>Password</th>" + "           <th width='15%'>SFTP</th>" + "           <th width='15%'>Hybrid</th>" + "           <th width='12%'>Action</th>" + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                for (int i = sindex; (i < this.linkList.size()) && (i < eindex); i++) {
                    String style = i % 2 == 0 ? "" : " d0";
                    style = " class='" + style + "'";
                    try {
                        out.print("<tr" + style + " title='" + "Register: " + (((OperConfig) this.linkList.get(i)).register_url != null ? ((OperConfig) this.linkList.get(i)).register_url : "") + "\n" + "Unsub: " + (((OperConfig) this.linkList.get(i)).unregister_url != null ? ((OperConfig) this.linkList.get(i)).unregister_url : "") + "\n" + "Status: " + (((OperConfig) this.linkList.get(i)).sub_stat_url != null ? ((OperConfig) this.linkList.get(i)).sub_stat_url : "") + "\n" + "SMS: " + (((OperConfig) this.linkList.get(i)).sms_link_url != null ? ((OperConfig) this.linkList.get(i)).sms_link_url : "") + "\n" + "MMS: " + (((OperConfig) this.linkList.get(i)).mms_link_url != null ? ((OperConfig) this.linkList.get(i)).mms_link_url : "") + "\n" + "IVR:" + (((OperConfig) this.linkList.get(i)).ivr_link_url != null ? ((OperConfig) this.linkList.get(i)).ivr_link_url : "") + "\n" + "==Truemove==\n" + "3rd party Url:" + (((OperConfig) this.linkList.get(i)).thrd_prty_url != null ? ((OperConfig) this.linkList.get(i)).thrd_prty_url : "") + "\n" + "Authorization:" + (((OperConfig) this.linkList.get(i)).thrd_prty_auth != null ? ((OperConfig) this.linkList.get(i)).thrd_prty_auth : "") + "\n" + "'><td>" + (i + 1) + "</td>");

                        out.print("<td style='text-align:left;'>" + ((OperConfig) this.linkList.get(i)).conf_name + "</td>");
                        out.print("<td>" + ((OperConfig) this.linkList.get(i)).user + "</td>");
                        out.print("<td>" + ((OperConfig) this.linkList.get(i)).password + "</td>");
                        out.print("<td>" + ((((OperConfig) this.linkList.get(i)).sftp_host != null) && (!((OperConfig) this.linkList.get(i)).sftp_host.isEmpty()) ? ((OperConfig) this.linkList.get(i)).sftp_user + "@" + ((OperConfig) this.linkList.get(i)).sftp_host : "-") + "</td>");

                        out.print("<td>" + (((OperConfig) this.linkList.get(i)).hybrid.getName() != null ? ((OperConfig) this.linkList.get(i)).hybrid.getName() : "-") + "</td>");

                        out.print("<td> <a href='javascript:doCopy(" + ((OperConfig) this.linkList.get(i)).conf_id + ")'>" + "<img title='copy' src='./images/copy16.gif' border='0'>" + "</a> " + " <a href='javascript:doEdit(" + ((OperConfig) this.linkList.get(i)).conf_id + ")'>" + "<img title='edit' src='./images/edit02.gif' border='0'>" + "</a> " + "<a href='javascript:doRemove(" + ((OperConfig) this.linkList.get(i)).conf_id + ", \"" + StringConvert.replace(((OperConfig) this.linkList.get(i)).conf_name, "'", "\\\\&#39;", true) + "\")'>" + "<img title='delete' src='./images/trash.gif' border='0'>" + "</a>" + "</td>");

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