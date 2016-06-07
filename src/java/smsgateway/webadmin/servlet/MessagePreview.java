package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.Message;
import hippoping.smsgw.api.db.MessageMms;
import hippoping.smsgw.api.db.MessageSms;
import hippoping.smsgw.api.db.MessageWap;
import hippoping.smsgw.api.db.ServiceContentAction;
import hippoping.smsgw.api.db.ThirdPartyConfig;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessagePreview extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String _ctnt_id = request.getParameter("ctnt_id");
            String _ctnt_type = request.getParameter("ctnt_type");

            int ctnt_id = Integer.parseInt(_ctnt_id);
            ServiceContentAction.ACTION_TYPE ctnt_type = ServiceContentAction.ACTION_TYPE.valueOf(_ctnt_type);

            Message message = null;
            ThirdPartyConfig tpc = null;

            switch (ctnt_type) {
                case SMS:
                    message = new MessageSms(ctnt_id);
                    break;
                case WAP:
                    message = new MessageWap(ctnt_id);
                    break;
                case MMS:
                    message = new MessageMms(ctnt_id);
                    break;
                case FORWARD:
                    tpc = new ThirdPartyConfig(ctnt_id);
            }

            String content = "";
            String full_message = "";
            String title = "";

            if ((message != null) && ((message instanceof MessageSms))) {
                MessageSms sms = (MessageSms) message;
                switch (sms.getSmsType()) {
                    case TEXT:
                        content = sms.getContentSub()[0];
                        int maxchar = 40;
                        if (content.length() > maxchar) {
                            content = content.substring(0, maxchar) + "...";
                        }
                        title = content;

                        if (sms.getMessage_num() > 1) {
                            content = content + "[concatenated]";
                        }

                        for (int j = 0; j < sms.getMessage_num(); j++) {
                            full_message = full_message + sms.getContentSub()[j];
                        }

                        break;
                    case PICTURE:
                        content = "<img src='header?file=" + sms.getFilename() + "' border='0' title='" + sms.getMessageInfo().getTitle() + "'>";

                        title = sms.getMessageInfo().getTitle();

                        break;
                    case RINGTONE:
                        title = sms.getFilename().substring(sms.getFilename().lastIndexOf('/') + 1).trim();
                        content = "<img src='images/music_note16.png' border='0' style='vertical-align: top'> " + title;
                }
            } else if ((message != null) && ((message instanceof MessageWap))) {
                MessageWap wap = (MessageWap) message;
                content = wap.title + " [" + wap.url + "]";
                title = wap.url;
            } else if ((message != null) && ((message instanceof MessageMms))) {
                MessageMms mms = (MessageMms) message;
                content = new MessageMmsDetail().printDetails(mms.getContent_id());
                title = mms.getSubject();
            } else if (tpc != null) {
                content = tpc.getUrl();
            } else {
                content = "<font color='red'>Message Error!!</font>";
            }

            out.print(content);
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