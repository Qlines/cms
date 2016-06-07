<%-- 
    Document   : footer
    Created on : 25 ต.ค. 2552, 8:36:53
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="companyProfile" scope="page" class="hippoping.smsgw.api.com.CompanyProfileFactory" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
    </head>
    <body style="padding-top: 10px; background-color:#FFF">
        <!--
        <div style="text-align:center;width:960px; margin:0px auto;">
            xxx
                    <img src="images/logo_sun_small_sdn.gif" border="0">
                    <img src="images/logo_java.jpg" border="0">
                    <img src="images/logo_glassfish.gif" border="0">
                    <img src="images/logo_netbeans.jpg" border="0">
                    <img src="images/logo_mysql.jpg" border="0">
                    <img src="images/logo_phpmyadmin.jpg" border="0">
                        <br>
                    <img src="images/logo_ais.jpg" border="0">
                    <img src="images/logo_dtac.jpg" border="0">
                    <img src="images/logo_dtac2.jpg" border="0">
                    <img src="images/logo_truemove.jpg" border="0">
            <table width="80%">
                <tr>
                    <td><img src="images/logo_sun_small_sdn.gif" border="0"></td>
                    <td><img src="images/logo_java.jpg" border="0"></td>
                    <td><img src="images/logo_glassfish.gif" border="0"></td>
                    <td><img src="images/logo_netbeans.jpg" border="0"></td>
                    <td><img src="images/logo_mysql.jpg" border="0"></td>
                    <td><img src="images/logo_phpmyadmin.jpg" border="0"></td>
                </tr>
            </table>
            <table width="50%">
                <tr>
                    <td><img src="images/logo_ais.jpg" border="0"></td>
                    <td><img src="images/logo_dtac.jpg" border="0"></td>
                    <td><img src="images/logo_dtac2.jpg" border="0"></td>
                    <td><img src="images/logo_truemove.jpg" border="0"></td>
                </tr>
            </table>
        </div>-->
        <div class="txt10" style="text-align:center;width:100%;">
            <hr class="line_thin05g" style="margin-left:50px;margin-right:50px;">
            <table width="100%" class="copyright">
                <tr><td align="center"><%=companyProfile.getCompanyProfile().getConfig().get("copyright")%><br>
                        <%=companyProfile.getCompanyProfile().getConfig().get("tel")%></td></tr>
            </table>
        </div>
    </body>
</html>
