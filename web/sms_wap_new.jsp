<%-- 
    Document   : link_new
    Created on : Jun 14, 2010, 2:23:14 PM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.UserFactory" %>
<%@page import="hippoping.smsgw.api.db.MessageWap" %>
<%@page import="hippoping.smsgw.api.db.Message.SMS_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceContentAction.ACTION_TYPE" %>
<%@page import="hippoping.smsgw.api.db.MessageWapFactory" %>
<%@page import="hippoping.smsgw.api.db.LogEvent" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_TYPE" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_ACTION" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.LOG_LEVEL" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <link rel="stylesheet" type="text/css" href="./css/dashboard.css" media="screen" />
        <style type="text/css">
            body{margin:0px; padding: 5px; background: black;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding-bottom:1em}
            h2{padding-top: 0.3em}
            input.shorttext{width:150px}
            input.longtext{width:300px;}
            input.number{width:50px;}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
            User user = (User) session.getAttribute("USER");
            if (user == null) {
                out.print("<script>window.location='logout?msg=Your session has been expired.'</script>");
                return;
            }

            String _cmd = request.getParameter("cmd");
            String _id = request.getParameter("id");

            String _title = "";
            String _url = "";

            MessageWap wap = null;
            if (_cmd.equals("edit")) { // edit link
                wap = new MessageWap(Integer.parseInt(_id));

                // inquiry service information
                _title = wap.title;
                _url = wap.url;
            } else if (_cmd.equals("copy")) { // copy link
                _cmd = "add";

                wap = new MessageWap(Integer.parseInt(_id));

                // inquiry service information
                _title = wap.title;
                _url = wap.url;
            }
        %>
        <script type="text/javascript">

            function _onsubmit() {
                var frm = document.forms["smsWapNewFrm"];
                if (frm.url.value.trim().length == 0) {
                    alert('Please enter the URL!');
                    frm.url.focus();
                    return false;
                }
                if (frm.title.value.trim().length == 0) {
                    alert('Please enter the title!');
                    frm.title.focus();
                    return false;
                }

                // check wap url format
                if (!isUrl(frm.url.value.trim())) {
                    alert('Wrong URL format!!\nFor example \'http://yourhost.somewhere.com/yourapp\'.');
                    frm.url.focus();
                    return false;
                }

                // close after submit
                window.close();
            }

            window.onload=function() {
                if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
            }
        </script>
    </head>
    <body class="content">
        <div id="smsTextNewEdit" style="width:100%;">
            <form name="smsWapNewFrm" id="foo" method="post" action="SmsWapListServlet" target="resultFrame"
                  onsubmit="return _onsubmit();">
                <input type='hidden' name='cmd' value='<%=_cmd%>'>
                <input type='hidden' name='id' value='<%=_id%>'>
                <div id="content" style="width:100%">
                    <h2><%=_cmd.toUpperCase()%> WAP Push</h2><hr>
                    <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px; padding:0px;">
                        <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                            <tr>
                                <th style="border-bottom:0px">Url :</th>
                                <td>
                                    <input type="text" name="url" style="width:300px;" value="<%=_url%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px">Title :</th>
                                <td>
                                    <input type="text" name="title" style="width:300px;" value="<%=_title%>">
                                </td>
                            </tr>
                        </table>
                        <div style="margin: 20px 0 20px 50px;">
                            <input id="submit" type="submit" class="button" value="Submit">
                            <input id="cancel" type="reset" class="button" value="Cancel" onclick="window.close()">
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>
