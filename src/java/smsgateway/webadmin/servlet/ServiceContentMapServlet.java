package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.MessageMms;
import hippoping.smsgw.api.db.MessageSms;
import hippoping.smsgw.api.db.MessageWap;
import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceContentAction;
import hippoping.smsgw.api.db.ThirdPartyConfig;
import hippoping.smsgw.api.db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.StringConvert;

public class ServiceContentMapServlet extends HttpServlet
{
  private static final Logger log = Logger.getLogger(ServiceContentMapServlet.class.getName());

  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter out = response.getWriter();
    try {
      User user = (User)request.getSession().getAttribute("USER");
      if (user == null) {
        out.print("<script>window.location='logout?msg=Your session has been expired! please relogin the page.'</script>");
      }
      else
      {
        String encoding = "UTF-8";
        if (request.getCharacterEncoding() != null) {
          encoding = request.getCharacterEncoding();
        }

        String srvcid = request.getParameter("srvcid");
        String srvcname = new String(request.getParameter("srvcname").getBytes("ISO8859_1"), encoding);

        if ((srvcid == null) || (srvcid.isEmpty()) || (!StringConvert.isDigit(srvcid)))
        {
          return;
        }
        String cmd = request.getParameter("cmd");

        if ((cmd != null) && (cmd.equals("swap"))) {
          cmd = "refresh";
          try
          {
            int toh = Integer.parseInt(request.getParameter("toh"));
            int tol = Integer.parseInt(request.getParameter("tol"));
            int operid = Integer.parseInt(request.getParameter("operid"));

            Map map = (Map)request.getSession().getAttribute("serviceContentMap");

            List scas = (List)map.get(Integer.valueOf(Integer.parseInt(srvcid)));

            ServiceContentAction toh_sca = (ServiceContentAction)scas.remove(toh);
            if (toh_sca != null) {
              scas.add(tol, toh_sca);
              log.log(Level.INFO, "move higher item ''{0}'' at index(toh) {1} to index(tol) {2}", new Object[] { toh_sca.getKeyword(), Integer.valueOf(toh), Integer.valueOf(tol) });

              log.log(Level.INFO, "verify item index(tol) is ''{0}''", ((ServiceContentAction)scas.get(tol)).getKeyword());
            }

            List order_list = new ArrayList();

            for (int i = 0; i < scas.size(); i++) {
              if (((ServiceContentAction)scas.get(i)).getOper().getId() == operid) {
                order_list.add(scas.get(i));
              }

            }

            ServiceContentAction.reorderPriority(order_list, false);
          }
          catch (Exception e) {
            log.severe(e.getMessage());
          }
        } else if ((cmd != null) && (cmd.equals("cancel"))) {
          cmd = "refresh";
          try
          {
            int _map_id = Integer.parseInt(request.getParameter("mapid"));
            int operid = -1;

            Map map = (Map)request.getSession().getAttribute("serviceContentMap");

            List scas = (List)map.get(Integer.valueOf(Integer.parseInt(srvcid)));

            for (int i = 0; i < scas.size(); i++) {
              if (((ServiceContentAction)scas.get(i)).getId() == _map_id) {
                ServiceContentAction sca = (ServiceContentAction)scas.remove(i);
                operid = sca.getOper().getId();
                sca.remove();
                break;
              }
            }

            if (operid >= OperConfig.CARRIER.ALL.getId())
            {
              List order_list = new ArrayList();

              for (int i = 0; i < scas.size(); i++) {
                if (((ServiceContentAction)scas.get(i)).getOper().getId() == operid) {
                  order_list.add(scas.get(i));
                }

              }

              ServiceContentAction.reorderPriority(order_list, false);
            }
          }
          catch (Exception e) {
            log.severe(e.getMessage());
          }

        }

        if ((cmd != null) && (cmd.equals("refresh"))) {
          srvcid = request.getParameter("srvcid");

          if ((srvcid == null) || (srvcid.equals(""))) {
            srvcid = "-1";
          }

          request.getSession().setAttribute("serviceContentMap", ServiceContentAction.getAll());
        }

        Map map = (Map)request.getSession().getAttribute("serviceContentMap");

        List scas = (List)map.get(Integer.valueOf(Integer.parseInt(srvcid)));

        out.println("<html><head>    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>    <link href='./css/cv.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>    <style type='text/css'>        body{margin:0px; padding: 0px; background: white;            font: 100.01% 'Trebuchet MS',Verdana,Arial,sans-serif}        h1,h2,p{margin: 0 10px}        h1{font-size: 250%;color: #FFF}        h2{font-size: 200%;color: #f0f0f0}        p{padding-bottom:1em}        h2{padding-top: 0.3em}        div#memberViewContent {background: #377CB1;}        table.table3 th {border:solid 1px #FFF; background: #ABCDEF; color: #333;}        .tdoper {         text-align:left;         font-weight:bold;         line-height: .8em;         margin:5px 5px 0 5px        }    </style>    <script src='./js/nifty.js' type='text/javascript'></script>    <script src='./js/utils.js' type='text/javascript'></script>    <script src='./js/filter_input.js' type='text/javascript'></script>    <script>    function doHigher(index, operid) {       var frm = document.forms[\"reloadFrm\"];       frm.action = 'ServiceContentMapServlet';       frm.target = '_self';       frm.cmd.value = 'swap';       frm.operid.value = operid;       frm.toh.value = index;       frm.tol.value = index-1;       frm.submit();    }    function doLower(index, operid) {       var frm = document.forms[\"reloadFrm\"];       frm.action = 'ServiceContentMapServlet';       frm.target = '_self';       frm.cmd.value = 'swap';       frm.operid.value = operid;       frm.toh.value = index+1;       frm.tol.value = index;       frm.submit();    }    function doEdit(mapid, operid) {       var frm = document.forms[\"reloadFrm\"];       frm.action = 'services_content_map_edit.jsp';       frm.target = '_parent';       frm.cmd.value = 'edit';       frm.operid.value = operid;       frm.mapid.value = mapid;       frm.submit();    }    function doCancel(id, msg) {       var frm = document.forms[\"reloadFrm\"];       frm.action = 'ServiceContentMapServlet';       frm.target = '_self';       frm.cmd.value='cancel';       frm.mapid.value=id;       if (!msg) msg='';       else msg = ' \"' + msg + '\"';       if (confirm('Click OK to confirm to remove content map' + msg + ', otherwise click cancel.'))           frm.submit();    }    </script></head><body style='background-color:#FFF;'>   <div id='data' style='padding: 0 10px 0 10px;width:97%;^width:100%;_width:100%;;'>       <form name='reloadFrm' method='POST'>       <input type=hidden name=cmd value=''>       <input type=hidden name=mapid value=''>" + (srvcid != null ? "       <input type=hidden name=srvcid value='" + srvcid + "'>" : "") + "       <input type=hidden name=srvcname value='" + srvcname + "'>" + "       <input type=hidden name=operid value=''>" + "       <input type=hidden name=swap value='0'>" + "       <input type=hidden name=csv value='0'>" + "       <input type=hidden name=toh value='0'>" + "       <input type=hidden name=tol value='0'>" + "       </form>" + "       <table class='table3' style='width:100%;padding:0;margin:0'>" + "       <tr>" + "           <th rowspan='2' width='6%'></th>" + "           <th colspan='2'>Condition</th>" + "           <th colspan='2'>Reply Message/Action</th>" + "           <th rowspan='2' width='2%'>Charge</th>" + "           <th rowspan='2' width='8%'>&nbsp;</th>" + "       </tr><tr>" + "           <th width='20%'>IVR</th>" + "           <th width='20%'>Keyword</th>" + "           <th width='5%'>Type</th>" + "           <th width='35%'>Content</th>" + "       </tr>");

        OperConfig.CARRIER oper = null;
        int subid = 0;

        for (int i = 0; i < scas.size(); i++) {
          boolean showup = true;
          boolean showdown = true;

          ServiceContentAction sca = (ServiceContentAction)scas.get(i);

          subid = i;

          if (oper != sca.getOper()) {
            oper = sca.getOper();
            out.println("<tr><td colspan=7 class='tdoper' style='color:#123456; text-align:left; padding: 5px; border-bottom:dotted 1px #FF8080;'>Operator: " + oper.toString() + "</td></tr>");

            subid = 0;
          }

          if (subid == 0) {
            showup = false;
          }
          if (i != scas.size() - 1) {
            if (oper != ((ServiceContentAction)scas.get(i + 1)).getOper())
              showdown = false;
          }
          else {
            showdown = false;
          }

          String style = subid % 2 == 1 ? " d0" : "";
          style = " class='" + style + "'";
          try
          {
            String message = "";
            String full_message = "";
            String title = "";
            boolean msg_ok = true;

            if ((sca.getMessage() instanceof MessageSms)) {
              MessageSms sms = (MessageSms)sca.getMessage();
              switch (sms.getSmsType()) {
              case TEXT:
                message = sms.getContentSub()[0];
                int maxchar = 40;
                if (message.length() > maxchar) {
                  message = message.substring(0, maxchar) + "...";
                }
                title = message;

                if (sms.getMessage_num() > 1) {
                  message = message + "[concatenated]";
                }

                for (int j = 0; j < sms.getMessage_num(); j++) {
                  full_message = full_message + sms.getContentSub()[j];
                }

                break;
              case PICTURE:
                message = "<img src='header?file=" + sms.getFilename() + "' border='0' title='" + sms.getMessageInfo().getTitle() + "'>";

                title = sms.getMessageInfo().getTitle();

                break;
              case RINGTONE:
                title = sms.getFilename().substring(sms.getFilename().lastIndexOf('/') + 1).trim();
                message = "<img src='images/music_note16.png' border='0' style='vertical-align: top'> " + title;
              }
            }
            else if ((sca.getMessage() instanceof MessageWap)) {
              MessageWap wap = (MessageWap)sca.getMessage();
              message = wap.title + " [" + wap.url + "]";
              title = wap.url;
            }
            else if ((sca.getMessage() instanceof MessageMms)) {
              MessageMms mms = (MessageMms)sca.getMessage();
              message = new MessageMmsDetail().printDetails(mms.getContent_id());
              title = mms.getSubject();
            } else if (sca.action_type == ServiceContentAction.ACTION_TYPE.FORWARD) {
              ThirdPartyConfig tpc = new ThirdPartyConfig(sca.contentId);
              message = tpc.getUrl();
            } else {
              message = "<font color='red'>Message Error!!</font>";
              msg_ok = false;
            }

            out.print("<tr" + style + "><td>" + (showup ? "<img style='vertical-align:top' src='./images/puce_bottom.gif' border='0' onclick='javascript:doHigher(" + i + ", " + sca.getOper().getId() + ")'>" : "") + " " + (showdown ? "<img style='vertical-align:top' src='./images/puce_top.gif' border='0' onclick='javascript:doLower(" + i + ", " + sca.getOper().getId() + ")'>" : "") + "</td>");

            out.print("<td>" + ((sca.getIvr_content_id() == null) || (sca.getIvr_content_id().isEmpty()) ? "<font style='color:red'>N/A</font>" : sca.getIvr_content_id()) + "</td>");

            out.print("<td>" + ((sca.getKeyword() == null) || (sca.getKeyword().isEmpty()) ? "<font style='color:red'>N/A</font>" : sca.getKeyword()) + "</td>");

            out.print("<td>" + sca.action_type.toString() + "</td>");

            out.print("<td width='300px' style='text-align:left;word-wrap: break-word'" + (!full_message.isEmpty() ? " title='" + StringConvert.replace(full_message, "'", "\\&#39;", true) + "'" : "") + ">" + message + "</td>");

            out.print("<td>" + ((sca.getChrg_flg() != null) && (sca.getChrg_flg().equalsIgnoreCase("MT")) ? "<img style='vertical-align:top' src='./images/accept16.gif'>" : "") + "</td>");

            out.print("<td> <a href='javascript:doEdit(" + sca.getId() + ", " + sca.getOper().getId() + ")'>" + "<img src='./images/edit02.gif' border='0'>" + "</a>");

            out.print(" <a href='javascript:doCancel(" + sca.getId() + (msg_ok ? ",\"" + StringConvert.replace(title, "'", "\\\\&#39;", true) + "\"" : "") + ")'>" + "<img src='./images/trash.gif' border='0'>" + "</a>");

            out.print("</td>");
            out.print("</tr>");
          } catch (Exception e) {
            log.severe(e.getMessage());
          }
        }
        out.println("</table></div></body></html>");
      }
    } finally { out.close(); }

  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    processRequest(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    processRequest(request, response);
  }

  public String getServletInfo()
  {
    return "Short description";
  }
}