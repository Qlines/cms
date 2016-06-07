package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.MessageMms.MessageMmsSortByFrom;
import hippoping.smsgw.api.comparator.MessageMms.MessageMmsSortBySubject;
import hippoping.smsgw.api.db.Message;
import hippoping.smsgw.api.db.MessageMms;
import hippoping.smsgw.api.db.MessageMmsFactory;
import hippoping.smsgw.api.db.MessageMmsSubContent;
import hippoping.smsgw.api.db.User;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.StringConvert;

public class MmsListServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(MmsListServlet.class.getClass().getName());
    protected int rows = 0;
    protected String old_orderby = "";
    protected int sort = 0;

    private void sort(List<MessageMms> list, String field, int swap) {
        Comparator comparator = null;

        if ((this.old_orderby != null) && (this.old_orderby.equals(field))) {
            if (swap == 1) {
                this.sort = (++this.sort % 2);
            }
        } else {
            this.sort = 0;
            this.old_orderby = field;
        }

        if (field.equals("subject")) {
            comparator = new MessageMmsSortBySubject();
        } else if (field.equals("from")) {
            comparator = new MessageMmsSortByFrom();
        }
        if (comparator != null) {
            Collections.sort(list, comparator);
            if (this.sort == 1) {
                Collections.reverse(list);
            }
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            User user = (User) request.getSession().getAttribute("USER");
            if (user == null) {
                out.print("<script>window.location='logout?msg=Your session has been expired! Please relogin the page.'</script>");
            } else {
                String encoding = "UTF-8";
                if (request.getCharacterEncoding() != null) {
                    encoding = request.getCharacterEncoding();
                }

                String orderby = "";
                String page = "";
                String swap = "";
                String row = "10";

                String cmd = "refresh";

                if ((request.getContentType() != null) && (request.getContentType().contains("multipart/form-data"))) {
                    int mms_ctnt_id = -1;

                    MultipartRequest multi = new MultipartRequest(request, ".", 5242880);

                    Enumeration params = multi.getParameterNames();
                    while (params.hasMoreElements()) {
                        String name = (String) params.nextElement();
                        String value = multi.getParameter(name);
                        if (name.equals("mms_ctnt_id")) {
                            if (!StringConvert.isDigit(value)) {
                                throw new Exception("invalid MMS ID number[" + value + "]!!");
                            }
                            mms_ctnt_id = Integer.parseInt(value);
                        }
                    }

                    if (mms_ctnt_id == -1) {
                        throw new Exception("parameter required error[mms_ctnt_id]!!");
                    }

                    Enumeration files = multi.getFileNames();
                    while (files.hasMoreElements()) {
                        String name = (String) files.nextElement();

                        File dir = new File("ext/mms/" + mms_ctnt_id);
                        if ((!dir.exists())
                                && (!dir.mkdirs())) {
                            throw new Exception("cannot create mms directory");
                        }

                        String filename = new String(multi.getFile(name).getName().getBytes("ISO8859_1"), encoding);
                        FileInputStream fis = new FileInputStream(multi.getFile(name));
                        FileOutputStream fos = new FileOutputStream("ext/mms/" + mms_ctnt_id + "/" + filename);
                        try {
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = fis.read(buf)) > 0) {
                                fos.write(buf, 0, len);
                            }
                        } finally {
                            fis.close();
                        }

                        if (multi.getFile(name).delete()) {
                            System.out.println("file moved.");
                        }

                        try {
                            Message.SMS_TYPE type = Message.SMS_TYPE.TEXT;
                            String extension = multi.getFile(name).getName().substring(multi.getFile(name).getName().lastIndexOf('.') + 1).trim();
                            if (extension.matches("(?i)(TXT|CSV)")) {
                                type = Message.SMS_TYPE.TEXT;
                            } else if (extension.matches("(?i)(AU|SND)")) {
                                type = Message.SMS_TYPE.RINGTONE;
                            } else if (extension.matches("(?i)(MID|RMI)")) {
                                type = Message.SMS_TYPE.RINGTONE;
                            } else if (extension.matches("(?i)(MP3)")) {
                                type = Message.SMS_TYPE.RINGTONE;
                            } else if (extension.matches("(?i)(AIF|AIFC|AIFF)")) {
                                type = Message.SMS_TYPE.RINGTONE;
                            } else if (extension.matches("(?i)(M3U)")) {
                                type = Message.SMS_TYPE.RINGTONE;
                            } else if (extension.matches("(?i)(RA|RAM)")) {
                                type = Message.SMS_TYPE.RINGTONE;
                            } else if (extension.matches("(?i)(WAV)")) {
                                type = Message.SMS_TYPE.RINGTONE;
                            } else if (extension.matches("(?i)(BMP)")) {
                                type = Message.SMS_TYPE.PICTURE;
                            } else if (extension.matches("(?i)(GIF)")) {
                                type = Message.SMS_TYPE.PICTURE;
                            } else if (extension.matches("(?i)(JPE|JPEG|JPG)")) {
                                type = Message.SMS_TYPE.PICTURE;
                            } else if (extension.matches("(?i)(PNG)")) {
                                type = Message.SMS_TYPE.PICTURE;
                            } else if (extension.matches("(?i)(TIF|TIFF)")) {
                                type = Message.SMS_TYPE.PICTURE;
                            } else {
                                throw new Exception("file extension isn't supported!!");
                            }

                            MessageMms mms = new MessageMms(mms_ctnt_id);
                            mms.addSubContent("ext/mms/" + mms_ctnt_id + "/" + filename, type, mms.getMaxSubOrderNumber() + 1);
                        } catch (Exception e) {
                            log.log(Level.SEVERE, "SQL error!!", e);
                        }

                    }

                    MessageMmsFactory.flush();
                } else {
                    try {
                        orderby = request.getParameter("orderby");
                        page = request.getParameter("page");
                        swap = request.getParameter("swap");

                        cmd = request.getParameter("cmd");
                        row = request.getParameter("rows");
                    } catch (Exception e) {
                        throw new Exception("invalid parameters");
                    }
                }

                if ((page == null) || (page.equals(""))) {
                    page = "1";
                }

                String sid = request.getParameter("id");

                int id = 0;
                try {
                    id = Integer.parseInt(sid);
                } catch (Exception e) {
                }
                if ((cmd != null) && (cmd.equals("remove"))) {
                    try {
                        MessageMms mms = new MessageMms(id);
                        if (mms.getContent_id() == id) {
                            System.out.println("Remove mms " + mms.remove() + " row(s)");
                        }
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "exception caught", e);
                    }

                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("remove_subcontent"))) {
                    Queue queue = new LinkedList();
                    for (String xid : sid.split(",")) {
                        try {
                            MessageMmsSubContent sc = new MessageMmsSubContent(Integer.parseInt(xid));
                            if (!queue.contains(Integer.valueOf(sc.getMms_mesg_id()))) {
                                queue.add(Integer.valueOf(sc.getMms_mesg_id()));
                            }
                            System.out.println("Remove mms sub content " + sc.remove() + " row(s)");
                        } catch (Exception e) {
                            log.log(Level.SEVERE, "exception caught", e);
                        }

                    }

                    for (Iterator iter = queue.iterator(); iter.hasNext();) {
                        new MessageMms(((Integer) iter.next()).intValue()).reorganize();
                    }

                    MessageMmsFactory.flush();

                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("swap_subcontentorder"))) {
                    MessageMmsSubContent sc = new MessageMmsSubContent(id);

                    List sc_list = new MessageMms(sc.getMms_mesg_id()).getSubcontent();

                    MessageMmsSubContent sc_prev = null;
                    for (Iterator iter = sc_list.iterator(); iter.hasNext();) {
                        MessageMmsSubContent sc_cur = (MessageMmsSubContent) iter.next();
                        if ((sc_prev != null) && (sc_cur.getContent_id() == id)) {
                            int tmp = sc_prev.getSub_order_number();
                            sc_prev.setSub_order_number(sc_cur.getSub_order_number());
                            sc_cur.setSub_order_number(tmp);

                            sc_prev.sync();
                            sc_cur.sync();
                        } else {
                            sc_prev = sc_cur;
                        }

                    }

                    MessageMmsFactory.flush();

                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("add"))) {
                    try {
                        MessageMms.add(request.getParameter("subject"), request.getParameter("from"), 0);
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "exception caught", e);
                    }

                    cmd = "refresh";
                } else if ((cmd != null) && (cmd.equals("edit")) && (id > 0)) {
                    try {
                        MessageMms mms = new MessageMms(id);
                        mms.setSubject(request.getParameter("subject"));
                        mms.setFrom(request.getParameter("from"));

                        mms.sync();
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "exception caught", e);
                    }

                    cmd = "refresh";
                }

                String subcmd = "";
                if ((cmd != null) && (cmd.equals("select"))) {
                    subcmd = cmd;

                    cmd = "refresh";
                }

                if ((cmd != null) && (cmd.equals("refresh"))) {
                    this.rows = Integer.parseInt(row);
                    String keyword = request.getParameter("keyword");
                    if (keyword != null) {
                        keyword = new String(request.getParameter("keyword").getBytes("ISO8859_1"), encoding);
                    }

                    request.getSession().setAttribute("mmsList", new MessageMmsFactory(keyword, 0, 0, user.getUid()).getMessageMmsList());
                }

                List list = (List) request.getSession().getAttribute("mmsList");

                if ((list != null) && (orderby != null) && (list.size() > 0)) {
                    sort(list, orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = list.size() / this.rows + (list.size() % this.rows != 0 ? 1 : 0);

                pg = pg == 0 ? 1 : pg;
                out.println("<html><head>    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>    <link href='./css/cv.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>    <style type='text/css'>        body{margin:0px; padding: 0px; background: white;            font: 100.01% 'Trebuchet MS',Verdana,Arial,sans-serif}        h1,h2,p{margin: 0 10px}        h1{font-size: 250%;color: #FFF}        h2{font-size: 200%;color: #f0f0f0}        p{padding-bottom:1em}        h2{padding-top: 0.3em}        div#memberViewContent {background: #377CB1;}    </style>    <script src='./js/nifty.js' type='text/javascript'></script>    <script src='./js/utils.js' type='text/javascript'></script>    <script src='./js/filter_input.js' type='text/javascript'></script>    <script>    var selectList = new Array();    var active_player;    function validate_page(page, maxpage) {        var frm = document.forms[\"reloadFrm\"];       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}" + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}" + "       else {frm.submit();}" + "    }" + "    function goto_page(page) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.page.value=page;" + "       frm.submit();" + "    }" + "    function doSwapSelect(id) {" + "       var obj = getElement(id);" + "       if (obj) {" + "           obj.checked = !obj.checked;" + "       }" + "       doUpdateSelectItems(obj);" + "    }" + "    function doSelect(id) {" + (subcmd.equals("select") ? "window.returnValue=id;window.close();" : "") + "       if (selectList.indexOf(id) == -1) {" + "           selectList.push(id);" + "       }" + "    }" + "    function doDeselect(id) {" + "       if (selectList.indexOf(id) >= 0) {" + "           selectList.splice(selectList.indexOf(id),1);" + "       }" + "    }" + "    function doUpdateSelectItems(obj) {" + "       if (obj.checked) {" + "           doSelect(obj.id);" + "       } else {" + "           doDeselect(obj.id);" + "       }" + "       var tdobj = getElement('td_' + obj.id);" + "       if (tdobj)" + "           tdobj.style.backgroundColor=obj.checked?'#555':'#fff';" + "    }" + "    function doEdit(id) {" + "       window.showModalDialog('mms_new.jsp?cmd=edit&id=' + id,'', 'dialogWidth:640px;dialogHeight:470px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doAdd() {" + "       window.showModalDialog('mms_new.jsp?cmd=add','', 'dialogWidth:640px;dialogHeight:470px');" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='refresh';" + "       frm.submit();" + "    }" + "    function doAddSubContent(id) {" + "         var opener = window.open('mms_sub_ctnt_new.html?id=' + id, null, 'height=394,width=732,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes');" + "    }" + "    function doPlay(id, url) {" + "       var pdiv = getElement(id);" + "       if (active_player) active_player.innerHTML='';" + "       if (pdiv) {" + "           pdiv.innerHTML = \"<img title='stop' src='images/stop.gif' style='vertical-align:middle' onclick='doStop(\\\"\" + id + \"\\\");'> now playing" + "                            <embed src='\" + url + \"' hidden=true autostart=true loop=false>\";" + "           pdiv.style.display = 'block';" + "           active_player = pdiv;" + "       }" + "    }" + "    function doStop(id) {" + "       var pdiv = getElement(id);" + "       if (pdiv) {" + "           pdiv.innerHTML = '';" + "           pdiv.style.display = 'none';" + "       }" + "    }" + "    function doRemove(id, subject) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='remove';" + "       frm.id.value=id;" + "       frm.action='MmsListServlet';" + "       var msg = 'Click OK to confirm to delete mms \"' + subject + '\", otherwise click cancel.';" + "       if (confirm(msg)) {" + "           frm.submit();" + "       }" + "    }" + "    function doRemoveSubContent() {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='remove_subcontent';" + "       frm.id.value=selectList.toString();" + "       frm.action='MmsListServlet';" + "       var msg = 'Click OK to confirm to delete selected sub content, otherwise click cancel.';" + "       if (confirm(msg)) {" + "           frm.submit();" + "       }" + "    }" + "    function doChangeSubContentOrder(id) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='swap_subcontentorder';" + "       frm.id.value=id;" + "       frm.action='MmsListServlet';" + "       frm.submit();" + "    }" + "    window.onload=function() {" + "    }" + "    </script>" + "</head>" + "<body>" + "   <div id='data' style='padding: 0 10px 0 10px;width:100%;'>" + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>" + "       <input type=hidden name=cmd value=''>" + "       <input type=hidden name=id value=''>" + "       <input type=hidden name=orderby value='" + orderby + "'>" + "       <input type=hidden name=rows value='" + this.rows + "'>" + "       <input type=hidden name=swap value='0'>" + "       <input type=hidden name=csv value='0'>" + "       <input type=hidden name=type value='" + (request.getParameter("type") != null ? request.getParameter("type") : "") + "'>" + "       <div id='content' class='floatl' style='font-size:75%; padding-left:5px; background:#FFF;'>" + "         <b>Total " + list.size() + " record(s) found. (Page " + page + " of " + pg + ")</b>" + (subcmd.equals("select") ? "" : "             | <a href='javascript:doAdd();' style='color:#333;'>             <img src='./images/new2.gif' border='0' style='vertical-align:middle'>              Create new MMS             </a>") + "       </div>" + "       <div class='floatr'>" + "         <span style='padding:0;'>" + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>") + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>") + "         </span>" + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>" + "           <input type=submit value=go>" + "       </div>" + "       <table class='table3' width='100%' style='padding:20px;'>" + "       <tr>" + "           <th width='3%'>No.</th>" + "           <th width='25%'>" + (orderby.equals("subject") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"subject\";frm.swap.value=1;frm.submit();'>Subject</a></th>" + "           <th width='25%'>" + (orderby.equals("from") ? "<img src='images/puce_" + (this.sort == 0 ? "top" : "bottom") + ".gif' border=0>" : "") + "<a href='javascript:frm=document.forms[\"reloadFrm\"];" + "frm.orderby.value=\"from\";frm.swap.value=1;frm.submit();'>From</a></th>" + "           <th width='47%' style='text-align:right'>Action</th>" + "       </tr>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                for (int i = sindex; (i < list.size()) && (i < eindex); i++) {
                    String style = i % 2 == 0 ? "" : " d0";
                    style = " class='" + style + "'";
                    try {
                        out.print("<tr" + (subcmd.equals("select") ? " onclick='doSelect(" + ((MessageMms) list.get(i)).getContent_id() + ")'" : (request.getParameter("type") != null) && (!request.getParameter("type").isEmpty()) && (request.getParameter("type").equals("popup")) ? " onclick='javascript:window.returnValue=" + ((MessageMms) list.get(i)).getContent_id() + ";window.close();'" : "") + style + "><td>" + (i + 1) + "</td>");

                        out.print("<td>" + ((MessageMms) list.get(i)).getSubject() + "</td>");
                        out.print("<td>" + ((MessageMms) list.get(i)).getFrom() + "</td>");
                        out.print("<td style='text-align:right'> <a href='javascript:doEdit(" + ((MessageMms) list.get(i)).getContent_id() + ")'>" + "<img title='edit' src='./images/edit02.gif' border='0'>" + "</a> " + "<a href='javascript:doRemove(" + ((MessageMms) list.get(i)).getContent_id() + ", \"" + StringConvert.replace(((MessageMms) list.get(i)).getSubject(), "'", "\\\\&#39;", true) + "\")'>" + "<img title='delete' src='./images/trash.gif' border='0'>" + "</a>" + "</td>");

                        out.print("</tr>");

                        out.print("<tr" + style + "><td colspan=4 style='text-align:left'>");

                        out.print("<div id='album' class='floatl' style='width:99%;background:#FFF;'><table class='album'>");

                        int ss = 0;
                        int ee = ((MessageMms) list.get(i)).getSubcontent().size();
                        boolean closed_tr = false;
                        int pic_per_line = 7;
                        for (int ii = 0; (ii < ((MessageMms) list.get(i)).getSubcontent().size()) && (ii < ee); ii++) {
                            MessageMmsSubContent sc = (MessageMmsSubContent) ((MessageMms) list.get(i)).getSubcontent().get(ii);

                            if ((ii - ss) % pic_per_line == 0) {
                                closed_tr = false;
                                out.println("<tr>");
                            }

                            if ((ii - ss > 0) && (ii < ((MessageMms) list.get(i)).getSubcontent().size()) && (ii < ee)) {
                                out.print("<td id='tdx_" + sc.getContent_id() + "' style='border:0px;width:16px;vertical-align:middle;padding:0;margin:0;display:none'" + " onmouseover='javascript:getElement(\"tdx_" + sc.getContent_id() + "\").style.display=\"block\";'" + " onmouseout='javascript:getElement(\"tdx_" + sc.getContent_id() + "\").style.display=\"none\";'>" + "<img style='border:0px;padding:0;margin:0'" + " onclick='javascript:doChangeSubContentOrder(" + sc.getContent_id() + ")'" + " src='images/refresh16.gif' border=0>" + "</td>");
                            }

                            out.print("<td style='text-align:left; padding:5px' id='td_" + sc.getContent_id() + "'" + (!subcmd.equals("select") ? " onclick='javascript:doSwapSelect(" + sc.getContent_id() + ")'" + ((ii - ss > 0) && (ii < ((MessageMms) list.get(i)).getSubcontent().size()) && (ii < ee) ? " onmouseover='javascript:getElement(\"tdx_" + sc.getContent_id() + "\").style.display=\"block\";'" + " onmouseout='javascript:getElement(\"tdx_" + sc.getContent_id() + "\").style.display=\"none\";'" : "") : "") + ">");

                            switch (sc.getCtnt_type()) {
                                case TEXT:
                                    File file = new File(sc.getFull_path_src());
                                    FileInputStream fis = new FileInputStream(file);

                                    DataInputStream in = new DataInputStream(fis);
                                    BufferedReader br = new BufferedReader(new InputStreamReader(in, "TIS-620"));

                                    String line = "";
                                    while ((line = br.readLine()) != null) {
                                        out.print(line + "<BR>");
                                    }

                                    fis.close();
                                    in.close();
                                    br.close();
                                    break;
                                case RINGTONE:
                                    out.print("<img src='images/play.gif' border=0 style='vertical-align:middle;' onclick='doPlay(\"playerDiv_" + sc.getContent_id() + "\", \"header?file=" + URLEncoder.encode(sc.getFull_path_src(), "UTF-8") + "\");'" + ">" + sc.getFull_path_src().substring(sc.getFull_path_src().lastIndexOf('/') + 1).trim());

                                    out.print("<div id='playerDiv_" + sc.getContent_id() + "'" + " style='width:100%;line-height:.8em;display:none;position:relative;left:0;top:0;background:transparent;padding:0;margin:0;font-size:75%;'>" + "</div>");

                                    break;
                                case PICTURE:
                                    out.print("<img style='padding:0;margin:0;' src='header?file=" + sc.getFull_path_src() + "' border=0>");
                                    break;
                            }

                            out.print("<br><input type='checkbox' id='" + sc.getContent_id() + "' onclick='this.checked = !this.checked'></td>");

                            if (((ii - ss) % pic_per_line == pic_per_line - 1) && (!closed_tr)) {
                                closed_tr = true;
                                out.println("</tr>");
                            }

                        }

                        if (!closed_tr) {
                            closed_tr = true;
                            out.println("</tr>");
                        }

                        out.print("</table>");
                        out.print("</td></tr>");
                        out.print("<tr" + style + "><td colspan=4 style='text-align:left'>" + "<a href='javascript:doAddSubContent(" + ((MessageMms) list.get(i)).getContent_id() + ");' style='color:#333;'>Add sub content</a> | " + "<a href='javascript:doRemoveSubContent();' style='color:#333;'>Delete sub content</a>" + "</td></tr>");
                    } catch (Exception e) {
                        log.log(Level.SEVERE, "page error!!", e);
                    }
                }
                out.println("</table>");
                out.println("</div></body></html>");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "page error!!", e);
        } finally {
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    public String getServletInfo() {
        return "Short description";
    }
}