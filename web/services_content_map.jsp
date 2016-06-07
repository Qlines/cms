<%-- 
    Document   : sms_download_content_map
    Created on : 23 ต.ค. 2554, 16:30:13
    Author     : nack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_STATUS" %>
<%@page import="hippoping.smsgw.api.db.TxQueue" %>
<%@page import="hippoping.smsgw.api.db.MessageSms" %>
<%@page import="hippoping.smsgw.api.db.ServiceContentAction" %>
<%@page import="hippoping.smsgw.api.db.ServiceContentAction.ACTION_TYPE" %>
<%@page import="hippoping.smsgw.api.db.OperConfig.CARRIER" %>
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
        <style type="text/css">
            body{margin:10px; padding: 0px; background: white;
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
            int srvc_type = SERVICE_TYPE.SMSDOWNLOAD.getId();
            int srvc_status = SERVICE_STATUS.ON.getId() | SERVICE_STATUS.TEST.getId();
        %>
        <jsp:include page="./services_bean.jsp">
            <jsp:param name="srvc_type" value="<%=srvc_type%>" />
            <jsp:param name="srvc_status" value="<%=srvc_status%>" />
        </jsp:include>
        <script type="text/javascript">
        <%
            String _cmd = request.getParameter("cmd");
            String _srvc_main_id = request.getParameter("srvc_id");
            
            if (_cmd != null) {
                if (_cmd.equals("edit")) { // Edit
                    String _map_id = request.getParameter("map_id");
                    String _ivr_ctnt_id = request.getParameter("ivr_ctnt_id");
                    String _keyword = request.getParameter("keyword");
                    String _chrg_flag = request.getParameter("chrg_flag");
                    String _ctnt_type = request.getParameter("ctnt_type");
                    String _ctnt_id = request.getParameter("ctnt_id");
                    
                    if (_chrg_flag != null) {
                        _chrg_flag = (_chrg_flag.equals("on") ? "MT" : null);
                    }
                    
                    ServiceContentAction sca = new ServiceContentAction(Integer.parseInt(_map_id));
                    sca.setAction_type(ACTION_TYPE.valueOf(_ctnt_type));
                    sca.setChrg_flg(_chrg_flag);
                    sca.setContentId(Integer.parseInt(_ctnt_id));
                    sca.setIvr_content_id(_ivr_ctnt_id == null || _ivr_ctnt_id.isEmpty() ? null : _ivr_ctnt_id);
                    sca.setKeyword(_keyword == null || _keyword.isEmpty() ? null : _keyword);
                    
                    sca.sync();
                    
                } else if (_cmd.equals("create")) { // New
                    String _oper_id = request.getParameter("oper_id");

                    String _ivr_ctnt_id = request.getParameter("ivr_ctnt_id");
                    String _keyword = request.getParameter("keyword");
                    String _chrg_flag = request.getParameter("chrg_flag");
                    if (_chrg_flag != null) {
                        _chrg_flag = (_chrg_flag.equals("on") ? "MT" : null);
                    }
                    String _ctnt_type = request.getParameter("ctnt_type");
                    String _ctnt_id = request.getParameter("ctnt_id");


                    ServiceContentAction sca = new ServiceContentAction();
                    sca.setSrvc_main_id(Integer.parseInt(_srvc_main_id));
                    sca.setOper(CARRIER.fromId(Integer.parseInt(_oper_id)));
                    sca.setIvr_content_id(_ivr_ctnt_id);
                    sca.setKeyword(_keyword);
                    sca.setUssd_content_id(null);
                    sca.setAction_type(ACTION_TYPE.valueOf(_ctnt_type));
                    sca.setContentId(Integer.parseInt(_ctnt_id));
                    sca.setPiority(sca.getNextPriority(sca.getSrvc_main_id(), sca.getOper()));
                    sca.setChrg_flg(_chrg_flag);

                    ServiceContentAction.add(sca);
                } else if (_cmd.equals("cancel")) { // Delete
                    String _map_id = request.getParameter("map_id");
                    
                    ServiceContentAction sca = new ServiceContentAction(Integer.parseInt(_map_id));
                    sca.remove();
                }
            }
        %>
            
            // update operator DDL regarding to selected service
            function updateOperOptions(srvcid, obj) {
                var oper = findOperByService(srvcid);
                //alert("oper=" + oper + ", srvcid=" + srvcid);
                
                // clear old items
                removeAllOptions(obj);
                
                if (operIdArry) {
                    if (operIdArry.length == 0) {
                        var tmpArry = new Array(); // create Array 2 dimensions
                        tmpArry[0] = new Array(2);
                        tmpArry[0][0] = "0";
                        tmpArry[0][1] = "No oper available";

                        addOption_list(obj, tmpArry);
                        obj.disabled = true;
                    } else {
                        
                        var tmpArry = new Array();
                        tmpArry[0] = new Array(2);
                        tmpArry[0][0] = "0";
                        tmpArry[0][1] = "ALL";
                        
                        for (var i=0;i< operIdArry.length;i++) {
                            //alert("find oper " + operIdArry[i] + " :" + hasOper(oper, operIdArry[i]));
                            if (hasOper(oper, operIdArry[i])) {
                                var j = tmpArry.length;
                                tmpArry[j] = new Array(2);
                                tmpArry[j][0] = operIdArry[i];
                                tmpArry[j][1] = operNameArry[i];
                            }
                        }
                        
                        addOption_list(obj, tmpArry);
                        obj.disabled = false;
                    }
                }

            }

            function updateServiceOptions2(oper_id, obj) {
                removeAllOptions(obj);

                if (!optionsArry[oper_id]) {
                    var tmpArry = new Array(); // create Array 2 dimensions
                    tmpArry[0] = new Array(2);
                    tmpArry[0][0] = "0";
                    tmpArry[0][1] = "No service available";

                    addOption_list(obj, tmpArry);
                    obj.disabled = true;
                }
                else {
                    addOption_list(obj, optionsArry[oper_id]);
                    obj.disabled = false;
                }
            }
            
            function srvc_onchange() {
                var frm = document.forms["contentMapFrm"];
                updateOperOptions(frm.srvcid.value, frm.operid);
                frm.srvcname.value = frm.srvcid.options[frm.srvcid.selectedIndex].text;
                frm.submit();
            }
            
            function find_srvcid(srvcid) {
                var frm = document.forms["contentMapFrm"];
                var len = frm.srvcid.options.length;
                for (var i = 0 ; i < len ; i++ ) {
                    if (frm.srvcid.options[i].value == srvcid) {
                        frm.srvcid.options[i].selected = true;
                    }
                }
            }
            
            function doCreate() {
                var frm = document.forms["contentMapFrm"];
                
                frm.action = "services_content_map_edit.jsp";
                frm.target = "_self";
                frm.cmd.value = "create";
                frm.srvcname.value = frm.srvcid.options[frm.srvcid.selectedIndex].text;
                
                frm.submit();
            }
            
            window.onload=
                function(){

                var frm = document.forms["contentMapFrm"];
                updateServiceOptions2(0, frm.srvcid);
                
                find_srvcid(<%=_srvc_main_id%>);
                
                frm.srvcid.onchange=
                    function(){srvc_onchange();}
                
                srvc_onchange();

                if(NiftyCheck())RoundedTop("div#content_header","#C0CDF2","#FFF");
                if(NiftyCheck())RoundedBottom("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
            }
        </script>
    </head>
    <body class="content">
        <div id="contentMap" style="width:100%; height: 540px">
            <form target="resultFrame" name="contentMapFrm" method="post" action="ServiceContentMapServlet"
                  onsubmit="return _onsubmit();">
                <input type="hidden" name="cmd" value="refresh">
                <input type="hidden" name="srvcname">
                <div id="content_header" style="width:95%; background-color:#FFF">
                    <h2 class="contentmap">Content Map</h2>
                </div>
                <div id="content" style="width:95%; padding-top: 10px;">
                    <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px 0 0 10px;">
                        <table class="table4" style="width:100%; margin-top:0; padding:5px;">
                            <tr>
                                <th width="15%" style="line-height:2.5em">Service Name :</th>
                                <th>
                                    <script>document.write(createOptions(null, null, 'srvcid', false, 0));</script>
                                </th>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    operator: <script>document.write(createOptions(null, null, 'operid', false, 0));</script>
                                    <a href="javascript:doCreate()" style="color:#333;">create new content map</a>
                                </td>
                            </tr>
                        </table>
                        <iframe name="resultFrame" frameborder="0" style="overflow:auto; margin-top: 5px; padding:0;width:100%;height:480px"></iframe>
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>
