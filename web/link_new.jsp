<%-- 
    Document   : link_new
    Created on : Jun 14, 2010, 2:23:14 PM
    Author     : ITZONE
--%>

<%@page import="hippoping.smsgw.api.db.HybridConfig"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.OperConfig" %>
<%@page import="hippoping.smsgw.api.db.HybridConfig" %>
<%@page import="java.util.List" %>
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
            String _register_url = "";
            String _unregister_url = "";
            String _sub_stat_url = "";
            String _sms_link_url = "";
            String _mms_link_url = "";
            String _ivr_link_url = "";
            String _thrd_prty_url = "";
            String _thrd_prty_auth = "";

            String _sftp_cust = "";
            String _sftp_host = "";
            String _sftp_port = "22";
            String _sftp_user = "";
            String _sftp_password = "";
            String _sftp_remote_dir = "";
            
            HybridConfig _hb = null;
            List<HybridConfig> hblist = HybridConfig.getAll();

            OperConfig lc = null;
            if (_cmd.equals("edit")) { // edit link
                lc = new OperConfig(Integer.parseInt(_conf_id));

                // inquiry service information
                _conf_name = lc.conf_name;
                _user = lc.user;
                _password = lc.password;
                _register_url = lc.register_url;
                _unregister_url = lc.unregister_url;
                _sub_stat_url = lc.sub_stat_url;
                _sms_link_url = lc.sms_link_url;
                _mms_link_url = lc.mms_link_url;
                _ivr_link_url = lc.ivr_link_url;
                _thrd_prty_url = lc.thrd_prty_url != null ? lc.thrd_prty_url : "";
                _thrd_prty_auth = lc.thrd_prty_auth != null ? lc.thrd_prty_auth : "";

                // SFTP config
                _sftp_cust = lc.sftp_cust != null ? lc.sftp_cust : "";
                _sftp_host = lc.sftp_host != null ? lc.sftp_host : "";
                _sftp_port = Integer.toString(lc.sftp_port);
                _sftp_user = lc.sftp_user != null ? lc.sftp_user : "";
                _sftp_password = lc.sftp_password != null ? lc.sftp_password : "";
                _sftp_remote_dir = lc.sftp_remote_dir != null ? lc.sftp_remote_dir : "";
                
                // Hybrid config
                _hb = lc.hybrid;
            } else if (_cmd.equals("copy")) { // copy link
                _cmd = "add";

                lc = new OperConfig(Integer.parseInt(_conf_id));

                // inquiry service information
                _conf_name = lc.conf_name;
                _user = lc.user;
                _password = lc.password;
                _register_url = lc.register_url;
                _unregister_url = lc.unregister_url;
                _sub_stat_url = lc.sub_stat_url;
                _sms_link_url = lc.sms_link_url;
                _mms_link_url = lc.mms_link_url;
                _ivr_link_url = lc.ivr_link_url;
                _thrd_prty_url = lc.thrd_prty_url != null ? lc.thrd_prty_url : "";
                _thrd_prty_auth = lc.thrd_prty_auth != null ? lc.thrd_prty_auth : "";

                // SFTP config
                _sftp_cust = lc.sftp_cust != null ? lc.sftp_cust : "";
                _sftp_host = lc.sftp_host != null ? lc.sftp_host : "";
                _sftp_port = Integer.toString(lc.sftp_port);
                _sftp_user = lc.sftp_user != null ? lc.sftp_user : "";
                _sftp_password = lc.sftp_password != null ? lc.sftp_password : "";
                _sftp_remote_dir = lc.sftp_remote_dir != null ? lc.sftp_remote_dir : "";
                
                // Hybrid config
                _hb = lc.hybrid;
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
            <form name="linkFrm" id="foo" method="post" action="LinkListServlet" target="resultFrame"
                  onsubmit="return _onsubmit();">
                <input type='hidden' name='cmd' value='<%=_cmd%>'>
                <input type='hidden' name='conf_id' value='<%=_conf_id%>'>
                <div id="content" style="width:100%">
                    <h2><%=_cmd.toUpperCase()%> link</h2><hr>
                    <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px; padding:0px;">
                        <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                            <tr>
                                <th style="border-bottom:0px">Link Name*</th>
                                <td>
                                    <input class="shorttext" type="text" name="conf_name" value="<%=_conf_name%>">
                                    <div id="conf_name_hidden" style="display:none;color:red">required</div>
                                </td>
                                <td rowspan="3" style="width:45%">
                                    <fieldset style="padding:10px;">
                                        <legend>Hybrid Support:</legend>
                                        <table align="center" style="font-size:medium;width:90%; margin:0; padding:0;">
                                            <tr>
                                                <th width="20%" style="border-bottom:0px">Link:</th>
                                                <td width="80%" style="align:left;">
                                                    <select name="link_hybd_id">
                                                        <option value="0">No Hybrid</option>
                                                        <%
                                                            if (hblist != null && hblist.size() > 0) {
                                                                for (int i = 0; i < hblist.size(); i++) {
                                                                    if (hblist.get(i) == null) {
                                                                        continue;
                                                                    }
                                                                    out.print("<option value='" + hblist.get(i).getLink_hybd_id() + "'"
                                                                            + ((_hb != null && hblist.get(i).getLink_hybd_id() == _hb.getLink_hybd_id()) ? " selected" : "") + ">"
                                                                            + hblist.get(i).getName() + "</option>");
                                                                }
                                                            }
                                                        %>
                                                    </select>
                                                </td>
                                            </tr>
                                        </table>
                                    </fieldset>
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
                                <td colspan="3">
                                    <fieldset>
                                        <legend>Mobile Operator:</legend>
                                        <table style=" font-size: 1.5em;">
                                            <tr>
                                                <th style="border-bottom:0px">Register</th>
                                                <td>
                                                    <input class="mediumtext" type="text" name="register_url" value="<%=_register_url%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">Unregister</th>
                                                <td>
                                                    <input class="mediumtext" type="text" name="unregister_url" value="<%=_unregister_url%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">Status Query</th>
                                                <td>
                                                    <input class="mediumtext" type="text" name="sub_stat_url" value="<%=_sub_stat_url%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">SMS Link</th>
                                                <td>
                                                    <input class="mediumtext" type="text" name="sms_link_url" value="<%=_sms_link_url%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">MMS Link</th>
                                                <td>
                                                    <input class="mediumtext" type="text" name="mms_link_url" value="<%=_mms_link_url%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">IVR Link</th>
                                                <td>
                                                    <input class="mediumtext" type="text" name="ivr_link_url" value="<%=_ivr_link_url%>">
                                                </td>
                                            </tr>
                                        </table>
                                    </fieldset>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="3">
                                    <fieldset>
                                        <legend>Truemove CSS 3rd Party:</legend>
                                        <table style=" font-size: 1.5em;">
                                            <tr>
                                                <th style="border-bottom:0px">URL</th>
                                                <td>
                                                    <input class="mediumtext" type="text" name="thrd_prty_url" value="<%=_thrd_prty_url%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">Authentication</th>
                                                <td>
                                                    <input class="mediumtext" type="text" name="thrd_prty_auth" value="<%=_thrd_prty_auth%>">
                                                </td>
                                            </tr>
                                        </table>
                                    </fieldset>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="3">
                                    <fieldset>
                                        <legend>Secure FTP (AIS Legacy / SSS Type L):</legend>
                                        <table style=" font-size: 1.5em;">
                                            <tr>
                                                <th style="border-bottom:0px">Customer ID</th>
                                                <td>
                                                    <input class="shorttext" type="text" name="sftp_cust" value="<%=_sftp_cust%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">Host</th>
                                                <td>
                                                    <input class="shorttext" type="text" name="sftp_host" value="<%=_sftp_host%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">Port</th>
                                                <td>
                                                    <input class="number" type="text" name="sftp_port" value="<%=_sftp_port%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">Username</th>
                                                <td>
                                                    <input class="shorttext" type="text" name="sftp_user" value="<%=_sftp_user%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">Password</th>
                                                <td>
                                                    <input class="shorttext" type="password" name="sftp_password" value="<%=_sftp_password%>">
                                                </td>
                                            </tr>
                                            <tr>
                                                <th style="border-bottom:0px;">Remote Dir</th>
                                                <td>
                                                    <input class="shorttext" type="text" name="sftp_remote_dir" value="<%=_sftp_remote_dir%>">
                                                </td>
                                            </tr>
                                        </table>
                                    </fieldset>
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
