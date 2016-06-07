package smsgateway.webadmin.servlet;

import com.nation.ies.request.Newslist;
import hippoping.smsgw.api.db.LogEvent;
import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceContentAction;
import hippoping.smsgw.api.db.TxQueue;
import hippoping.smsgw.api.db.User;
import hippoping.smsgw.api.ies.IesReceiver;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import lib.common.DBPoolManager;
import lib.common.DatetimeUtil;

public class pushMessageSmsServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(pushMessageSmsServlet.class.getClass().getName());

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String encoding = "UTF-8";
            if (request.getCharacterEncoding() != null) {
                encoding = request.getCharacterEncoding();
            }
            String old_txqid = request.getParameter("txqid");
            String type = request.getParameter("type").toUpperCase();
            String to = request.getParameter("to");
            String serviceid = request.getParameter("srvc_main_id");
            String operid = request.getParameter("operid");
            String sendtime = request.getParameter("deliver_dt");

            LogEvent.EVENT_ACTION action = LogEvent.EVENT_ACTION.ADD;

            if ((old_txqid != null) && (!old_txqid.isEmpty())) {
                try {
                    TxQueue txQueue = new TxQueue(Long.parseLong(old_txqid));
                    if (txQueue != null) {
                        txQueue.remove();
                        action = LogEvent.EVENT_ACTION.MODIFY;
                    }
                } catch (Exception e) {
                }
            }
            String dt_fmt = "yyyy-MM-dd HH:mm:ss";
            if ((sendtime == null) || (!sendtime.trim().matches("^(19|20)\\d{2}-(0[1-9]|1[012]|[1-9])-(0[1-9]|[1-9]|[12][0-9]|3[01]) ((0|1)\\d{1}|2[0-3]):([0-5]\\d{1}):([0-5]\\d{1})$"))) {
                log.log(Level.WARNING, "use default current time regarding to input sendtime formation error");
                sendtime = DatetimeUtil.getDateTime(dt_fmt);
            }

            if (to.equalsIgnoreCase("IES")) {
                String message = "";
                String url = "";
                String title = "";
                try {
                    if (type.equalsIgnoreCase("SMS")) {
                        message = new String(request.getParameter("message").getBytes("ISO8859_1"), encoding);
                    } else if (type.equalsIgnoreCase("WAP")) {
                        url = request.getParameter("url");
                        title = new String(request.getParameter("title").getBytes("ISO8859_1"), encoding);
                    }
                } catch (Exception e) {
                    log.log(Level.SEVERE, "exception caught", e);
                }

                String data = "<?xml version=\"1.0\" encoding=\"TIS-620\" ?>"
                        + "<Nation:newslist xmlns:Nation=\"www.nationmultimedia.com\" xmlns=\"www.nationmultimedia.com\""
                        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + " xsi:schemaLocation=\"www.nationmultimedia.com news.xsd\">"
                        + "<news>"
                        + "<news_id b=\"blah\">0000000000001</news_id>"
                        + "<topic>TOPIC</topic>"
                        + "<validDate>2009-03-22T18:09:00</validDate>"
                        + "<updateDate>2009-03-22T18:09:00</updateDate>"
                        + "<expireDate>2009-03-23T18:09:00</expireDate>"
                        + "<publisher>MOBILE</publisher>"
                        + "<category />"
                        + "<type>" + type + "</type>"
                        + "<org />"
                        + "<edition />"
                        + "<section />"
                        + "<colsection />"
                        + "<subcolumn />"
                        + "<ranking />"
                        + "<priority />"
                        + "<page>0</page>"
                        + "<page_code />"
                        + "<db_code>DBCODE</db_code>"
                        + "<stts_code>SND</stts_code>"
                        + "<language />"
                        + "<author>backoffice</author>"
                        + "<content><![CDATA[" + message + "]]></content>"
                        + "<url>" + url + "</url>"
                        + "<note>" + title + "</note>"
                        + "<picturelist />"
                        + "</news></Nation:newslist>";

                Newslist newslist = null;
                try {
                    JAXBContext jc = JAXBContext.newInstance("com.nation.ies.request");

                    Unmarshaller u = jc.createUnmarshaller();

                    newslist = (Newslist) u.unmarshal(new StreamSource(new StringReader(data)));
                } catch (JAXBException e) {
                    throw new ServletException(e);
                }

                if (newslist != null) {
                    Newslist.News news = (Newslist.News) newslist.getNews().get(0);

                    news.setValidDate(sendtime);
                    try {
                        DBPoolManager cp = new DBPoolManager();
                        try {
                            String whereoper = "";
                            if (operid != null) {
                                whereoper = " AND ( 0";
                                int number = Integer.parseInt(operid);
                                for (int i = OperConfig.CARRIER.length(); i > 0; i--) {
                                    if (number - Math.pow(2.0D, i) >= 0.0D) {
                                        number = (int) (number - Math.pow(2.0D, i));
                                        whereoper = whereoper + " OR oper_id=" + i;
                                    }
                                }
                                whereoper = whereoper + " )";
                            }

                            String sql = 
                                    "   SELECT db_code, oper_id"
                                    + "   FROM ctnt_mngr_map"
                                    + "  WHERE 1"
                                    + "    AND srvc_main_id=?"
                                    + whereoper;

                            cp.prepareStatement(sql);
                            cp.getPreparedStatement().setInt(1, Integer.parseInt(serviceid));
                            ResultSet rs = cp.execQueryPrepareStatement();

                            while (rs.next()) {
                                news.setDbCode(rs.getString(1));

                                new IesReceiver().serviceMap(news);

                                LogEvent.log(type.equals("WAP") ? LogEvent.EVENT_TYPE.WAP : LogEvent.EVENT_TYPE.SMS, action, 
                                        rs.getString(1) + "|" + type + "|" + (type.equals("WAP") ? title + "," + url : message), 
                                        (User) request.getSession().getAttribute("USER"), 
                                        null, 
                                        OperConfig.CARRIER.fromId(rs.getInt(2)), 
                                        Integer.parseInt(serviceid),
                                        0, 0, LogEvent.LOG_LEVEL.INFO);
                            }

                        } catch (SQLException e) {
                            log.log(Level.SEVERE, "SQL Error!!", e);
                        } finally {
                            cp.release();
                        }
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
            } else {
                String content_id = request.getParameter("ctnt_id");
                try {
                    DBPoolManager cp = new DBPoolManager();
                    try {
                        String whereoper = "";
                        if (operid != null) {
                            whereoper = " AND ( 0";
                            int number = Integer.parseInt(operid);
                            for (int i = OperConfig.CARRIER.length(); i > 0; i--) {
                                if (number - Math.pow(2.0D, i) >= 0.0D) {
                                    number = (int) (number - Math.pow(2.0D, i));
                                    whereoper = whereoper + " OR oper_id=" + i;
                                }
                            }
                            whereoper = whereoper + " )";
                        }

                        String sql = 
                                "   SELECT db_code, oper_id"
                                + "   FROM ctnt_mngr_map"
                                + "  WHERE 1"
                                + "    AND srvc_main_id=?"
                                + whereoper;

                        cp.prepareStatement(sql);
                        cp.getPreparedStatement().setInt(1, Integer.parseInt(serviceid));
                        ResultSet rs = cp.execQueryPrepareStatement();

                        while (rs.next()) {
                            ServiceContentAction sca = new ServiceContentAction();
                            sca.action_type = ServiceContentAction.ACTION_TYPE.valueOf(type);
                            sca.contentId = Integer.parseInt(content_id);
                            new IesReceiver().serviceMap(rs.getString(1), sca, sendtime);

                            LogEvent.log(LogEvent.EVENT_TYPE.valueOf(type), action, rs.getString(1) + "|" + type + "|smart message: " + content_id, (User) request.getSession().getAttribute("USER"), null, OperConfig.CARRIER.fromId(rs.getInt(2)), Integer.parseInt(serviceid), 0, 0, LogEvent.LOG_LEVEL.INFO);
                        }

                    } catch (SQLException e) {
                        log.log(Level.SEVERE, "SQL Error!!", e);
                    } finally {
                        cp.release();
                    }
                } catch (Exception e) {
                    throw new ServletException(e);
                }
            }

            out.print("<script>window.location='push_message_sms.jsp?srvc_main_id=" + serviceid + "'</script>");
        } finally {
            out.close();
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}