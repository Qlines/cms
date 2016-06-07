package smsgateway.webadmin.bean;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import lib.common.DatetimeUtil;
import lib.common.InetUtil;

public class InetBean
  implements Serializable
{
  public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
  private String sampleProperty;
  private PropertyChangeSupport propertySupport;

  public InetBean()
  {
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

  public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    this.propertySupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    this.propertySupport.removePropertyChangeListener(listener);
  }

  public String getHostname(String ip) {
    return InetUtil.getHostname(ip);
  }

  public String getLoginName() {
    return InetUtil.getLoginName();
  }

  public long getJvmUptime() {
    RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
    return mx.getUptime();
  }

  public String getJvmStartUptime() {
    RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
    return DatetimeUtil.getDateTime(mx.getStartTime(), "dd/MM/yyyy HH:mm:ss");
  }

  public String getCurrentDatetime() {
    return DatetimeUtil.getDateTime("dd/MM/yyyy HH:mm:ss");
  }

  public String getCurrentDatetime(String format) {
    return DatetimeUtil.getDateTime(format);
  }
}