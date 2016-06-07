package smsgateway.webadmin.bean;

import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.Subscriber;
import hippoping.smsgw.api.db.SubscriberFactory;
import hippoping.smsgw.api.db.User;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.io.StringWriter;

public class MemberBean
        implements Serializable {

    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    private String sampleProperty;
    private PropertyChangeSupport propertySupport;

    public MemberBean() {
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

    public String findMember(String[] msisdn, int srvc_main_id, int oper_id, String sort, int from, int records, User user) {
        StringWriter sw = new StringWriter();
        String tmp = "";
        Subscriber[] subList = new SubscriberFactory().getSubscriberList(msisdn, srvc_main_id, oper_id, sort, from, records, user);

        if ((subList != null) && (subList.length > 0)) {
            for (Subscriber sub : subList) {
                if (sub != null) {
                    sw.append(sub.getMsisdn() + "|");
                    sw.append(sub.getShortcode() + " - " + sub.getSrvc_name() + "|");
                    sw.append(OperConfig.CARRIER.fromId(sub.getOper_id()) + "|");
                    sw.append(sub.getFree_trial() + "|");
                    sw.append((sub.getRegister_date() != null ? sub.getRegister_date("dd/MM/yy") : "-") + "|");
                    sw.append((sub.getExpired_date() != null ? sub.getExpired_date("dd/MM/yy") : "-") + "|");
                    sw.append((sub.getBalanced_date() != null ? sub.getBalanced_date("dd/MM/yy") : "-") + "|");
                    sw.append(hippoping.smsgw.api.db.SubscriberGroup.sub_status_detail[sub.getState()] + "|");
                    sw.append(sub.getOper_id() + "|");
                    sw.append(sub.getSrvc_main_id() + "|");
                    sw.append(sub.getState() + ";");
                }
            }

            tmp = sw.toString();

            if (!tmp.isEmpty()) {
                tmp = tmp.substring(0, tmp.length() - 1);
            }
        }
        return tmp;
    }

    public int findMemberLen(String[] msisdn, int srvc_main_id, int oper_id, User user) {
        StringWriter sw = new StringWriter();
        Subscriber[] subList = new SubscriberFactory().getSubscriberList(msisdn, srvc_main_id, oper_id, null, -1, -1, user);

        return subList.length;
    }
}