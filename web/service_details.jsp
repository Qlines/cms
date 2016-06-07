<%-- 
    Document   : service_details
    Created on : May 2, 2010, 11:56:01 PM
    Author     : nack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.ServiceElement" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_STATUS" %>
<%@page import="hippoping.smsgw.api.db.ServiceCharge.SRVC_CHRG" %>

<%
    String srvc_main_id = request.getParameter("srvc_main_id");
    String oper_id = request.getParameter("oper_id");
    int type = SERVICE_TYPE.ALL.getId();
    int status = SERVICE_STATUS.ALL.getId();

    ServiceElement se = new ServiceElement(Integer.parseInt(srvc_main_id), Integer.parseInt(oper_id), type, status);
    
    if (se != null && se.srvc_main_id > 0) {
        String charge = SRVC_CHRG.fromId(se.srvc_chrg_type_id).toString().toLowerCase().replace('_', ' ');

%>

<ul>
    <li class='title'><%=se.srvc_name%>(<%=se.srvc_id%>)</li>
    <li class='desc'>id:<%=se.srvc_main_id%>|owner:<%=se.getOwner().getName()%> : <%=se.price%> baht <%=charge%></li>
    <li>IVR(sub/unsub): <%=(se.ivr_register!=null)?se.ivr_register:"-"%> / <%=(se.ivr_unregister!=null)?se.ivr_unregister:"-"%></li>
    <li>SMS(sub/unsub): <%=(se.sms_register!=null)?se.sms_register:"-"%> / <%=(se.sms_unregister!=null)?se.sms_unregister:"-"%></li>
</ul>
<%
    }
    %>
