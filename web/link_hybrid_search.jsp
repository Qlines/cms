<%-- 
    Document   : services_search
    Created on : 5 พ.ย. 2552, 0:15:28
    Author     : nack_ki
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
            body{margin:0; padding:0; background: black;
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
                var frm = document.forms["linkHybridSearchFrm"];

                if(NiftyCheck())RoundedTop("div#linkHybridSearchContent","#C0CDF2","#FFF");

                frm.submit();
            }

            function link_search_onsubmit() {
            }
            
            function _onkeypress(event) {
                if (isEnterKey(getKeyPress(event))) {
                    doRefresh();
                }
            }

            function doRefresh() {
                var frm = document.forms["linkHybridSearchFrm"];
                frm.submit();
            }
        </script>
    </head>
    <body class="content">
        <div id="linkHybridSearchContent" style="width:100%; height:500px; background-color:#FFF;">
            <form target="hybridResultFrame" id="linkHybridSearchFrm" method="post" action="HybridLinkServlet" onsubmit="return link_search_onsubmit();">
                <input type="hidden" name="cmd" value="refresh" />
                <input type="hidden" name="page" value="1" />
                <input type="hidden" name="orderby" value="register" />
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="link">Hybrid Gateway Config</h2><hr>
                    <table class="table4" style="width:100%">
                        <tr>
                            <th width="50%" style="border-bottom:0px;vertical-align: baseline">
                                Search: <input type="text" name="search" style="width:100px;" onkeypress="_onkeypress(event);" /></th>
                            <th width="100%" style="border-bottom:0px;vertical-align: baseline;width:100%;text-align:right;">
                                Show:<input type="text" name="rows" value="18" style="width:40px;" onkeypress="_onkeypress(event);return filter_digit_char(event);" />
                                Row(s)/Page
                                <input type="image" src="./images/refresh24.gif" title="refresh" />
                            </th>
                        </tr>
                    </table>
                </div>
            </form>
            <iframe name="hybridResultFrame" frameborder="0" style="overflow:auto; padding:0;width:100%;height:100%;"></iframe>
        </div>
    </body>
</html>
