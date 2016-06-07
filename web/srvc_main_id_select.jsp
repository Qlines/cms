<%-- 
    Document   : srvcid_select
    Created on : Oct 19, 2010, 1:46:27 PM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.OperConfig.CARRIER" %>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.User.*" %>
<%@page import="lib.common.StringConvert" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_STATUS" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%
            User user = (User) session.getAttribute("USER");
            if (user == null) {
                out.print("<script>window.location='logout?msg=Your session has been expired.'</script>");
                return;
            }

            int oper_id = 0;
            String soper = request.getParameter("oper_id");
            if (soper != null && StringConvert.isDigit(soper)) {
                oper_id = Integer.parseInt(soper);
            }
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <style type="text/css">
            body{text-align:center; /* for IE */
                 margin:0 auto; /* for the rest */
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            </style>
            <script src="./js/nifty.js" type="text/javascript"></script>
            <script src='./js/utils.js' type='text/javascript'></script>
            <script src='./js/webstyle.js' type='text/javascript'></script>
            <script src='./js/datetime.js' type='text/javascript'></script>
            <%
                        int srvc_type = SERVICE_TYPE.ALL.getId();
                        int srvc_status = SERVICE_STATUS.ON.getId();
                        if (user.getType() == USER_TYPE.ADMIN) {
                            srvc_status |= SERVICE_STATUS.TEST.getId();
                        }
            %>
            <jsp:include page="./services_bean.jsp">
                <jsp:param name="srvc_type" value="<%=srvc_type%>" />
                <jsp:param name="srvc_status" value="<%=srvc_status%>" />
            </jsp:include>
            <script type="text/javascript">
                function show_oper(obj) {
                    var selected = 0;
                    var myOpers = new Array(4); // create Array 2 dimensions
                    for (operid in operIdArry) {
                        myOpers[operid] = new Array(operid, operNameArry[operid]);
                        if (operIdArry[operid] == <%=oper_id%>)
                            selected = operid;
                    }

                    // Add options to dropdownlist
                    addOption_list(obj, myOpers);
                    obj.selectedIndex = selected;
                    obj.remove(0);
                }

                function updateServiceOptions2(oper_id, obj) {
                    var frm = document.forms["srvcMainIdSelectFrm"];
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
                        tmpArry[1] = "== Please select one ==";
                        var myArry = optionsArry[oper_id].slice();
                        myArry.unshift(tmpArry);

                        addOption_list(obj, myArry);
                        frm.srvcid.disabled = false;
                    }
                }

                window.onload=function(){
                    var frm = document.srvcMainIdSelectFrm;
                    show_oper(frm.operid);
                    updateServiceOptions2(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);

                    frm.operid.onchange=
                        function(){updateServiceOptions2(frm.operid.options[frm.operid.selectedIndex].value, frm.srvcid);}
                }

                function _onsubmit() {
                    var frm = document.srvcMainIdSelectFrm;

                    var srvc = frm.srvcid.options[frm.srvcid.selectedIndex].value;
                    window.returnValue = frm.operid.options[frm.operid.selectedIndex].value + ";" + srvc;

                    // leave it out when production
                    //alert(window.returnValue);
                    //return false;

                    window.close();
                }

                function doCancel() {
                    window.returnValue = '0;0';
                    window.close();
                }
            </script>
        </head>
        <body>
            <div align="center" style="width:90%; padding: 20px 0 20px 0">
            <form name="srvcMainIdSelectFrm">
                <fieldset>
                    <legend>Service List:</legend>
                    <table align="center" class="table4" style="width:90%; margin-top:0px; padding:0;">
                        <tr>
                            <th style="border-bottom:0px;text-align: left" width="30%">Operator :</th>
                            <td style="border-bottom:0px;text-align: left">
                                <script type="text/javascript">
                                    document.write(createOptions(null, null, 'operid', false, 0));
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <th style="border-bottom:0px;text-align: left" width="30%">Service Name :</th>
                            <td style="border-bottom:0px;text-align: left">
                                <script type="text/javascript">
                                    document.write(createOptions(null, null, 'srvcid', false, 0));
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" style="text-align: left">
                                <input type="button" value="Select" onclick="javascript:_onsubmit()">
                                <input type="button" value="Cancel" onclick="javascript:doCancel()">
                            </td>
                        </tr>
                    </table>
                </fieldset>
            </form>
        </div>
    </body>
</html>
