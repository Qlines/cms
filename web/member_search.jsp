<%-- 
    Document   : member_search
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
            int srvc_type =
                    SERVICE_TYPE.SUBSCRIPTION.getId();
        %>
        <jsp:include page="./services_bean.jsp">
            <jsp:param name="srvc_type" value="<%=srvc_type%>" />
        </jsp:include>
        <script type="text/javascript">
            function show_oper(obj) {
                var myOpers = new Array(); // create Array 2 dimensions
                for (operid in operIdArry) {
                    myOpers[operid] = new Array(operid, operNameArry[operid]);
                }

                // Add options to dropdownlist
                addOption_list(obj, myOpers);
                obj.selectedIndex = 0;
            }

            function updateServiceOptions2(oper_id, obj) {
                var frm = document.forms["memberSearchFrm"];
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
                var frm = document.forms["memberSearchFrm"];
                show_oper(frm.operid);
                updateServiceOptions2(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);

                frm.operid.onchange=
                    function(){updateServiceOptions2(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);}

                if(NiftyCheck())RoundedTop("div#memberSearchContent","#C0CDF2","#FFF");
            }

            function member_search_onsubmit() {
                // check mobile format
                var frm = document.forms["memberSearchFrm"];
                frm.msisdn.value = frm.msisdn.value.replace(/(\r\n|[\r\n])/g, ';');
                if (frm.msisdn.value.replace(/\;/g, '').trim().length==0) {
                    alert('Please insert at lease one mobile number!!');
                    frm.msisdn.focus();
                    return false;
                }

                var msisdns = new Array();
                msisdns = frm.msisdn.value.replace(/\;/g, ' ').trim().split(' ');
                var tmp = '';
                for (var i in msisdns) {
                    try {
                        // trim number
                        msisdns[i] = msisdns[i].trim();

                        // skip blank token
                        if (msisdns[i] == '') {
                            continue;
                        }
                    
                        // replace 08XXXXXXXX with 668XXXXXXXX
                        if ((msisdns[i].indexOf('08', 0) == 0)
                            || (msisdns[i].indexOf('09', 0) == 0)
                            || (msisdns[i].indexOf('06', 0) == 0)) {
                            msisdns[i] = msisdns[i].replace('0', '66');
                        }

                        if (!isMsisdn(msisdns[i])) {
                            alert('You have enter wrong mobile number "' + msisdns[i] + '"\nPlease insert in format 66[689]XXXXXXXX!!');
                            return false;
                        }

                        tmp += msisdns[i] + ';';
                    } catch (e) {
                        
                    }
                }

                frm.msisdn.value = tmp;
            }
        </script>
    </head>
    <body class="content">
        <div id="memberSearchContent" style="width:98%; height:400px; background-color:#FFF;">
            <form target="resultFrame" id="memberSearchFrm" method="post" action="MemberViewServlet" onsubmit="return member_search_onsubmit();">
                <input type="hidden" name="cmd" value="refresh">
                <input type="hidden" name="page" value="1">
                <input type="hidden" name="orderby" value="register">
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="users">Search Member</h2><hr>
                    <table class="table4" style="width:100%;">
                        <tr>
                            <th style="border-bottom:0px;" width="15%"><b>Select Group:</b>
                            </th>
                            <td>
                                <script>
                                    document.write(createOptions(null, null, 'operid', false, 0));
                                    document.write("<b> | </b>");
                                    document.write(createOptions(null, null, 'srvcid', false, 0));
                                </script>
                            </td>
                            <th align="right" style="border-bottom:0px;">Show:
                                <input type="text" name="rows" value="15" style="width:40px;" onkeypress='return filter_digit_char(event)'>
                                Row(s)/Page</th>
                        </tr>
                        <tr>
                            <th style="border-bottom:0px;">Mobile Number :
                            </th>
                            <td colspan="2" align="left">
                                <textarea name="msisdn" rows="5" cols="46" onkeypress='return filter_digit_char(event, ";")'></textarea>
                                <!--<input type="button" class="button" value="Test" onclick="member_search_onsubmit()">-->
                                <input type="submit" class="button" value="Search">
                                <input type="reset" class="button" value="Clear">
                                <br><font class="txthint">* Use semicolon ';' to delimit such a mobile number.</font>
                            </td>
                        </tr>
                    </table>
                </div>
            </form>
            <iframe name="resultFrame" frameborder="0" style="overflow:auto; padding:0;width:100%;height:100%"></iframe>
        </div>
    </body>
</html>
