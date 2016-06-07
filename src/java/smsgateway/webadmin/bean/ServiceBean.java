package smsgateway.webadmin.bean;

import hippoping.smsgw.api.db.OperConfig;
import hippoping.smsgw.api.db.ServiceElement;
import hippoping.smsgw.api.db.User;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.common.DBPoolManager;

public class ServiceBean
        implements Serializable {

    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    private String sampleProperty;
    private PropertyChangeSupport propertySupport;

    public ServiceBean() {
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

    public String findService(String name, String sort, int from, int records, User user) {
        return findService(name, sort, from, records, null, user);
    }

    public String findService(String name, String sort, int from, int records, ServiceElement.SERVICE_TYPE type, User user) {
        StringWriter sw = new StringWriter();
        try {
            DBPoolManager cp = new DBPoolManager();
            try {
                String whereuid = "";
                if (user != null) {
                    whereuid = " AND ( 0";
                    for (int i = 0; i < user.getChildUid().length; i++) {
                        if (!user.getChildUid()[i].isEmpty()) {
                            whereuid = whereuid + " OR s.uid=" + user.getChildUid()[i];
                        }
                    }
                    whereuid = whereuid + " )";
                }

                String order = " ORDER BY srvc_main_id, oper_id";
                if ((sort != null)
                        && (!sort.equals(""))) {
                    order = " ORDER BY " + sort;
                }

                String limit = "";
                if (from >= 0) {
                    limit = " LIMIT " + from + (records > 0 ? " ," + records : "");
                }

                String wheretype = ServiceElement.SERVICE_TYPE.where(type.getId(), "s.srvc_type");

                String sql = "SELECT m.srvc_main_id"
                        + "     , m.name"
                        + "     , m.price"
                        + "     , s.free_trial"
                        + "     , m.srvc_chrg_amnt"
                        + "     , t.chrg_desc"
                        + "     , s.oper_id"
                        + "     , s.status"
                        + "  FROM srvc_main m"
                        + " INNER JOIN srvc_chrg_type t"
                        + "    ON m.srvc_chrg_type_id = t.srvc_chrg_type_id"
                        + " INNER JOIN srvc_sub s"
                        + "    ON m.srvc_main_id = s.srvc_main_id"
                        + " WHERE 1" 
                        + whereuid 
                        + wheretype 
                        + order 
                        + limit;

                ResultSet rs = cp.execQuery(sql);
                int last_srvc_main_id = -1;
                int first_row = 1;
                int status = 61440;
                while (rs.next()) {
                    if (rs.getInt(1) != last_srvc_main_id) {
                        if (first_row == 0) {
                            sw.append(status + ";");
                        }

                        last_srvc_main_id = rs.getInt(1);
                        status = 61440;

                        sw.append(rs.getString(1) + "|" + rs.getString(2) + "|" + rs.getString(3) + "|" + rs.getString(4) + "|" + rs.getString(5) + "|" + rs.getString(6) + "|");
                    }

                    int curr_status = 1;
                    status |= curr_status << rs.getInt(7) - 1 + rs.getInt(8) * 4;

                    status &= (curr_status << rs.getInt(7) - 1 + 12 ^ 0xFFFFFFFF);

                    first_row = 0;
                }

                rs.close();
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "SQL Error!!", e);
            } finally {
                cp.release();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
        }
        return sw.toString();
    }

    public String findServiceCharge() {
        StringWriter sw = new StringWriter();
        try {
            DBPoolManager cp = new DBPoolManager();
            try {
                String sql = "SELECT *  FROM srvc_chrg_type";

                ResultSet rs = cp.execQuery(sql);
                while (rs.next()) {
                    sw.append(rs.getInt(1) + "|" + rs.getString(2) + ";");
                }

                rs.close();
            } catch (SQLException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "SQL Error!!", e);
            } finally {
                cp.release();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
        }
        return sw.toString();
    }

    public String getServiceName(int status) {
        String tmp2 = "";
        ServiceElement[] se = ServiceElement.getServiceElementList(null, (OperConfig.CARRIER) null, ServiceElement.SERVICE_TYPE.ALL.getId(), status);

        Hashtable ht = new Hashtable();
        for (ServiceElement s : se) {
            if (!ht.containsKey(Integer.valueOf(s.srvc_main_id))) {
                ht.put(Integer.valueOf(s.srvc_main_id), s.srvc_name);
                tmp2 = tmp2 + s.srvc_main_id + "|" + s.srvc_name + ";";
            }
        }
        return tmp2;
    }

    public String getServiceName(int status, int oper_id) {
        String tmp2 = "";
        ServiceElement[] se = ServiceElement.getServiceElementList(null, OperConfig.CARRIER.fromId(oper_id), ServiceElement.SERVICE_TYPE.ALL.getId(), status);

        Hashtable ht = new Hashtable();
        for (ServiceElement s : se) {
            if (!ht.containsKey(Integer.valueOf(s.srvc_main_id))) {
                ht.put(Integer.valueOf(s.srvc_main_id), s.srvc_name);
                tmp2 = tmp2 + s.srvc_main_id + "|" + s.srvc_name + ";";
            }
        }
        return tmp2;
    }

    public String getServiceNameByUserID(int status, int type, User user) {
        String tmp2 = "";
        ServiceElement[] se = ServiceElement.getServiceElementList(null, (OperConfig.CARRIER) null, type, status, user);
        Hashtable ht = new Hashtable();
        for (ServiceElement s : se) {
            if (!ht.containsKey(Integer.valueOf(s.srvc_main_id))) {
                ht.put(Integer.valueOf(s.srvc_main_id), s.srvc_name);
                tmp2 = tmp2 + s.srvc_main_id + "|" + s.srvc_id + " - " + s.srvc_name + ";";
            }
        }
        return tmp2;
    }

    public String getServiceNameByUserID(int status, int type, int oper_id, User user) {
        String tmp2 = "";
        ServiceElement[] se = ServiceElement.getServiceElementList(null, OperConfig.CARRIER.fromId(oper_id), type, status, user);
        Hashtable ht = new Hashtable();
        for (ServiceElement s : se) {
            if (!ht.containsKey(Integer.valueOf(s.srvc_main_id))) {
                ht.put(Integer.valueOf(s.srvc_main_id), s.srvc_name);
                tmp2 = tmp2 + s.srvc_main_id + "|" + s.srvc_id + " - " + s.srvc_name + ";";
            }
        }
        return tmp2;
    }

    public String getDtacServiceNameByUserID(int status, int type, User user, boolean dcc) {
        String tmp2 = "";
        ServiceElement[] se = ServiceElement.getServiceElementList(null, OperConfig.CARRIER.DTAC, type, status, user);
        Hashtable ht = new Hashtable();
        for (ServiceElement s : se) {
            if ((!ht.containsKey(Integer.valueOf(s.srvc_main_id))) && ((!dcc) || (s.isAble2ManageSub()))) {
                ht.put(Integer.valueOf(s.srvc_main_id), s.srvc_name);
                tmp2 = tmp2 + s.srvc_main_id + "|" + s.srvc_id + " - " + s.srvc_name + ";";
            }
        }
        return tmp2;
    }

    public String getDtacSdpServiceNameByUserID(int status, int type, User user, boolean dcc) {
        String tmp2 = "";
        ServiceElement[] se = ServiceElement.getServiceElementList(null, OperConfig.CARRIER.DTAC_SDP, type, status, user);
        Hashtable ht = new Hashtable();
        for (ServiceElement s : se) {
            if ((!ht.containsKey(Integer.valueOf(s.srvc_main_id))) && ((!dcc) || (s.isAble2ManageSub()))) {
                ht.put(Integer.valueOf(s.srvc_main_id), s.srvc_name);
                tmp2 = tmp2 + s.srvc_main_id + "|" + s.srvc_id + " - " + s.srvc_name + ";";
            }
        }
        return tmp2;
    }
}