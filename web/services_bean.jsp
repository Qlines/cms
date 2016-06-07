<%-- 
    Document   : services_bean
    Created on : 5 ก.พ. 2553, 14:53:11
    Author     : developer
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_STATUS" %>
<%@page import="hippoping.smsgw.api.db.OperConfig.CARRIER" %>
<jsp:useBean id="serviceBean" scope="application" class="smsgateway.webadmin.bean.ServiceBean" />
<%
        String srvctype = request.getParameter("srvc_type");
        if (srvctype == null) {
            srvctype = "0";
        }
        int srvc_type = Integer.parseInt(srvctype);
        String tmp = request.getParameter("srvc_status");
        int status = (tmp==null)?SERVICE_STATUS.ALL.getId():Integer.parseInt(tmp);

        String dcc = request.getParameter("dcc");

        User user = (User)request.getSession().getAttribute("USER");
        if (user == null) {
            out.print("<script>window.location='logout?msg=Your session has been expired.'</script>");
            return;
        }
%>
<script>
    var _allServices = '<%=serviceBean.getServiceNameByUserID(status, srvc_type, user)%>';
    var _aisServices = '<%=serviceBean.getServiceNameByUserID(status, srvc_type, CARRIER.AIS_LEGACY.getId(), user)%>';
    var _aisCdgServices = '<%=serviceBean.getServiceNameByUserID(status, srvc_type, CARRIER.AIS.getId(), user)%>';
    var _dtacServices = '<%=(dcc != null) ? serviceBean.getDtacServiceNameByUserID(status, srvc_type, user, dcc.equals("1")) : serviceBean.getServiceNameByUserID(status, srvc_type, CARRIER.DTAC.getId(), user)%>';
    var _dtacSdpServices = '<%=(dcc != null) ? serviceBean.getDtacSdpServiceNameByUserID(status, srvc_type, user, dcc.equals("1")) : serviceBean.getServiceNameByUserID(status, srvc_type, CARRIER.DTAC_SDP.getId(), user)%>';
    var _truemoveServices = '<%=serviceBean.getServiceNameByUserID(status, srvc_type, CARRIER.TRUE.getId(), user)%>';
    var _truehServices = '<%=serviceBean.getServiceNameByUserID(status, srvc_type, CARRIER.TRUEH.getId(), user)%>';
    var _catServices = '<%=serviceBean.getServiceNameByUserID(status, srvc_type, CARRIER.CAT.getId(), user)%>';
    
    var ALLOPER = <%=CARRIER.ALL.getId()%>;
    var DTAC = <%=CARRIER.DTAC.getId()%>;
    var TRUEMOVE = <%=CARRIER.TRUE.getId()%>;
    var AIS = <%=CARRIER.AIS_LEGACY.getId()%>;
    var AIS_CDG = <%=CARRIER.AIS.getId()%>;
    var TMH = <%=CARRIER.TRUEH.getId()%>;
    var DTAC_SDP = <%=CARRIER.DTAC_SDP.getId()%>;
    var CAT = <%=CARRIER.CAT.getId()%>;
    
    var operNameArry = new Array("All Operators", "DTAC", "Truemove", "AIS", "AIS_CDG", "TrueH", "DTAC_SDP", "CAT");
    var operIdArry = new Array(
        ALLOPER, 
        DTAC, 
        TRUEMOVE, 
        AIS, 
        AIS_CDG, 
        TMH,
        DTAC_SDP,
        CAT
        );
    var rawOptions = new Array(_allServices, _dtacServices, _truemoveServices, _aisServices, _aisCdgServices, _truehServices, _dtacSdpServices, _catServices);

    var optionsArry = new Array();
    // load services name
    for (var oper in operIdArry) {
        if (rawOptions[oper]) {
            optionsArry[oper] = new Array();
            try 
            {
                var tmp = rawOptions[oper].split(';');
                for (i=0;i<tmp.length;i++) {
                    optionsArry[oper][i] = tmp[i].split('|');
                }
            } catch (e) {
            }
        }
    }

    /**
     * return summary of multiply a number by 2
     */
    function isServiceInOper(id, oper) {
        if (optionsArry[oper]) {
            for (var j=0;j<optionsArry[oper].length;j++) {
                if (optionsArry[oper][j][0] == id) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * return summary of multiply a number by 2
     */
    function findOperByService(id) {
        var available = new Array(0,0,0,0,0,0);

        if (operIdArry) {
            for (var i=1;i<operIdArry.length;i++) {
                var oper_id = operIdArry[i];
                if (optionsArry[oper_id]) {
                    for (var j=0;j<optionsArry[oper_id].length;j++) {
                        if (optionsArry[oper_id][j][0] == id) {
                            available[i] = 1;
                        }
                    }
                }
            }
        }

        return (Math.pow(2,DTAC) * available[DTAC]) +
            (Math.pow(2,TRUEMOVE) * available[TRUEMOVE]) +
            (Math.pow(2,AIS) * available[AIS]) +
            (Math.pow(2,AIS_CDG) * available[AIS_CDG]) +
            (Math.pow(2,TMH) * available[TMH]) +
            (Math.pow(2,DTAC_SDP) * available[DTAC_SDP]) +
            (Math.pow(2,CAT) * available[CAT]);
    }

    /**
     * convert multiply number to oper
     */
    function hasOper(number, oper) {
        var available = new Array(0,0,0,0,0,0);
        if (operIdArry) {
            for (var i=operIdArry.length;i>=1;i--) {
                if (number-Math.pow(2,operIdArry[i]) >= 0) {
                    number = number - Math.pow(2,operIdArry[i]);
                    available[i] = 1;
                }
            }
        }
        return available[oper]==1;
    }
</script>