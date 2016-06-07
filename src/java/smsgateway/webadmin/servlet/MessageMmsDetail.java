package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.MessageMms;
import hippoping.smsgw.api.db.MessageMmsSubContent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessageMmsDetail extends HttpServlet {

    public String printDetails(int id)
            throws Exception {
        StringWriter out = new StringWriter();
        try {
            MessageMms mms = new MessageMms(id);

            out.append("    <table class='table3' width='100%' style='padding:20px; height:100px;border: solid 1px #EEE'>       <tr>           <td width='10%' style='padding:5px; background:#482A74; color:#FFF; vertical-align:top'>               <div class='rotate90' style='height:10px'>" + mms.getSubject() + "<br>" + mms.getFrom() + "               </div>" + "           </td>");

            List subcontents = mms.getSubcontent();
            if (subcontents == null) {
                return "";
            }
            for (Iterator iter = subcontents.iterator(); iter.hasNext();) {
                MessageMmsSubContent sc = (MessageMmsSubContent) iter.next();

                out.append("<td width='20%' style='padding:5px; border: solid 1px #EEE'>");

                switch (sc.getCtnt_type()) {
                    case TEXT:
                        File file = new File(sc.getFull_path_src());
                        FileInputStream fis = new FileInputStream(file);

                        DataInputStream in = new DataInputStream(fis);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in, "TIS-620"));

                        String line = "";
                        while ((line = br.readLine()) != null) {
                            out.append(line + "<BR>");
                        }

                        fis.close();
                        in.close();
                        br.close();
                        break;
                    case RINGTONE:
                        out.append("<img src='images/music_note16.png' border=0 style='vertical-align:top;'>" + sc.getFull_path_src().substring(sc.getFull_path_src().lastIndexOf('/') + 1).trim());

                        break;
                    case PICTURE:
                        out.append("<img style='padding:0;margin:0;' src='header?file=" + sc.getFull_path_src() + "' border=0>");
                        break;
                }

                out.append("</td>");
            }
            out.append("</tr></table>");
        } catch (Exception e) {
            throw e;
        }

        return out.toString();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println(printDetails(Integer.parseInt(request.getParameter("id"))));
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