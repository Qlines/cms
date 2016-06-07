<%-- 
    Document   : sms_text
    Created on : Aug 19, 2010, 9:52:24 PM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%
    String bar_color = "#659222";
%>

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
                var frm = document.forms["mmsSearchFrm"];

                if(NiftyCheck())RoundedTop("div#mmsSearchContent","#C0CDF2","#FFF");
                if(NiftyCheck())RoundedBottom("div#searchBar","#FFF","<%=bar_color%>");
                if(NiftyCheck())Rounded("div#searchBox","<%=bar_color%>","#FFF");

                mms_search_onblur();


                frm.submit();
            }

            function mms_search_onsubmit() {
                var frm = document.forms["mmsSearchFrm"];
                if (frm.search.value != 'Search...') {
                    frm.keyword.value = frm.search.value.trim();
                }
            }

            function mms_search_onblur() {
                var frm = document.forms["mmsSearchFrm"];
                if (frm.search.value.trim().length == 0) {
                    frm.search.value = "Search...";
                }
            }

            function mms_search_onfocus() {
                var frm = document.forms["mmsSearchFrm"];
                if (frm.search.value == "Search...") {
                    frm.search.value = "";
                }
            }
        </script>
    </head>
    <body class="content" style="padding:20px">
        <div id="mmsSearchContent" style="width:100%; height:500px; background-color:#FFF;">
            <form target="resultFrame" id="mmsSearchFrm" method="post" action="MmsListServlet" onsubmit="return mms_search_onsubmit();">
                <input type="hidden" name="cmd" value="refresh">
                <input type="hidden" name="page" value="1">
                <input type="hidden" name="orderby" value="register">
                <input type="hidden" name="keyword" value="">
                <input type="hidden" name="type" value="<%=(request.getParameter("type") != null)?request.getParameter("type") :""%>">
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="mms">MMS Content</h2>
                    <div id="searchBar" style="background-color:<%=bar_color%>;">
                        <div class="transparent" style="padding: 5px 10px 2px 10px;">
                            <div class="floatr txt11">
                                Show: <input type="text" name="rows" value="20" style="width:40px;" onkeypress='return filter_digit_char(event)'>
                                Row(s)/Page
                            </div>
                            <div>
                                <div id="searchBox" class="transparent" style="background-color:#fff; width: 280px;">
                                    <div class="transparent" style="padding: 0 10px 0 10px;">
                                        <input type="text" name="search" id="search" style="width:200px;"
                                               onblur="mms_search_onblur()" onfocus="mms_search_onfocus()">
                                        <input type="image" style="vertical-align: bottom" src="./images/search24.gif" title="search">
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
