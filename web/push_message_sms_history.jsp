<%-- 
    Document   : push_message_sms_history
    Created on : May 3, 2010, 12:44:18 PM
    Author     : nack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_TYPE" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%
            String bar_color = "#ED950F";
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <link rel="stylesheet" type="text/css" href="./css/dashboard.css" media="screen" />
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
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
                    int srvc_type = SERVICE_TYPE.SUBSCRIPTION.getId();
                    String srvc_main_id = request.getParameter("srvc_main_id");
                    if (srvc_main_id == null) {
                        srvc_main_id = "0";
                    }

        %>
        <jsp:include page="./services_bean.jsp">
            <jsp:param name="srvc_type" value="<%=srvc_type%>" />
        </jsp:include>
        <script type="text/javascript">
            var now = new Date();

            var dd = now.getDate();
            var mm = now.getMonth();
            var yy = now.getFullYear();

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
                var frm = document.forms["pushMessageHistoryFrm"];
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

            function _onsubmit() {
                var frm = document.forms["pushMessageHistoryFrm"];

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
                var frm = document.forms["pushMessageHistoryFrm"];
                if (_onsubmit() != false) {
                    frm.submit();
                }
            }

            window.onload=function(){

                var frm = document.forms["pushMessageHistoryFrm"];
                show_oper(frm.operid);
                updateServiceOptions2(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);

                frm.operid.onchange=
                    function(){updateServiceOptions2(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);}

                frm.fdate.value = frm.fyear.value + "-" + frm.fmonth.value + "-" + frm.fday.value;
                frm.tdate.value = frm.tyear.value + "-" + frm.tmonth.value + "-" + frm.tday.value;

                if ("<%=srvc_main_id%>" != "0") {
                    frm.srvcid.value = <%=srvc_main_id%>;
                    frm.submit();
                }


                if(NiftyCheck())RoundedTop("div#pushMessageHistory","#C0CDF2","#FFF");
                if(NiftyCheck())RoundedBottom("div#searchBar","#FFF","<%=bar_color%>");
                if(NiftyCheck())Rounded("div#searchBox","<%=bar_color%>","#FFF");
            }
        </script>
    </head>
    <body class="content">
        <div id="pushMessageHistory" style="width:98%; height:450px; background-color:#FFF;">
            <form target="resultFrame" id="pushMessageHistoryFrm" method="post" action="MessageHistoryServlet" onsubmit="_onsubmit()">
                <input type="hidden" name="cmd" value="refresh">
                <input type="hidden" name="page" value="1">
                <input type="hidden" name="orderby" value="deliver_dt">
                <input type="hidden" name="fdate" value="">
                <input type="hidden" name="tdate" value="">
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="history">Message History</h2>
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
                                        <b>Select Group:</b>
                                        <script type="text/javascript">
                                            document.write(createOptions(null, null, 'operid', false, 0));
                                            document.write("<b> | </b>");
                                            document.write(createOptions(null, null, 'srvcid', false, 0));
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
                                            showYear(yy+1, "tyear");
                                        </script>
                                        <input type="image" src="./images/search24.gif" style="vertical-align: text-bottom" title="search">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
            <iframe name="resultFrame" frameborder="0" style="overflow:auto; padding:0;width:100%;height:100%"></iframe>
        </div>
    </body>
</html>
