<%-- 
    Document   : login
    Created on : 12 ก.พ. 2555, 23:25:09
    Author     : nack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="companyProfile" scope="page" class="hippoping.smsgw.api.com.CompanyProfileFactory" />
<!doctype html>
<%
    request.getSession().invalidate();
%>
<html>
    <head>
        <title>Welcome to CMS login page</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <style text="text/css">
        body {
            margin:0;
            padding:0;
            background-color: #fff;
            color: #222;
            font-family: tahoma;
        }
        /*
        a, a:hover, a:visited, a:active {
            font-style: normal;
            color: #222;
        }
        */
        </style>
        <script type="text/javascript" src="js/utils.js"></script>
        <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
        <script type="text/javascript" src="js/jquery.hoveraccordion.min.js"></script>
        <script>
            qs();if (qsParm["alert"]) { try { alert(decodeURIComponent(qsParm["alert"]))} catch (e) {}}
            
            function inIframe() {
                try {
                    return window.self !== window.top;
                } catch (e) {
                    return true;
                }
            }

            if (inIframe()) {
                window.top.location.href = window.location.href;
            }
            
            $(document).ready(function(){
                var top = ($(window).height() - $("#logindialog").height()) / 2;
                var left = ($(window).width() - $("#logindialog").width()) / 2;
                $("#logindialog").offset({top:top,left:left});
            });
            
            $(window).resize(function(){
                var top = ($(window).height() - $("#logindialog").height()) / 2;
                var left = ($(window).width() - $("#logindialog").width()) / 2;
                $("#logindialog").offset({top:top,left:left});
            });
        </script>
    </head>
    <body style="text-align: center">
        <form method=post action="j_security_check" >
            <div>
                <div id="logindialog" style="background: url('images/bg_logo.gif') repeat-x;padding:0;margin:0;position:relative;vertical-align:middle;height:350px;width:474px;">
                    <div style="position:relative;top:0px;left:0px; padding: 30px 0 15px 0; font: 13px monospace; text-align: center">
                        <table style="width:100%;text-align: left">
                            <tr><td colspan="2" style="text-align:left; padding-left: 10px">
                                    <font style="font: 28px monospace">
                                        <font style="font-size: 58px">L</font>ogin
                                        <font style="font-size: 58px">P</font>age
                                    </font>
                            </td></tr>
                            <tr>
                                <td colspan="2"
                                    style="padding: 5px 0 2px 0; text-align: right; font: 0.9em sans-serif;">
                                    <hr>
                                    Secured by <b><%=companyProfile.getCompanyProfile().getConfig().get("name")%></b>
                                </td>
                            </tr>
                            <tr><td colspan="2" style="padding-top:30px; ">&nbsp;</td></tr>
                            <tr>
                                <td width="30%" style="text-align:right; padding-right: 10px">USERNAME:</td>
                                <td width="70%"><input type="text" name= "j_username" style="width:150px" /></td>
                            </tr>
                            <tr>
                                <td style="text-align:right; padding-right: 10px">PASSWORD:</td>
                                <td><input type="password" name= "j_password" style="width:150px" /></td>
                            </tr>
                            <tr>
                                <td style="text-align:right; padding: 35px 10px 0 0">
                                    <input type="submit" value="Login" />
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </form> 
    </body>
</html>
