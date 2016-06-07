package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.subscriber.SubscriberSortByBalancedDate;
import hippoping.smsgw.api.comparator.subscriber.SubscriberSortByCarrier;
import hippoping.smsgw.api.comparator.subscriber.SubscriberSortByExpiredDate;
import hippoping.smsgw.api.comparator.subscriber.SubscriberSortByMsisdn;
import hippoping.smsgw.api.comparator.subscriber.SubscriberSortByRegisterDate;
import hippoping.smsgw.api.comparator.subscriber.SubscriberSortByServiceName;
import hippoping.smsgw.api.comparator.subscriber.SubscriberSortByShortcode;
import hippoping.smsgw.api.comparator.subscriber.SubscriberSortByUnregisterDate;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceElement;
import hippoping.smsgw.api.db.Subscriber;
import hippoping.smsgw.api.db.SubscriberBlocked;
import hippoping.smsgw.api.db.SubscriberFactory;
import hippoping.smsgw.api.db.SubscriberGroup;
import hippoping.smsgw.api.db.User;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.StringConvert;

public class MemberViewServlet extends HttpServlet {

    protected int rows = 0;
    protected String old_orderby = "";
    protected int sort = 0;
    protected int nregistered = 0;

    private void sort(List<Subscriber> subList, String field, int swap) {
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
            comparator = new SubscriberSortByMsisdn();
        } else if (field.equals("shortcode")) {
            comparator = new SubscriberSortByShortcode();
        } else if (field.equals("service")) {
            comparator = new SubscriberSortByServiceName();
        } else if (field.equals("carrier")) {
            comparator = new SubscriberSortByCarrier();
        } else if (field.equals("register")) {
            comparator = new SubscriberSortByRegisterDate();
        } else if (field.equals("unregister")) {
            comparator = new SubscriberSortByUnregisterDate();
        } else if (field.equals("expired")) {
            comparator = new SubscriberSortByExpiredDate();
        } else if (field.equals("balanced")) {
            comparator = new SubscriberSortByBalancedDate();
        }
        Collections.sort(subList, comparator);
        if (this.sort == 1) {
            Collections.reverse(subList);
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
                    String tmp = request.getParameter("msisdn").replaceAll("\\r\\n", "");
                    if (tmp.trim().isEmpty()) {
                        System.err.println("MSISDN is required!!");
                        return;
                    }
                    String[] msisdn = tmp.split(";");
                    this.rows = Integer.parseInt(request.getParameter("rows"));

                    if ((srvcid == null) || (srvcid.equals(""))) {
                        srvcid = "-1";
                    }
                    if ((operid == null) || (operid.equals(""))) {
                        operid = "-1";
                    }

                    int srvc_id = Integer.parseInt(request.getParameter("srvcid"));
                    int oper_id = Integer.parseInt(request.getParameter("operid"));

                    request.getSession().setAttribute("subscriberList", 
                            Arrays.asList(new SubscriberFactory().getSubscriberList(msisdn, srvc_id, oper_id, null, -1, -1, user)));

                    LogEvent.log(LogEvent.EVENT_TYPE.SUBSCRIBER, 
                            LogEvent.EVENT_ACTION.SEARCH,
                            tmp,
                            (User) request.getSession().getAttribute("USER"),
                            null, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);
                }

                List subList = (List) request.getSession().getAttribute("subscriberList");

