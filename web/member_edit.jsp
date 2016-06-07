<%-- 
    Document   : link_new
    Created on : Jun 14, 2010, 2:23:14 PM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="lib.common.DatetimeUtil" %>
<%@page import="lib.common.StringConvert" %>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.ThirdPartyConfig" %>
<%@page import="hippoping.smsgw.api.db.Subscriber" %>
<%@page import="hippoping.smsgw.api.db.SubscriberGroup.SUB_STATUS" %>
<%@page import="hippoping.smsgw.api.db.OperConfig.CARRIER" %>
<%@page import="hippoping.smsgw.api.db.LogEvent" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_TYPE" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_ACTION" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.LOG_LEVEL" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Subscriber Profile Edit</title>
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <link rel="stylesheet" type="text/css" href="./css/dashboard.css" media="screen" />
        <style type="text/css">
            body{margin:0px; padding: 5px; background: black;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding-bottom:1em}
            h2{padding-top: 0.3em}
            input.shorttext{width:150px}
            input.longtext{width:300px;}
            input.number{width:50px;}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
                    String _cmd = request.getParameter("cmd");
                    String _subcmd = request.getParameter("subcmd");

                    Subscriber subscriber = (Subscriber) request.getSession().getAttribute("subscriber_edit");

                    if (_cmd.equals("edit")) { // edit link
                        // get parameters of subscriber that want to be modify
                        String _msisdn = request.getParameter("msisdn");
                        String _srvcid = request.getParameter("srvcid");
                        String _operid = request.getParameter("operid");

                        request.getSession().setAttribute("subscriber_edit", new Subscriber(_msisdn, Integer.valueOf(_srvcid), CARRIER.fromId(Integer.valueOf(_operid))));
                        subscriber = (Subscriber) request.getSession().getAttribute("subscriber_edit");

                    } else if (_cmd.equals("submit")) {
                        if (_subcmd != null && _subcmd.equals("edit")) {
                            if (subscriber != null) {
                                // modify subscriber with new parameters
                                String _ctnt_ctr = request.getParameter("ctnt_ctr");
                                String _free_trial = request.getParameter("free_trial");
                                String _rmdr_ctr = request.getParameter("rmdr_ctr");
                                String _rchg_ctr = request.getParameter("rchg_ctr");
                                String _register_date = request.getParameter("register_date");
                                String _unregister_date = request.getParameter("unregister_date");
                                String _expired_date = request.getParameter("expired_date");
                                String _balanced_date = request.getParameter("balanced_date");
                                String _state = request.getParameter("state");

                                //System.out.println("register:" + ((_register_date != null && !_register_date.isEmpty()) ? DatetimeUtil.toDate(_register_date, "yyyy-MM-dd") : null));

                                String old = subscriber.print();
                                subscriber.setCtnt_ctr(Integer.valueOf(_ctnt_ctr));
                                if (_free_trial != null && StringConvert.isDigit(_free_trial)) {
                                    subscriber.setFree_trial(Integer.valueOf(_free_trial));
                                }
                                subscriber.setRmdr_ctr(Integer.valueOf(_rmdr_ctr));
                                subscriber.setRchg_ctr(Integer.valueOf(_rchg_ctr));
                                subscriber.setRegister_date((_register_date != null && !_register_date.isEmpty()) ? DatetimeUtil.toDate(_register_date, "yyyy-MM-dd") : null);
                                subscriber.setUnregister_date((_unregister_date != null && !_unregister_date.isEmpty()) ? DatetimeUtil.toDate(_unregister_date, "yyyy-MM-dd") : null);
                                subscriber.setExpired_date((_expired_date != null && !_expired_date.isEmpty()) ? DatetimeUtil.toDate(_expired_date, "yyyy-MM-dd") : null);
                                subscriber.setBalanced_date((_balanced_date != null && !_balanced_date.isEmpty()) ? DatetimeUtil.toDate(_balanced_date, "yyyy-MM-dd") : null);
                                if (_state != null && StringConvert.isDigit(_state)) {
                                    subscriber.setState(Integer.valueOf(_state));
                                }
                                
                                int rows = subscriber.sync();
                                if (rows == 1) {
                                    System.out.println("effect " + rows + " row(s)");
                                    out.println("<script>alert('The settings successfully saved.');</script>");
                                    LogEvent.log(EVENT_TYPE.SUBSCRIBER, EVENT_ACTION.MODIFY, "old:" + old + "\n" + "new:" + subscriber.print()
                                            , (User) request.getSession().getAttribute("USER")
                                            , subscriber.getMsisdn()
                                            , CARRIER.fromId(subscriber.getOper_id())
                                            , 0, 0, 0, LOG_LEVEL.INFO);
                                }
                            } else {
                                out.println("<script>alert('The session may be expired!!');</script>");
                            }
                        }
                        out.println("<script>window.close();</script>");
                    }
        %>
        <script type="text/javascript">
            function _onsubmit() {
                var frm = document.forms["subscriberEditFrm"];

                if (frm.ctnt_ctr.value == "") {
                    alert("parameter required!!");
                    frm.ctnt_ctr.focus();
                    frm.ctnt_ctr.select();
                    return false;
                }

                if (frm.free_trial && frm.free_trial.value == "") {
                    alert("parameter required!!");
                    frm.free_trial.focus();
                    frm.free_trial.select();
                    return false;
                }

                if (frm.rmdr_ctr.value == "") {
                    alert("parameter required!!");
                    frm.rmdr_ctr.focus();
                    frm.rmdr_ctr.select();
                    return false;
                }

                if (frm.rchg_ctr.value == "") {
                    alert("parameter required!!");
                    frm.rchg_ctr.focus();
                    frm.rchg_ctr.select();
                    return false;
                }
                
                // test
                //return false;
            }

            function updateUnregiterDate() {
                var frm = document.forms["subscriberEditFrm"];
                var obj = getElement('unregister_date');
                if (obj) {
                    obj.disabled = !frm.unregister_cb.checked;
                }
            }

            function updateExpiredDate() {
                var frm = document.forms["subscriberEditFrm"];
                var obj = getElement('expired_date');
                if (obj) {
                    obj.disabled = !frm.expired_cb.checked;
                }
            }
        </script>

        <script src='./js/mootools.js' type='text/javascript'></script>
        <script src='./js/calendar.rc4.js' type='text/javascript'></script>
        <script type="text/javascript">

            window.onload=function() {
                updateUnregiterDate();
                updateExpiredDate();

                if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
            }

            window.addEvent('domready', function() { myCal = new Calendar({ register_date: 'Y-m-d' }, { classes: ['dashboard'], direction: 0 }); });
            window.addEvent('domready', function() { myCal = new Calendar({ unregister_date: 'Y-m-d' }, { classes: ['dashboard'], direction: 0 }); });
            window.addEvent('domready', function() { myCal = new Calendar({ expired_date: 'Y-m-d' }, { classes: ['dashboard'], direction: 0 }); });
        </script>
    </head>
    <body class="content">
        <div id="servicesSubEdit" style="width:90%; text-align: center">
            <form name="subscriberEditFrm" id="foo" method="post" action="member_edit.jsp" onsubmit="return _onsubmit()">
                <input type='hidden' name='cmd' value='submit'>
                <input type='hidden' name='subcmd' value='<%=_cmd%>'>
                <div id="content" style="width:100%">
                    <h2><%=_cmd.toUpperCase()%></h2><hr>
                    <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px; padding:0px;">
                        <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                            <tr>
                                <th style="border-bottom:0px;">Msisdn</th>
                                <td>
                                    <%=subscriber.getMsisdn()%>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Service</th>
                                <td>
                                    <%=subscriber.getSrvc_name()%>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Content Counter</th>
                                <td>
                                    <input class="shorttext" type="text" name="ctnt_ctr" id="ctnt_ctr" maxlength="2" 
                                           value="<%=subscriber.getCtnt_ctr()%>" onkeypress='return filter_digit_char(event)' >
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Free Trial (remaining)</th>
                                <td>
                                    <%=subscriber.getFree_trial()%>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Warning Counter</th>
                                <td>
                                    <input class="shorttext" type="text" name="rmdr_ctr" id="rmdr_ctr" maxlength="2" 
                                           value="<%=subscriber.getRmdr_ctr()%>" onkeypress='return filter_digit_char(event)'>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Retry Charge</th>
                                <td>
                                    <input class="shorttext" type="text" name="rchg_ctr" id="rchg_ctr" maxlength="2" 
                                           value="<%=subscriber.getRchg_ctr()%>" onkeypress='return filter_digit_char(event)'>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Register Date</th>
                                <td>
                                    <input type="checkbox" id="register_cb" name="register_cb" checked disabled>
                                    <input type="text" name="register_date" id="register_date" class="calendar" style="width:70px"
                                           value="<%=subscriber.getRegister_date()%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Unregister Date</th>
                                <td>
                                    <input type="checkbox" id="unregister_cb" name="unregister_cb" onclick="updateUnregiterDate()"
                                           <%=subscriber.getUnregister_date() != null ? " checked" : ""%>>
                                    <input class="calendar" type="text" name="unregister_date" id="unregister_date" style="width:70px"
                                           value="<%=subscriber.getUnregister_date() != null ? subscriber.getUnregister_date() : ""%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Expired Date</th>
                                <td>
                                    <input type="checkbox" id="expired_cb" name="expired_cb" onclick="updateExpiredDate()"
                                           <%=subscriber.getExpired_date() != null ? " checked" : ""%>>
                                    <input class="calendar" type="text" name="expired_date" id="expired_date" style="width:70px"
                                           value="<%=subscriber.getExpired_date() != null ? subscriber.getExpired_date() : ""%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Status</th>
                                <td>
                                    <input class="shorttext" type="text" name="state" id="state" maxlength="2" 
                                           value="<%=subscriber.getState()%>" onkeypress='return filter_digit_char(event)'>
                                    <%=SUB_STATUS.fromId(subscriber.getState())%>
                                </td>
                            </tr>
                        </table>
                        <div style="margin: 20px 0 20px 50px;">
                            <input id="submit" type="submit" class="button" value="Submit">
                            <input id="cancel" type="reset" class="button" value="Cancel">
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>
