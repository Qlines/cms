package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.comparator.MessageSms.MessageSmsSortByTitle;
import hippoping.smsgw.api.db.Message;
import hippoping.smsgw.api.db.MessageSms;
import hippoping.smsgw.api.db.MessageSmsFactory;
import hippoping.smsgw.api.db.User;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SmsPictureListServlet extends HttpServlet {

    protected int rows = 0;
    protected String old_orderby = "";
    protected int sort = 0;

    private void sort(List<MessageSms> list, String field, int swap) {
        Comparator comparator = null;

        if ((this.old_orderby != null) && (this.old_orderby.equals(field))) {
            if (swap == 1) {
                this.sort = (++this.sort % 2);
            }
        } else {
            this.sort = 0;
            this.old_orderby = field;
        }

        if (field.equals("title")) {
            comparator = new MessageSmsSortByTitle();
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
            String encoding = "UTF-8";
            if (request.getCharacterEncoding() != null) {
                encoding = request.getCharacterEncoding();
            }
            User user = (User) request.getSession().getAttribute("USER");
            if (user == null) {
                out.print("<script>window.location='logout?msg=Your session has been expired! please relogin the page.'</script>");
            } else {
                String orderby = "";
                String page = "";
                String swap = "";
                String row = "10";

                String cmd = "refresh";

                if ((request.getContentType() != null) && (request.getContentType().contains("multipart/form-data"))) {
                    File dir = new File("ext/sms/picture");
                    if ((!dir.exists())
                            && (!dir.mkdirs())) {
                        throw new Exception("cannot create picture sms directory");
                    }

                    MultipartRequest multi = new MultipartRequest(request, "ext/sms/picture", 5242880);

                    Enumeration files = multi.getFileNames();
                    while (files.hasMoreElements()) {
                        String name = (String) files.nextElement();
                        try {
                            new MessageSms().add("ext/sms/picture/" + multi.getFile(name).getName(), Message.SMS_TYPE.PICTURE, 0, multi.getFile(name), user.getUid());
                        } catch (Exception e) {
                        }
                    }
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

                if ((cmd != null) && (cmd.equals("remove"))) {
                    for (String id : sid.split(",")) {
                        try {
                            MessageSms sms = new MessageSms(Integer.parseInt(id));
                            System.out.println("Remove sms picture " + sms.remove() + " row(s)");
                        } catch (Exception e) {
                        }
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

                    request.getSession().setAttribute("smsPictureList", new MessageSmsFactory(Message.SMS_TYPE.PICTURE, keyword, 0, 0, user.getUid()).getMessageSmsList());
                }

                List list = (List) request.getSession().getAttribute("smsPictureList");

                if ((list != null) && (orderby != null) && (list.size() > 0)) {
                    sort(list, orderby, (swap != null) && (swap.equals("1")) ? 1 : 0);
                }

                int pg = list.size() / this.rows + (list.size() % this.rows != 0 ? 1 : 0);

                pg = pg == 0 ? 1 : pg;
                out.println("<html><head>    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>    <link href='./css/cv.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyCorners.css' rel='stylesheet' type='text/css'>    <link href='./css/niftyPrint.css' rel='stylesheet' type='text/css' media='print'>    <style type='text/css'>        body{margin:0px; padding: 0px; background: white;            font: 100.01% 'Trebuchet MS',Verdana,Arial,sans-serif}        h1,h2,p{margin: 0 10px}        h1{font-size: 250%;color: #FFF}        h2{font-size: 200%;color: #f0f0f0}        p{padding-bottom:1em}        h2{padding-top: 0.3em}        div#memberViewContent {background: #377CB1;}    </style>    <script src='./js/nifty.js' type='text/javascript'></script>    <script src='./js/utils.js' type='text/javascript'></script>    <script src='./js/filter_input.js' type='text/javascript'></script>    <script>    var selectList = new Array();    function validate_page(page, maxpage) {        var frm = document.forms[\"reloadFrm\"];       if (page=='') {alert('Please enter page number.'); frm.page.value=" + page + ";return false;}" + "       else if (page>maxpage || page<=0) {alert('Page ' + page + ' not found!'); frm.page.value=" + page + ";return false;}" + "       else {frm.submit();}" + "    }" + "    function goto_page(page) {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.page.value=page;" + "       frm.submit();" + "    }" + "    function doSwapSelect(id) {" + "       var obj = getElement(id);" + "       if (obj) {" + "           obj.checked = !obj.checked;" + "       }" + "       doUpdateSelectItems(obj);" + "    }" + "    function doUpdateSelectItems(obj) {" + "       if (obj.checked) {" + "           doSelect(obj.id);" + "       } else {" + "           doDeselect(obj.id);" + "       }" + "       var tdobj = getElement('td_' + obj.id);" + "       if (tdobj)" + "           tdobj.style.backgroundColor=obj.checked?'#555':'#fff';" + "    }" + "    function doSelect(id) {" + "       if (selectList.indexOf(id) == -1) {" + "           selectList.push(id);" + (subcmd.equals("select") ? "var obj = getElement('img_' + id);window.returnValue=id + '|' + obj.src;window.close();" : "") + "       }" + "    }" + "    function doDeselect(id) {" + "       if (selectList.indexOf(id) >= 0) {" + "           selectList.splice(selectList.indexOf(id),1);" + "       }" + "    }" + "    function doSelectAll() {" + "       var inputs = document.getElementsByTagName('input');" + "       for (i=0;i<inputs.length;i++){" + "           if (inputs[i].type == 'checkbox') {" + "               inputs[i].checked = true;" + "               doUpdateSelectItems(inputs[i]);" + "           }" + "       }" + "    }" + "    function doDeselectAll() {" + "       var inputs = document.getElementsByTagName('input');" + "       for (i=0;i<inputs.length;i++){" + "           if (inputs[i].type == 'checkbox') {" + "               inputs[i].checked = false;" + "               doUpdateSelectItems(inputs[i]);" + "           }" + "       }" + "    }" + "    function doAdd() {" + "         window.name = 'sms_picture';" + "         var opener = window.open('sms_picture_new.html',null, 'height=394,width=732,status=yes,toolbar=no,menubar=no,location=no,scrollbars=yes,resizable=yes');" + "    }" + "    function doRemove() {" + "       frm=document.forms[\"reloadFrm\"];" + "       frm.cmd.value='remove';" + "       frm.id.value=selectList.toString();" + "       frm.action='SmsPictureListServlet';" + "       var msg = 'Click OK to confirm to delete Picture SMS, otherwise click cancel.';" + "       if (confirm(msg)) {" + "           frm.submit();" + "       }" + "    }" + "    function _onMouseOver(obj, id) {" + "       var pic = getElement('hidden_' + id);" + "       if (pic) {" + "           pic.style.display = 'block';" + "           pic.top = obj.offsetTop + 80;" + "       }" + "    }" + "    function _onMouseOut(obj, id) {" + "       var pic = getElement('hidden_' + id);" + "       if (pic) {" + "           pic.style.display = 'none';" + "       }" + "    }" + "    window.onload=function() {" + "        if(NiftyCheck())Rounded(\"div#album\",\"#FFF\",\"#EEE\");" + "       var inputs = document.getElementsByTagName('input');" + "       for (i=0;i<inputs.length;i++){" + "           if (inputs[i].type == 'button') {" + "               inputs[i].disabled = " + (list.isEmpty() ? "true" : "false") + ";" + "           }" + "       }" + "    }" + "    </script>" + "</head>" + "<body>" + "   <div id='data' style='padding: 0 10px 0 10px;width:100%;'>" + "       <form name='reloadFrm' method='POST' onsubmit='return validate_page(document.forms[\"reloadFrm\"].page.value, " + pg + ");'>" + "       <input type=hidden name=cmd value=''>" + "       <input type=hidden name=id value=''>" + "       <input type=hidden name=orderby value='" + orderby + "'>" + "       <input type=hidden name=rows value='" + this.rows + "'>" + "       <input type=hidden name=swap value='0'>" + "       <input type=hidden name=type value='" + (request.getParameter("type") != null ? request.getParameter("type") : "") + "'>" + "       <div id='content' class='floatl' style='font-size:75%; padding-left:5px; background:#FFF;'>" + "         <b>Total " + list.size() + " record(s) found. (Page " + page + " of " + pg + ")</b>" + (subcmd.equals("select") ? "" : "             | <a href='javascript:doAdd();' style='color:#333;'>             <img src='./images/new2.gif' border='0' style='vertical-align:middle'>              Create New Picture SMS             </a>") + "       </div>" + "       <div class='floatr'>" + "         <span style='padding:0;'>" + (Integer.parseInt(page) > 1 ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) - 1) + ")'><img src='images/previous.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/previous_dis.gif' border=0 style='vertical-align:middle;'>") + (Integer.parseInt(page) < pg ? "<a href='javascript:goto_page(" + (Integer.parseInt(page) + 1) + ")'><img src='images/next.gif' border=0 style='vertical-align:middle;'></a>" : "<img src='images/next_dis.gif' border=0 style='vertical-align:middle;'>") + "         </span>" + "           <span style='font-size:75%; padding-left:5px; vertical-align:middle;'>Goto page</span> <input type=text name=page size=2 value='" + page + "' onkeypress='return filter_digit_char(event)'>" + "           <input type=submit value=go>" + "       </div>" + "       <div id='album' class='floatl'>" + "       <table class='album'>");

                int sindex = (Integer.parseInt(page) - 1) * this.rows;
                int eindex = sindex + this.rows;
                boolean closed_tr = false;
                int pic_per_line = 5;
                for (int i = sindex; (i < list.size()) && (i < eindex); i++) {
                    try {
                        if ((i - sindex) % pic_per_line == 0) {
                            closed_tr = false;
                            out.println("<tr>");
                        }
                        out.println("<td" + ((request.getParameter("type") != null) && (!request.getParameter("type").isEmpty()) && (request.getParameter("type").equals("popup")) ? " onclick='javascript:window.returnValue=" + ((MessageSms) list.get(i)).getContent_id() + ";window.close();'" : new StringBuilder().append(" onclick='javascript:doSwapSelect(").append(((MessageSms) list.get(i)).getContent_id()).append(")'>").toString()) + " id='td_" + ((MessageSms) list.get(i)).getContent_id() + "' " + " onmouseover='javascript:_onMouseOver(this, \"" + ((MessageSms) list.get(i)).getContent_id() + "\");'" + " onmouseout='javascript:_onMouseOut(this, \"" + ((MessageSms) list.get(i)).getContent_id() + "\");'" + " <span style='line-height:1.5em;display:none;position:absolute;" + " background:#FFF;padding:5px;margin:0;text-align:center'" + " id='hidden_" + ((MessageSms) list.get(i)).getContent_id() + "'>" + "<img style='padding:0;margin:0;' src='header?file=" + ((MessageSms) list.get(i)).getFilename() + "' border=0 title='" + ((MessageSms) list.get(i)).getMessageInfo().getTitle() + "'></span>" + "<img id='img_" + ((MessageSms) list.get(i)).getContent_id() + "' src='header?file=" + ((MessageSms) list.get(i)).getFilename() + "' border=0><br>" + "<input type='checkbox' id='" + ((MessageSms) list.get(i)).getContent_id() + "' onclick='this.checked = !this.checked'>" + ((MessageSms) list.get(i)).getMessageInfo().getTitle() + "</td>");

                        if (((i - sindex) % pic_per_line == pic_per_line - 1) && (!closed_tr)) {
                            closed_tr = true;
                            out.println("</tr>");
                        }
                    } catch (Exception e) {
                    }
                }
                if (!closed_tr) {
                    closed_tr = true;
                    out.println("</tr>");
                }
                out.println("</table></div><br>" + (subcmd.equals("select") ? "" : "<input type=button class='button' value='Select All' onclick='doSelectAll();'> <input type=button class='button' value='Deselect All' onclick='doDeselectAll();'> <input type=button class='button' value='Delete' onclick='return doRemove();'>") + "</body></html>");
            }

        } catch (Exception e) {
            e.printStackTrace();
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