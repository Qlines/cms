<%-- 
    Document   : log_event_realtime
    Created on : 25 มิ.ย. 2554, 20:15:24
    Author     : nack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.UserFactory" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_TYPE" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.LOG_LEVEL" %>
<%@page import="java.util.List" %>
<%@page import="java.util.ArrayList" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%
    User user = (User) request.getSession().getAttribute("USER");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link href="./css/cv.css" rel="stylesheet" type="text/css" />
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css" />
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print" />
        <style type="text/css">
            body{margin:0; padding: 0; background: black;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding-bottom:1em}
            h2{padding-top: 0.3em}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <script type="text/javascript">
            <%
                if (user == null) {
                    out.print("<script>window.location='logout?msg=Your session has been expired!\nPlease relogin the page.'</script>");
                    return;
                }
            %>
                var tick;

                window.onload=function(){
                    var frm = document.forms["logEventRealtimeFrm"];
                    if(NiftyCheck())RoundedTop("div#logEventSearchContent","#C0CDF2","#FFF");
                    
                    frm.log_viewer.value = "-- Realtime Status Log --\n";
                    synctime();
                }
                
                function scrollBox()
                {
                    var elem = getElement('log_viewer');
                    elem.scrollTop = elem.scrollHeight + 100000;
                }
                
                function clearViewer()
                {
                    var frm = document.forms["logEventRealtimeFrm"];
                    frm.log_viewer.value = "-- Realtime Status Log --\n";
                }

                function synctime()
                {
                    var frm = document.forms["logEventRealtimeFrm"];
                    
                    if (frm.auto_refresh.checked) {
                        var objHTTP;
                        if (browserType == "gecko" ){
                            objHTTP = new XMLHttpRequest();
                            objHTTP.open('GET',"LogEventOnlineServlet",false);
                        }else{
                            objHTTP = new ActiveXObject('Microsoft.XMLHTTP');
                            objHTTP.open('POST',"LogEventOnlineServlet",false);
                            objHTTP.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                        }

                        objHTTP.send(null);
                        var _lastlog=objHTTP.responseText.toString();
                    
                        frm.log_viewer.value += _lastlog;
                        scrollBox();
                    }
                    
                    tick=setTimeout("synctime()",5000);
                }
        </script>
    </head>
    <body class="content">
        <div id="logEventSearchContent" style="width:98%; height:580px; background-color:#FFF; color: #333;">
            <form target="resultFrame" id="logEventRealtimeFrm" method="post" action="LogEventReportServlet" onsubmit="return logEvent_search_onsubmit();">
                <input type="hidden" name="cmd" value="refresh">
                <input type="hidden" name="page" value="1">
                <input type="hidden" name="orderby" value="timestamp">
                <input type="hidden" name="fdate" value="">
                <input type="hidden" name="tdate" value="">
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="logEvent">Log Event</h2><hr>
                    <table style="padding: 10px;">
                        <tr>
                            <td style="text-align:right;">
                                <input type="checkbox" id="auto_refresh" checked>
                                <label for="auto_refresh" class="txt11"> Automatic Refresh</label>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <textarea cols="90" rows="30" id="log_viewer" style="font-family: courier;font-size: 11px;" readonly></textarea>
                            </td>
                        </tr>
                        <tr><td><input type="button" value="Clear" onclick="clearViewer()"></td></tr>
                    </table>
                </div>
            </form>
        </div>
    </body>
</html>
