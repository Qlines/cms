<%-- 
    Document   : services_search
    Created on : 5 พ.ย. 2552, 0:15:28
    Author     : nack_ki
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
    String bar_color = "#ED950F";
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

                out.println("var users = new Array();");
                String[] uarray = user.getChildUid();
                for (int i = 0; uarray != null && i < uarray.length; i++) {
                    User u = new User(Integer.parseInt(uarray[i]));
                    out.println("users[" + i + "] = new Array(2);");
                    out.println("users[" + i + "][0] = '" + u.getUid() + "';");
                    out.println("users[" + i + "][1] = '" + u.getName() + "';");
                }

                out.println("var event_types = new Array();");
                EVENT_TYPE[] event_types = EVENT_TYPE.values();
                for (int i = 0; event_types != null && i < event_types.length; i++) {
                    EVENT_TYPE t = event_types[i];
                    out.println("event_types[" + i + "] = new Array(2);");
                    out.println("event_types[" + i + "][0] = '" + t.getId() + "';");
                    out.println("event_types[" + i + "][1] = '" + t.toString() + "';");
                }

                out.println("var log_levels = new Array();");
                LOG_LEVEL[] log_levels = LOG_LEVEL.values();
                for (int i = 0; log_levels != null && i < log_levels.length; i++) {
                    LOG_LEVEL l = log_levels[i];
                    out.println("log_levels[" + i + "] = new Array(2);");
                    out.println("log_levels[" + i + "][0] = '" + l.getId() + "';");
                    out.println("log_levels[" + i + "][1] = '" + l.toString() + "';");
                }
            %>
                var now = new Date();

                var dd = now.getDate();
                var mm = now.getMonth();
                var yy = now.getFullYear();

                window.onload=function(){
                    var frm = document.forms["logEventSearchFrm"];

                    addOption_list(frm.uid, users);
                    frm.uid.disabled = false;

                    addOption_list(frm.event_type, event_types);
                    frm.event_type.disabled = false;

                    addOption_list(frm.log_level, log_levels);
                    frm.log_level.disabled = false;

                    if(NiftyCheck())RoundedTop("div#logEventSearchContent","#C0CDF2","#FFF");
                    if(NiftyCheck())RoundedBottom("div#searchBar","#FFF","<%=bar_color%>");
                    if(NiftyCheck())Rounded("div#searchBox","<%=bar_color%>","#FFF");

                    //frm.fdate.value = frm.fyear.value + "-" + frm.fmonth.value + "-" + frm.fday.value;
                    //frm.tdate.value = frm.tyear.value + "-" + frm.tmonth.value + "-" + frm.tday.value;

                    //frm.submit();
                }

                function logEvent_search_onsubmit() {
                    var frm = document.forms["logEventSearchFrm"];

                    // Date validate
                    if (!isValidDate(frm.fday.value, frm.fmonth.value ,frm.fyear.value)) {
                        alert('From date is not valid.');
                        frm.fday.focus();
                        return false;
                    }
                    if (!isValidDate(frm.tday.value, frm.tmonth.value ,frm.tyear.value)) {
                        alert('To date is not valid.');
                        frm.tday.focus();
                        return false;
                    }


                    frm.fdate.value = frm.fyear.value + "-" + frm.fmonth.value + "-" + frm.fday.value;
                    frm.tdate.value = frm.tyear.value + "-" + frm.tmonth.value + "-" + frm.tday.value;

                    // leave it when production
                    //alert(frm.fdate.value + " : " + frm.tdate.value);
                    //return false;
                }

                function doRefresh() {
                    var frm = document.forms["logEventSearchFrm"];
                    frm.submit();
                }
        </script>
    </head>
    <body class="content">
        <div id="logEventSearchContent" style="width:100%; height:420px; background-color:#FFF;">
            <form target="resultFrame" id="logEventSearchFrm" method="post" action="LogEventReportServlet" onsubmit="return logEvent_search_onsubmit();">
                <input type="hidden" name="cmd" value="refresh">
                <input type="hidden" name="page" value="1">
                <input type="hidden" name="orderby" value="timestamp">
                <input type="hidden" name="fdate" value="">
                <input type="hidden" name="tdate" value="">
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="logEvent">Log Event</h2><hr>
                    <div id="searchBar" style="background-color:<%=bar_color%>;">
                        <div class="transparent" style="padding: 5px 10px 2px 10px;">
                            <div class="floatr txt11">
                                Show: <input type="text" name="rows" value="15" style="width:40px;" onkeypress='return filter_digit_char(event)'>
                                Row(s)/Page
                            </div>
                            <div>
                                <div id="searchBox" class="transparent" style="background-color:#fff; width: 580px;">
                                    <div class="temtxt" style="padding: 0 10px 0 10px;color: #333;">
                                        <div style="padding: 2px 0 0 0">
                                            Select Group:
                                            <script type="text/javascript">
                                                document.write(createOptions(null, null, 'uid', false, 0));
                                                document.write("<b> | </b>");
                                                document.write(createOptions(null, null, 'event_type', false, 0));
                                                document.write("<b> | Level:</b>");
                                                document.write(createOptions(null, null, 'log_level', false, 0));
                                            </script>
                                        </div>
                                        <div style="padding: 2px 0 0 0">
                                            From:
                                            <script type="text/javascript">
                                                showDate(dd, "fday");
                                                document.write(" ");
                                                showMonth(mm, "fmonth");
                                                document.write(" ");
                                                showYear(yy, "fyear");
                                            </script>
                                            To:
                                            <script type="text/javascript">
                                                showDate(dd, "tday");
                                                document.write(" ");
                                                showMonth(mm, "tmonth");
                                                document.write(" ");
                                                showYear(yy, "tyear");
                                            </script>
                                        </div>
                                        <div style="padding: 2px 0 0 0">
                                            Msisdn:
                                            <input type="text" name="msisdn" maxlength="11" />(66xxxxxxxxx)
                                            <input type="image" src="./images/search24.gif" style="vertical-align: text-bottom" title="search">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
            <iframe name="resultFrame" frameborder="0" style="overflow:auto; padding:0;width:100%;height:100%;"></iframe>
        </div>
    </body>
</html>
