<%-- 
    Document   : link_new
    Created on : Jun 14, 2010, 2:23:14 PM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.MessageMms" %>
<%@page import="hippoping.smsgw.api.db.MessageMmsFactory" %>
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
                    String _id = request.getParameter("id");
                    String _subcmd = request.getParameter("subcmd");

                    String _subject = "";
                    String _from = "";

                    MessageMms mms = null;
                    if (_cmd.equals("edit")) { // edit link
                        mms = new MessageMms(Integer.parseInt(_id));

                        // inquiry service information
                        _subject = mms.getSubject();
                        _from = mms.getFrom();
                    } else if (_cmd.equals("submit")) {
                        String encoding = "UTF-8";
                        if (request.getCharacterEncoding() != null) {
                            encoding = request.getCharacterEncoding();
                        }
                        _subject = request.getParameter("subject");
                        _from = request.getParameter("from");
                        if (_subject != null && !_subject.isEmpty() && _from != null && !_from.isEmpty()) {
                            _subject = new String(_subject.getBytes("ISO8859_1"), encoding);
                            _from = new String(_from.getBytes("ISO8859_1"), encoding);
                            if (_subcmd != null && _subcmd.equals("add")) {
                                // return new ID to parent window
                                out.println("<script>window.returnValue = '" + new MessageMms().add(_subject, _from, 0) + "';//alert('window.returnValue=' + window.returnValue);</script>");
                            } else if (_subcmd != null && _subcmd.equals("edit") && _id != null) {
                                MessageMmsFactory.remove(Integer.parseInt(_id));
                                mms = new MessageMms(Integer.parseInt(_id));
                                mms.setSubject(_subject);
                                mms.setFrom(_from);

                                mms.sync();
                            }
                        }
                        out.println("<script>window.close();</script>");
                    }
        %>
        <script type="text/javascript">

            function _onsubmit() {
                var frm = document.forms["mmsNewFrm"];

                if (frm.subject.value.trim() == '') {
                    alert("Please enter subject!!");
                    frm.subject.select();
                    frm.subject.focus();
                    return false;
                }

                if (frm.from.value.trim() == '') {
                    alert("Please enter from!!");
                    frm.from.select();
                    frm.from.focus();
                    return false;
                }

                // close after submit
                //window.close();
                var params = 
                    "&subject=" + frm.subject.value
                    + "&from=" + frm.from.value;
                var url = "mms_new.jsp?cmd=submit&subcmd=" + frm.subcmd.value;
                if (frm.subcmd.value == 'edit') {
                    url += '&id=' + frm.id.value;
                }
                //alert(url + params);
                window.returnValue = window.showModalDialog(url + params);
                //alert(window.returnValue);

                // close after submit
                window.close();
                
                // test
                return false;
            }

            window.onload=function() {
                if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
            }
        </script>
    </head>
    <body class="content">
        <div id="mmsNewEdit" style="width:100%;">
            <form name="mmsNewFrm" id="foo" method="post" action="MmsListServlet"
                  onsubmit="return _onsubmit();">
                <input type='hidden' name='cmd' value='submit'>
                <input type='hidden' name='subcmd' value='<%=_cmd%>'>
                <input type='hidden' name='id' value='<%=_id%>'>
                <div id="content" style="width:100%">
                    <h2><%=_cmd.toUpperCase()%> MMS</h2><hr>
                    <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px; padding:0px;">
                        <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                            <tr>
                                <th style="border-bottom:0px;">Subject:</th>
                                <td>
                                    <input type="text" name="subject" style="width:300px;" value="<%=_subject%>">
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;">From:</th>
                                <td>
                                    <input type="text" name="from" style="width:300px;" value="<%=_from%>">
                                </td>
                            </tr>
                        </table>
                        <div style="margin: 20px 0 20px 50px;">
                            <input id="submit" type="submit" class="button" value="Submit">
                            <input id="cancel" type="reset" class="button" value="Cancel" onclick="window.close()">
                        </div>
                    </div> 
                </div>
            </form>
        </div>
    </body>
</html>
