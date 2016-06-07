package smsgateway.webadmin.bean;

import hippoping.smsgw.api.db.DeliveryReport;
import hippoping.smsgw.api.db.Message;
import hippoping.smsgw.api.db.MessageSms;
import hippoping.smsgw.api.db.MessageWap;
import hippoping.smsgw.api.db.TxQueue;
import hippoping.smsgw.api.db.report.TxQueueReport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

public class MessageHistoryBean
        implements Serializable {

    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    private String sampleProperty;
    private PropertyChangeSupport propertySupport;

    public MessageHistoryBean() {
        this.propertySupport = new PropertyChangeSupport(this);
    }

    public String getSampleProperty() {
        return this.sampleProperty;
    }

    public void setSampleProperty(String value) {
        String oldValue = this.sampleProperty;
        this.sampleProperty = value;
        this.propertySupport.firePropertyChange("sampleProperty", oldValue, this.sampleProperty);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertySupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertySupport.removePropertyChangeListener(listener);
    }

    public String getMessageHistory(String msisdn, int srvc_main_id, int oper_id, String sort, int from, int records, String fdate, String tdate) {
        StringWriter sw = new StringWriter();
        List tqrList = TxQueueReport.getMessageHistory(msisdn, srvc_main_id, oper_id, sort, from, records, fdate, tdate);

        if (tqrList == null) {
            return "<tr><td>no message found.</td></tr>";
        }
        if (tqrList.size() == 0) {
            return "<tr><td>no message found.</td></tr>";
        }

        for (int i = 0; i < tqrList.size(); i++) {
            TxQueueReport tqr = (TxQueueReport) tqrList.get(i);
            TxQueue txq = tqr.getTxQueue();
            Message message = tqr.getMessage();
            List tdrList = tqr.getTxQueueDeliveryReport();

            String content = "";
            if ((message instanceof MessageSms)) {
                for (String tmp : ((MessageSms) message).getContent()) {
                    content = content + tmp;
                }
            } else if ((message instanceof MessageWap)) {
                content = ((MessageWap) message).url + "[" + ((MessageWap) message).title + "]";
            }

            String style = i % 2 > 0 ? " style='background-color:#8EC2E7;'" : "";

            sw.append("<tr " + style + ">");
            sw.append("<td width=120 style='padding-left:5px; font-size:80%; border-right:1px dotted #333; text-align: center;'>" + txq.deliver_dt + "</td>");

            sw.append("<td width=50 style='font-size:70%; border-right:1px dotted #333; vertical-align: middle;'>" + message.getContentType() + (tdrList.size() > 0 ? " <a href='javascript:swap(\\\"" + txq.txid + "\\\");'><img title='show/hide DR records' style='vertical-align:middle;' border=0 src='./images/dr.gif'></a>" : "") + "</td>");

            sw.append("<td width=60% style='padding-left:5px; font-size:80%; font-weight:bold; border-right:1px dotted #333; text-align: left;" + (txq.chrg_flg.equals("MT") ? "color:#FF0000;" : "") + "'>" + content + "</td>");

            sw.append("<td width=50 style='font-size:70%; vertical-align:middle;'>" + txq.getStatus() + "</td>");

            sw.append("</tr>");

            if (tdrList.size() > 0) {
                sw.append("<tr id='" + txq.txid + "' style='display:none;'>" + "<td colspan=4 style='text-align:left; padding: 0 0 10px 0;'>" + "<table class=table3 width=90%><tr>");

                sw.append("<td style='padding-left:5px; font-size:80%; font-weight: bold; text-align: left; vertical-align:middle; border-bottom:1px dotted #333;'><a href='javascript:hide2(\\\"" + txq.txid + "\\\");'><img style='vertical-align:middle;' border=0 title='hide' src='./images/collapse.gif'></a> Delivery Report #" + txq.txid + "</td></tr><tr>");
            }

            for (int j = 0; j < tdrList.size(); j++) {
                DeliveryReport dr = (DeliveryReport) tdrList.get(j);

                sw.append("<td width=100% style='padding-left:5px; font-size:80%; text-align: left;'>" + dr.getDr_timestamp() + " -- " + (dr.getStatus_desc().equals("") ? Integer.valueOf(dr.getStatus_code()) : new StringBuilder().append(dr.getStatus_desc()).append("(").append(dr.getStatus_code()).append(")").toString()) + "</td>");
            }

            if (tdrList.size() > 0) {
                sw.append("</tr></table></td></tr>");
            }

        }

        return sw.toString();
    }
}