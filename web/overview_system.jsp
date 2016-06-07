<%-- 
    Document   : overview_dtac
    Created on : 25 ต.ค. 2552, 22:35:52
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="inetbean" scope="page" class="smsgateway.webadmin.bean.InetBean" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src="./js/datetime.js" type="text/javascript"></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <style type="text/css">
            body{margin:0px; padding: 0px; background: white;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding-bottom:1em}
            h2{padding-top: 0.3em}
        </style>
        <script type="text/javascript">
            var tick, tick2;
            var MonthArry = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
            var _datetime = '<%=inetbean.getCurrentDatetime("MMddHHmmyyyyss")%>';

            var _year = _datetime.charAt(8) + _datetime.charAt(9) + _datetime.charAt(10) + _datetime.charAt(11);
            var _month = _datetime.charAt(0) + _datetime.charAt(1);
            _month=eval(_month + '-' + '1' );
            var _date = _datetime.charAt(2) + _datetime.charAt(3);
            var _hour = _datetime.charAt(4) + _datetime.charAt(5);
            var _min = _datetime.charAt(6) + _datetime.charAt(7);
            var _sec = _datetime.charAt(12) + _datetime.charAt(13);

            var ut=new Date(_year,_month,_date,_hour,_min,_sec);

            function usnotime()
            {
                var h,m,s;
                var d,mth,y;
                var time="  ";
                var obj = getElement('timebox');

                s=ut.setSeconds(eval(ut.getSeconds()+'+'+'1'));
                y=ut.getFullYear().toString();
                mth=ut.getMonth();
                d=ut.getDate();
                h=ut.getHours();
                m=ut.getMinutes();
                s=ut.getSeconds();
                if(s<=9) s="0"+s;
                if(m<=9) m="0"+m;
                if(h<=9) h="0"+h;
                //time+=d+" / "+MonthArry[mth]+" / "+y +" ";
                time+=d + "/"+ (mth+1) + "/" + y + " ";
                time+=h+":"+m+":"+s;
                obj.innerHTML = time;
                tick=setTimeout("usnotime()",1000);
            }

            function synctime()
            {
                var objHTTP;
                if (browserType == "gecko" ){
                    objHTTP = new XMLHttpRequest();
                    objHTTP.open('GET',"current_datetime.jsp",false);
                }else{
                    objHTTP = new ActiveXObject('Microsoft.XMLHTTP');
                    objHTTP.open('POST',"current_datetime.jsp",false);
                    objHTTP.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                }

                objHTTP.send(null);
                _datetime=objHTTP.responseText.toString().trim();

                _year = _datetime.charAt(8) + _datetime.charAt(9) + _datetime.charAt(10) + _datetime.charAt(11);
                _month = _datetime.charAt(0) + _datetime.charAt(1);
                _month=eval(_month + '-' + '1' );
                _date = _datetime.charAt(2) + _datetime.charAt(3);
                _hour = _datetime.charAt(4) + _datetime.charAt(5);
                _min = _datetime.charAt(6) + _datetime.charAt(7);
                _sec = _datetime.charAt(12) + _datetime.charAt(13);

                ut=new Date(_year,_month,_date,_hour,_min,_sec);
                tick2=setTimeout("synctime()",300000);
            }

            window.onload=function(){
                if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
                usnotime();
                tick2=setTimeout("synctime()",10000);
            }

            var DAYSEC = 24 * 60 * 60;
            var HOURSEC = 60 * 60;
            var MINSEC = 60;
            var uptime = parseInt((<%=inetbean.getJvmUptime()%>) / 1000);
            var up_sec = uptime % MINSEC;
            var up_min = parseInt(((uptime - up_sec) % HOURSEC) / MINSEC);
            var up_hour = parseInt(((uptime - up_sec + up_min*MINSEC) % DAYSEC) / HOURSEC);
            var up_day = parseInt(uptime / DAYSEC);
        </script>
    </head>
    <body class="content">
        <div id="content" style="width:60%;">
            <h2>System Status</h2><hr>
            <div id="content2" style="width:90%; text-align:center; background-color:#FFF; margin:10px 0 0 10px;">
                <table width="90%" class="table4" style="margin:0; padding:10px;">
                    <tr>
                        <th width="50%">Current datetime :</th>
                        <td width="70%" style="text-align: right; vertical-align: bottom"><span id='timebox'><%=inetbean.getCurrentDatetime()%></span></td>
                    </tr>
                    <tr>
                        <th>Startup time :</th>
                        <td style="text-align: right; vertical-align: bottom"><%=inetbean.getJvmStartUptime()%></td>
                    </tr>
                    <tr>
                        <th>System uptime :</th>
                        <td style="text-align: right; vertical-align: bottom">
                            <script>document.write(up_day + " d. " + up_hour + " hr " + up_min + ":" + up_sec + " min(s)");</script>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </body>
</html>
