<%-- 
    Document   : push_message
    Created on : 12 มี.ค. 2553, 11:29:52
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.User.*" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_STATUS" %>
<%@page import="smsgateway.webadmin.bean.TabMenuAccess" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%
    User user = (User)session.getAttribute("USER");
    if (user == null) {
        out.print("<script>window.location='logout?msg=Your session has been expired.'</script>");
        return;
    }

    String [][]menu = {
        {"history",   "History",        "push_message_sms_history.jsp"}
        ,{"text",      "Text",           "push_message_sms_text.jsp"}
        ,{"file",      "Upload",         "push_message_sms_text_with_csv.jsp"}
        ,{"wap",       "Wap Push",       "push_message_sms_wap.jsp"}
        
        //{"picture",   "Picture",        "push_message_sms_picture.jsp"},
        //{"ringtone",  "Ringtone",       "push_message_sms_ringtone.jsp"},
        //{"mms",       "MMS",            "push_message_mms.jsp"}
        };

    TabMenuAccess tabMenu = new TabMenuAccess(menu, user);
%>

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
            div#memberSearchContent {background: #377CB1;}
            div#memberViewContent {background: #377CB1;}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
                    int srvc_type = SERVICE_TYPE.SUBSCRIPTION.getId();
                    int srvc_status = SERVICE_STATUS.ON.getId();
                    if (user.getType() == USER_TYPE.ADMIN) {
                        srvc_status |= SERVICE_STATUS.TEST.getId();
                    }
        %>
        <jsp:include page="./services_bean.jsp">
            <jsp:param name="srvc_type" value="<%=srvc_type%>" />
            <jsp:param name="srvc_status" value="<%=srvc_status%>" />
        </jsp:include>
        <script type="text/javascript">
            qs();if (qsParm["error"]){ alert(qsParm["error"]);}

            var tabs = new Array(<%=tabMenu.getTabs()%>);
            var titles = new Array(<%=tabMenu.getTitles()%>);
            var links = new Array(<%=tabMenu.getLinks()%>);

            //if (qsParm["page"]) {
            //    if (qsParm["page"]=="history" && qsParm["srvc_main_id"]) {
            //        var page = findArrayIndex(tabs, qsParm["page"]);
            //        links[page] += "?srvc_main_id=" + qsParm["srvc_main_id"];
            //    }
            //}

            if (qsParm["srvc_main_id"]) {
                links[findArrayIndex(tabs, "history")] += "?srvc_main_id=" + qsParm["srvc_main_id"];
            }

            window.onload=function(){
                // show tab page
                var page = 0;
                if (qsParm["page"]) page = findArrayIndex(tabs, qsParm["page"]);
                changeTab2(tabs, links[page], page);

                if(NiftyCheck())RoundedTop("div#nav li","transparent","#BEFF9A");
            }
        </script>
    </head>
    <body class="content">
        <script>
            genMenu2(tabs, links, titles, tabs.length);
            genFrame2();
        </script>

    </body>
</html>
