<%-- 
    Document   : member_view
    Created on : 4 พ.ย. 2552, 13:56:21
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement" %>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.*" %>
<%@page import="hippoping.smsgw.api.db.ServiceCharge.*" %>
<%@page import="hippoping.smsgw.api.db.OperConfig.CARRIER" %>

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
            div#memberViewContent {background: #377CB1;}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script type="text/javascript">
            <%

                        User user = (User) request.getSession().getAttribute("USER");
                        if (user == null) {
                            out.print("<script>window.location='logout?msg=Your session has been expired.'</script>");
                            return;
                        }

                        int srvc_type = SERVICE_TYPE.ALL.getId();
                        int srvc_status = SERVICE_STATUS.ALL.getId();

                        // get parameters
                        String keyword = request.getParameter("keyword");
                        int srvcid = Integer.parseInt(request.getParameter("srvcid"));
                        int operid = Integer.parseInt(request.getParameter("operid"));
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

                        // call findMember with designated criteria
                        List<ServiceElement> seList = ServiceElement.getAllService(null, srvc_type, srvc_status);

                        out.println("var member=\"" + memberList + "\".split(';');");
                        out.println("var member_len=" + memberBean.findMemberLen(msisdn, srvcid, operid, user) + ";");
                        out.println("var page=" + pg + ";");
                        out.println("var records=" + records + ";");
                        out.println("var input_param='"
                                + "?msisdn=" + msisdn_tmp
                                + "&srvcid=" + srvcid
                                + "&operid=" + operid
                                + "&sort=" + ((sort != null) ? sort : "")
                                + "&records=" + records
                                + "&page="
                                + "';");
            %>

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
                    ret += "<table class=\"table3\" style=\"width:100%;padding:0;\">" +
                        "<tr><th>MSISDN</th><th>Service</th><th>Carrier</th><th>FT</th><th>Register</th>" +
                        "<th>Expired</th><th>Billed</th><th>Status</th></tr>";
                    var subList = new Array();
                    for (i=0;i<member.length;i++) {
                        subList = member[i].split('|');

                        var color = "#333";
                        if (subList[7]=='inactive')
                            color = "#FF5555";
                        else if (subList[7]=='recharging')
                            color = "#F19503";
                        else
                            color = "#333";
                        var style = (((i%2)>0)?" style='background-color:#EEE;color:" + color + ";'":" style='color:" + color + ";'");
                        ret += "<tr>";
                        for (j=0;j<subList.length && j<=7;j++) {
                            ret += "<td"+ style +
                                ((j==0)?
                                " onclick='showPopupMenu(\"" + i + "\")'" +
                                " onmouseover=\"document.body.style.cursor='pointer'\"" +
                                " onmouseout=\"document.body.style.cursor='auto'\"":
                                "") +
                                ">" + subList[j] + "</td>";
                        }
                        ret += "</tr>";
                    }
                    ret += "</table>";

                    return ret;
                }

                function getPageIndex(p)
                {
                    var ret="";
                    var maxpage = getPage(member_len-1);

                    // previous page
                    if (p>1) {
                        ret += "<a rel='page 1' href=\"javascript:window.location='member_view.jsp"
                            + input_param + 1
                            + "';\">&lt;&lt;</a>&nbsp;";
                        ret += "<a href=\"javascript:window.location='member_view.jsp"
                            + input_param + (p-1)
                            + "';\">&lt;</a>&nbsp;";
                    } else {
                        //ret += "&lt;&lt;&nbsp;&lt&nbsp;";
                    }

                    if (p-3 > 1 )
                        ret += " ... ";

                    for (i=p-3;i<p;i++) {
                        if (i<=0)
                            continue;
                        ret += "<a href=\"javascript:window.location='member_view.jsp"
                            + input_param + i
                            + "';\">" + i + "</a>&nbsp;&nbsp;";
                    }

                    ret += "<b>" + p + "</b>";

                    for (i=p+1;i<=p+3 && i<=maxpage;i++) {
                        ret += "&nbsp;&nbsp;<a href=\"javascript:window.location='member_view.jsp"
                            + input_param + i
                            + "';\">" + i + "</a>";
                    }

                    if (p+3 < maxpage)
                        ret += " ... ";

                    // next page
                    if (p<maxpage) {
                        ret += "&nbsp;<a href=\"javascript:window.location='member_view.jsp"
                            + input_param + (p+1)
                            + "';\">&gt;</a>&nbsp;&nbsp;";
                        ret += "<a href=\"javascript:window.location='member_view.jsp"
                            + input_param + maxpage
                            + "';\">&gt;&gt;</a>&nbsp;";
                    } else {
                        //ret += "&nbsp;&gt&nbsp;&gt;&gt;";
                    }

                    return ret;
                }

                function showData(p, obj)
                {
                    var data = getData(p);
                    obj.innerHTML = data;
                    hide2('popupmenu');
                    //if(NiftyCheck())Rounded("div#data","#377CB1","#FFF");
                }

                function showPageIndex(p, obj)
                {
                    var data = getPageIndex(p);
                    obj.innerHTML = data;
                }

                function showTotal(n)
                {
                    var obj = getElement('total');
                    obj.innerHTML = "<font style='font-size:x-small;'>Total <span style='font-size:100%'>" + n + "</span> record(s) found.</font>";
                }

                function doActionMember(id, action)
                {
                    var subList = member[id].split("|");

                    var sure = true;

                    hide2('popupmenu');

                    if (action == 'history') {
                        window.open("message_history.jsp" +
                            "?operid=" + subList[8] +
                            "&srvcid=" + subList[9] +
                            "&msisdn=" + subList[0] +
                            "&sort=deliver_dt%20DESC" +
                            "&page=1&records=10"
                        ,null, "height=394,width=732,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes");
                        return;
                    }
                    else if (action == 'unregister')
                        sure = confirm("Click 'OK' to remove subscriber '" + subList[0] + "' from '" + subList[1] + "'.");

                    if (sure)
                        window.location = 'member_manage' +
                        "?operid=" + subList[8] +
                        "&srvcid=" + subList[9] +
                        "&msisdn=" + subList[0] +
                        "&action=" + action +
                        "&forward=member_view.jsp" + input_param + getPage(id);
                }

                function showPopupMenu(id)
                {
                    var obj = getElement('popupmenucontent');

                    var subList = member[id].split("|");

                    var color = "";
                    if (subList[7]=='inactive')
                        color = "color:#FF5555";
                    else if (subList[7]=='recharging')
                        color = "color:#F19503";

                    var tmp = "";
                    tmp += "<table class='viewlist txt11' width='180'>";
                    tmp += "<tr><th width=75% style='font-size:80%;'>" + subList[1] + "</th><th style='font-size:80%;"+ color +"'>" + subList[0] + "</th></tr>";
                    tmp += "<tr><td colspan=2>Action:</td></tr>";
                    tmp += "<tr><td colspan=2>" + ((subList[7]=='inactive')?
                        "<a href='javascript:doActionMember(" + id + ", \"register\");'>activate</a>":
                        "<a href='javascript:doActionMember(" + id + ", \"unregister\");'>cancel</a>");
                    tmp += " | <a href='javascript:doActionMember(" + id + ", \"history\");void(0);'>message history</a></td></tr>";
                    tmp += "</table>";
                    obj.innerHTML = tmp;

                    obj = getElement('popupmenu');

                    obj.style.display = 'block';
                    obj.style.top = ym-10;
                    obj.style.left = xm;
                }

                function member_view_onload()
                {
                    showData(page, document.getElementById('data'));
                    showPageIndex(page, document.getElementById('pgindex'));
                    showTotal(member_len);

                    if(NiftyCheck())Rounded("div#memberViewContent","#C0CDF2","#377CB1");
                    if(NiftyCheck())Rounded("div#popupmenubg","transparent","#BEFF9A");
                    if(NiftyCheck())Rounded("div#popupmenubox","transparent","#BEFF9A");
                    if(NiftyCheck())Rounded("div#popupmenucontentbox","#BEFF9A","#FFF");


                }
        </script>

    </head>
    <body class="content">
        <div id="memberViewContent" style="width:925px;padding:0;">
            <div style="padding: 0; margin: 0;width:100%;background-color: #FFF;">
                <div id="data" style=" overflow: auto ;padding: 0 10px 0 10px;width:100%;"></div>
            </div>
            <div id="pgindex" style="text-align:center;width:100%;padding: 10px 0 10px 0;">
            </div>
            <div id="popupmenu"
                 style="display:none;
                 background-repeat: no-repeat; background-position: left bottom;
                 height:70px; padding-bottom:14px; position:absolute;  left:0; top:0;">
                <div id="popupmenubg" style="height:60px; width:200px; text-align:center; background-color:transparent; position:relative; padding:0px;">
                    <div id="popupmenubox" style="height:60px; width:100%; text-align:center; background-color:#000; position:relative; padding:0px;">
                        <div id="popupmenucontentbox" style="height:100%; width:100%; background-color:#FFF; position:relative; padding: 0; margin: 1px;" onclick="javascript:;">
                            <div class="floatr" style="padding:2px 2px 0 0;"><img src="./images/close03.gif" onclick="hide2('popupmenu');"></div>
                            <div id="popupmenucontent" style="background-image: url(./images/ball.gif);
                                 background-repeat: no-repeat; background-position: right top; height:50px; width:100%; background-color:#FFF; position:relative; padding: 0px;"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script>
            member_view_onload();
        </script>

    </body>
