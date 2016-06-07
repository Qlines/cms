package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lib.common.DatetimeUtil;

public class LogEventOnlineServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(LogEventOnlineServlet.class.getClass().getName());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            User user = (User) request.getSession().getAttribute("USER");
            if (user == null) {
                return;
            }

            int level = -1;

            String _level = request.getParameter("level");
            if (_level != null) {
                level = Integer.parseInt(_level);
            }

            HttpSession session = request.getSession();
            Integer last_id = Integer.valueOf(-1);
            if ((session != null) && (session.getAttribute("lastLogEventID") != null)) {
                last_id = (Integer) session.getAttribute("lastLogEventID");
            }

            List logs = LogEvent.get(level, user, null, null, LogEvent.EVENT_TYPE.ALL, LogEvent.EVENT_ACTION.NONE, last_id.intValue());
            if ((logs == null) || (logs.isEmpty())) {
                return;
            }
            request.getSession().setAttribute("lastLogEventID", Integer.valueOf(((LogEvent) logs.get(0)).log_evnt_id));
            Collections.reverse(logs);

            for (Iterator iter = logs.iterator(); iter.hasNext();) {
                LogEvent logEvent = (LogEvent) iter.next();
                out.print("+");
                out.print(logEvent.log_level + "|");
                out.print(DatetimeUtil.print("yyyy-MM-dd HH:mm:ss|", logEvent.timestamp));
                out.print(logEvent.user.getName() + "|");
                out.print((logEvent.event_action != LogEvent.EVENT_ACTION.NONE ? logEvent.event_action : "") + "|");
                out.print(logEvent.event_type + "|");
                out.print(logEvent.event_desc + "|");
                if ((logEvent.event_type != LogEvent.EVENT_TYPE.LOG_IN) && (logEvent.event_type != LogEvent.EVENT_TYPE.LOG_OUT)) {
                    out.print(logEvent.getDetails());
                }
                out.println("");
            }
        } catch (Exception e) {
            Iterator iter;
            log.severe(e.getMessage());
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