                this.nregistered = 0;
                for (int i = 0; i < subList.size(); i++) {
                    if (subList.get(i) != null) {
                        this.nregistered += (((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.REGISTER.getId() ? 1 : 0);
                    }

                }

                if ((subList != null) && (subList.size() > 0)) {
                    sort(subList, orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = subList.size() / this.rows + (subList.size() % this.rows != 0 ? 1 : 0);

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
                        + "    var input_param='?msisdn=" + request.getParameter("msisdn") 
                        + "&srvcid=" + srvcid + "&operid=" + operid 
                        + "&orderby=" + (orderby != null ? orderby : "")
                        + "&rows=" + this.rows + "&page=" + "';"
                        + "    function doHistory(operid, srvcid, msisdn)" 
                        + "    {" 
                        + "         window.open(\"message_history.jsp\" +" 
                        + "    \"?operid=\" + operid +"
                        + "    \"&srvcid=\" + srvcid +" 
                        + "    \"&msisdn=\" + msisdn +"
                        + "    \"&cmd=refresh\""
                        + "    ,null, \"height=650,width=732,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=no\");" 
                        + "    return;"
                        + "    }"
                        + "    function doEdit(operid, srvcid, msisdn) {"
                        + "       var ret = '';"
                        + "       window.open('member_edit.jsp?cmd=edit&msisdn=' + msisdn + '&operid=' + operid + '&srvcid=' + srvcid"
                        + "         ,null, 'height=650,width=732,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=no');" 
                        + "    }" 
                        + "    function doUnsub(operid, srvcid, msisdn)" 
                        + "    {" 
                        + "         window.open(\"member_manage\" +" 
                        + "    \"?operid=\" + operid +" 
                        + "    \"&srvcid=\" + srvcid +" 
                        + "    \"&msisdn=\" + msisdn +" 
                        + "    \"&action=unregister\" +" 
                        + "    \"&forward=MemberViewServlet\"" 
                        + "    + input_param + \"" + page + "\"" 
                        + "    ,null, \"height=650,width=732,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=no\");" 
                        + "    return;" 
                        + "    }" 
                        + "    function doUpdate(operid, srvcid, msisdn)"
                        + "    {"
                        + "         if (operid==1) {"
                        + "         ajaxPost(window.location.protocol + \"//\" + window.location.host"
                        + " + \"/cms/dtac/syncsub?msisdn=\" + msisdn + \"&srvc_main_id=\" + srvcid);"
                        + "         } else if (operid==6) {"
                        + "         ajaxPost(window.location.protocol + \"//\" + window.location.host"
                        + " + \"/cms/dtacsdp/syncsub?msisdn=\" + msisdn + \"&srvc_main_id=\" + srvcid);"
                        + "         }"
                        + "         mytime = setTimeout(window.location.reload(), 2);"
                        + "    }"
                        + "    function validate_page(page, maxpage) { "
                        + "       var frm = document.forms[\"reloadFrm\"];" + "       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}" + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}" + "       else {frm.submit();}" + "    }" + "    function goto_page(page) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.page.value=page;" + "       frm.submit();" + "    }" + "    </script>" + "</head>" + "<body style='background-color:#FFF;'>" + "   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;;'>" + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>" + "       <input type=hidden name=orderby value='" + orderby + "'>" + "       <input type=hidden name=swap value='0'>" + "       <input type=hidden name=csv value='0'>" + "       <div class='floatl' style='font-size:75%; padding-left:5px;'><b>Total " + subList.size() + "(" + this.nregistered + " registered) record(s) found." + "           (Page " + page + " of " + pg + ")</b> " + "           | Export <a href='javascript:window.location=\"./MemberViewServlet?csv=1&page=" + page + "\";'><img src='./images/csv_2.gif'></a>" + "       </div>" + "       <div class='floatr'>" + "         <span style='padding:0;'>" + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>") + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>") + "         </span>" + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>" + "           <input type=submit value=go>" + "       </div>" + "       </form>" + "       <table class='table3' style='width:100%;padding:0;'>" + "       <tr>" + "           <th width='2%'>No.</th>" + "           <th width='12%'>" + (orderby.equals("msisdn") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"msisdn\";frm.swap.value=1;frm.submit();'>MSISDN</a></th>" + "           <th width='9%'>" + (orderby.equals("shortcode") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"shortcode\";frm.swap.value=1;frm.submit();'>Shortcode</a></th>" + "           <th width='25%'>" + (orderby.equals("service") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"service\";frm.swap.value=1;frm.submit();'>Service</a></th>" + "           <th width='10%'>" + (orderby.equals("carrier") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"carrier\";frm.swap.value=1;frm.submit();'>Carrier</a></th>" + "           <th width='7%'>" + (orderby.equals("register") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"register\";frm.swap.value=1;frm.submit();'>Register</a></th>" + "           <th width='7%'>" + (orderby.equals("expired") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"expired\";frm.swap.value=1;frm.submit();'>Expired</a></th>" + "           <th width='7%'>" + (orderby.equals("unregister") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"unregister\";frm.swap.value=1;frm.submit();'>Unregister</a></th>" + "           <th width='7%'>" + (orderby.equals("balanced") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"balanced\";frm.swap.value=1;frm.submit();'>Billed</a></th>" + "           <th width='7%'>Status</th>" + "           <th width='7%'>Action</th>" + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                for (int i = sindex; (i < subList.size()) && (i < eindex); i++) {
                    String style = i % 2 == 0 ? "" : " d0";
                    style = style + (((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.UNREGISTER.getId() ? " d1" : "");
                    style = " class='" + style + "'";
                    out.print("<tr" + style + "><td>" + (i + 1) + "</td>");
                    out.print("<td>" + (SubscriberBlocked.isBlocked(((Subscriber) subList.get(i)).getMsisdn()) ? "<img title='blocked' src='images/block01.gif' border=0 style='vertical-align:middle'>" : "") + ((Subscriber) subList.get(i)).getMsisdn() + "</td>");

                    out.print("<td>" + ((Subscriber) subList.get(i)).getShortcode() + "</td>");
                    out.print("<td style='text-align:left;'>" + ((Subscriber) subList.get(i)).getSrvc_name() + "</td>");
                    out.print("<td>" + ((Subscriber) subList.get(i)).getOper_name() + "</td>");
                    out.print("<td>" + (((Subscriber) subList.get(i)).getRegister_date() == null ? "-" : ((Subscriber) subList.get(i)).getRegister_date("dd/MM/yy")) + "</td>");
                    out.print("<td>" + (((Subscriber) subList.get(i)).getExpired_date() == null ? "-" : ((Subscriber) subList.get(i)).getExpired_date("dd/MM/yy")) + "</td>");
                    out.print("<td>" + (((Subscriber) subList.get(i)).getUnregister_date() == null ? "-" : ((Subscriber) subList.get(i)).getUnregister_date("dd/MM/yy")) + "</td>");
                    out.print("<td>" + (((Subscriber) subList.get(i)).getBalanced_date() == null ? "-" : ((Subscriber) subList.get(i)).getBalanced_date("dd/MM/yy")) + "</td>");
                    out.print("<td>" + (((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.PREPARE2REGISTER.getId() ? "Renew" : ((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.REGISTER.getId() ? "Active" : "Expired") + "</td>");

                    out.print("<td>");
                    try {
                        ServiceElement se = new ServiceElement(((Subscriber) subList.get(i)).getSrvc_main_id(), ((Subscriber) subList.get(i)).getOper_id(), ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ON.getId() | ServiceElement.SERVICE_STATUS.TEST.getId());

                        out.print((se.isAble2ManageSub()) && ((((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.REGISTER.getId()) || (((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.PREPARE2REGISTER.getId())) ? "<a href='javascript:doUnsub(" + ((Subscriber) subList.get(i)).getOper_id() + "," + ((Subscriber) subList.get(i)).getSrvc_main_id() + ",\"" + ((Subscriber) subList.get(i)).getMsisdn() + "\");'><img title='unsub' src='./images/trash.gif'>" : "");

                        out.print((se.isAble2ManageSub()) && ((((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.REGISTER.getId()) || (((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.PREPARE2REGISTER.getId()) || (((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.BARRING.getId())) ? "<a href='javascript:doEdit(" + ((Subscriber) subList.get(i)).getOper_id() + "," + ((Subscriber) subList.get(i)).getSrvc_main_id() + ",\"" + ((Subscriber) subList.get(i)).getMsisdn() + "\");'><img title='edit' src='./images/edit02.gif'>" : "");

                        out.print(((se.srvc_type & ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getId()) > 0) && ((se.srvc_type & ServiceElement.SERVICE_TYPE.NOCSS.getId()) == 0) && (se.oper_id == OperConfig.CARRIER.DTAC.getId()) ? "<a href='javascript:doUpdate(" + ((Subscriber) subList.get(i)).getOper_id() + "," + ((Subscriber) subList.get(i)).getSrvc_main_id() + ",\"" + ((Subscriber) subList.get(i)).getMsisdn() + "\");'><img title='update' src='./images/refresh16.gif'>" : "");
                        
                        out.print(((se.srvc_type & ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getId()) > 0) && ((se.srvc_type & ServiceElement.SERVICE_TYPE.NOCSS.getId()) == 0) && (se.oper_id == OperConfig.CARRIER.DTAC_SDP.getId()) ? "<a href='javascript:doUpdate(" + ((Subscriber) subList.get(i)).getOper_id() + "," + ((Subscriber) subList.get(i)).getSrvc_main_id() + ",\"" + ((Subscriber) subList.get(i)).getMsisdn() + "\");'><img title='update' src='./images/refresh16.gif'>" : "");
                    } catch (Exception e) {
                    }

                    out.print("<a href='javascript:doHistory(" + ((Subscriber) subList.get(i)).getOper_id() + "," + ((Subscriber) subList.get(i)).getSrvc_main_id() + ",\"" + ((Subscriber) subList.get(i)).getMsisdn() + "\");'><img title='history' src='./images/sms.gif'></a>");

                    out.print("</td>");
                    out.print("</tr>");
                }
                out.println("</table></div>");
                out.println("<div id=\"popupmenu\"style=\"display:none;background-repeat: no-repeat; background-position: left bottom;height:70px; padding-bottom:14px; position:absolute;  left:0; top:0;\">   <div id=\"popupmenubg\" style=\"height:60px; width:200px; text-align:center; background-color:transparent; position:relative; padding:0px;\">       <div id=\"popupmenubox\" style=\"height:60px; width:100%; text-align:center; background-color:#000; position:relative; padding:0px;\">           <div id=\"popupmenucontentbox\" style=\"height:100%; width:100%; background-color:#FFF; position:relative; padding: 0; margin: 1px;\" onclick=\"javascript:;\">               <div class=\"floatr\" style=\"padding:2px 2px 0 0;\"><img src=\"./images/close03.gif\" onclick=\"hide2('popupmenu');\"></div>               <div id=\"popupmenucontent\" style=\"background-image: url(./images/ball.gif);               background-repeat: no-repeat; background-position: right top; height:50px; width:100%; background-color:#FFF; position:relative; padding: 0px;\"></div>           </div>       </div>   </div></div>");

                out.println("</body></html>");
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
                List subList = (List) request.getSession().getAttribute("subscriberList");

                if (subList == null) {
                    response.sendError(204);
                }

                multi.startResponse("text/csv;charset=tis-620");
                response.setHeader("Content-disposition", "attachment; filename=Member_View_Report.csv");
                response.setContentType("text/csv;charset=tis-620");
                out.append("No.,MSISDN,Shortcode,Description,Carrier,Register,Expired,Unregister,Billed,Status\r\n");
                for (int i = 0; i < subList.size(); i++) {
                    out.append(i + 1 + ",");
                    out.append(((Subscriber) subList.get(i)).getMsisdn() + ",");
                    out.append(((Subscriber) subList.get(i)).getShortcode() + ",");
                    out.append(StringConvert.Unicode2ASCII2(((Subscriber) subList.get(i)).getSrvc_name()) + ",");
                    out.append(((Subscriber) subList.get(i)).getOper_name() + ",");
                    out.append((((Subscriber) subList.get(i)).getRegister_date() == null ? "-" : ((Subscriber) subList.get(i)).getRegister_date("MM/dd/yy")) + ",");

                    out.append((((Subscriber) subList.get(i)).getExpired_date() == null ? "-" : ((Subscriber) subList.get(i)).getExpired_date("MM/dd/yy")) + ",");

                    out.append((((Subscriber) subList.get(i)).getUnregister_date() == null ? "-" : ((Subscriber) subList.get(i)).getUnregister_date("MM/dd/yy")) + ",");

                    out.append((((Subscriber) subList.get(i)).getBalanced_date() == null ? "-" : ((Subscriber) subList.get(i)).getBalanced_date("MM/dd/yy")) + ",");

                    out.append(((Subscriber) subList.get(i)).getState() == SubscriberGroup.SUB_STATUS.UNREGISTER.getId() ? " Expired" : "Active");
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