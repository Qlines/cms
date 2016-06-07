<%-- 
    Document   : thirdparty_search
    Created on : Jul 27, 2010, 7:25:32 PM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <style type="text/css">
            body{margin:0px; padding: 5px; background: black;
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

            window.onload=function(){
                var frm = document.forms["thirdPartySearchFrm"];

                if(NiftyCheck())RoundedTop("div#thirdPartySearchContent","#C0CDF2","#FFF");

                frm.submit();
            }

            function thirdparty_search_onsubmit() {
            }

            function doRefresh() {
                var frm = document.forms["thirdPartySearchFrm"];
                frm.submit();
            }
        </script>
    </head>
    <body class="content" style="padding:20px">
        <div id="thirdPartySearchContent" style="width:100%; height:500px; background-color:#FFF;">
            <form target="resultFrame" id="thirdPartySearchFrm" method="post" action="ThirdPartyListServlet" onsubmit="return thirdparty_search_onsubmit();">
                <input type="hidden" name="cmd" value="refresh">
                <input type="hidden" name="page" value="1">
                <input type="hidden" name="orderby" value="register">
                <input type="hidden" name="type" value="<%=(request.getParameter("type") != null)?request.getParameter("type") :""%>">
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="thirdparty">3rd Party Config</h2><hr>
                    <table class="table4" style="width:100%">
                        <tr>
                            <th width="100%" style="border-bottom:0px;vertical-align: baseline;width:100%;text-align:right;">
                                Show:<input type="text" name="rows" value="20" style="width:40px;" onkeypress='return filter_digit_char(event)'>
                                Row(s)/Page
                                <img src="./images/refresh24.gif" title="refresh" onclick="doRefresh()">
                            </th>
                        </tr>
                    </table>
                </div>
            </form>
            <iframe name="resultFrame" frameborder="0" style="overflow:auto; padding:0;width:100%;height:100%;"></iframe>
        </div>
    </body>
</html>
