<%-- 
    Document   : block_list
    Created on : May 19, 2010, 1:04:40 AM
    Author     : ITZONE
--%>



<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
            body{margin:0px; padding: 0px; background: white;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding-bottom:1em}
            h2{padding-top: 0.3em}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <script type="text/javascript">
            /**
             var now = new Date();
             
             var dd = now.getDate();
             var mm = now.getMonth();
             var yy = now.getFullYear();
             **/

            function _onsubmit() {
                /**
                 var frm = document.forms["blockedListFrm"];
                 
                 // Date validate
                 if (!isValidDate(frm.fday.value, frm.fmonth.value ,frm.fyear.value)) {
                 alert('From date is not valid.');
                 frm.fday.focus();
                 return false;
                 }
                 if (!isValidDate(frm.tday.value, frm.tmonth.value ,frm.tyear.value)) {
                 alert('To date is not valid.');
                 frm.tday.focus();
                 return false;
                 }
                 
                 
                 frm.fdate.value = frm.fyear.value + "-" + frm.fmonth.value + "-" + frm.fday.value;
                 frm.tdate.value = frm.tyear.value + "-" + frm.tmonth.value + "-" + frm.tday.value;
                 **/

                // leave it when production
                //alert(frm.fdate.value + " : " + frm.tdate.value);
                //return false;
                // check mobile format
                var frm = document.forms["blockedListFrm"];
                frm.msisdn.value = frm.msisdn.value.replace(/(\r\n|[\r\n])/g, ';');
                if (frm.msisdn.value.replace(/\;/g, '').trim().length == 0) {
                    alert('Please insert at lease one mobile number!!');
                    frm.msisdn.focus();
                    return false;
                }

                var msisdn = frm.msisdn.value;
                // replace 08XXXXXXXX with 668XXXXXXXX
                if ((msisdn.indexOf('08', 0) == 0)
                        || (msisdn.indexOf('09', 0) == 0)
                        || (msisdn.indexOf('06', 0) == 0)) {
                    msisdn = msisdn.replace('0', '66');
                }

                if (!isMsisdn(msisdn)) {
                    alert('You have enter wrong mobile number "' + msisdn + '"\nPlease insert in format 66[689]XXXXXXXX!!');
                    return false;
                }

                frm.msisdn.value = msisdn;
            }

            window.onload = function() {

                /**
                 var frm = document.forms["blockedListFrm"];
                 
                 frm.fdate.value = frm.fyear.value + "-" + frm.fmonth.value + "-" + frm.fday.value;
                 frm.tdate.value = frm.tyear.value + "-" + frm.tmonth.value + "-" + frm.tday.value;
                 frm.submit();
                 **/

                if (NiftyCheck())
                    Rounded("div#pushMessageHistory", "#C0CDF2", "#FFF");
            }
        </script>
    </head>
    <body class="content" style="padding:10px;">
        <div id="pushMessageHistory" style="width:100%; height:500px; background-color:#FFF;">
            <form target="resultFrame" id="blockedListFrm" method="post" action="BlockedList" onsubmit="return _onsubmit();">
                <input type="hidden" name="cmd" value="refresh">
                <input type="hidden" name="page" value="1">
                <input type="hidden" name="orderby" value="create_dt">
                <input type="hidden" name="fdate" value="">
                <input type="hidden" name="tdate" value="">
                <div style="padding: 5px 10px 5px 10px;">
                    <h2 class="block">Blocked List</h2><hr>
                    <table class="table4" style="width:100%;">
                        <tr>
                            <th style="border-bottom:0px;"><b>Search MSISDN:</b>
                                <input type="text" name="msisdn" maxlength="11" onkeypress='return filter_digit_char(event, "!#^%$*()[],+.~`_-\\/")'>
                                | Blocked:
                                <script>
                                    document.write(createOptions(null, null, 'blocked', false, 0));
                                    var obj = getElement('blocked');
                                    addOption(obj, 0, 'Any', true);
                                    addOption(obj, 1, 'Blocked', false);
                                    addOption(obj, 2, 'Unblocked', false);
                                </script>
                            </th>
                            <th align="right" style="border-bottom:0px;">Show:
                                <input type="text" name="rows" value="15" style="width:40px;" onkeypress='return filter_digit_char(event)'>
                                Row(s)/Page
                                <input type="submit" class="button" value="Search">
                                <input type="reset" class="button" value="Clear"></th>
                        </tr>
                        <!--
                        <tr>
                            <th style="border-bottom:0px;" colspan="2">From:
                                <script>
                                    showDate(dd, "fday");
                                    document.write(" ");
                                    showMonth(mm, "fmonth");
                                    document.write(" ");
                                    showYear(yy, "fyear");
                                </script>
                                To:
                                <script>
                                    showDate(dd, "tday");
                                    document.write(" ");
                                    showMonth(mm, "tmonth");
                                    document.write(" ");
                                    showYear(yy+1, "tyear");
                                </script>
                            </th>
                        </tr>
                        -->
                    </table>
                </div>
            </form>
            <iframe name="resultFrame" frameborder="0" style="overflow:auto; padding:0;width:100%;height:100%"></iframe>
        </div>
    </body>
</html>
