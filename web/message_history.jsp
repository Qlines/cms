<%-- 
    Document   : message_history
    Created on : 18 ?.?. 2552, 19:40:20
    Author     : nack_ki
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@page import="hippoping.smsgw.api.db.report.TxQueueReport" %>
<%@page import="hippoping.smsgw.api.db.DeliveryReport" %>
<%@page import="hippoping.smsgw.api.db.TxQueue.TX_STATUS" %>
<%@page import="hippoping.smsgw.api.db.TxQueue" %>
<%@page import="hippoping.smsgw.api.db.Message" %>
<%@page import="hippoping.smsgw.api.db.MessageSms" %>
<%@page import="hippoping.smsgw.api.db.MessageWap" %>
<%@page import="lib.common.DatetimeUtil" %>
<%@page import="java.util.List" %>
<%@page import="java.io.StringWriter" %>
<jsp:useBean id="messageHistoryBean" scope="page" class="smsgateway.webadmin.bean.MessageHistoryBean" />

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
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script type="text/javascript">

            function doRefresh() {
                var frm = document.forms["messageHistoryReportFrm"];
                frm.submit();
            }

            function goto_page(page) {
                frm=document.forms["messageHistoryReportFrm"];
                frm.page.value=page;
                frm.submit();
            }

            function validate_page(page, maxpage) {
                var frm = document.forms["messageHistoryReportFrm"];
                if (page=='') {alert('Please enter page number.'); frm.page.value=page;return false;}
                else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=page;return false;}
                else {frm.submit();}
            }

            function _onsubmit() {
            }

            window.onload = function(){
                var frm = document.forms["messageHistoryReportFrm"];
                if(NiftyCheck())RoundedTop("div#MessageHistory","#C0CDF2","#FFF");
                frm.submit();
            }
        </script>

    </head>
    <body class="content" style="padding:20px;">
        <div id="messageHistory" style="width:100%;">
            <div id='data' style='padding: 0 10px 0 10px;width:100%;height: 500px;background-color: #FFF'>
                <form name='messageHistoryReportFrm' method='POST' action='MessageHistoryReportServlet' target="resultFrame" onsubmit='return _onsubmit()'>
                    <input type="hidden" name="msisdn" value="<%=request.getParameter("msisdn")%>">
                    <input type="hidden" name="srvcid" value="<%=request.getParameter("srvcid")%>">
                    <input type="hidden" name="operid" value="<%=request.getParameter("operid")%>">
                    <input type="hidden" name="orderby" value="deliver">
                    <input type="hidden" name="cmd" value="refresh">
                    <h2 class="history">Message History</h2><hr>
                    <table class="table4" style="width:100%">
                        <tr>
                            <th width="100%" style="border-bottom:0px;vertical-align: baseline;width:100%;text-align:right;">
                                Show:<input type="text" name="rows" value="10" style="width:40px;" onkeypress='return filter_digit_char(event)'>
                                Row(s)/Page
                                <img src="./images/refresh24.gif" title="refresh" onclick="doRefresh()">
                            </th>
                        </tr>
                    </table>
                </form>
                <iframe name="resultFrame" frameborder="0" style="overflow:auto; padding:0;width:100%;height:100%"></iframe>
            </div>
        </div>

    </body>
</html>
