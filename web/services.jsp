<%-- 
    Document   : services
    Created on : 4 พ.ย. 2552, 13:32:41
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.User" %>
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
        {"service_view",   "View",        "services_search.jsp"},
        {"service_new",    "Add",         "services_main_new.jsp"}
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
        <script type="text/javascript">
            qs();if (qsParm["error"]) if (qsParm["error"] == 1){ alert("An error occurs when apply the settings!!\nPlease check the related configurations.");}

            var tabs = new Array(<%=tabMenu.getTabs()%>);
            var titles = new Array(<%=tabMenu.getTitles()%>);
            var links = new Array(<%=tabMenu.getLinks()%>);

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
