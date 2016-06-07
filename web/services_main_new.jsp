<%-- 
    Document   : services_new
    Created on : 10 ธ.ค. 2552, 11:21:50
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="serviceBean" scope="page" class="smsgateway.webadmin.bean.ServiceBean" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>New Service Name</title>
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
            div#memberViewContent {background: #377CB1;}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
<%
        String _srvc_main_id = request.getParameter("srvc_main_id");
%>
        <script type='text/javascript'>
            var _allServiceCharge = '<%=serviceBean.findServiceCharge()%>';

            function services_new_onload() {
                var frm = document.servicesNewFrm;
                updateServiceOptions(_allServiceCharge, frm.srvcchrg);
                frm.srvcchrg.selectedIndex = 1;

                if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
            }

        </script>
    </head>
    <body class="content">
        <form name="servicesNewFrm" method="post" enctype="multipart/form-data" action="ServicesManageServlet">
            <div id="content" style="width:100%;">
                <h2>Create Service Name</h2><hr>
                <div id="content2" style="width:60%; text-align:center; background-color:#FFF; margin:10px 0 0 10px;">
                    <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                        <tr>
                            <th style="border-bottom:0px;">Service Name :</th>
                            <td width="70%">
                                <input type="text" name="new_service" style="width:300px;">
                            </td>
                        </tr>
                        <tr>
                            <th style="border-bottom:0px;">Price :</th>
                            <td>
                                <input type="text" name="price" value="0" style="width:40px;"> baht
                            </td>
                        </tr>
                        <tr>
                            <th style="border-bottom:0px;">Charge period :</th>
                            <td>
                                <input type="text" name="chrg_amnt" value="30" style="width:25px;">
                                <script>
                                    document.write(createOptions(null, null, 'srvcchrg', false, 0));
                                </script>
                            </td>
                        </tr>
                        <tr>
                            <th style="border-bottom:0px;">Warning Message :</th>
                            <td>
                                <input type="text" name="warn_cnt" value="2" style="width:40px;"> time(s)
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
        <script>
            services_new_onload();
        </script>
    </body>
</html>
