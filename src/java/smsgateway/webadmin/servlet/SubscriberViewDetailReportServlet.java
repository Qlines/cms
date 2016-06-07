package smsgateway.webadmin.servlet;

import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceElement;
import hippoping.smsgw.api.db.report.SubscriptionTracker;
import hippoping.smsgw.api.db.report.SubscriptionTrackerFactory;
import hippoping.smsgw.api.db.report.SummaryReport;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lib.common.DatetimeUtil;
import lib.common.StringConvert;

public class SubscriberViewDetailReportServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(SubscriberViewDetailReportServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "ISO8859_1");

        String id = request.getParameter("id");
        String type = request.getParameter("type");
        String srvc_main_id = request.getParameter("srvc_main_id");
        String oper_id = request.getParameter("oper_id");
        String date = request.getParameter("date");
        String ssname = request.getParameter("ssname");

        MultipartResponse multi = new MultipartResponse(response);
        try {
            List trackers = null;
            Iterator iter;
            if ((id != null) && (type != null) && (StringConvert.isDigit(id)) && (StringConvert.isDigit(type))) {
                trackers = SubscriptionTrackerFactory.get(Integer.parseInt(id), SummaryReport.COMMAND.fromId(Integer.parseInt(type)));
            } else if ((srvc_main_id != null) && (type != null) && (oper_id != null) && (date != null) && (StringConvert.isDigit(srvc_main_id)) && (StringConvert.isDigit(oper_id)) && (StringConvert.isDigit(type))) {
                try {
                    Date mydate = DatetimeUtil.toDate(date, "yyyy-MM-dd");

                    trackers = SubscriptionTrackerFactory.get(Integer.parseInt(srvc_main_id), OperConfig.CARRIER.fromId(Integer.parseInt(oper_id)), SummaryReport.COMMAND.fromId(Integer.parseInt(type)), mydate);
                } catch (Exception e) {
                    throw e;
                }
            } else if ((type != null) && (oper_id != null) && (ssname != null) && (StringConvert.isDigit(oper_id)) && (StringConvert.isDigit(type))) {
                List tmp = (List) request.getSession().getAttribute(ssname);
                if ((tmp != null) && (!tmp.isEmpty())) {
                    log.info("trackBuffer is not null and size is " + tmp.size());
                    trackers = new ArrayList();
                    for (iter = tmp.iterator(); iter.hasNext();) {
                        SubscriptionTracker tracker = (SubscriptionTracker) iter.next();
                        if (((tracker.getRept_actn_type() & (int) Math.pow(2.0D, Integer.parseInt(type))) > 0) && (tracker.getOper().toString().equals(OperConfig.CARRIER.fromId(Integer.parseInt(oper_id)).toString()))) {
                            trackers.add(tracker);
                        }
                    }
                }
            }

            multi.startResponse("text/csv;charset=tis-620");
            response.setHeader("Content-disposition", "attachment; filename=Subscriber_View_Detail_" + DatetimeUtil.getDateTime("yyyyMMddHHmmss") + ".csv");

            out.append("No.,MSISDN,Service,Shortcode,Oper,subtype,channel,datetime,\r\n");

            for (int i = 0; i < trackers.size(); i++) {
                ServiceElement se = new ServiceElement(((SubscriptionTracker) trackers.get(i)).getSrvc_main_id(), ((SubscriptionTracker) trackers.get(i)).getOper().getId(), ServiceElement.SERVICE_TYPE.SUBSCRIPTION.getId(), ServiceElement.SERVICE_STATUS.ON.getId() | ServiceElement.SERVICE_STATUS.TEST.getId());

                out.append(i + 1 + ",");
                out.append(((SubscriptionTracker) trackers.get(i)).getMsisdn() + ",");
                out.append(StringConvert.Unicode2ASCII2(se.srvc_name) + ",");
                out.append(se.srvc_id + ",");
                out.append(((SubscriptionTracker) trackers.get(i)).getOper().toString() + ",");
                out.append(((SubscriptionTracker) trackers.get(i)).getSubtype() + ",");
                out.append(((SubscriptionTracker) trackers.get(i)).getChannel() + ",");
                out.append(DatetimeUtil.print("MM/dd/yy HH:mm:ss", ((SubscriptionTracker) trackers.get(i)).getRecv_dt()) + ",");
                out.append("\r\n");
            }

            out.flush();
            multi.endResponse();
        } catch (Exception e) {
            log.severe(e.getMessage());
        } finally {
            multi.finish();
            out.close();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    public String getServletInfo() {
        return "Short description";
    }
}