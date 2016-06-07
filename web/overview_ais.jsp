<%-- 
    Document   : overview_ais
    Created on : Mar 26, 2010, 12:59:15 PM
    Author     : nack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="overviewBean" scope="page" class="smsgateway.webadmin.bean.OverviewBean" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src="./js/datetime.js" type="text/javascript"></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <style type="text/css">
            body{margin:0px; padding: 0px; background: white;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding-bottom:1em}
            h2{padding-top: 0.3em}
        </style>
        <script type="text/javascript">
            window.onload=function(){
                if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
            }
        </script>
    </head>
    <body class="content">
        <div id="content" style="width:60%;">
            <h2>AIS Status</h2><hr>
            <div id="content2" style="width:90%; text-align:center; background-color:#FFF; margin:10px 0 0 10px;">
                <table width="90%" class="table4" style="margin:0; padding:10px;">
                    <tr>
                        <th width="50%">Last time sent :</th>
                        <td style="text-align: right; vertical-align: bottom"><%= overviewBean.getLastTimeSent(4)%>
                        </td></tr>
                    <tr>
                        <th width="50%">Maximum speed :</th>
                        <td style="text-align: right; vertical-align: bottom"><%= overviewBean.getMaxSpeed(4)%> msg/sec
                        </td></tr>
                    <tr>
                        <th width="50%">Undeliver (New/Queue) :</th>
                        <td style="text-align: right; vertical-align: bottom"><%= overviewBean.getNewTx(4)%>/<%= overviewBean.getSendingTx(4)%> msg
                        </td></tr>
                    <tr>
                        <th width="50%">Total sent message :</th>
                        <td style="text-align: right; vertical-align: bottom"><%= overviewBean.getSuccessTx(4)%> msg
                        </td></tr>
                    <tr>
                        <th width="50%">Failed sent:</th>
                        <td style="text-align: right; vertical-align: bottom"><%= overviewBean.getFailTx(4)%> msg
                        </td></tr>
                </table>
            </div>
        </div>
    </body>
</html>
