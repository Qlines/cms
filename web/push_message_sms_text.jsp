<%-- 
    Document   : push_message_text
    Created on : 12 มี.ค. 2553, 12:47:23
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_STATUS" %>
<%@page import="hippoping.smsgw.api.db.TxQueue" %>
<%@page import="hippoping.smsgw.api.db.MessageSms" %>
<%@page import="lib.common.DatetimeUtil" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <link rel="stylesheet" type="text/css" href="./css/dashboard.css" media="screen" />
        <link href="./css/infobox01.css" rel="stylesheet" type="text/css">
        <style type="text/css">
            body{margin:0px; padding: 0px; background: white;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding:0}
            p.tt{color:#EEE;font-weight:bold;padding-bottom:2px}
            span.vl{color:yellow;font-weight:normal;padding-left:5px}
            h2{padding-top: 0.3em}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
            String _srvcid = "";
            String _operid = "";
            String _msg = "";
            String _schedule = "";
            String _now = "checked";
            String _date = "";
            String _time = "";

            String _txqid = request.getParameter("txqid") != null ? request.getParameter("txqid") : "";
            if (!_txqid.isEmpty()) {
                int txqid = Integer.parseInt(_txqid);
                TxQueue txQueue = new TxQueue(txqid);
                MessageSms messageSms = null;

                try {
                    messageSms = new MessageSms(txQueue.content_id);

                    for (int i = 0; i < messageSms.getMessage_num(); i++) {
                        _msg += messageSms.getContentSub()[i];
                    }
                } catch (Exception e) {
                }

                _srvcid = String.valueOf(txQueue.srvc_main_id);
                _operid = String.valueOf(txQueue.oper_id);
                _schedule = "checked";
                _now = "";
                _date = DatetimeUtil.changeDateFormat(txQueue.deliver_dt, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
                _time = DatetimeUtil.changeDateFormat(txQueue.deliver_dt, "yyyy-MM-dd HH:mm:ss", "HH:mm");
            }

            int srvc_type = SERVICE_TYPE.SMS.getId() | SERVICE_TYPE.SUBSCRIPTION.getId();
            int srvc_status = SERVICE_STATUS.ON.getId() | SERVICE_STATUS.TEST.getId();
        %>
        <jsp:include page="./services_bean.jsp">
            <jsp:param name="srvc_type" value="<%=srvc_type%>" />
            <jsp:param name="srvc_status" value="<%=srvc_status%>" />
        </jsp:include>
        <script type="text/javascript">
            var now = new Date();

            var hh = now.getHours();
            var mn = now.getMinutes();

            var MAX_THAI_CHAR = 140;
            var MAX_ENG_CHAR = 160;

            var wasSubmitted = false;

            function updateServiceOptions(oper_id, obj) {
                var frm = document.pushMessageSmsTextFrm;
                removeAllOptions(obj);

                if (!optionsArry[oper_id]) {
                    var tmpArry = new Array(); // create Array 2 dimensions
                    tmpArry[0] = new Array(2);
                    tmpArry[0][0] = "0";
                    tmpArry[0][1] = "No service available";

                    addOption_list(obj, tmpArry);
                    frm.srvc_main_id.disabled = true;
                }
                else {
                    if ("<%=_srvcid%>" == "")
                        addOption_list(obj, optionsArry[oper_id]);
                    else
                        addOption_list(obj, optionsArry[oper_id], "<%=_srvcid%>");
                    frm.srvc_main_id.disabled = false;
                }
            }

            function updateSendtimeBox() {
                getElement('senddate').disabled = (getElement('schedule1').checked) ? true : false;
                getElement('sendtime').disabled = (getElement('schedule1').checked) ? true : false;
            }

            function updateMessageLen() {
                var frm = document.pushMessageSmsTextFrm;
                obj = getElement('messageLen');
                var remaining = getRemainChar();
                obj.innerHTML = ((isEnglishText(frm.message.value))
                        ? MAX_ENG_CHAR + " maximum length allow for English"
                        : MAX_THAI_CHAR + " maximum length allow for ไทย")
                        + ", remaining " + ((remaining < 0) ? 0 : remaining) + " char(s)."
                        + ((remaining < 0) ? "<br><font color=red>The message length exceeds the meximum length!!</font>" : "");

            }

            function checkMessageLen() {
                var frm = document.pushMessageSmsTextFrm;
                if (getRemainChar() <= 0) {
                    frm.message.style.maxlength = frm.message.value.length;
                    return false;
                }
            }

            function getRemainChar() {
                var frm = document.pushMessageSmsTextFrm;
                return (isEnglishText(frm.message.value) ? MAX_ENG_CHAR : MAX_THAI_CHAR) - frm.message.value.length;
            }

            function updateOperSelect(id) {
                obj = getElement('operSelectDiv');
                var str = "";
                for (var i = 1; i < operIdArry.length; i++) {
                    str += "<input type='checkbox' name='oper' id='oper" + operIdArry[i] + "'"
                            + ((isServiceInOper(id, operIdArry[i]))
                                    ? (("<%=_operid%>" == "" || ("<%=_operid%>" != "" && "<%=_operid%>" == i)) ? " checked" : "")
                                    : " disabled")
                            + "><label for='oper" + operIdArry[i] + "'>" + operNameArry[i] + "</label> ";
                }
                obj.innerHTML = str;
            }

            function whichOper() {
                for (i in operIdArry) {
                    var obj = getElement('oper' + i);
                    if (obj && !obj.disabled && obj.checked) {
                        return i;
                    }
                }
            }

            function getSelectedOper() {
                var tmp = "";
                for (i in operIdArry) {
                    var obj = getElement('oper' + i);
                    if (obj && !obj.disabled && obj.checked) {
                        if (tmp.length > 0)
                            tmp += "|";
                        tmp += operNameArry[i];
                    }
                }

                return tmp;
            }

            function getServiceDetails(srvc_main_id, oper_id) {
                var url = "service_details.jsp?srvc_main_id=" + srvc_main_id + "&oper_id=" + oper_id;
                var obj = getElement('service_info_box');
                if (obj)
                    obj.innerHTML = ajaxPost(url);
            }

            function showConfirmBox() {
                var frm = document.pushMessageSmsTextFrm;
                var isok = false;

                isok = _onsubmit();

                if (!isok)
                    return;

                getElement('lbl_service').innerHTML = frm.srvc_main_id.options[frm.srvc_main_id.selectedIndex].text;
                getElement('lbl_oper').innerHTML = getSelectedOper();
                getElement('lbl_sendtime').innerHTML = frm.deliver_dt.value.replace("NOW()",
                        new DateFormatter("%y-%m-%d %H:%M:%S").format(new Date()));
                getElement('lbl_message').innerHTML = frm.message.value;

                // disable main button
                frm.cancel.disabled = true;
                frm.submit.disabled = true;
                show2('confirm_box');
            }

            function after_confirm() {
                if (!wasSubmitted) {
                    wasSubmitted = true;
                    getElement('send_btn').value = "Sending...";
                    return wasSubmitted;
                }
                return false;
            }

            function hideConfirmBox() {
                var frm = document.pushMessageSmsTextFrm;
                frm.cancel.disabled = false;
                frm.submit.disabled = false;
                hide2('confirm_box');
            }

            function _onsubmit() {
                var frm = document.pushMessageSmsTextFrm;
                if (frm.message.value.trim().length == 0) {
                    alert('Please enter the text message!');
                    frm.message.focus();
                    return false;
                }

                // check time format
                var regexp = "^(\\d{1}|(0|1)\\d{1}|2[0-3]):(([0-5]\\d{1})|\\d{1})$";
                if (!frm.sendtime.value.test(regexp)) {
                    alert('Time format error!');
                    frm.sendtime.focus();
                    frm.sendtime.select();
                    return false;
                }

                // padding hour
                var regexp = "^\\d{1}:(([0-5]\\d{1})|\\d{1})$";
                if (frm.sendtime.value.test(regexp)) {
                    frm.sendtime.value = "0" + frm.sendtime.value;
                }

                // check date format
                if (getElement('schedule2').checked && frm.senddate.value == "") {
                    alert('Date format error!');
                    frm.senddate.focus();
                    frm.senddate.select();
                    return false;
                }

                // check message length
                if (getRemainChar() < 0) {
                    alert('The message is larger than the maximum length');
                    return false;
                }

                // deliver datetime
                frm.deliver_dt.value = (getElement('schedule1').checked) ? "NOW()" : frm.senddate.value + " " + frm.sendtime.value + ":00";

                // summary oper ID
                var oper_id = 0;
                for (var i = 1; i < operIdArry.length; i++) {
                    oper_id += (getElement('oper' + i)) ? (getElement('oper' + i).checked ? Math.pow(2, operIdArry[i]) : 0) : 0;
                }
                if (oper_id == 0) {
                    alert('Please select at least 1 operator!');
                    return false;
                }
                frm.operid.value = oper_id;

                // leave it out when production
                //alert(frm.deliver_dt.value);
                //return false;
                return true;
            }
        </script>

        <script src='./js/mootools.js' type='text/javascript'></script>
        <script src='./js/calendar.rc4.js' type='text/javascript'></script>
        <script>
            window.onload = function() {
                var frm = document.pushMessageSmsTextFrm;
                updateServiceOptions(0, frm.srvc_main_id);

                if (NiftyCheck())
                    RoundedTop("div#content_header", "#C0CDF2", "#FFF");
                if (NiftyCheck())
                    RoundedBottom("div#content", "#C0CDF2", "#377CB1");
                if (NiftyCheck())
                    Rounded("div#content2", "#377CB1", "#FFF");

                var obj = getElement('srvc_main_id');
                updateOperSelect(obj.value);
                if (!obj.disabled) {
                    getServiceDetails(obj.value, whichOper());
                }
                frm.submit.disabled = obj.disabled;
                frm.cancel.disabled = obj.disabled;

                updateSendtimeBox();
                updateMessageLen();

                obj.onchange = function() {
                    updateOperSelect(obj.value);
                    getServiceDetails(obj.value, whichOper());
                }

                // disable NOW on 20:00PM - 06:00AM
                var d = new Date();
                var hour = d.getHours();
                var minute = d.getMinutes();
                var mins = hour * 100 + minute;
                if (mins >= 2000 || mins < 600) {
                    var timeobj = getElement('schedule1');
                    if (timeobj) {
                        timeobj.disabled = true;
                    }
                    var timeobj = getElement('schedule2');
                    if (timeobj) {
                        timeobj.checked = true;
                    }
                }

            }
            window.addEvent('domready', function() {
                myCal = new Calendar({senddate: 'Y-m-d'}, {classes: ['dashboard'], direction: .5});
            });
        </script>
    </head>
    <body class="content">
        <div id="pushMessageSmsText" style="width:100%;">
            <form target="ctFrame" name="pushMessageSmsTextFrm" method="post" action="pushMessageSmsServlet"
                  onsubmit="return after_confirm()">
                <%
                    if (!_txqid.isEmpty()) {
                        out.print("<input type='hidden' name='txqid' value='" + _txqid + "'>");
                    }
                %>
                <input type="hidden" name="to" value="ies" >
                <input type="hidden" name="type" value="sms" >
                <input type="hidden" name="deliver_dt" value="">
                <input type="hidden" name="operid" value="">
                <div id="confirm_box" class="transparent"
                     style="
                     position:absolute;display:none;
                     top:50px;left:100px;padding:0;margin:0;padding-top:10px;
                     width:460px;
                     text-align:center;z-index: 1009;
                     background-color: #234">
                    <strong style="color:#EEE;font-size:21px;font-weight:bold">Confirm</strong>
                    <div style="
                         position:relative;
                         float: left;
                         padding:15px;margin:10px 1px 1px 1px;
                         width:428px;
                         text-align:left;
                         vertical-align:middle;
                         background:#EEE;">

                        <div style="color:#222;font-size:14px;padding-bottom:10px;">
                            <img src="images/warning16.gif" style="vertical-align: middle;border:0"> Confirm sending message?</div>
                        <div id="confirm_box_detail" style="
                             position:relative;
                             float: left;
                             width:418px;padding:5px;margin-top:10px;
                             word-wrap: break-word;font-size: 12px;
                             background:#876;">
                            <p class="tt">Service:<span id="lbl_service" class="vl"></span></p>
                            <p class="tt">Carrier:<span id="lbl_oper" class="vl"></span></p>
                            <p class="tt">Delivery:<span id="lbl_sendtime" class="vl"></span></p>
                            <p class="tt">Message:<br/><span id="lbl_message" class="vl"></span></p>
                        </div>

                        <div style="
                             position:relative;
                             float: left;
                             width:418px;
                             text-align:right">
                            <input id="send_btn" type="submit" value="Send" style="padding:10px 20px" />
                            <input type="button" value="Cancel" style="padding:10px 20px;margin:10px" onclick="hideConfirmBox()" />
                        </div>
                    </div>
                </div>
                <div id="content_header" style="width:75%; background-color:#FFF">
                    <h2 class="smstext">Text Message</h2>
                </div>
                <div id="content" style="width:75%;padding-top: 10px;">
                    <div id="content2" style="width:90%; text-align:center; background-color:#FFF; margin:10px 0 0 10px;">
                        <table align="center" class="table4" style="width:90%; margin-top:0px; padding:0;">
                            <tr>
                                <th style="border-bottom:0px;" width="30%">Service Name :</th>
                                <td>
                                    <script>document.write(createOptions(null, null, 'srvc_main_id', false, 0));</script>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;"></th>
                                <td>
                                    <div id="service_info_box" style="padding:1px;"></div>
                                </td>
                            </tr>
                            <tr>
                                <th>Operator :</th>
                                <td>
                                    <div id="operSelectDiv" class="txt10"></div>
                                </td>
                            </tr>
                            <tr>
                                <th>Message :</th>
                                <td>
                                    <textarea name="message" rows="6" 
                                              onkeypress="return checkMessageLen()"
                                              onkeyup="updateMessageLen(this)"
                                              style="width:300px"><%=_msg%></textarea>
                                    <br><div id="messageLen" class="txt11" style="verical-align:top;"></div>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Deliver Date/Time :</th>
                                <td>
                                    <input type="radio" id="schedule1" name="schedule" value="0" onclick="updateSendtimeBox()" <%=_now%>>
                                    <label for="schedule1"><span style="vertical-align:baseline;">Now (6:00AM - 20:00PM)</span></label><br>
                                    <input type="radio" id="schedule2" name="schedule" value="1" onclick="updateSendtimeBox()" <%=_schedule%>>
                                    <label for="schedule2"><span style="vertical-align:baseline;">On Time :</span></label>

                                    <input type="text" name="senddate" id="senddate" class="calendar" style="width:70px" value="<%=_date%>" disabled>
                                    <script>
                                        document.write("<input type='text' name='sendtime' id='sendtime'");
                                        if ("<%=_time%>" == "")
                                            document.write("value='" + hh + ":" + ((mn < 10) ? "0" : "") + mn + "'");
                                        else
                                            document.write("value='<%=_time%>'");
                                        document.write("maxlength=5 style='width:40px' onkeypress='return filter_digit_char(event, \":\")' disabled> (yyyy-mm-dd HH:MM)");
                                    </script>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div style="margin: 20px 0 20px 50px;">
                        <input id="submit" type="button" class="button" value="Send" onclick="javascript:showConfirmBox();">
                        <input id="cancel" type="reset" class="button" value="Cancel">
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>
