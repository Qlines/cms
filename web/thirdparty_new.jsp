<%-- 
    Document   : link_new
    Created on : Jun 14, 2010, 2:23:14 PM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.ThirdPartyConfig" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
            input.shorttext{width:150px}
            input.longtext{width:300px;}
            input.number{width:50px;}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
                    String _cmd = request.getParameter("cmd");
                    String _conf_id = request.getParameter("conf_id");

                    String _cpny_name = "";
                    String _url = "";
                    String _auth_type = "";
                    String _user = "";
                    String _password = "";
                    String _method = "";

                    ThirdPartyConfig tpc = null;
                    if (_cmd.equals("edit")) { // edit link
                        tpc = new ThirdPartyConfig(Integer.parseInt(_conf_id));

                        // inquiry service information
                        _cpny_name = tpc.getCompany_name();
                        _url = tpc.getUrl();
                        _auth_type = tpc.getAuth_type();
                        _user = tpc.getUser();
                        _password = tpc.getPassword();
                        _method = tpc.getMethod();
                    } else if (_cmd.equals("copy")) { // copy link
                        _cmd = "add";

                        tpc = new ThirdPartyConfig(Integer.parseInt(_conf_id));

                        // inquiry service information
                        _cpny_name = tpc.getCompany_name();
                        _url = tpc.getUrl();
                        _auth_type = tpc.getAuth_type();
                        _user = tpc.getUser();
                        _password = tpc.getPassword();
                        _method = tpc.getMethod();
                    }
        %>
        <script type="text/javascript">
            function _onsubmit() {
                var frm = document.forms["linkFrm"];
                hide2('conf_name_hidden');
                hide2('user_hidden');
                hide2('password_hidden');
                if (frm.url.value == '') {
                    show2('url_hidden');
                    frm.url.focus();
                    return false;
                }

                if (!frm.user.disabled && frm.user.value == '') {
                    show2('user_hidden');
                    frm.user.focus();
                    return false;
                }

                if (!frm.password.disabled && frm.password.value == '') {
                    show2('password_hidden');
                    frm.password.focus();
                    return false;
                }

                if (!isUrl(frm.url.value)) {
                    alert("Invalid URL format!!");
                    frm.url.select();
                    frm.url.focus();
                    return false;
                }

                // close after submit
                window.close();
            }

            function auth_type_onclick() {
                getElement('user').disabled = (getElement('type1').checked)?true:false;
                getElement('password').disabled = (getElement('type1').checked)?true:false;
            }
            
            function show_method(obj) {
                var methods = new Array(2); // create Array 2 dimensions
                methods[0] = new Array("POST", "POST");
                methods[1] = new Array("GET", "GET");

                // Add options to dropdownlist
                addOption_list(obj, methods);
                obj.selectedIndex = ("<%=_method%>" == "POST") ? 0: 1;
            }

            window.onload=function() {
                var frm = document.forms["linkFrm"];
                show_method(frm.method);
                auth_type_onclick();
                
                if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
            }
        </script>
    </head>
    <body class="content">
        <div id="servicesSubEdit" style="width:100%;">
            <form name="linkFrm" id="linkFrm" method="post" action="ThirdPartyListServlet" target="resultFrame"
                  onsubmit="return _onsubmit();">
                <input type='hidden' name='cmd' value='<%=_cmd%>'>
                <input type='hidden' name='conf_id' value='<%=_conf_id%>'>
                <div id="content" style="width:100%">
                    <h2><%=_cmd.toUpperCase()%> 3rd Party</h2><hr>
                    <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px; padding:0px;">
                        <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                            <tr>
                                <th style="border-bottom:0px;">3rd Party Name</th>
                                <td>
                                    <input class="longtext" type="text" name="conf_name" maxlength="64" value="<%=_cpny_name%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">URL*</th>
                                <td>
                                    <input class="longtext" type="text" name="url" maxlength="200" value="<%=_cmd.equals("add")?"http://":_url%>" onkeypress="return filter_url_ip_char(event, '/-_:?&=$}{')">
                                    <div id="url_hidden" style="display:none;color:red">required</div>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Method</th>
                                <td>
                                    <script>document.write(createOptions(null, null, 'method', false, 0));</script>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Authenticate</th>
                                <td>
                                    <input type="radio" name="auth_type" id="type1" value="None" onclick="auth_type_onclick();" <%=_auth_type == null || _auth_type.toLowerCase().equals("none") || _cmd.equals("add")?"checked":""%>>
                                    <label for="type1"><span style="vertical-align:baseline;">None</span></label><br>
                                    <input type="radio" name="auth_type" id="type2" value="Basic" onclick="auth_type_onclick();" <%=_auth_type != null && _auth_type.toLowerCase().equals("basic")?"checked":""%>>
                                    <label for="type2"><span style="vertical-align:baseline;">Basic</span></label><br>
                                    <input type="radio" name="auth_type" id="type3" value="Digest" onclick="auth_type_onclick();" <%=_auth_type != null && _auth_type.toLowerCase().equals("digest")?"checked":""%> disabled>
                                    <label for="type3"><span style="vertical-align:baseline;color: #666;">Digest</span></label><br>
                                    <input type="radio" name="auth_type" id="type4" value="Client" onclick="auth_type_onclick();" <%=_auth_type != null && _auth_type.toLowerCase().equals("client")?"checked":""%> disabled>
                                    <label for="type4"><span style="vertical-align:baseline;color: #666;">Client Certificated</span></label>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">User</th>
                                <td>
                                    <input class="shorttext" type="text" name="user" id="user" maxlength="32" value="<%=_user%>">
                                    <div id="user_hidden" style="display:none;color:red">required</div>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">Password</th>
                                <td>
                                    <input class="shorttext" type="text" name="password" id="password" maxlength="32" value="<%=_password%>">
                                    <div id="password_hidden" style="display:none;color:red">required</div>
                                </td>
                            </tr>
                            <tr><td colspan="2" style="margin-top:10px">* required parameter</td></tr>
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
