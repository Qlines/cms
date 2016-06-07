<%-- 
    Document   : link_new
    Created on : Jun 14, 2010, 2:23:14 PM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.UserFactory" %>
<%@page import="hippoping.smsgw.api.db.MessageSms" %>
<%@page import="hippoping.smsgw.api.db.Message.SMS_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceContentAction.ACTION_TYPE" %>
<%@page import="hippoping.smsgw.api.db.MessageSmsFactory" %>
<%@page import="hippoping.smsgw.api.db.LogEvent" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_TYPE" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_ACTION" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.LOG_LEVEL" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
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

            String _content = "";

            MessageSms sms = null;
            if (_cmd.equals("edit")) { // edit link
                sms = new MessageSms(Integer.parseInt(_id));

                // inquiry service information
                for (String sub_content : sms.getContent()) {
                    _content += sub_content;
                }
            } else if (_cmd.equals("copy")) { // copy link
                _cmd = "add";

                sms = new MessageSms(Integer.parseInt(_id));

                // inquiry service information
                for (String sub_content : sms.getContent()) {
                    _content += sub_content;
                }
            } 
        %>
        <script type="text/javascript">

            var MAX_THAI_CHAR = 140;
            var MAX_ENG_CHAR = 160;

            function updateMessageLen() {
                var frm = document.smsTextNewFrm;
                obj = getElement('messageLen');
                var remaining = getRemainChar();
                obj.innerHTML = ((isEnglishText(frm.content.value))
                    ?MAX_ENG_CHAR + " maximum length allow for English"
                :MAX_THAI_CHAR + " maximum length allow for ไทย")
                    + ", remaining " + ((remaining<0)?0:remaining) + " char(s)."
                    + ((remaining<0)?"<br><font color=red>The message length exceeds the meximum length!!</font>":"");

            }

            function checkMessageLen() {
                var frm = document.smsTextNewFrm;
                if (getRemainChar()<=0) {
                    frm.content.style.maxlength=frm.content.value.length;
                    return false;
                }
            }

            function getRemainChar() {
                var frm = document.smsTextNewFrm;
                return (isEnglishText(frm.content.value)?MAX_ENG_CHAR:MAX_THAI_CHAR)-frm.content.value.length;
            }

            function _onsubmit() {
                var frm = document.forms["smsTextNewFrm"];

                if (frm.content.value.trim() == '') {
                    alert("Please enter content message!!");
                    frm.url.select();
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
            <form name="smsTextNewFrm" id="foo" method="post" action="SmsTextListServlet" target="resultFrame"
                  onsubmit="return _onsubmit();">
                <input type='hidden' name='cmd' value='<%=_cmd%>'>
                <input type='hidden' name='id' value='<%=_id%>'>
                <div id="content" style="width:100%">
                    <h2><%=_cmd.toUpperCase()%> SMS Text</h2><hr>
                    <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px; padding:0px;">
                        <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                            <tr>
                                <th style="border-bottom:0px;">Message Content:</th>
                                <td>
                                    <textarea name="content" rows="6"
                                              onkeypress="return checkMessageLen()"
                                              onkeyup="updateMessageLen(this)"
                                              style="width:300px"><%=_content%></textarea>
                                    <br><div id="messageLen" class="txt11" style="vertical-align:top;"></div>
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
