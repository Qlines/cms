package smsgateway.webadmin.bean;

import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.TxQueue;
import hippoping.smsgw.api.db.report.TxQueueReport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class OverviewBean
        implements Serializable {

    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    private String sampleProperty;
    private PropertyChangeSupport propertySupport;

    public OverviewBean() {
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

    public String getLastTimeSent(int oper_id) throws Exception {
        return TxQueueReport.getLastTimeSent(OperConfig.CARRIER.fromId(oper_id));
    }

    public String getMaxSpeed(int oper_id) throws Exception {
        return String.format("%d", new Object[]{Integer.valueOf(TxQueueReport.getMaxThroughput(OperConfig.CARRIER.fromId(oper_id)))});
    }

    public String getNewTx(int oper_id) throws Exception {
        return String.format("%d", new Object[]{Long.valueOf(TxQueueReport.getTxCount(OperConfig.CARRIER.fromId(oper_id), TxQueue.TX_STATUS.QUEUE))});
    }

    public String getSendingTx(int oper_id) throws Exception {
        return String.format("%d", new Object[]{Long.valueOf(TxQueueReport.getTxCount(OperConfig.CARRIER.fromId(oper_id), TxQueue.TX_STATUS.SENDING))});
    }

    public String getSuccessTx(int oper_id) throws Exception {
        return String.format("%d", new Object[]{Long.valueOf(TxQueueReport.getTxCount(OperConfig.CARRIER.fromId(oper_id), TxQueue.TX_STATUS.SENT))});
    }

    public String getFailTx(int oper_id) throws Exception {
        return String.format("%d", new Object[]{Long.valueOf(TxQueueReport.getTxCount(OperConfig.CARRIER.fromId(oper_id), TxQueue.TX_STATUS.ERROR))});
    }

    public String getBcTx(int oper_id) throws Exception {
        return String.format("%d", new Object[]{Long.valueOf(TxQueueReport.getTxCount(OperConfig.CARRIER.fromId(oper_id), TxQueue.TX_TYPE.BULK))});
    }

    public String getInterTx(int oper_id) throws Exception {
        return String.format("%d", new Object[]{Long.valueOf(TxQueueReport.getTxCount(OperConfig.CARRIER.fromId(oper_id), TxQueue.TX_TYPE.INTERACTIVE))});
    }

    public String getWarnTx(int oper_id) throws Exception {
        return String.format("%d", new Object[]{Long.valueOf(TxQueueReport.getTxCount(OperConfig.CARRIER.fromId(oper_id), TxQueue.TX_TYPE.WARNING))});
    }

    public String getRecurringTx(int oper_id) throws Exception {
        return String.format("%d", new Object[]{Long.valueOf(TxQueueReport.getTxCount(OperConfig.CARRIER.fromId(oper_id), TxQueue.TX_TYPE.RECURRING))});
    }
}