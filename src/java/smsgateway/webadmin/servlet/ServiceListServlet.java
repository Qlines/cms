package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.service.ServiceElementSortByServiceName;
import hippoping.smsgw.api.comparator.service.ServiceElementSortByShortcode;
import hippoping.smsgw.api.db.ContentManagerMap;
import hippoping.smsgw.api.db.DroConfigure;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceCharge;
import hippoping.smsgw.api.db.ServiceElement;
import hippoping.smsgw.api.db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DBPoolManager;
import lib.common.StringConvert;
import smsgateway.webadmin.bean.ServiceBean;

public class ServiceListServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ServiceListServlet.class.getName());
    protected List<ServiceElement> serviceMainList = null;
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

        if ((field == null) || (field.equals("shortcode"))) {
            comparator = new ServiceElementSortByShortcode();
        } else if (field.equals("service")) {
            comparator = new ServiceElementSortByServiceName();
        }
        if (comparator != null) {
            Collections.sort(this.serviceMainList, comparator);
            if (this.sort == 1) {
                Collections.reverse(this.serviceMainList);
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
                String encoding = "UTF-8";
                if (request.getCharacterEncoding() != null) {
                    encoding = request.getCharacterEncoding();
                }

                String orderby = request.getParameter("orderby");
                String page = request.getParameter("page");
                String swap = request.getParameter("swap");

                String cmd = request.getParameter("cmd");

                if ((page == null) || (page.equals(""))) {
                    page = "1";
                }

                String srvc_main_id = request.getParameter("srvc_main_id");
                String operid = request.getParameter("oper_id");

                int srvc_id = 0;
                try {
                    srvc_id = Integer.parseInt(srvc_main_id);
                } catch (Exception e) {
                }
                int oper_id = 0;
                try {
                    oper_id = Integer.parseInt(operid);
                } catch (Exception e) {
                }
                if ((cmd != null) && (cmd.equals("remove"))) {
                    new ContentManagerMap().remove(OperConfig.CARRIER.fromId(oper_id), srvc_id);

                    if (oper_id > 0) {
                        try {
                            ServiceElement se = new ServiceElement(srvc_id, oper_id, ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());

                            DroConfigure dro = new DroConfigure(se);
                            dro.remove();

                            se.remove();

                            LogEvent.log(LogEvent.EVENT_TYPE.SERVICE, LogEvent.EVENT_ACTION.DELETE, "remove service sub", (User) request.getSession().getAttribute("USER"), null, OperConfig.CARRIER.fromId(oper_id), srvc_id, 0, 0, LogEvent.LOG_LEVEL.INFO);
                        } catch (Exception e) {
                            log.severe(e.getMessage());
                        }
                    } else {
                        OperConfig.CARRIER[] opers = {OperConfig.CARRIER.AIS, OperConfig.CARRIER.AIS_LEGACY, OperConfig.CARRIER.TRUE, OperConfig.CARRIER.DTAC, OperConfig.CARRIER.TRUEH, OperConfig.CARRIER.DTAC_SDP};
                        for (OperConfig.CARRIER oper : opers) {
                            try {
                                ServiceElement se = new ServiceElement(srvc_id, oper.getId(), ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());
                                if (se.srvc_id != null) {
                                    DroConfigure dro = new DroConfigure(se);
                                    dro.remove();

                                    se.remove();

                                    LogEvent.log(LogEvent.EVENT_TYPE.SERVICE, LogEvent.EVENT_ACTION.DELETE, "remove all services sub dependencies", (User) request.getSession().getAttribute("USER"), null, oper, srvc_id, 0, 0, LogEvent.LOG_LEVEL.INFO);
                                }
                            } catch (Exception e) {
                            }

                        }

                        try {
                            DBPoolManager cp = new DBPoolManager();
                            try {
                                String sql = "DELETE FROM srvc_main  WHERE srvc_main_id=" + srvc_id;

                                Logger.getLogger(getClass().getName()).log(Level.INFO, "remove service {0} row(s).", Integer.valueOf(cp.execUpdate(sql)));

                                LogEvent.log(LogEvent.EVENT_TYPE.SERVICE, LogEvent.EVENT_ACTION.DELETE, "remove services main", (User) request.getSession().getAttribute("USER"), null, null, srvc_id, 0, 0, LogEvent.LOG_LEVEL.INFO);
                            } catch (SQLException e) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "SQL Error!!", e);
                            } finally {
                                cp.release();
                            }
                        } catch (Exception e) {
                            log.severe(e.getMessage());
                        }

                    }

                    srvc_id = 0;
                    oper_id = 0;
                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("add"))) {
                    String srvc_name = new String(request.getParameter("srvc_name").getBytes("ISO8859_1"), encoding);
                    int price = Integer.parseInt(request.getParameter("price"));
                    int srvc_chrg_type_id = Integer.parseInt(request.getParameter("srvcchrg"));
                    int srvc_chrg_amnt = Integer.parseInt(request.getParameter("chrg_amnt"));

                    if (!srvc_name.trim().isEmpty()) {
                        try {
                            DBPoolManager cp = new DBPoolManager();
                            try {
                                String sql = "INSERT INTO srvc_main (name, price, srvc_chrg_type_id, srvc_chrg_amnt)  VALUES (?, ?, ?, ?)";

                                cp.prepareStatement(sql, 1);
                                cp.getPreparedStatement().setString(1, srvc_name);
                                cp.getPreparedStatement().setInt(2, price);
                                cp.getPreparedStatement().setInt(3, srvc_chrg_type_id);
                                cp.getPreparedStatement().setInt(4, srvc_chrg_amnt);

                                int row = cp.execUpdatePrepareStatement();
                                if (row == 1) {
                                    LogEvent.log(LogEvent.EVENT_TYPE.SERVICE, LogEvent.EVENT_ACTION.ADD, "create service main", (User) request.getSession().getAttribute("USER"), null, null, srvc_id, 0, 0, LogEvent.LOG_LEVEL.INFO);

                                    ResultSet rs = cp.getPreparedStatement().getGeneratedKeys();
                                    try {
                                        if (rs.next()) {
                                            srvc_id = rs.getInt(1);
                                        }
                                    } finally {
                                        rs.close();
                                    }
                                }
                                Logger.getLogger(getClass().getName()).log(Level.INFO, "insert service {0} row(s).", Integer.valueOf(row));
                            } catch (SQLException e) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "SQL Error!!", e);
                            } finally {
                                cp.release();
                            }
                        } catch (Exception e) {
                            log.severe(e.getMessage());
                        }

                    }

                    srvc_id = 0;
                    oper_id = 0;
                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("edit")) && (srvc_id > 0)) {
                    String srvc_name = new String(request.getParameter("srvc_name").getBytes("ISO8859_1"), encoding);
                    int price = Integer.parseInt(request.getParameter("price"));
                    int srvc_chrg_type_id = Integer.parseInt(request.getParameter("srvcchrg"));
                    int srvc_chrg_amnt = Integer.parseInt(request.getParameter("chrg_amnt"));
                    try {
                        DBPoolManager cp = new DBPoolManager();
                        try {
                            String sql = 
                                    "   UPDATE srvc_main sm"
                                    + "   LEFT JOIN srvc_sub ss"
                                    + "     ON sm.srvc_main_id = ss.srvc_main_id"
                                    + "    SET sm.name=?"
                                    + "      , sm.price=?"
                                    + "      , sm.srvc_chrg_type_id=?"
                                    + "      , sm.srvc_chrg_amnt=?"
                                    + "      , ss.chrg_flg=?"
                                    + "  WHERE sm.srvc_main_id=?";

                            cp.prepareStatement(sql);
                            cp.getPreparedStatement().setString(1, srvc_name);
                            cp.getPreparedStatement().setInt(2, price);
                            cp.getPreparedStatement().setInt(3, srvc_chrg_type_id);
                            cp.getPreparedStatement().setInt(4, srvc_chrg_amnt);
                            cp.getPreparedStatement().setString(5, srvc_chrg_type_id == ServiceCharge.SRVC_CHRG.PER_MESSAGE.getId() ? "MT" : "MO");
                            cp.getPreparedStatement().setInt(6, srvc_id);

                            Logger.getLogger(getClass().getName()).log(Level.INFO, "update service {0} row(s).", Integer.valueOf(cp.execUpdatePrepareStatement()));

                            LogEvent.log(LogEvent.EVENT_TYPE.SERVICE, LogEvent.EVENT_ACTION.MODIFY, "modify service main", (User) request.getSession().getAttribute("USER"), null, null, srvc_id, 0, 0, LogEvent.LOG_LEVEL.INFO);
                        } catch (SQLException e) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "SQL Error!!", e);
                        } finally {
                            cp.release();
                        }
                    } catch (Exception e) {
                        log.severe(e.getMessage());
                    }

                    srvc_id = 0;
                    oper_id = 0;
                    cmd = "refresh";
                }

                if ((cmd != null) && (cmd.equals("refresh"))) {
                    this.rows = Integer.parseInt(request.getParameter("rows"));

                    String search = request.getParameter("search");
                    if (search != null) {
                        search = new String(search.getBytes("ISO8859_1"), encoding);
                        request.getSession().setAttribute(getServletName() + "_search", search);
                    } else {
                        search = (String) request.getSession().getAttribute(getServletName() + "_search");
                    }

                    this.serviceMainList = ServiceElement.getAllServiceMain(oper_id, srvc_id, search);

                    LogEvent.log(LogEvent.EVENT_TYPE.SERVICE, LogEvent.EVENT_ACTION.SEARCH, "list all services", (User) request.getSession().getAttribute("USER"), null, null, 0, 0, 0, LogEvent.LOG_LEVEL.INFO);
                }

                if ((this.serviceMainList != null) && (this.serviceMainList.size() > 0)) {
                    sort(orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = this.serviceMainList.size() / this.rows + (this.serviceMainList.size() % this.rows != 0 ? 1 : 0);

                pg = pg == 0 ? 1 : pg;
                out.println("<html><head>"
                        + "    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>"
                        + "    <link href='./css/cv.css' rel='stylesheet' type='text/css'>"
                        + "    <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>"
                        + "    <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>"
                        + "    <link href='./css/infobox01.css' rel='stylesheet' type='text/css'>"
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
                        + "    <script src='./js/mouseevent.js' type='text/javascript'></script>"
                        + "    <script src='./js/filter_input.js' type='text/javascript'></script>"
                        + "    <script>"
                        + "    var _allServiceCharge = '" + new ServiceBean().findServiceCharge() + "';"
                        + "    function validate_page(page, maxpage) { "
                        + "       var frm = document.forms[\"reloadFrm\"];"
                        + "       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}"
                        + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}"
                        + "       else {frm.submit();}"
                        + "    }"
                        + "    function goto_page(page) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.page.value=page;"
                        + "       frm.submit();"
                        + "    }"
                        + "    function resetEditDiv() {"
                        + "       hide2('editdiv');"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.srvc_main_id.value=0;"
                        + "       frm.srvc_name.value='';"
                        + "       frm.price.value=0;"
                        + "       frm.chrg_amnt.value=1;"
                        + "       frm.srvcchrg.selectedIndex=0;"
                        + "       frm.cmdbtn.value='Submit';"
                        + "       show2('editdiv');"
                        + "    }"
                        + "    function doEdit(sid, srvc_name, price, chrg_amnt, srvc_chrg) {"
                        + "       resetEditDiv();"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='edit';"
                        + "       frm.srvc_main_id.value=sid;"
                        + "       frm.srvc_name.value=srvc_name;"
                        + "       frm.price.value=price;"
                        + "       frm.chrg_amnt.value=chrg_amnt;"
                        + "       frm.srvcchrg.selectedIndex=srvc_chrg;"
                        + "       frm.cmdbtn.value='Change';"
                        + "    }"
                        + "    function doEdit2(sid, oid, srvc_name, srvc_chrg) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='edit';"
                        + "       frm.srvc_main_id.value=sid;"
                        + "       frm.oper_id.value=oid;"
                        + "       frm.srvcchrg.selectedIndex=srvc_chrg;"
                        + "       frm.srvc_name.value=srvc_name;"
                        + "       frm.action='services_sub_edit.jsp';"
                        + "       frm.target='_parent';"
                        + "       frm.submit();"
                        + "    }"
                        + "    function doRemove(sid, oid, service_name) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='remove';"
                        + "       frm.oper_id.value=oid;"
                        + "       frm.srvc_main_id.value=sid;"
                        + "       var msg = 'Click OK to confirm to delete service \"' + service_name + ((oid==0)?'\" and all sub-service dependencies':'\"') + ', otherwise click cancel.';"
                        + "       if (confirm(msg)) {"
                        + "           frm.submit();"
                        + "       }"
                        + "    }"
                        + "    function doAdd() {"
                        + "       resetEditDiv();"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='add';"
                        + "       frm.cmdbtn.value='Add';"
                        + "    }"
                        + "    function doAdd2(sid, oid, srvc_name, srvc_chrg) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='add';"
                        + "       frm.srvc_main_id.value=sid;"
                        + "       frm.oper_id.value=oid;"
                        + "       frm.srvcchrg.selectedIndex=srvc_chrg;"
                        + "       frm.srvc_name.value=srvc_name;"
                        + "       frm.action='services_sub_edit.jsp';"
                        + "       frm.target='_parent';"
                        + "       frm.submit();"
                        + "    }"
                        + "    function doCopy(sid, oid, srvc_name, srvc_chrg) {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       frm.cmd.value='copy';"
                        + "       var ret = '';"
                        + "       if (!(ret = window.showModalDialog('srvc_main_id_select.jsp?oper_id=' + oid, '', 'dialogWidth:640px;dialogHeight:260px'))) return;"
                        + "       try {"
                        + "         frm.oper_id.value=ret.split(';')[0];"
                        + "         frm.copy_srvc_main_id.value=ret.split(';')[1];"
                        + "         frm.copy_oper_id.value=oid;"
                        + "       } catch (e) {return};"
                        + "       if (frm.copy_srvc_main_id.value == 0) return;"
                        + "       frm.srvc_main_id.value=sid;"
                        + "       frm.srvcchrg.selectedIndex=srvc_chrg;"
                        + "       frm.srvc_name.value=srvc_name;"
                        + "       frm.action='services_sub_edit.jsp';"
                        + "       frm.target='_parent';" 
                        + "       frm.submit();"
                        + "    }"
                        + "    function doSend() {"
                        + "       frm=document.forms[\"reloadFrm\"];"
                        + "       if (frm.srvc_name.value.trim() == '') {" + "           alert('Please enter service name!!');"
                        + "           frm.srvc_name.focus();" + "       } else {" + "           frm.submit();" + "       }" + "    }"
                        + "    function getServiceDetails(srvc_main_id, oper_id) {"
                        + "       show2('service_info_box');"
                        + "       var url = 'service_details.jsp?srvc_main_id=' + srvc_main_id + '&oper_id=' + oper_id;"
                        + "       var obj = getElement('service_info_box');"
                        + "       if (obj) obj.innerHTML = ajaxPost(url);"
                        + "       setPosition('service_info_box');" + "    }"
                        + "    window.onload=function() {"
                        + "       var frm = document.reloadFrm;"
                        + "       updateServiceOptions(_allServiceCharge, frm.srvcchrg);"
                        + "       frm.srvcchrg.selectedIndex = 1;" + "    }"
                        + "    </script>"
                        + "</head>"
                        + "<body style='background-color:#FFF;'>" + "   <div id='service_info_box' class='hintbox' style='display:none;'></div>"
                        + "   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;'>"
                        + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>"
                        + "       <input type=hidden name=cmd value=''>"
                        + "       <input type=hidden name=copy_oper_id value=''>"
                        + "       <input type=hidden name=copy_srvc_main_id value=''>"
                        + "       <input type=hidden name=srvc_main_id value=''>"
                        + "       <input type=hidden name=oper_id value=''>"
                        + "       <input type=hidden name=orderby value='" + orderby + "'>"
                        + "       <input type=hidden name=rows value='" + this.rows + "'>"
                        + "       <input type=hidden name=swap value='0'>"
                        + "       <input type=hidden name=csv value='0'>"
                        + "       <div id='content' class='floatl' style='font-size:75%; padding-left:5px; background:#FFF;'>"
                        + "           <b>Total " + this.serviceMainList.size() + " record(s) found. (Page " + page + " of " + pg + ")</b> "
                        + "             | <a href='javascript:doAdd();' style='color:#333;'>"
                        + "             <img src='./images/new2.gif' border='0' style='vertical-align:middle'> " + "             Creat new service" + "             </a>" + "       </div>" + "       <div class='floatr'>" + "         <span style='padding:0;'>" + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>") + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>") + "         </span>" + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>" + "           <input type=submit value=go>" + "       </div>" + "       <table class='table3' style='width:100%;padding:1px;margin:1px 0 0 1px;'>" + "       <tr>" + "           <th width='3%'>No.</th>" + "           <th width='30%'>" + ((orderby != null) && (orderby.equals("service")) ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"service\";frm.swap.value=1;frm.submit();'>Service</a></th>" + "           <th width='5%'></th>" + "           <th width='8%'>Shortcode</th>"
                        + "           <th width='10%'>Price</th>"
                        + "           <th width='7%'>AIS</th>"
                        + "           <th width='7%'>DTAC</th>"
                        + "           <th width='7%'>TMV</th>"
                        + "           <th width='7%'>TMH</th>"
                        + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                String[] bullet = {"./images/button16pixr.gif", "./images/button16pixgr.gif", "./images/button16pixy.gif", "./images/button16pixg.gif"};
                String[] bgcolor = {"#FF0000", "#33FF33", "#FFFF00", "#DDD"};
                for (int i = sindex; (i < this.serviceMainList.size()) && (i < eindex); i++) {
                    String style = i % 2 == 0 ? "" : " d0";
                    style = " class='" + style + "'";
                    try {
                        ServiceElement srvc_dtac_cpa = new ServiceElement(((ServiceElement) this.serviceMainList.get(i)).srvc_main_id, 
                                OperConfig.CARRIER.DTAC.getId(), 
                                ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());

                        ServiceElement srvc_dtac_sdp = new ServiceElement(((ServiceElement) this.serviceMainList.get(i)).srvc_main_id, 
                                OperConfig.CARRIER.DTAC_SDP.getId(), 
                                ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());

                        ServiceElement srvc_true = new ServiceElement(((ServiceElement) this.serviceMainList.get(i)).srvc_main_id, 
                                OperConfig.CARRIER.TRUE.getId(), 
                                ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());

                        ServiceElement srvc_trueh = new ServiceElement(((ServiceElement) this.serviceMainList.get(i)).srvc_main_id, 
                                OperConfig.CARRIER.TRUEH.getId(), 
                                ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());

                        ServiceElement srvc_ais_legacy = new ServiceElement(((ServiceElement) this.serviceMainList.get(i)).srvc_main_id, 
                                OperConfig.CARRIER.AIS_LEGACY.getId(),
                                ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());

                        ServiceElement srvc_ais_cdg = new ServiceElement(((ServiceElement) this.serviceMainList.get(i)).srvc_main_id,
                                OperConfig.CARRIER.AIS.getId(), 
                                ServiceElement.SERVICE_TYPE.ALL.getId(), ServiceElement.SERVICE_STATUS.ALL.getId());

                        ServiceElement srvc_ais = srvc_ais_legacy.srvc_id != null ? srvc_ais_legacy : srvc_ais_cdg;
                        ServiceElement srvc_dtac = srvc_dtac_cpa.srvc_id != null ? srvc_dtac_cpa : srvc_dtac_sdp;

                        if (srvc_ais_cdg.srvc_id != null) {
                            ((ServiceElement) this.serviceMainList.get(i)).srvc_id = srvc_ais_cdg.srvc_id;
                        } else if (srvc_ais_legacy.srvc_id != null) {
                            ((ServiceElement) this.serviceMainList.get(i)).srvc_id = srvc_ais_legacy.srvc_id;
                        } else if (srvc_true.srvc_id != null) {
                            ((ServiceElement) this.serviceMainList.get(i)).srvc_id = srvc_true.srvc_id;
                        } else if (srvc_trueh.srvc_id != null) {
                            ((ServiceElement) this.serviceMainList.get(i)).srvc_id = srvc_trueh.srvc_id;
                        } else if (srvc_dtac_cpa.srvc_id != null) {
                            ((ServiceElement) this.serviceMainList.get(i)).srvc_id = srvc_dtac_cpa.srvc_id;
                        } else if (srvc_dtac_sdp.srvc_id != null) {
                            ((ServiceElement) this.serviceMainList.get(i)).srvc_id = srvc_dtac_sdp.srvc_id;
                        } else {
                            ((ServiceElement) this.serviceMainList.get(i)).srvc_id = "-";
                        }

                        if (srvc_ais.srvc_id == null) {
                            srvc_ais.status = 3;
                        }
                        if (srvc_dtac.srvc_id == null) {
                            srvc_dtac.status = 3;
                        }
                        if (srvc_true.srvc_id == null) {
                            srvc_true.status = 3;
                        }
                        if (srvc_trueh.srvc_id == null) {
                            srvc_trueh.status = 3;
                        }

                        out.print("<tr" + style + "><td>" + (i + 1) + "</td>");
                        out.print("<td>" + ((ServiceElement) this.serviceMainList.get(i)).srvc_name + "</td>");
                        out.print("<td> <a href='javascript:doEdit(" 
                                + ((ServiceElement) this.serviceMainList.get(i)).srvc_main_id 
                                + "," + "\"" + StringConvert.replace(((ServiceElement) this.serviceMainList.get(i)).srvc_name, "'", "\\\\&#39;", true) 
                                + "\"," + ((ServiceElement) this.serviceMainList.get(i)).price 
                                + "," + ((ServiceElement) this.serviceMainList.get(i)).srvc_chrg_amnt 
                                + "," + ((ServiceElement) this.serviceMainList.get(i)).srvc_chrg_type_id 
                                + ")'>" 
                                + "<img src='./images/edit02.gif' border='0'>" 
                                + "</a> " 
                                + "<a href='javascript:doRemove(" 
                                + ((ServiceElement) this.serviceMainList.get(i)).srvc_main_id 
                                + "," + "0,\"" + StringConvert.replace(((ServiceElement) this.serviceMainList.get(i)).srvc_name, "'", "\\\\&#39;", true) 
                                + "\")'>"
                                + "<img src='./images/trash.gif' border='0'>" 
                                + "</a>" 
                                + "</td>");

                        out.print("<td>" + ((ServiceElement) this.serviceMainList.get(i)).srvc_id + "</td>");
                        out.print("<td>" + ((ServiceElement) this.serviceMainList.get(i)).price / (((ServiceElement) this.serviceMainList.get(i)).srvc_chrg_amnt <= 0 ? 1 : ((ServiceElement) this.serviceMainList.get(i)).srvc_chrg_amnt) + " บาท " + ServiceCharge.SRVC_CHRG.fromId(((ServiceElement) this.serviceMainList.get(i)).srvc_chrg_type_id).toString() + "</td>");

                        ServiceElement[] ses = {srvc_ais, srvc_dtac, srvc_true, srvc_trueh};
                        int[] oo = {OperConfig.CARRIER.AIS.getId(),
                            OperConfig.CARRIER.DTAC.getId(),
                            OperConfig.CARRIER.TRUE.getId(),
                            OperConfig.CARRIER.TRUEH.getId()};
                        int j = 0;
                        for (ServiceElement sss : ses) {
                            out.print("<td style='background:" + bgcolor[sss.status] + "'"
                                    + (sss.status <= ServiceElement.SERVICE_STATUS.TEST.getDbId()
                                    ? " onmouseout='javascript:hide2(\"service_info_box\");'"
                                    + " onmouseover='javascript:getServiceDetails("
                                    + ((ServiceElement) this.serviceMainList.get(i)).srvc_main_id + "," + sss.oper_id + ");'"
                                    : "")
                                    + ">"
                                    + (sss.status <= ServiceElement.SERVICE_STATUS.TEST.getDbId()
                                    ? " <a href='javascript:doEdit2("
                                    + ((ServiceElement) this.serviceMainList.get(i)).srvc_main_id
                                    + "," + sss.oper_id
                                    + ",\""
                                    + StringConvert.replace(((ServiceElement) this.serviceMainList.get(i)).srvc_name, "'", "\\\\&#39;", true)
                                    + "\"," + ((ServiceElement) this.serviceMainList.get(i)).srvc_chrg_type_id
                                    + ")'>"
                                    + "<img src='./images/edit03.gif' border='0'>"
                                    + "</a> "
                                    + "<a href='javascript:doRemove("
                                    + ((ServiceElement) this.serviceMainList.get(i)).srvc_main_id
                                    + "," + sss.oper_id
                                    + ",\""
                                    + StringConvert.replace(((ServiceElement) this.serviceMainList.get(i)).srvc_name, "'", "\\\\&#39;", true)
                                    + "\")'>"
                                    + "<img src='./images/delete01.gif' border='0'>"
                                    + "</a>"
                                    : new StringBuilder().append("<a href='javascript:doAdd2(").append(((ServiceElement) this.serviceMainList.get(i)).srvc_main_id).append(",").append(oo[j]).append(",\"").append(StringConvert.replace(((ServiceElement) this.serviceMainList.get(i)).srvc_name, "'", "\\\\&#39;", true)).append("\",").append(((ServiceElement) this.serviceMainList.get(i)).srvc_chrg_type_id).append(")'>").append("<img src='./images/new2.gif' border=0 style='vertical-align:middle;'></a>&nbsp;").append("<a href='javascript:doCopy(").append(((ServiceElement) this.serviceMainList.get(i)).srvc_main_id).append(",").append(oo[j]).append(",\"").append(StringConvert.replace(((ServiceElement) this.serviceMainList.get(i)).srvc_name, "'", "\\\\&#39;", true)).append("\",").append(((ServiceElement) this.serviceMainList.get(i)).srvc_chrg_type_id).append(")'>").append("<img src='./images/copy03.gif' border=0 style='vertical-align:middle;'></a>").toString())
                                    + "</td>");

                            j++;
                        }
                        out.print("</tr>");
                    } catch (Exception e) {
                        log.severe(e.getMessage());
                    }
                }
                out.println("<tr style='display:block;text-align:left;background:#333;'><td width='100%' colspan='9' style='text-align:left;color:#FFF;' onclick='javascript:doAdd();'>Click here to creat new service &gt;&gt;</td></tr>");

                out.println("<tr id='editdiv' style='display:none;text-align:left;background:#BBB;'><td style='text-align:left;vertical-align:baseline;' onclick='hide2(\"editdiv\")'>&lt;&lt;</td><td colspan='7' style='text-align:left;'>service name: <input type='text' name='srvc_name' maxlength=100 style='width:120px;font-size:.95em'> | price: <input type='text' name='price' value='0' maxlength=2 style='width:20px;font-size:.95em' onkeypress='return filter_digit_char(event)'> บาท<input type='hidden' name='chrg_amnt' value='1'><script>document.write(createOptions(null, null, 'srvcchrg', false, 0));</script> <input class='button' type='button' name='cmdbtn' value='Add' onclick='javascript:doSend();'></td></tr></form>");

                out.println("<tr><td colspan='9' style='font-weight:normal; font-size:.85em; text-align:right; padding: 10px 10px 0 0;'><img src='" + bullet[0] + "' border=0 style='vertical-align:middle;'> off " + "<img src='" + bullet[1] + "' border=0 style='vertical-align:middle;'> on " + "<img src='" + bullet[2] + "' border=0 style='vertical-align:middle;'> test " + "<img src='" + bullet[3] + "' border=0 style='vertical-align:middle;'> unavailabled " + "</td></tr>");

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