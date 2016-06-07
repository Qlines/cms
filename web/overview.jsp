<%--
    Document   : overview
    Created on : 25 ต.ค. 2552, 11:03:56
    Author     : nack_ki
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
        <style type="text/css">
            body{margin:0px; padding: 0px; background: white;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding-bottom:1em}
            h2{padding-top: 0.3em}
        </style>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script type="text/javascript">

            var tabs = new Array('SYSTEM', 'AIS', 'DTAC', 'TRUEMOVE');
            var titles = new Array('System', 'AIS', 'DTAC', 'Truemove');
            var links = new Array('./overview_system.jsp', './overview_ais.jsp', './overview_dtac.jsp', './overview_truemove.jsp');

            function changeTab(tabs, showid, len)
            {
                for ( var i=0; i<len ; i++ ) {
                    if (i!=showid) {
                        getElement(tabs[i]).className = "";
                    }
                    else {
                        window.open(links[i], 'iframediv');
                        getElement(tabs[i]).className = "activelink";
                    }
                }
            }

            window.onload=function(){
                qs();
                // show tab page
                var showpage = tabs[0];
                if (qsParm["page"]) showpage = qsParm["page"];
                changeTab(tabs, findArrayIndex(tabs, showpage), tabs.length);

                if(NiftyCheck())RoundedTop("div#nav li","transparent","#BEFF9A");
            }
        </script>
    </head>
    <body class="content">
        <script>
            genMenu(tabs, titles, tabs.length);
        </script>
        <div class='contentbox' style='overflow:hidden; height:auto; padding: 15px 0 0 20px;'>
            <iframe name='iframediv' height="500" width="732" frameborder="0" style="overflow:auto;"></iframe>
        </div>
    </body>
</html>
