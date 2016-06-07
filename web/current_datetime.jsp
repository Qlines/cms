<%-- 
    Document   : current_datetime
    Created on : 25 ต.ค. 2552, 23:16:12
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="inetbean" scope="page" class="smsgateway.webadmin.bean.InetBean" />
<%= inetbean.getCurrentDatetime("MMddHHmmyyyyss") %>