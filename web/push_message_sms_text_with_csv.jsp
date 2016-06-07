<%-- 
    Document   : push_message_text
    Created on : 12 มี.ค. 2553, 12:47:23
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.SERVICE_STATUS" %>
<%@page import="hippoping.smsgw.api.db.TxQueue" %>
<%@page import="hippoping.smsgw.api.db.MessageSms" %>
<%@page import="lib.common.DatetimeUtil" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <link rel="stylesheet" type="text/css" href="./css/dashboard.css" media="screen" />
        <link href="./css/infobox01.css" rel="stylesheet" type="text/css">
        <style type="text/css">
            body{margin:0px; padding: 0px; background: white;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding:0}
            p.tt{color:#EEE;font-weight:bold;padding-bottom:2px}
            span.vl{color:yellow;font-weight:normal;padding-left:5px}
            h2{padding-top: 0.3em}
            table.file_preview{border:solid 1px; margin:0; padding: 0; font-size: 1.2em}
            table.file_preview th{border:0; background:#ffcc99; color: #fff; font-size: 1.1em; font-style: bold; margin:0; padding: 0 5px; vertical-align:top; line-height: 1.5em}
            table.file_preview td{border:0; border-left:solid 1px; margin:0; padding: 0 5px; vertical-align:top; line-height: 1.5em}
            hr{border:none; height:solid 1px}
        </style>
        <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
            String _srvcid = "";
            String _operid = "";

            int srvc_type = SERVICE_TYPE.SMS.getId() | SERVICE_TYPE.SUBSCRIPTION.getId();
            int srvc_status = SERVICE_STATUS.ON.getId() | SERVICE_STATUS.TEST.getId();
        %>
        <jsp:include page="./services_bean.jsp">
            <jsp:param name="srvc_type" value="<%=srvc_type%>" />
            <jsp:param name="srvc_status" value="<%=srvc_status%>" />
        </jsp:include>
        <script type="text/javascript">
            function updateServiceOptions(oper_id, obj) {
                var frm = document.pushMessageSmsTextWithCsvFrm;
                removeAllOptions(obj);

                if (!optionsArry[oper_id]) {
                    var tmpArry = new Array(); // create Array 2 dimensions
                    tmpArry[0] = new Array(2);
                    tmpArry[0][0] = "0";
                    tmpArry[0][1] = "No service available";

                    addOption_list(obj, tmpArry);
                    frm.srvc_main_id.disabled = true;
                }
                else {
                    if ("<%=_srvcid%>" == "")
                        addOption_list(obj, optionsArry[oper_id]);
                    else
                        addOption_list(obj, optionsArry[oper_id], "<%=_srvcid%>");
                    frm.srvc_main_id.disabled = false;
                }
            }

            function updateOperSelect(id) {
                obj = getElement('operSelectDiv');
                var str = "";
                for (var i = 1; i < operIdArry.length; i++) {
                    str += "<input type='checkbox' name='oper' id='oper" + operIdArry[i] + "'"
                            + ((isServiceInOper(id, operIdArry[i]))
                                    ? (("<%=_operid%>" == "" || ("<%=_operid%>" != "" && "<%=_operid%>" == i)) ? " checked" : "")
                                    : " disabled")
                            + "><label for='oper" + operIdArry[i] + "'>" + operNameArry[i] + "</label> ";
                }
                obj.innerHTML = str;
            }

            function whichOper() {
                for (i in operIdArry) {
                    var obj = getElement('oper' + i);
                    if (obj && !obj.disabled && obj.checked) {
                        return i;
                    }
                }
            }

            function getSelectedOper() {
                var tmp = "";
                for (i in operIdArry) {
                    var obj = getElement('oper' + i);
                    if (obj && !obj.disabled && obj.checked) {
                        if (tmp.length > 0)
                            tmp += "|";
                        tmp += operNameArry[i];
                    }
                }

                return tmp;
            }

            function getServiceDetails(srvc_main_id, oper_id) {
                var url = "service_details.jsp?srvc_main_id=" + srvc_main_id + "&oper_id=" + oper_id;
                var obj = getElement('service_info_box');
                if (obj)
                    obj.innerHTML = ajaxPost(url);
            }

            function _onsubmit() {
                var frm = document.pushMessageSmsTextWithCsvFrm;

                // summary oper ID
                var oper_id = 0;
                for (var i = 1; i < operIdArry.length; i++) {
                    oper_id += (getElement('oper' + i)) ? (getElement('oper' + i).checked ? Math.pow(2, operIdArry[i]) : 0) : 0;
                }
                if (oper_id === 0) {
                    alert('Please select at least 1 operator!');
                    return false;
                }
                frm.operid.value = oper_id;
                frm.remove_header.value = remove_header ? 1 : 0;

                // leave it out when production
                //alert(frm.deliver_dt.value);
                //return false;
                return true;
            }

            window.onload = function() {
                var frm = document.pushMessageSmsTextWithCsvFrm;
                updateServiceOptions(0, frm.srvc_main_id);

                if (NiftyCheck())
                    RoundedTop("div#content_header", "#C0CDF2", "#FFF");
                if (NiftyCheck())
                    RoundedBottom("div#content", "#C0CDF2", "#377CB1");
                if (NiftyCheck())
                    Rounded("div#content2", "#377CB1", "#FFF");

                var obj = getElement('srvc_main_id');
                updateOperSelect(obj.value);
                if (!obj.disabled) {
                    getServiceDetails(obj.value, whichOper());
                }
                frm.submit.disabled = obj.disabled;
                frm.cancel.disabled = obj.disabled;

                obj.onchange = function() {
                    updateOperSelect(obj.value);
                    getServiceDetails(obj.value, whichOper());
                }

                line_delimiter = "\r";
                field_delimiter = ",";
                remove_header = $("#csv_header").prop("checked");
            }

            /* CSV parameter*/
            var line_delimiter = "\r";
            var field_delimiter = ",";
            var field_encap = "";
            var remove_header = true;
            var max_preview_line = 10;

            var file_content = null;

            function update_preview() {

                if (!file_content) {
                    $("#csvimporthint").html("");
                    $("#csvimporthinttitle").html("");
                    return;
                }
                var lines = file_content.split(line_delimiter);

                var html = "<table class='file_preview' border=1>";
                var exact_line = 0;
                for (l in lines) {
                    if (lines[l].trim() == "") continue;
                    var cols = lines[l].splitCsv(field_delimiter, field_encap);
                    if (remove_header && l == 0)
                        continue;
                    if (cols[0] && cols[0].trim() == "")
                        continue;

                    exact_line++;
                    if (l > max_preview_line)
                        continue;

                    html += "<tr>";
                    html += "<th>" + exact_line + ": </th>";

                    for (f in cols) {
                        var tmp = cols[f];
                        if (tmp.trim() == '') break;
                        
                        // formation
                        var pass=true;
                        var error;
console.log(f);
                        switch (f) {
                            case '0': // datetime
                                var dt = new DateTimeChecker(tmp.trim());
console.log("Tmp: " + tmp.trim());
console.log("Type: " + dt.getType());
console.log("Pattern: " + dt.getPattern());
console.log("Validation: " + dt.isValidate());
console.log("Status: " + dt.getError());

                                error = dt.getError();
                                pass = (dt.getType() === 'a' && error === 'OK');
                                break;
                            case '1': // message
                                if (isEnglishText(tmp)) {
console.log("English text " + tmp.trim().length + " characters.");
                                    if (tmp.length > 160) {
                                        pass=false;
                                        error='Message length is exceed the maximum length(160 characters)';
                                    }
                                } else {
console.log("Other language text " + tmp.trim().length + " characters.");
                                    if (tmp.length > 140) {
                                        pass=false;
                                        error='Message length is exceed the maximum length(140 characters)';
                                    }
                                }
                                    
                                break;
			    default:
				error = '';
				pass = false;
                        }
console.log("");
                        
                        html += "<td style='background:" + (f<2?(pass?"#ccffcc":"#ff6666"):"") + "' title='" + error + "'>" + tmp + "</td>";
                    }
                    html += "</tr>";
                }
                html += "</table>";

                var title = "found " + exact_line + " line(s).";

                $("#csvimporthint").html(html + (exact_line > max_preview_line ? "<br/>..." : ""));
                $("#csvimporthinttitle").html(title);

            }

            $(document).ready(function() {
                $("#csv_header").change(function() {
                    remove_header = $(this).is(":checked");
                    console.log(remove_header);

                    if (file_content)
                        update_preview();
                });

                $("input[name=line_delimiter]").change(function() {
                    var val = $("input[name=line_delimiter]:checked").val();
                    console.log(val);
                    switch (val) {
                        case '0':
                            line_delimiter = "\r\n";
                            break;
                        case '1':
                            line_delimiter = "\r";
                            break;
                        case '2':
                            line_delimiter = "\n";
                            break;
                    }

                    if (file_content)
                        update_preview();
                });

                $("input[name=field_delimiter]").change(function() {
                    var val = $("input[name=field_delimiter]:checked").val();
                    console.log(val);
                    switch (val) {
                        case '0':
                            field_delimiter = ",";
                            break;
                        case '1':
                            field_delimiter = "\t";
                            break;
                        case '2':
                            field_delimiter = $('#fdx_value').val();
                            break;
                    }

                    if (file_content)
                        update_preview();
                });

                $("input[name=field_encap]").change(function() {
                    var val = $("input[name=field_encap]:checked").val();
                    console.log(val);
                    switch (val) {
                        case '0':
                            field_encap = "";
                            break;
                        case '1':
                            field_encap = "'";
                            break;
                        case '2':
                            field_encap = "\"";
                            break;
                    }

                    if (file_content)
                        update_preview();
                });

                $("#filename").change(function(e) {
                    var ext = $("input#filename").val().split(".").pop().toLowerCase();

                    if ($.inArray(ext, ["csv", "txt"]) == -1) {
                        alert('Please upload CSV or Text file');
                        file_content = null;
                        update_preview();
                        return false;
                    }

                    if (e.target.files != undefined) {
                        var reader = new FileReader();
                        reader.onload = function(e) {
                            file_content = e.target.result;
                            update_preview();
                        };

                        reader.readAsText(e.target.files.item(0));
                    }
                    return false;
                });
            });

        </script>
    </head>
    <body class="content">
        <div id="pushMessageSmsText" style="width:100%;">
            <form target="ctFrame" name="pushMessageSmsTextWithCsvFrm" ENCTYPE="multipart/form-data" method="POST" action="PushMessageSmsWithCsvServlet"
                  onsubmit="return _onsubmit()">
                <input type="hidden" name="type" value="sms" >
                <input type="hidden" name="operid" value="">
                <input type="hidden" name="remove_header" value="1" >
                <div id="content_header" style="width:75%; background-color:#FFF">
                    <h2 class="smstext">Text Message with text file</h2>
                </div>
                <div id="content" style="width:75%;padding-top: 10px;">
                    <div id="content2" style="width:90%; text-align:center; background-color:#FFF; margin:10px 0 0 10px;">
                        <table align="center" class="table4" style="width:90%; margin-top:0px; padding:0;">
                            <tr>
                                <th style="border-bottom:0px;" width="30%">Service Name :</th>
                                <td>
                                    <script>document.write(createOptions(null, null, 'srvc_main_id', false, 0));</script>
                                </td>
                            </tr>
                            <tr>
                                <th style="border-bottom:0px;"></th>
                                <td>
                                    <div id="service_info_box" style="padding:1px;"></div>
                                </td>
                            </tr>
                            <tr>
                                <th>Operator :</th>
                                <td>
                                    <div id="operSelectDiv" class="txt10"></div>
                                </td>
                            </tr>
                            <tr>
                                <th>Upload file (.csv, .txt) :<br/>-- Pattern --<br/>Deliver , Message</th>
                                <td>
                                    <input type="checkbox" name="csv_header" id="csv_header" checked>
                                    <label for="csv_header">remove header</label><br/>
                                    Line delimiter: 
                                    <input type="radio" id="ld1" name="line_delimiter" value="0" checked>
                                    <label for="ld1">CR+LF</label>
                                    <input type="radio" id="ld2" name="line_delimiter" value="1">
                                    <label for="ld2">CR</label>
                                    <input type="radio" id="ld3" name="line_delimiter" value="2">
                                    <label for="ld3">LF</label>
                                    <br/>
                                    Field delimiter: 
                                    <input type="radio" id="fd1" name="field_delimiter" value="0" checked>
                                    <label for="fd1">Comma(,)</label>
                                    <input type="radio" id="fd2" name="field_delimiter" value="1">
                                    <label for="fd2">Tab</label>
                                    <input type="radio" id="fdx" name="field_delimiter" value="2">
                                    <label for="fdx">Other:</label>
                                    <input type="text" id="fdx_value" name="fdx_value" value=":" size=2>
                                    <br/>
                                    Encapsulate with: 
                                    <input type="radio" id="ec1" name="field_encap" value="0" checked>
                                    <label for="ec1">None</label>
                                    <input type="radio" id="ec2" name="field_encap" value="1">
                                    <label for="ec2">Quote(')</label>
                                    <input type="radio" id="ec3" name="field_encap" value="2">
                                    <label for="ec3">Double Quote(")</label>
                                    <br/>
                                    <input type="file" name="filename" id="filename">
                                    <br/><br/><hr/>Preview ::
                                    <div id="csvimporthint"></div>
                                    <div id="csvimporthinttitle"></div>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div style="margin: 20px 0 20px 50px;">
                        <input id="submit" type="submit" class="button" value="Send">
                        <input id="cancel" type="reset" class="button" value="Cancel">
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>