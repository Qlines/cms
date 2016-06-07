<%-- 
    Document   : services_content_map_edit
    Created on : 25 ต.ค. 2554, 0:21:54
    Author     : nack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Map" %>
<%@page import="java.util.List" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement" %>
<%@page import="hippoping.smsgw.api.db.OperConfig.CARRIER" %>
<%@page import="hippoping.smsgw.api.db.Message.SMS_TYPE" %>
<%@page import="hippoping.smsgw.api.db.MessageSms" %>
<%@page import="hippoping.smsgw.api.db.MessageWap" %>
<%@page import="hippoping.smsgw.api.db.MessageMms" %>
<%@page import="hippoping.smsgw.api.db.ThirdPartyConfig" %>
<%@page import="hippoping.smsgw.api.db.ServiceContentAction" %>
<%@page import="hippoping.smsgw.api.db.ServiceContentAction.ACTION_TYPE" %>
<%@page import="lib.common.StringConvert" %>
<!doctype html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
            input.message{width:450px;}
            input.number{width:50px;}
            input.shorttext{width:150px}
            input.mediumtext{width:300px}
            th{width: 100px}
            table.preview{width:340px;font-size:1.5em}
            table.preview th{background: #F5FAA3}
            table.preview td{background: #FFF}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
            String _cmd = request.getParameter("cmd");
            String _map_id = request.getParameter("mapid");
            
            String encoding = "UTF-8";
            if (request.getCharacterEncoding() != null) {
                encoding = request.getCharacterEncoding();
            }

            String _srvcid = request.getParameter("srvcid");
            String _srvcname = new String(request.getParameter("srvcname").getBytes("ISO8859_1"), encoding);
            String _operid = request.getParameter("operid");
            String _ivr_ctnt_id = "";
            String _keyword = "";
            String _chrg_flg = "";
            ACTION_TYPE _ctnt_type = null;
            int _ctnt_id = 0;
            
            int srvc_main_id = 0;
            CARRIER oper = null;
            

            ServiceContentAction sca = null;
            if (_cmd.equals("edit")) { // edit link
                sca = new ServiceContentAction(Integer.parseInt(_map_id));

                // inquiry service information
                srvc_main_id = sca.getSrvc_main_id();
                oper = sca.getOper();
                _ivr_ctnt_id = sca.getIvr_content_id();
                _keyword = sca.getKeyword();
                _chrg_flg = sca.getChrg_flg();
                _ctnt_type = sca.action_type;
                _ctnt_id = sca.contentId;
            }
            
            System.out.println("test");
        %>
        <script type="text/javascript">
            function selectContent() {
                var frm = document.forms["ServiceContentMapEditFrm"];
                var link = "";
                var type = "";
                if (frm.ctnt_type[0].checked) {
                    link = "thirdparty_search.jsp?type=popup";
                    type = "FORWARD";
                } else if (frm.ctnt_type[1].checked) {
                    link = "sms_text_search.jsp?type=popup";
                    type = "SMS";
                } else if (frm.ctnt_type[2].checked) {
                    link = "sms_picture_search.jsp?type=popup";
                    type = "SMS";
                } else if (frm.ctnt_type[3].checked) {
                    link = "sms_ringtone_search.jsp?type=popup";
                    type = "SMS";
                } else if (frm.ctnt_type[4].checked) {
                    link = "sms_wap_search.jsp?type=popup";
                    type = "WAP";
                } else if (frm.ctnt_type[5].checked) {
                    link = "mms_search.jsp?type=popup";
                    type = "MMS";
                } 
                
                var ctnt_id = window.showModalDialog(link,'', 'dialogWidth:640px;dialogHeight:670px');
                
                if (ctnt_id) {
                    showContent(ctnt_id, type, getElement('ctnt_preview'));
                    frm.ctnt_id.value = ctnt_id;
                }
            }
            
            function showContent(ctnt_id, ctnt_type, panel) {
                var str = ajaxPost('MessagePreview?ctnt_id=' + ctnt_id + '&ctnt_type=' + ctnt_type );
                if (panel) {
                    panel.innerHTML = 
                        "<table class='preview'>"
                        + "<tr><th width='30%' style='border-bottom: 0'>Content-Type:</th>" 
                        + "<td>" + ctnt_type + "</td></tr>" 
                        + "<tr><th colspan=2>Content</th></tr>"
                        + "<tr><td colspan=2>"
                        + "<div style='width:320px;word-wrap: break-word;'>"
                        + str 
                        + "</div>"
                        + "</td></tr></table>";
                }
            }
            
            function _onsubmit() {
                var frm = document.forms["ServiceContentMapEditFrm"];
                
                if (frm.ivr_ctnt_id.value.trim() == '' && frm.keyword.value.trim() == '') {
                    alert('Please enter the either one of IVR or SMS Keyword for mapping to incoming message(MO)!!');
                    frm.ivr_ctnt_id.focus();
                    return false;
                }
                
                var radio_check = false;
                for (var i = 0;  i < frm.ctnt_type.length ; i++) {
                    if (frm.ctnt_type[i].checked) {
                        radio_check = true;
                        break;
                    }
                }
                
                if (!radio_check) {
                    alert('Please select a least one of reply content type!!');
                    frm.ctnt_type.focus();
                    return false;
                }
                
                if (frm.ctnt_id.value == 0) {
                    alert('Please choose reply content!!');
                    var obj = getElement('browse_btn');
                    if (obj) {
                        obj.focus();
                    }
                    return false;
                }
                
                
            }

            window.onload=function() {
        <%
            if (_cmd.equals("edit") && sca != null) {
                out.print("showContent(" + sca.contentId + ", '" + sca.action_type + "', getElement('ctnt_preview'));");
            }
        %>

                if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
            }
        </script>
    </head>
    <body class="content">
        <div id="servicesSubEdit" style="width:100%;">
            <form name="ServiceContentMapEditFrm" id="foo" method="post" action="services_content_map.jsp"
                  onsubmit="return _onsubmit();">
                <input type='hidden' name='cmd' value='<%=_cmd%>'>
                <input type='hidden' name='map_id' value='<%=_map_id%>'>
                <input type='hidden' name='srvc_id' value='<%=_srvcid%>'>
                <input type='hidden' name='oper_id' value='<%=_operid%>'>
                <input type='hidden' name='ctnt_id' value='<%=(_cmd.equals("edit")) ? sca.contentId : 0%>'>
                <div id="content" style="width:100%">
                    <h2><%=_cmd.toUpperCase()%> Content Map</h2><hr>
                    <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px; padding:0px;">
                        <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                            <tr>
                                <th width="25%" style="border-bottom:0px">Service</th>
                                <td width="25%"><%=_srvcname%></td>
                                <td width="50%" rowspan="7">
                                    <fieldset style="width:360px">
                                        <legend>Content Preview</legend>
                                        <div id="ctnt_preview" style="word-wrap: break-word;min-height: 200px; width:340px; padding:10px; background: #ABCDEF">
                                        </div>
                                        <input id="browse_btn" type="button" value="Choose content" onclick="selectContent()">
                                    </fieldset>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px">Operator</th>
                                <td>
                                    
        <%
                        if (_operid != null) {
                           out.print(CARRIER.fromId(Integer.parseInt(_operid)));
                           out.print("<input type='hidden' name='operid' value='" + _operid + "'");
                        }
        %>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px">IVR</th>
                                <td>
                                    <input class="shorttext" type="text" name="ivr_ctnt_id" 
                                           value="<%=_ivr_ctnt_id!=null ? _ivr_ctnt_id : ""%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px">SMS Keyword</th>
                                <td>
                                    <input class="shorttext" type="text" name="keyword" 
                                           value="<%=_keyword!=null ? _keyword : ""%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px">Force charging</th>
                                <td>
                                    <input type="checkbox" name="chrg_flag" id="chrg_flag" 
                                           <%=( _chrg_flg != null && _chrg_flg.equals("MT")) ? "checked" : ""%>>
                                    <label for="chrg_flag">Force charging</label>
                                </td>
                            </tr>
                            <tr>
                                <th rowspan="2" style="border-bottom:0px">Action</th>
                                <td>
                                    <input type="radio" name="ctnt_type" id="type_forward" value="<%=ACTION_TYPE.FORWARD%>"
                                           <%= (sca!=null && sca.action_type == ACTION_TYPE.FORWARD)?"checked":""%>>
                                    <label for="type_forward" id="type_forward_txt">Forward to 3rd party</label><br>
                                    
                                    <input type="radio" name="ctnt_type" id="type_sms_text" value="<%=ACTION_TYPE.SMS%>"
                                           <%= (sca!=null && (sca.action_type == ACTION_TYPE.SMS) && (((MessageSms)sca.getMessage()).getSmsType()) == SMS_TYPE.TEXT)?"checked":""%>>
                                    <label for="type_sms_text" id="type_sms_text_txt">SMS Text message</label><br>
                                    
                                    <input type="radio" name="ctnt_type" id="type_sms_picture" value="<%=ACTION_TYPE.SMS%>"
                                           <%= (sca!=null && (sca.action_type == ACTION_TYPE.SMS) && (((MessageSms)sca.getMessage()).getSmsType()) == SMS_TYPE.PICTURE)?"checked":""%>>
                                    <label for="type_sms_picture" id="type_sms_picture_txt">Picture message</label><br>
                                    
                                    <input type="radio" name="ctnt_type" id="type_sms_ringtone" value="<%=ACTION_TYPE.SMS%>"
                                           <%= (sca!=null && (sca.action_type == ACTION_TYPE.SMS) && (((MessageSms)sca.getMessage()).getSmsType()) == SMS_TYPE.RINGTONE)?"checked":""%>>
                                    <label for="type_sms_ringtone" id="type_sms_ringtone_txt">Ringtone message</label><br>
                                    
                                    <input type="radio" name="ctnt_type" id="type_sms_wap" value="<%=ACTION_TYPE.WAP%>" 
                                           <%= (sca!=null && (sca.action_type == ACTION_TYPE.WAP))?"checked":""%>>
                                    <label for="type_sms_wap" id="type_sms_wap_txt">WAP push</label><br>
                                    
                                    <input type="radio" name="ctnt_type" id="type_mms" value="<%=ACTION_TYPE.MMS%>"
                                           <%= (sca!=null && (sca.action_type == ACTION_TYPE.MMS))?"checked":""%>>
                                    <label for="type_mms" id="type_mms_txt">MMS</label>
                                </td>
                            </tr>
                            <tr>
                                <td>
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
