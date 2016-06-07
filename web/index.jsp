<%-- 
    Document   : index
    Created on : 24 ต.ค. 2552, 11:13:42
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.UserFactory" %>
<%@page import="hippoping.smsgw.api.db.Group" %>
<%@page import="hippoping.smsgw.api.db.LogEvent" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_TYPE" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_ACTION" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.LOG_LEVEL" %>
<jsp:useBean id="companyProfile" scope="page" class="hippoping.smsgw.api.com.CompanyProfileFactory" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<%

            //User user = new User(User.findUserID(request.getRemoteUser()));
            User user = UserFactory.getUser(User.findUserID(request.getRemoteUser()));

            if (user == null) {
                out.println("<script>alert('Your session has been expired!');</script>");
                return;
            } else {
                User current_user = (User) session.getAttribute("USER");
                if (current_user == null) {
                    session.setAttribute("USER", user);

                    // log event
                    LogEvent.log(EVENT_TYPE.LOG_IN, EVENT_ACTION.NONE, "logging in",
                            (User) request.getSession().getAttribute("USER"),
                            null,
                            null,
                            0, 0, 0, LOG_LEVEL.INFO);
                }
            }

            // Group
            Group group = new Group(user.getGid());
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="<%=companyProfile.getCompanyProfile().getConfig().get("logo-icon")%>">
        <link href="./css/qm.css" rel="stylesheet" type="text/css">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/quickmenu2.jsp' type='text/javascript'></script>
        <title><%=companyProfile.getCompanyProfile().getConfig().get("name")%> #CMS v.2.0</title>
    </head>
    <body>
        <div style="width:100%">
            <span id="sitemap" class="bold1 txt11" style="position:absolute; z-index:1005; left:234px;top:70px;">Home</span>
            <div id="logo" style="z-index:1004; background: url(./images/bg_logo.gif) repeat-x;">
                <a target=_new href="<%=companyProfile.getCompanyProfile().getConfig().get("url")%>">
                    <img src="<%=companyProfile.getCompanyProfile().getConfig().get("logo")%>" border=0>
                </a>
            </div>
            <div id="logo_right"></div>
            <div id="logo_bottom"></div>
            <div id="top_left"></div>
            <div id="dmain" style="position:absolute; width:11px; height:666px; z-index:1004; left: 200px; top: 99px;padding: 0;">
                <iframe name="" src="menu_right.jsp" height="666" width="100%" frameborder="0" scrolling="no">
                </iframe>
            </div>
            <div style="position:absolute; width:80%; height:100px; z-index:1004; left: 211px; top: 0px;">
                <div id="header" style="position:relative; width:99%; height:100px; z-index:1004; left: 0; top: 0px;padding: 0 0 0 12px;">
                    <iframe src="header" height="100" width="100%" frameborder="0" scrolling="no">
                    </iframe>
                </div>
                <div id="dmain" style="position:relative; width:100%; height:657px; z-index:1006; left: 0; top: 0;padding: 0;">
                    <iframe name="ctFrame" src="<%=group.getHomepage()%>" height="657" width="100%" frameborder="0" scrolling="no">
                    </iframe>
                </div>
            </div>
        </div>
        <div id="footer" style="position:absolute; width:100%; height:300px; z-index:1007; left: 0px; top: 760px; padding: 0px;">
            <iframe src="footer.jsp" height="300" width="100%" frameborder="0" scrolling="no">
            </iframe>
        </div>
    </body>
</html>
