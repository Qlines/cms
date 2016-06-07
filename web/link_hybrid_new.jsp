<%-- 
    Document   : link_new
    Created on : Jun 14, 2010, 2:23:14 PM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.HybridConfig" %>
<%@page import="hippoping.smsgw.api.db.HybridConfig.SGWID" %>
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
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
            String _cmd = request.getParameter("cmd");
            String _conf_id = request.getParameter("conf_id");

            String _conf_name = "";
            String _user = "";
            String _password = "";
            String _url = "";
            SGWID _sgwid = null;

            HybridConfig hb = null;
            if (_cmd.equals("edit")) { // edit link
                hb = new HybridConfig(Integer.parseInt(_conf_id));

                // inquiry service information
                _conf_name = hb.getName();
                _user = hb.getUser();
                _password = hb.getPassword();
                _url = hb.getUrl();
                _sgwid = hb.getSgwid();
            } else if (_cmd.equals("copy")) { // copy link
                _cmd = "add";

                hb = new HybridConfig(Integer.parseInt(_conf_id));

                // inquiry service information
                _conf_name = hb.getName();
                _user = hb.getUser();
                _password = hb.getPassword();
                _url = hb.getUrl();
                _sgwid = hb.getSgwid();
            }
        %>
        <script type="text/javascript">
            function _onsubmit() {
                var frm = document.forms["linkFrm"];
                if (frm.conf_name.value == '') {
                    show2('conf_name_hidden');
                    return false;
                }

                // close after submit
                window.close();
            }

            window.onload=function() {

                if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
            }
        </script>
    </head>
    <body class="content">
        <div id="servicesSubEdit" style="width:100%;">
            <form name="linkFrm" id="foo" method="post" action="HybridLinkServlet" target="hybridResultFrame"
                  onsubmit="return _onsubmit();">
                <input type='hidden' name='cmd' value='<%=_cmd%>'>
                <input type='hidden' name='conf_id' value='<%=_conf_id%>'>
                <div id="content" style="width:100%">
                    <h2><%=_cmd.toUpperCase()%> Hybrid Gateway</h2><hr>
                    <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px; padding:0px;">
                        <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                            <tr>
                                <th style="border-bottom:0px">Hybrid Name*</th>
                                <td>
                                    <input class="shorttext" type="text" name="conf_name" value="<%=_conf_name%>">
                                    <div id="conf_name_hidden" style="display:none;color:red">required</div>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px">User</th>
                                <td>
                                    <input class="shorttext" type="text" name="user" value="<%=_user%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px">Password</th>
                                <td>
                                    <input class="shorttext" type="text" name="password" value="<%=_password%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px">Url</th>
                                <td>
                                    <input class="mediumtext" type="text" name="url" value="<%=_url%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px">SGWID</th>
                                <td>
                                    <select name="sgwid">
                                        <option value="<%=SGWID.AD_IVR.getId()%>"<%=(_sgwid == SGWID.AD_IVR) ? " selected" : ""%>>
                                            <%=SGWID.AD_IVR%></option>
                                        <option value="<%=SGWID.CC_IVR.getId()%>"<%=(_sgwid == SGWID.CC_IVR) ? " selected" : ""%>>
                                            <%=SGWID.CC_IVR%></option>
                                    </select>
                                </td>
                            </tr>
                            <tr><td colspan="3" style="margin-top:10px">* required parameter</td></tr>
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
