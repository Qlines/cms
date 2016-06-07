<%-- 
    Document   : services_search
    Created on : 5 พ.ย. 2552, 0:15:28
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_TYPE" %>
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
            function show_oper(obj) {
                var myOpers = new Array(4); // create Array 2 dimensions
                for (operid in operIdArry) {
                    myOpers[operid] = new Array(operid, operNameArry[operid]);
                }

                // Add options to dropdownlist
                addOption_list(obj, myOpers);
                obj.selectedIndex = 0;
            }

            function updateServiceOptions2(oper_id, obj) {
                var frm = document.forms["servicesSearchFrm"];
                removeAllOptions(obj);

                if (!optionsArry[oper_id]) {
                    var tmpArry = new Array(); // create Array 2 dimensions
                    tmpArry[0] = new Array(2);
                    tmpArry[0][0] = "0";
                    tmpArry[0][1] = "No service available";

                    addOption_list(obj, tmpArry);
                    frm.srvcid.disabled = true;
                }
                else {
                    // insert "All Services" at index 0
                    var tmpArry = new Array(2);
                    tmpArry[0] = "0";
                    tmpArry[1] = "All Services";
                    var myArry = optionsArry[oper_id].slice();
                    myArry.unshift(tmpArry);

                    addOption_list(obj, myArry);
                    frm.srvcid.disabled = false;
                }
            }

            window.onload=function(){
                var frm = document.forms["servicesSearchFrm"];
                //show_oper(frm.operid);
                //updateServiceOptions2(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);

                //frm.operid.onchange=
                //    function(){updateServiceOptions2(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);}

                if(NiftyCheck())RoundedTop("div#servicesSearchContent","#C0CDF2","#FFF");

                frm.submit();
            }

            function doRefresh() {
                var frm = document.forms["servicesSearchFrm"];
                frm.submit();
            }

            function services_search_onsubmit() {
            }
        </script>
    </head>
    <body class="content" style="padding:20px">
        <div id="servicesSearchContent" style="width:100%; height:520px; background-color:#FFF;">
            <form target="resultFrame" id="servicesSearchFrm" method="post" action="ServiceListServlet" onsubmit="return services_search_onsubmit();">
                <input type="hidden" name="cmd" value="refresh">
                <input type="hidden" name="page" value="1">
                <input type="hidden" name="orderby" value="register">
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="service">Services</h2><hr>
                    <table class="table4" style="width:100%;">
                        <tr>
                            <th width="50%" style="border-bottom:0px;vertical-align: baseline">
                                Keyword: <input type="text" name="search" style="width:100px;"></th>
                            <th width="50%" style="border-bottom:0px;vertical-align: baseline;width:100%;text-align:right;">
                                Show:<input type="text" name="rows" value="18" style="width:40px;" onkeypress='return filter_digit_char(event)'>
                                Row(s)/Page
                                <input type="image" src="./images/refresh24.gif" title="refresh">
                            </th>
                        </tr>
                    </table>
                </div>
            </form>
            <iframe name="resultFrame" frameborder="0" style="overflow:auto; padding:0;width:100%;height:100%"></iframe>
        </div>
    </body>
</html>
