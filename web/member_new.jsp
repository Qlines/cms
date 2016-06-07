<%-- 
    Document   : member_add
    Created on : 4 พ.ย. 2552, 12:56:14
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_STATUS" %>
<jsp:useBean id="memberBean" scope="page" class="smsgateway.webadmin.bean.MemberBean" />
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
            int srvc_type = SERVICE_TYPE.SUBSCRIPTION.getId();
            int srvc_status = SERVICE_STATUS.ON.getId() | SERVICE_STATUS.TEST.getId();
        %>
        <jsp:include page="./services_bean.jsp">
            <jsp:param name="srvc_type" value="<%=srvc_type%>" />
            <jsp:param name="srvc_status" value="<%=srvc_status%>" />
            <jsp:param name="dcc" value="1" />
        </jsp:include>
        <script type="text/javascript">
            function member_new_onload() {
                var frm = document.memberNewFrm;
                show_oper(frm.operid);
                updateServiceOptions(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);
                updateElement();

                frm.operid.onchange=
                    function(){updateServiceOptions(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);}

                if(NiftyCheck())RoundedTop("div#memberNewContent","#C0CDF2","#FFF");
            }
            
            function show_oper(obj) {
                var myOpers = new Array(); // create Array 2 dimensions
                for (operid in operIdArry) {
                    myOpers[operid] = new Array(operid, operNameArry[operid]);
                }

                // Add options to dropdownlist
                addOption_list(obj, myOpers);
                obj.selectedIndex = 0;
            }

            function updateElement() {
                var frm = document.memberNewFrm;
                // set default color
                //chgColor('source1txt', '#555');
                //chgColor('source2txt', '#555');
                frm.msisdn.disabled = true;
                frm.file.disabled = true;

                if (frm.source[1].checked) {
                    //chgColor('source1txt', 'white');
                    frm.msisdn.disabled = false;
                } else {
                    //chgColor('source2txt', 'white');
                    frm.file.disabled = false;
                }
            }

            function updateServiceOptions(oper_id, obj) {
                var frm = document.memberNewFrm;
                //alert("update service options to " + oper_id);
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
                    addOption_list(obj, optionsArry[oper_id]);
                    frm.srvcid.disabled = false;
                }
            }

            function member_new_onsubmit() {
                var frm = document.forms["memberNewFrm"];
                if (frm.source[1].checked) {
                
                    // check mobile format
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

                if (!frm.ignore_numplan.checked) {
                    frm.ignore_numplan.value = "0";
                }

                // test
                //return false;
            }
        </script>
    </head>
    <body class="content">
        <div id="memberNewContent" style="width:100%; height:480px; background-color:#FFF;">
            <form name="memberNewFrm" method="post" enctype="multipart/form-data" action="member_manage" onsubmit="return member_new_onsubmit();">
                <input type="hidden" name="action" value="register">
                <input type="hidden" name="forward" value="member_new.jsp">
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="user">New Member</h2><hr>
                    <table align="center" class="table4" style="width:90%;">
                        <tr>
                            <th colspan="2" style="border-bottom:0px;">Operator :</th>
                            <td width="70%">
                                <script>
                                    document.write(createOptions(null, null, 'operid', false, 0));
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <th colspan="2">Service Name :</th>
                            <td>
                                <script>document.write(createOptions(null, null, 'srvcid', false, 0));</script>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input type="radio" name="source" id="source2" value="file" onclick="updateElement();">
                            </td>
                            <th style="border-bottom:0px;">
                                <label for="source2" id="source2txt">Upload File :</label>
                            </th>
                            <td><input type="file" name="file"></td>
                        </tr>
                        <tr>
                            <td>
                                <input type="radio" name="source" id="source1" value="text" onclick="updateElement();" checked>
                            </td>
                            <th style="border-bottom:0px;">
                                <label for="source1" id="source1txt">Mobile Number :</label>
                            </th>
                            <td><textarea name="msisdn" rows="5" cols="36" onkeypress='return filter_digit_char(event, ";")'></textarea>
                                <br><font class="txthint">* Use semicolon ';' to delimit such a mobile number.</font>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2"></td>
                            <td>
                                <br>
                                <input type="checkbox" name="ignore_numplan" id="ignore_numplan">
                                <label for="ignore_numplan" id="ignore_numplantxt">Ignore verify numbering plan.</label>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2"></td>
                            <td>
                                <input type="checkbox" name="result" id="result" checked>
                                <label for="result" id="resulttxt">Display the result after create new profile.</label>
                            </td>
                        </tr>
                    </table>
                    <div style="margin: 20px 0 20px 50px;">
                        <!--<input type="button" class="button" value="Test" onclick="member_new_onsubmit()">-->
                        <input type="submit" class="button" value="OK">
                        <input type="reset" class="button" value="Cancel">
                    </div>
                </div>
            </form>

            <script>
                member_new_onload();
            </script>
        </div>
    </body>
</html>
