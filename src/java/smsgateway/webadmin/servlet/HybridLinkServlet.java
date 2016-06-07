package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.link.hybrid.HybridConfigSortByName;
import hippoping.smsgw.api.db.HybridConfig;
import hippoping.smsgw.api.db.LogEvent;
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

public class HybridLinkServlet extends HttpServlet {

    protected List<HybridConfig> linkList = null;
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
            comparator = new HybridConfigSortByName();
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
                        HybridConfig hb = new HybridConfig(conf_id);
                        System.out.println("Remove link " + hb.remove() + " row(s)");

                        LogEvent.log(LogEvent.EVENT_TYPE.LINK, LogEvent.EVENT_ACTION.DELETE, "delete hybrid link config", (User) request.getSession().getAttribute("USER"), null, null, 0, conf_id, 0, LogEvent.LOG_LEVEL.INFO);
                    } catch (Exception e) {
                    }

                    conf_id = 0;
                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("add"))) {
                    try {
                        HybridConfig hb = new HybridConfig();
                        hb.setName(new String(request.getParameter("conf_name").getBytes("ISO8859_1"), encoding));
                        hb.setUser(request.getParameter("user"));
                        hb.setPassword(request.getParameter("password"));
                        hb.setUrl(request.getParameter("url"));
                        hb.setSgwid(HybridConfig.SGWID.fromId(request.getParameter("sgwid")));

                        HybridConfig.add(hb);

                        LogEvent.log(LogEvent.EVENT_TYPE.LINK, LogEvent.EVENT_ACTION.ADD, "create Hybrid link config", (User) request.getSession().getAttribute("USER"), null, null, 0, hb.getLink_hybd_id(), 0, LogEvent.LOG_LEVEL.INFO);
                    } catch (Exception e) {
                    }

                    conf_id = 0;
                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("edit")) && (conf_id > 0)) {
                    try {
                        HybridConfig hb = new HybridConfig(Integer.parseInt(request.getParameter("conf_id")));
                        hb.setName(new String(request.getParameter("conf_name").getBytes("ISO8859_1"), encoding));
                        hb.setUser(request.getParameter("user"));
                        hb.setPassword(request.getParameter("password"));
                        hb.setUrl(request.getParameter("url"));
                        hb.setSgwid(HybridConfig.SGWID.fromId(request.getParameter("sgwid")));

                        hb.sync();

                        LogEvent.log(LogEvent.EVENT_TYPE.LINK, LogEvent.EVENT_ACTION.MODIFY, "modify Hybrid link config", (User) request.getSession().getAttribute("USER"), null, null, 0, conf_id, 0, LogEvent.LOG_LEVEL.INFO);
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
                    this.linkList = HybridConfig.getAll(search);

                    LogEvent.log(LogEvent.EVENT_TYPE.LINK, LogEvent.EVENT_ACTION.SEARCH, "search hybrid config", (User) request.getSession().getAttribute("USER"), null, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);
                }

                if ((this.linkList != null) && (orderby != null) && (this.linkList.size() > 0)) {
                    sort(orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = this.linkList.size() / this.rows + (this.linkList.size() % this.rows != 0 ? 1 : 0);

                pg = pg == 0 ? 1 : pg;
                out.println("<html><head>    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>    <link href='./css/cv.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>    <style type='text/css'>        body{margin:0px; padding: 0px; background: white;            font: 100.01% 'Trebuchet MS',Verdana,Arial,sans-serif}        h1,h2,p{margin: 0 10px}        h1{font-size: 250%;color: #FFF}        h2{font-size: 200%;color: #f0f0f0}        p{padding-bottom:1em}        h2{padding-top: 0.3em}        div#memberViewContent {background: #377CB1;}    </style>    <script src='./js/nifty.js' type='text/javascript'></script>    <script src='./js/utils.js' type='text/javascript'></script>    <script src='./js/filter_input.js' type='text/javascript'></script>    <script>    function validate_page(page, maxpage) {        var frm = document.forms[\"reloadFrm\"];       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}" + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}" + "       else {frm.submit();}" + "    }" + "    function goto_page(page) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.page.value=page;" + "       frm.submit();" + "    }" + "    function doEdit(id) {" + "       window.showModalDialog('link_hybrid_new.jsp?cmd=edit&conf_id=' + id,'', 'dialogWidth:640px;dialogHeight:670px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doCopy(id) {" + "       window.showModalDialog('link_hybrid_new.jsp?cmd=copy&conf_id=' + id,'', 'dialogWidth:640px;dialogHeight:670px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doAdd() {" + "       window.showModalDialog('link_hybrid_new.jsp?cmd=add','', 'dialogWidth:640px;dialogHeight:670px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doRemove(id, name) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='remove';" + "       frm.conf_id.value=id;" + "       frm.action='HybridLinkServlet';" + "       var msg = 'Click OK to confirm to delete hybrid link \"' + name + '\", otherwise click cancel.';" + "       if (confirm(msg)) {" + "           frm.submit();" + "       }" + "    }" + "    window.onload=function() {" + "    }" + "    </script>" + "</head>" + "<body>" + "   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;'>" + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>" + "       <input type=hidden name=cmd value=''>" + "       <input type=hidden name=conf_id value=''>" + "       <input type=hidden name=orderby value='" + orderby + "'>" + "       <input type=hidden name=rows value='" + this.rows + "'>" + "       <input type=hidden name=swap value='0'>" + "       <input type=hidden name=csv value='0'>" + "       <div id='content' class='floatl' style='font-size:75%; padding-left:5px; background:#FFF;'>" + "         <b>Total " + this.linkList.size() + " record(s) found. (Page " + page + " of " + pg + ")</b>" + "             | <a href='javascript:doAdd();' style='color:#333;'>" + "             <img src='./images/new2.gif' border='0' style='vertical-align:middle'> " + "             Create new link" + "             </a>" + "       </div>" + "       <div class='floatr'>" + "         <span style='padding:0;'>" + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>") + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>") + "         </span>" + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>" + "           <input type=submit value=go>" + "       </div>" + "       <table class='table3' width='100%' style='padding:20px;'>" + "       <tr>" + "           <th width='3%'>No.</th>" + "           <th width='5%'>" + (orderby.equals("name") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"name\";frm.swap.value=1;frm.submit();'>Name</a></th>" + "           <th width='15%'>User</th>" + "           <th width='15%'>Password</th>" + "           <th width='40%'>URL</th>" + "           <th width='10%'>SGWID</th>" + "           <th width='12%'>Action</th>" + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                for (int i = sindex; (i < this.linkList.size()) && (i < eindex); i++) {
                    String style = i % 2 == 0 ? "" : " d0";
                    style = " class='" + style + "'";
                    try {
                        out.print("<tr" + style + "><td>" + (i + 1) + "</td>");
                        out.print("<td style='text-align:left;'>" + ((HybridConfig) this.linkList.get(i)).getName() + "</td>");
                        out.print("<td>" + ((HybridConfig) this.linkList.get(i)).getUser() + "</td>");
                        out.print("<td>" + ((HybridConfig) this.linkList.get(i)).getPassword() + "</td>");
                        out.print("<td>" + ((HybridConfig) this.linkList.get(i)).getUrl() + "</td>");
                        out.print("<td>" + ((HybridConfig) this.linkList.get(i)).getSgwid().toString() + "</td>");
                        out.print("<td> <a href='javascript:doCopy(" + ((HybridConfig) this.linkList.get(i)).getLink_hybd_id() + ")'>" + "<img title='copy' src='./images/copy16.gif' border='0'>" + "</a> " + " <a href='javascript:doEdit(" + ((HybridConfig) this.linkList.get(i)).getLink_hybd_id() + ")'>" + "<img title='edit' src='./images/edit02.gif' border='0'>" + "</a> " + "<a href='javascript:doRemove(" + ((HybridConfig) this.linkList.get(i)).getLink_hybd_id() + ", \"" + StringConvert.replace(((HybridConfig) this.linkList.get(i)).getName(), "'", "\\\\&#39;", true) + "\")'>" + "<img title='delete' src='./images/trash.gif' border='0'>" + "</a>" + "</td>");

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