<%-- 
    Document   : service_view
    Created on : 1 ธ.ค. 2552, 4:02:50
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement" %>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.User.*" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.*" %>
<%@page import="hippoping.smsgw.api.db.ServiceCharge.*" %>
<%@page import="hippoping.smsgw.api.db.OperConfig.CARRIER" %>
<jsp:useBean id="serviceBean" scope="application" class="smsgateway.webadmin.bean.ServiceBean" />

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <style type="text/css">
            body{margin:0px; padding: 0px; background: white;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding-bottom:1em}
            h2{padding-top: 0.3em}
            div#serviceViewContent {background: #377CB1;}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <%
                    User user = (User) request.getSession().getAttribute("USER");
                    if (user == null) {
                        out.print("<script>window.location='logout?msg=Your session has been expired.'</script>");
                        return;
                    }

                    int srvc_type = SERVICE_TYPE.ALL.getId();
                    int srvc_status = SERVICE_STATUS.ALL.getId();
        %>
        <jsp:include page="./services_bean.jsp">
            <jsp:param name="srvc_type" value="<%=srvc_type%>" />
            <jsp:param name="srvc_status" value="<%=srvc_status%>" />
        </jsp:include>
        <script type="text/javascript">
            <%
                        // get parameters
                        String service_name = request.getParameter("srvcname");
                        String sort = request.getParameter("sort");

                        int pg = 1;
                        try {
                            pg = Integer.parseInt(request.getParameter("page"));
                        } catch (NumberFormatException e) {
                        }
                        int records = 10;
                        try {
                            records = Integer.parseInt(request.getParameter("records"));
                        } catch (NumberFormatException e) {
                        }

                        String serviceList = serviceBean.findService(service_name, sort, -1, -1, user); // get all records

                        out.println("var service=\"" + serviceList + "\".split(';');");
                        out.println("var page=" + pg + ";");
                        out.println("var records=" + records + ";");
                        out.println("var input_param='"
                                + "?srvcname=" + serviceList
                                + "&sort=" + ((sort != null) ? sort : "")
                                + "&records=" + records
                                + "&page="
                                + "';");
            %>
    // fix remove an unwant element
    //service.splice(service.length-1, 1);

    // test
    //records=1;

    var xm = 0;
    var x_offset = 5;
    var ym = 0;
    var y_offset = 15;
    var nav = (document.layers);
    if(nav) document.captureEvents(Event.MOUSEMOVE);
    document.onmousemove = get_mouse;

    function getPage(id)
    {
        lineperpage = records;
        return Math.floor(id/lineperpage) + 1;
    }

    function get_mouse(e)
    {

        if (!e)
            var e = window.event||window.Event;
        xm = (nav) ? e.pageX : e.clientX+document.body.scrollLeft;
        ym = (nav) ? e.pageY : e.clientY+document.body.scrollTop;
        xm += x_offset;
        ym += y_offset;
    }

    function getData(p)
    {
        var ret = "";
        var records = <%=records%>;
        var from = records*(p-1);
        var to = records*p;
        ret += "<table class=\"table3\" style=\"width:100%;\">" +
            "<tr><th width=40%>Service Name</th><th>Price</th><th>FT<br>(days)</th><th>Charge<br>Amount</th>" +
            "<th width=5% style='font-size:80%;'>AIS</th>" +
            "<th width=5% style='font-size:80%;'>DTAC</th>" +
            "<th width=5% style='font-size:80%;'>TMV/TMH</th></tr>";
        
<%
        out.print("ret += \"");
        String []bullet = {"./images/button16pixr.gif", "./images/button16pixgr.gif", "./images/button16pixy.gif", "./images/button16pixg.gif"};
        List seList = new ArrayList<ServiceElement>();
        seList = ServiceElement.getAllService(null, srvc_type, srvc_status);
        for (int i=0;i<seList.size();i++) {
            out.println("<tr>");
            out.println("<td>" + ((ServiceElement)seList.get(i)).srvc_name + "</td>");
            out.println("<td>" + ((ServiceElement)seList.get(i)).price + "</td>");
            out.println("<td>" + ((ServiceElement)seList.get(i)).free_trial + "</td>");
            out.println("<td>" + ((ServiceElement)seList.get(i)).srvc_chrg_amnt + SRVC_CHRG.fromId(((ServiceElement)seList.get(i)).srvc_chrg_type_id) + "</td>");
            ServiceElement srvc_dtac = new ServiceElement(((ServiceElement)seList.get(i)).srvc_main_id, CARRIER.DTAC.getId(), srvc_type, srvc_status);
            ServiceElement srvc_true = new ServiceElement(((ServiceElement)seList.get(i)).srvc_main_id, CARRIER.TRUE.getId(), srvc_type, srvc_status);
            ServiceElement srvc_ais_legacy = new ServiceElement(((ServiceElement)seList.get(i)).srvc_main_id, CARRIER.AIS_LEGACY.getId(), srvc_type, srvc_status);
            ServiceElement srvc_ais_cdg = new ServiceElement(((ServiceElement)seList.get(i)).srvc_main_id, CARRIER.AIS.getId(), srvc_type, srvc_status);
            ServiceElement srvc_ais = null;
            if (srvc_ais_legacy!=null) {
                srvc_ais = srvc_ais_legacy;
            } else if (srvc_ais_cdg!=null) {
                srvc_ais = srvc_ais_cdg;
            }
            out.println("<td><img src='" + ((srvc_ais!=null)?bullet[srvc_ais.status]:bullet[3]) + "' border=0 style='vertical-align:middle;'></td>");
            out.println("<td><img src='" + ((srvc_dtac!=null)?bullet[srvc_dtac.status]:bullet[3]) + "' border=0 style='vertical-align:middle;'></td>");
            out.println("<td><img src='" + ((srvc_true!=null)?bullet[srvc_true.status]:bullet[3]) + "' border=0 style='vertical-align:middle;'></td>");
            out.println("</tr>");
        }
        out.print("\"");
%>


        // print the description of icon
        ret += "<tr><td colspan='8' style='font-weight:normal; font-size:.85em; text-align:right; padding: 10px 10px 0 0;'>" +
            "<img src='" + <%=bullet[0]%> + "' border=0 style='vertical-align:middle;'> off " +
            "<img src='" + <%=bullet[1]%> + "' border=0 style='vertical-align:middle;'> on " +
            "<img src='" + <%=bullet[2]%> + "' border=0 style='vertical-align:middle;'> test " +
            "<img src='" + <%=bullet[3]%> + "' border=0 style='vertical-align:middle;'> unavailabled " +
            "</td></tr>";

        // print the close tr of last record
        ret += "</table>";

        //alert(ret);

        return ret;
    }

    function getPageIndex(p)
    {
        var ret="";
        var maxpage = getPage(service.length-1);

        // previous page
        if (p>1) {
            ret += "<a rel='page 1' href=\"javascript:showData(1, document.getElementById('data'));" +
                "showPageIndex(1, document.getElementById('pgindex'));\">&lt;&lt;</a>&nbsp;";
            ret += "<a href=\"javascript:showData(" + (p-1) + ", document.getElementById('data'));" +
                "showPageIndex(" + (p-1) + ", document.getElementById('pgindex'));\">&lt;</a>&nbsp;";
        } else {
            ret += "&lt;&lt;&nbsp;&lt&nbsp;";
        }

        if (p-3 > 1 )
            ret += " ... ";

        for (i=p-3;i<p;i++) {
            if (i<=0)
                continue;
            ret += "<a href=\"javascript:showData(" + i + ", document.getElementById('data'));" +
                "showPageIndex(" + i + ", document.getElementById('pgindex'));\">" + i + "</a>&nbsp;&nbsp;";
        }

        ret += "<b>" + p + "</b>";

        for (i=p+1;i<=p+3 && i<=maxpage;i++) {
            ret += "&nbsp;&nbsp;<a href=\"javascript:showData(" + i + ", document.getElementById('data'));" +
                "showPageIndex(" + i + ", document.getElementById('pgindex'));\">" + i + "</a>";
        }

        if (p+3 < maxpage)
            ret += " ... ";

        // next page
        if (p<maxpage) {
            ret += "&nbsp;<a href=\"javascript:showData(" + (p+1) + ", document.getElementById('data'));" +
                "showPageIndex(" + (p+1) + ", document.getElementById('pgindex'));\">&gt;</a>&nbsp;&nbsp;";
            ret += "<a href=\"javascript:showData(" + maxpage + ", document.getElementById('data'));" +
                "showPageIndex(" + maxpage + ", document.getElementById('pgindex'));\">&gt;&gt;</a>&nbsp;";
        } else {
            ret += "&nbsp;&gt&nbsp;&gt;&gt;";
        }

        return ret;
    }

    function showData(p, obj)
    {
        var data = getData(p);
        alert(data);
        obj.innerHTML = data;
        hide2('popupmenu');
        if(NiftyCheck())Rounded("div#data","#377CB1","#FFF");
    }

    function showPageIndex(p, obj)
    {
        var data = getPageIndex(p);
        obj.innerHTML = data;
    }

    function showTotal(n)
    {
        var obj = getElement('total');
        obj.innerHTML = "<font style='font-weight:bold;font-size:x-small;'>total <span style='font-size:160%'>" + n + "</span></font>";
    }

    function doAction(id, action)
    {
        var subList = service[id].split("|");

        var sure = true;

        hide2('popupmenu');

        if (action == 'edit') {
            window.open("services_edit.jsp" +
                "?srvcid=" + subList[0]
            ,null, "height=394,width=732,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes");
            return;
        }
        else if (action == 'delete')
            sure = confirm("Click 'OK' to remove service '" + subList[1] + "'.");

        if (sure)
            window.location = 'services_manage' +
            "?srvcid=" + subList[0] +
            "&action=" + action +
            "&forward=service_view.jsp" + input_param + getPage(id);
    }

    function showPopupMenu(id)
    {
        var obj = getElement('popupmenucontent');

        var subList = service[id].split("|");

        var color = "color:#333;";

        var tmp = "";
        tmp += "<table class='viewlist txt11' width='180'>";
        tmp += "<tr>";
        tmp += "<th style='font-size:.8em;font-weight:normal;"+ color +"'>" + subList[1] + "</th></tr>";
        tmp += "<tr style='line-height:1.5em;'>";
        tmp += "<td style='font-size:.8em;font-weight:bold;"+ color +"'>";
        tmp += "Action:</td></tr>";
        tmp += "<tr style='line-height:1.5em;'><td style='font-size:.75em;font-weight:normal;'>";
        tmp += "<a href='javascript:doAction(" + id + ", \"delete\");'>" +
            "<img src='./images/trash.gif' border=0 style='vertical-align:middle;'>remove</a> | ";
        tmp += "<a href='javascript:doAction(" + id + ", \"edit\");void(0);'>" +
            "<img src='./images/edit.gif' border=0  style='vertical-align:middle;'>edit</a>";
        tmp += "</td></tr>";
        tmp += "</table>";
        obj.innerHTML = tmp;

        obj = getElement('popupmenu');

        obj.style.display = 'block';
        obj.style.top = ym-120;
        obj.style.left = xm-14;
    }

    function service_view_onload()
    {
        showData(page, document.getElementById('data'));
        //showPageIndex(page, document.getElementById('pgindex'));
        //showTotal(service.length);

        if(NiftyCheck())Rounded("div#serviceViewContent","#C0CDF2","#377CB1");
        if(NiftyCheck())Rounded("div#refresh","#377CB1","#8EC2E7");
        if(NiftyCheck())Rounded("div#popupmenubg","transparent","#BEFF9A");
        if(NiftyCheck())Rounded("div#popupmenubox","transparent","#BEFF9A");
        if(NiftyCheck())Rounded("div#popupmenucontentbox","#BEFF9A","#FFF");


    }
        </script>

    </head>
    <body class="content">
        <div id="serviceViewContent" style="width:100%;">
            <h2>Service List</h2>
            <div id="data" style="padding: 20px 10px 5px 10px;width:100%;">
            </div>
            <div id="pgindex" style="text-align:center;width:100%;padding: 10px 0 10px 0;">
            </div>
            <div id="total" style="position:absolute; padding: 0;left:600px;top:34px;width:100px;text-align:right;">
            </div>
            <div onclick="javascript:location.reload(true);" onmouseover="document.body.style.cursor='pointer'" onmouseout="document.body.style.cursor='auto'"
                 id="refresh" style="position:absolute;padding: 0;width:80px; left:630px;top:10px;background-color:#8EC2E7;">
                <h4>Refresh</h4>
            </div>
            <div id="popupmenu"
                 style="display:none; background-image: url(./images/triangle01.gif);
                 background-repeat: no-repeat; background-position: left bottom;
                 height:70px; padding-bottom:14px; position:absolute;  left:50px; top:50px;">
                <div id="popupmenubg" style="height:60px; width:200px; text-align:center; background-color:transparent; position:relative; padding:0px;">
                    <div id="popupmenubox" style="height:60px; width:100%; text-align:center; background-color:#BEFF9A; position:relative; padding:0px;">
                        <div id="popupmenucontentbox" style="height:50px; width:92%; background-color:#FFF; position:relative; padding: 0px;" onclick="javascript:;">
                            <div class="floatr" style="padding:0 5px 0 0;"><img src="./images/close03.gif" onclick="hide2('popupmenu');"></div>
                            <div id="popupmenucontent" style="background-image: url(./images/ball.gif);
                                 background-repeat: no-repeat; background-position: right top; height:50px; width:100%; background-color:#FFF; position:relative; padding: 0px;"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script>
            service_view_onload();
        </script>

    </body>
