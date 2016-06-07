<%-- 
    Document   : services_sub_edit
    Created on : Jun 11, 2010, 10:27:04 AM
    Author     : ITZONE
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.UserFactory" %>
<%@page import="hippoping.smsgw.api.db.DroConfigure" %>
<%@page import="hippoping.smsgw.api.db.DroConfigure.DRO_EVENT_TYPE" %>
<%@page import="hippoping.smsgw.api.db.ServiceCharge.SRVC_CHRG" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement" %>
<%@page import="hippoping.smsgw.api.db.ServiceElement.*" %>
<%@page import="hippoping.smsgw.api.db.OperConfig.CARRIER" %>
<%@page import="hippoping.smsgw.api.db.OperConfig" %>
<%@page import="hippoping.smsgw.api.db.AisLegacyCommand" %>
<%@page import="hippoping.smsgw.api.db.ContentManagerMap" %>
<%@page import="hippoping.smsgw.api.db.CCT" %>
<%@page import="hippoping.smsgw.api.db.LogEvent" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_TYPE" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.EVENT_ACTION" %>
<%@page import="hippoping.smsgw.api.db.LogEvent.LOG_LEVEL" %>
<%@page import="java.util.Hashtable" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Arrays" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="./css/cv.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyCorners.css" rel="stylesheet" type="text/css">
        <link href="./css/niftyPrint.css" rel="stylesheet" type="text/css" media="print">
        <link rel="stylesheet" type="text/css" href="./css/dashboard.css" media="screen" />
        <style type="text/css">
            body{margin:0px; padding: 5px; background: black;
                 font: 100.01% "Trebuchet MS",Verdana,Arial,sans-serif}
            h1,h2,p{margin: 0 10px}
            h1{font-size: 250%;color: #FFF}
            h2{font-size: 200%;color: #f0f0f0}
            p{padding-bottom:1em}
            h2{padding-top: 0.3em}
            table.tr.th{border-bottom: 0}
            input.message{width:450px;}
            input.number{width:50px;}
            input.shorttext{width:50px}
            input.mediumtext{width:100px}
            input.longtext{width:200px;font-size: .9em}
            legend{margin-left:10px}
        </style>
        <script src="./js/nifty.js" type="text/javascript"></script>
        <script src='./js/utils.js' type='text/javascript'></script>
        <script src='./js/filter_input.js' type='text/javascript'></script>
        <script src='./js/webstyle.js' type='text/javascript'></script>
        <script src='./js/datetime.js' type='text/javascript'></script>
        <%
            String encoding = "UTF-8";
            if (request.getCharacterEncoding() != null) {
                encoding = request.getCharacterEncoding();
            }

            String _srvc_main_id = request.getParameter("srvc_main_id");
            String _oper_id = request.getParameter("oper_id");
            String _srvc_chrg = request.getParameter("srvcchrg");
            String _srvc_name = "";
            String _cmd = request.getParameter("cmd");

            if (_srvc_main_id == null || _srvc_main_id.isEmpty()
                    || _oper_id == null || _oper_id.isEmpty()) {
                out.println("<script>alert('input parameters error!!');history.back(-1);</script>");
                return;
            }

            String _srvc_id = "";
            String _srvc_id_non_chrg = "";
            String _srvc_id_mo_test = "";
            String _srvc_id_mo = "";
            String _srvc_id_mt = "";
            String _srvc_id_mt_chrg = "";
            String _bcast_srvc_id = "";
            int _srvc_type = 0;

            String _ivr_register = "";
            String _ivr_unregister = "";
            String _sms_register = "";
            String _sms_unregister = "";
            String _thrd_prty_register = "";
            String _thrd_prty_unregister = "";
            String _sender = "";
            String _chrg_flg = "";

            int _free_trial = 0;
            int _ctnt_ctr = -1;
            int _rmdr_ctr = 0;
            int _rchg_ctr = 0;

            String _msg_sub_ft = "";
            String _msg_usub_ft = "";
            String _msg_warn_ft = "";
            String _msg_sub_nm = "";
            String _msg_usub_nm = "";
            String _msg_warn_nm = "";
            String _msg_err_no_srvc = "";
            String _msg_err_dup = "";

            CCT _cct = CCT.findBySrvcMainId(0);
            String _cct_register = _cct.getRegister();
            String _cct_cancel = _cct.getCancel();
            String _cct_warning = _cct.getWarning();
            String _cct_recurring = _cct.getRecurring();
            String _cct_broadcast = _cct.getBroadcast();
            String _cct_invalid = _cct.getInvalid();
            String _cct_charge = _cct.getCharge();
            String _cpaction_register = _cct.getCpaction_register();
            int _status = 0;

            int _conf_id = 0;
            int _conf_id_test = 0;
            int _conf_id_non_chrg = 0;
            int _uid = 0;
            int _priority = 0;

            SRVC_CHRG sc = null;

            ServiceElement se = null;
            User user = null;
            List<User> userList = null;
            List<OperConfig> linkList = null;

            // Command Set for AIS Legacy
            AisLegacyCommand _aisLegacyCommand = null;
            String _mt_chrg_cmd = "";
            String _mt_non_chrg_cmd = "";
            String _mt_warn_cmd = "";
            String _mt_sub_cmd = "";
            String _mt_unsub_cmd = "";

            DroConfigure _dro_configure = new DroConfigure();

            String _dbcode = "";
            if (_cmd.equals("add")) {
                _dbcode = CARRIER.fromId(Integer.parseInt(_oper_id)) + "_";
            }

            if (_cmd.equals("submit")) { // add or modify sub-service profile

                String _subcmd = request.getParameter("subcmd");
                String[] columns = {"srvc_main_id", "oper_id", "srvc_id", "srvc_id_non_chrg", "srvc_id_mo_test", 
                "srvc_id_mo", "srvc_id_mt", "srvc_id_mt_chrg", "bcast_srvc_id", "srvc_type", "chrg_flg", 
                "ivr_register", "ivr_unregister", "sms_register", "sms_unregister", "thrd_prty_register", "thrd_prty_unregister", 
                "sender", "free_trial", "ctnt_ctr", "rmdr_ctr", "rchg_ctr", "msg_sub_ft", "msg_usub_ft", "msg_warn_ft", "msg_sub_nm", "msg_usub_nm", 
                "msg_warn_nm", "msg_err_no_srvc", "msg_err_dup", "status", "conf_id", "conf_id_test", "conf_id_non_chrg", "uid", "priority"};
                String[] col_encoding = {"msg_sub_ft", "msg_usub_ft", "msg_warn_ft", "msg_sub_nm", "msg_usub_nm", "msg_warn_nm", "msg_err_no_srvc", "msg_err_dup"};
                List<String> col_encoding_list = Arrays.asList(col_encoding);
                Hashtable<String, String> raw = new Hashtable<String, String>();
                for (int i = 0; i < columns.length; i++) {
                    String value = request.getParameter(columns[i]);
                    if (value == null) {
                        System.err.println("warning: parameter " + columns[i] + " is null!!");
                        value = "";
                    } else if (col_encoding_list.contains(columns[i])) {
                        value = new String(request.getParameter(columns[i]).getBytes("ISO8859_1"), encoding);
                    }

                    raw.put(columns[i], value);
                }

                // add or edit AIS Legacy Command
                int oper_id = Integer.parseInt(request.getParameter("oper_id"));
                if (oper_id == CARRIER.AIS_LEGACY.getId()) {
                    String[] commands = {"srvc_main_id", "mt_chrg_cmd", "mt_non_chrg_cmd", "mt_warn_cmd", "mt_sub_cmd", "mt_unsub_cmd"};
                    Hashtable<String, String> cmd_map = new Hashtable<String, String>();
                    for (String cmd : commands) {
                        String value = request.getParameter(cmd);
                        if (value == null) {
                            System.err.println("warning: parameter " + cmd + " is null!!");
                            value = "";
                        }

                        cmd_map.put(cmd, value);
                    }

                    int row = AisLegacyCommand.add(cmd_map);
                    if (row == 0) {
                        AisLegacyCommand.sync(cmd_map);
                    }
                }

                // add or edit AIS SSS CCT
                if (oper_id == CARRIER.AIS.getId()) {
                    String[] ccts = {"srvc_main_id", "cct_register", "cct_cancel", "cct_warning", "cct_recurring", "cct_broadcast", "cct_invalid", "cct_charge", "cpaction_register"};
                    Hashtable<String, String> cct_map = new Hashtable<String, String>();
                    for (String cct : ccts) {
                        String value = request.getParameter(cct);
                        if (value == null) {
                            System.err.println("warning: parameter " + cct + " is null!!");
                            value = "";
                        }

                        cct_map.put(cct, value);
                    }

                    int id = CCT.add(cct_map);
                    if (id == 0) {
                        CCT.sync(cct_map);
                    }
                }

                int row = 0;
                if (_subcmd.equals("add")) {
                    row = ServiceElement.add(raw);
                    out.println("<script>alert('" + ((row > 0) ? "New service settings successfully saved." : "Error create new service failed!!") + "')</script>");

                    // log event
                    LogEvent.log(EVENT_TYPE.SERVICE, EVENT_ACTION.ADD, "create service sub",
                            (User) request.getSession().getAttribute("USER"),
                            null,
                            CARRIER.fromId(Integer.parseInt(request.getParameter("oper_id"))),
                            Integer.parseInt(request.getParameter("srvc_main_id")), 0, 0, LOG_LEVEL.INFO);

                    // add new dbcode
                    int srvc_type = Integer.parseInt(request.getParameter("srvc_type"));
                    if (row > 0 && (srvc_type & SERVICE_TYPE.SUBSCRIPTION.getId()) > 0) {
                        String[] dbcodes = request.getParameter("dbcode").split(",");
                        for (String code : dbcodes) {
                            new ContentManagerMap().add(code.trim(), CARRIER.fromId(oper_id), Integer.parseInt(request.getParameter("srvc_main_id")));
                        }
                    }
                } else if (_subcmd.equals("edit")) {
                    row = ServiceElement.sync(raw);
                    out.println("<script>alert('" + ((row > 0) ? "Service settings successfully saved." : "Error modify service failed!!") + "')</script>");

                    // log event
                    LogEvent.log(EVENT_TYPE.SERVICE, EVENT_ACTION.MODIFY, "modify service sub",
                            (User) request.getSession().getAttribute("USER"),
                            null,
                            CARRIER.fromId(Integer.parseInt(request.getParameter("oper_id"))),
                            Integer.parseInt(request.getParameter("srvc_main_id")), 0, 0, LOG_LEVEL.INFO);

                    // add or remove dbcode
                    int srvc_type = Integer.parseInt(request.getParameter("srvc_type"));
                    if (row > 0) {
                        // remove old records
                        new ContentManagerMap().remove(CARRIER.fromId(oper_id), Integer.parseInt(request.getParameter("srvc_main_id")));

                        // add new record
                        if ((srvc_type & SERVICE_TYPE.SUBSCRIPTION.getId()) > 0) {
                            String[] dbcodes = request.getParameter("dbcode").split(",");
                            for (String code : dbcodes) {
                                if (!code.trim().isEmpty()) {
                                    new ContentManagerMap().add(code.trim(), CARRIER.fromId(oper_id), Integer.parseInt(request.getParameter("srvc_main_id")));
                                }
                            }
                        }
                    }
                }

                // add dro configure if oper is TRUE
                String tmp = request.getParameter("dro_flag");
                if (row > 0 && tmp != null) {
                    se = new ServiceElement(Integer.parseInt(request.getParameter("srvc_main_id")), oper_id, SERVICE_TYPE.ALL.getId(), SERVICE_STATUS.ALL.getId());
                    DroConfigure dro = new DroConfigure(se);

                    dro.setDroFlag(Integer.parseInt(tmp));
                    row = dro.sync();
                    if (row == 0) {
                        DroConfigure.add(dro);
                    }
                }

                out.println("<script>window.location='services_search.jsp'</script>");
                return;
            } else {
                sc = SRVC_CHRG.fromId(Integer.parseInt(_srvc_chrg));
                _srvc_name = new String(request.getParameter("srvc_name").getBytes("ISO8859_1"), encoding);
                if (_cmd.equals("edit") || _cmd.equals("copy")) { // edit service sub

                    // backup master copy srvc_main_id
                    String tmp_srvc_main_id = _srvc_main_id;
                    String tmp_oper_id = _oper_id;
                    if (_cmd.equals("copy")) {
                        _srvc_main_id = request.getParameter("copy_srvc_main_id");
                        tmp_oper_id = request.getParameter("copy_oper_id");
                        _cmd = "add";
                    }

                    se = new ServiceElement(Integer.parseInt(_srvc_main_id), Integer.parseInt(_oper_id), SERVICE_TYPE.ALL.getId(), SERVICE_STATUS.ALL.getId());

                    Hashtable<String, String> raw = se.getRawData();

                    // inquiry service information
                    _srvc_id = raw.get("srvc_id");
                    _srvc_id_non_chrg = raw.get("srvc_id_non_chrg");
                    _srvc_id_mo_test = raw.get("srvc_id_mo_test");
                    _srvc_id_mo = raw.get("srvc_id_mo");
                    _srvc_id_mt = raw.get("srvc_id_mt");
                    _srvc_id_mt_chrg = raw.get("srvc_id_mt_chrg");

                    _bcast_srvc_id = raw.get("bcast_srvc_id");
                    try {
                        _srvc_type = Integer.parseInt(raw.get("srvc_type"));
                    } catch (Exception e) {
                    }

                    _ivr_register = raw.get("ivr_register");
                    _ivr_unregister = raw.get("ivr_unregister");
                    _sms_register = raw.get("sms_register");
                    _sms_unregister = raw.get("sms_unregister");
                    _thrd_prty_register = raw.get("thrd_prty_register");
                    _thrd_prty_unregister = raw.get("thrd_prty_unregister");
                    _sender = raw.get("sender");
                    _chrg_flg = raw.get("chrg_flg");

                    try {
                        _free_trial = Integer.parseInt(raw.get("free_trial"));
                    } catch (Exception e) {
                    }
                    try {
                        _ctnt_ctr = Integer.parseInt(raw.get("ctnt_ctr"));
                    } catch (Exception e) {
                    }
                    try {
                        _rmdr_ctr = Integer.parseInt(raw.get("rmdr_ctr"));
                    } catch (Exception e) {
                    }
                    try {
                        _rchg_ctr = Integer.parseInt(raw.get("rchg_ctr"));
                    } catch (Exception e) {
                    }

                    _msg_sub_ft = raw.get("msg_sub_ft");
                    _msg_usub_ft = raw.get("msg_usub_ft");
                    _msg_warn_ft = raw.get("msg_warn_ft");
                    _msg_sub_nm = raw.get("msg_sub_nm");
                    _msg_usub_nm = raw.get("msg_usub_nm");
                    _msg_warn_nm = raw.get("msg_warn_nm");
                    _msg_err_no_srvc = raw.get("msg_err_no_srvc");
                    _msg_err_dup = raw.get("msg_err_dup");

                    try {
                        _status = Integer.parseInt(raw.get("status"));
                    } catch (Exception e) {
                    }
                    try {
                        _conf_id = Integer.parseInt(raw.get("conf_id"));
                    } catch (Exception e) {
                    }
                    try {
                        _conf_id_test = Integer.parseInt(raw.get("conf_id_test"));
                    } catch (Exception e) {
                    }
                    try {
                        _conf_id_non_chrg = Integer.parseInt(raw.get("conf_id_non_chrg"));
                    } catch (Exception e) {
                    }
                    try {
                        _uid = Integer.parseInt(raw.get("uid"));
                    } catch (Exception e) {
                    }
                    try {
                        _priority = Integer.parseInt(raw.get("priority"));
                    } catch (Exception e) {
                    }

                    if (Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) {
                        try {
                            _aisLegacyCommand = new AisLegacyCommand(Integer.parseInt(_srvc_main_id));
                            if (_aisLegacyCommand != null) {
                                _mt_chrg_cmd = _aisLegacyCommand.getMt_chrg_cmd() == null ? "" : _aisLegacyCommand.getMt_chrg_cmd();
                                _mt_non_chrg_cmd = _aisLegacyCommand.getMt_non_chrg_cmd() == null ? "" : _aisLegacyCommand.getMt_non_chrg_cmd();
                                _mt_warn_cmd = _aisLegacyCommand.getMt_warn_cmd() == null ? "" : _aisLegacyCommand.getMt_warn_cmd();
                                _mt_sub_cmd = _aisLegacyCommand.getMt_sub_cmd() == null ? "" : _aisLegacyCommand.getMt_sub_cmd();
                                _mt_unsub_cmd = _aisLegacyCommand.getMt_unsub_cmd() == null ? "" : _aisLegacyCommand.getMt_unsub_cmd();
                            }
                        } catch (Exception e) {
                        }
                    } else if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) {
                        try {
                            _cct = CCT.findBySrvcMainId(Integer.parseInt(_srvc_main_id));
                            if (_cct != null) {
                                _cct_register = _cct.getRegister();
                                _cct_cancel = _cct.getCancel();
                                _cct_warning = _cct.getWarning();
                                _cct_recurring = _cct.getRecurring();
                                _cct_broadcast = _cct.getBroadcast();
                                _cct_invalid = _cct.getInvalid();
                                _cct_charge = _cct.getCharge();
                                _cpaction_register = _cct.getCpaction_register() == null ? "" : _cct.getCpaction_register();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // get dbcode
                    List<ContentManagerMap> dbcodeList = ContentManagerMap.getDbcodes(CARRIER.fromId(Integer.parseInt(_oper_id)), Integer.parseInt(_srvc_main_id));
                    for (int i = 0; i < dbcodeList.size(); i++) {
                        _dbcode += dbcodeList.get(i).getDb_code();
                        if (i < dbcodeList.size() - 1) {
                            _dbcode += ",";
                        }
                    }

                    // get dro configure
                    _dro_configure = new DroConfigure(se);

                    // restore srvc_main_id
                    _srvc_main_id = tmp_srvc_main_id;
                    _oper_id = tmp_oper_id;
                }

                user = (User) session.getAttribute("USER");
                userList = null;
                if (_cmd.equals("edit")) {
                    userList = User.getAllUser();
                    //user = new User(_uid);
                    user = UserFactory.getUser(_uid);
                }

                linkList = OperConfig.getAll();
            }
        %>
        <script type="text/javascript">
            function doAddLink(id) {
                var all_link = new Array("conf_id", "conf_id_test", "conf_id_non_chrg");
                var ret = window.showModalDialog('link_new.jsp?cmd=add','', 'dialogWidth:640px;dialogHeight:670px');
                if (ret.trim().length > 0 && ret.indexOf('|') >= 0) {
                    var options = ret.split('|');
                    for (var link in all_link) {
                        var obj = getElement(all_link[link]);
                        if (obj) {
                            addOption(obj, options[0], options[1], (id == all_link[link]));
                        }
                    }
                }
            }

            function copyServiceId() {
                var frm = document.forms["servicesSubEditFrm"];
                frm.srvc_id_mo_test.value = frm.srvc_id.value;
            }

            function copyServiceIdMo() {
                var frm = document.forms["servicesSubEditFrm"];
                frm.srvc_id_mo.value = frm.srvc_id.value;
            }

            function copyConfigId() {
                var frm = document.forms["servicesSubEditFrm"];
                frm.conf_id_non_chrg.selectedIndex = frm.conf_id.selectedIndex;
            <%
                if (Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                        || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) { // DTAC use the only one link config
            %>
        frm.conf_id_test.selectedIndex = frm.conf_id.selectedIndex;
            <%  }
            %>
                }

                function operid_onchange() {
            <%
                if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                        || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) { // DTAC use the only one link config
            %>
                    getElement('chainq').checked = false;
                    getElement('cpvalidate').checked = false;
                    getElement('cpvalidate').disabled = false;
                    
                    hideUnhideCct();
                    hideUnhideAisCpValidateDiv();
                    hideUnhideAisLegacyCommandDiv();
                    hideUnhideMessageDiv();
                    hideUnhideServiceIdMoDiv();
                    hideUnhideServiceIdNonchargeDiv();
                    hideUnhideSubscriptionCounterDiv();
                    hideUnhideSSSTypeDiv();
                    hideUnhideCpValidateSelectDiv();
                    hideUnhideSftpSelectDiv();
                    hideUnhideSrvcIdMtDiv();
            <%  }
            %>
            <%
                if (Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                        || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) { // DTAC use the only one link config
            %>
                    hideUnhideSrvcIdMtDiv();
            <%  }
            %>
                }

                function copyServiceIdTruemove() {
                    var frm = document.forms["servicesSubEditFrm"];
                    frm.srvc_id_mt_chrg.value = frm.srvc_id_mo.value;
                }

                function hideUnhideCct() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('cctdiv');
                    if (frm.oper_id.options[frm.oper_id.selectedIndex].value==<%=CARRIER.AIS.getId()%>) {
                        show2('cctdiv');
                    }
                }

                function hideUnhideServiceIdMoDiv() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('serviceidmodiv');
                    if (frm.oper_id.options[frm.oper_id.selectedIndex].value==<%=CARRIER.AIS.getId()%>) {
                        show2('serviceidmodiv');
                    }
                }

                function hideUnhideServiceIdNonchargeDiv() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('serviceidnonchargediv');
                    if (frm.oper_id.options[frm.oper_id.selectedIndex].value==<%=CARRIER.AIS_LEGACY.getId()%>) {
                        show2('serviceidnonchargediv');
                    }
                }

                function hideUnhideDbcodeDiv() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('dbcodediv');
                    if (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SUBSCRIPTION.getId()%>) {
                        show2('dbcodediv');
                    }
                }

                function hideUnhideSSSTypeDiv() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('ssstypediv');
                    if (frm.oper_id.options[frm.oper_id.selectedIndex].value==<%=CARRIER.AIS.getId()%>) {
                        show2('ssstypediv');
                    }
                }

                function hideUnhideSrvcIdMtDiv() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('srvcidmtdiv');
                    if (frm.oper_id.options[frm.oper_id.selectedIndex].value==<%=CARRIER.AIS.getId()%>
                        || frm.oper_id.options[frm.oper_id.selectedIndex].value==<%=CARRIER.DTAC_SDP.getId()%>) {
                        show2('srvcidmtdiv');
                    }
                }

                function hideUnhideSubscriptionDiv() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('subscriptionDiv');
                    if (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SUBSCRIPTION.getId()%>) {
                        show2('subscriptionDiv');
                    }
                }

                function hideUnhideChrgflgDiv() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('chrgflgdiv');
                    if (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SMSDOWNLOAD.getId()%>
                        || frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SUBSCRIPTION.getId()%>) {
                        show2('chrgflgdiv');
                    }
                }

                function hideUnhideSubscriptionCounterDiv() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('subscriptionCounterDiv');
                    // SSS TypeL or TypeL+
                    if ((frm.oper_id.value == <%=CARRIER.AIS.getId()%>) 
                        && !(frm.sss_type[2].checked || frm.sss_type[3].checked)) {return} 
                    if (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SUBSCRIPTION.getId()%>
                        && <%=_srvc_chrg%> != <%=SRVC_CHRG.PER_MESSAGE.getId()%> ) {
                        show2('subscriptionCounterDiv');
                    }
                }

                function hideUnhideAisCpValidateDiv() {
            <%
                if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) {
            %>
                    // always show sub/unsub command for AIS
                    var frm = document.forms["servicesSubEditFrm"];
                    var old_checked = getElement("cpvalidate").checked;
                    hide2('aisCpValidateDiv');
                    //var obj = getElement('cpvalidate');
                    if (frm.oper_id.value == <%=CARRIER.AIS.getId()%>) {
                        show2('aisCpValidateDiv');
                        getElement("cpvalidate").checked = old_checked;
                    }
            <%
                }
            %>
                }

                function hideUnhideCpValidateSelectDiv() {
                    // hide/unhide AIS CP Validate checkbox division
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('cpvalidateSelectDiv');
                    if (frm.oper_id.options[frm.oper_id.selectedIndex].value==<%=CARRIER.AIS.getId()%>) {
                        show2('cpvalidateSelectDiv');
                    }
                }

                function hideUnhideSftpSelectDiv() {
                    // hide/unhide AIS CP Validate checkbox division
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('sftpSelectDiv');
                    if (frm.oper_id.options[frm.oper_id.selectedIndex].value==<%=CARRIER.AIS_LEGACY.getId()%>) {
                        show2('sftpSelectDiv');
                    }
                }

                function hideUnhideAisLegacyCommandDiv() {
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('aisLegacyCommandDiv');
                    if (frm.oper_id.options[frm.oper_id.selectedIndex].value==<%=CARRIER.AIS_LEGACY.getId()%>) {
                        show2('aisLegacyCommandDiv');
                    }
                }

                function hideUnhideNoCssDiv() {
            <%
                if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId() || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
            %>
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('nocss');
                    hide2('nocss_label');
                    if (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SUBSCRIPTION.getId()%>) {
                        show2('nocss');
                        show2('nocss_label');
                    }
            <%
                }
            %>
                }

                function hideUnhideUssdDirectReplyDiv() {
            <%
                if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId() || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
            %>
                    var frm = document.forms["servicesSubEditFrm"];
                    hide2('ussddirectreply');
                    hide2('ussddirectreply_label');
                    if (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SMSDOWNLOAD.getId()%>) {
                        show2('ussddirectreply');
                        show2('ussddirectreply_label');
                    }
            <%
                }
            %>
                }

                function changeSSSType() {
            <%
                if ((_cmd.equals("add")
                        && (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                        || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()))
                        || (_cmd.equals("edit") && Integer.parseInt(_oper_id) == CARRIER.AIS.getId())) {
            %>
                    var frm = document.forms["servicesSubEditFrm"];
                    var old_checked = 0;
                    for(var i=0; i < frm.sss_type.length; i++){
                        if(frm.sss_type[i].checked) {
                            old_checked = i;
                        }
                    }
                    
                    //getElement('ssstype1').disabled = true;
                    //getElement('ssstype2').disabled = true;
                    //getElement('ssstype3').disabled = true;
                    //getElement('ssstype4').disabled = true;
                    getElement('ssstype1').disabled = false;
                    getElement('ssstype2').disabled = false;
                    getElement('ssstype3').disabled = false;
                    getElement('ssstype4').disabled = false;
                    getElement('ssstype5').disabled = false;
            <%
                if ((_cmd.equals("add"))) {
            %>
                    if (frm.oper_id.options[frm.oper_id.selectedIndex].value == <%=CARRIER.AIS.getId()%>) {
            <%
                }
            %>
                        // remove this to support to create SMS Download service with other type
                        if (false)
                            switch (parseInt(frm.service_type.options[frm.service_type.selectedIndex].value)){
                                case <%=SERVICE_TYPE.SUBSCRIPTION.getId()%>:
                                        getElement('ssstype1').disabled = false;
                                    getElement('ssstype2').disabled = false;
                                    getElement('ssstype3').disabled = false;
                                    getElement('ssstype4').disabled = false;
                                    obj = getElement('ssstype1');
                                    if (old_checked < 3) {
                                        frm.sss_type[old_checked].checked = true;
                                    }
                                break;
                            case <%=SERVICE_TYPE.SMSDOWNLOAD.getId()%>:
                                    obj = getElement('ssstype5');
                                obj.disabled = false;
                                obj.checked = true;
                                break;
                            default:
                                // do nothing
                            }
            <%
                if ((_cmd.equals("add"))) {
            %>
                        }
            <%                }
            %>
            <%
                }
            %>
                    }

                    function hideUnhideMessageDiv() {
                        var frm = document.forms["servicesSubEditFrm"];
                        hide2('MessageDiv');
                        if (frm.oper_id) {
                            if ((frm.oper_id.type == "select-one") && frm.oper_id.options[frm.oper_id.selectedIndex].value == <%=CARRIER.AIS_LEGACY.getId()%>) {
                                show2('MessageDiv');
                            } else if ((frm.oper_id.type == "select-one") && frm.oper_id.options[frm.oper_id.selectedIndex].value == <%=CARRIER.AIS.getId()%>
                                && frm.service_type.options[frm.service_type.selectedIndex].value == 2) {
                                show2('MessageDiv');
                            } else if (frm.oper_id.value == <%=CARRIER.AIS.getId()%>
                            //&& frm.service_type.options[frm.service_type.selectedIndex].value == 2) {
                                && (frm.sss_type[2].checked || frm.sss_type[3].checked)) {
                                show2('MessageDiv');
                            } else if (frm.oper_id.value == <%=CARRIER.DTAC.getId()%> 
                                || frm.oper_id.value == <%=CARRIER.DTAC_SDP.getId()%>) {
                                var dcc = getElement('dcc');
                                if (dcc && dcc.checked) {
                                    show2('MessageDiv');
                                }
                            } else if (frm.oper_id.value == <%=CARRIER.AIS_LEGACY.getId()%>) {
                                show2('MessageDiv');
                            } else if (frm.oper_id.value == <%=CARRIER.TRUE.getId()%> 
                                || frm.oper_id.value == <%=CARRIER.TRUEH.getId()%>) {
                                
                                var ussddirectreply = getElement('ussddirectreply');
                                if (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SUBSCRIPTION.getId()%>
                                    || (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SMSDOWNLOAD.getId()%>
                                    && (ussddirectreply && ussddirectreply.checked)
                                    )
                                ) {
                                    show2('MessageDiv');
                                }
                            }
                        }
                    }
                    
                    function chainQuiz_OnChange(obj) {
                        var obj2 = getElement('cpvalidate');
                        if (obj2) {
                            obj2.disabled = obj.checked;
                            if (obj && obj.checked) {
                                obj2.checked = obj.checked;
                                hideUnhideAisCpValidateDiv();
                            }
                        }
                    
                    }

            <%
                if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                        || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
            %>
                    function dro_checkall(checked) {
                        var frm = document.forms["servicesSubEditFrm"];
                        for(var i=0; i < frm.dro_event.length; i++){
                            frm.dro_event[i].checked = checked;
                        }
                    }
            <%            }
            %>
                    function _onsubmit() {
                        var frm = document.forms["servicesSubEditFrm"];
                        var srvc_type = frm.service_type.options[frm.service_type.selectedIndex].value;
                        for(var i=0; i < frm.ctnt_type.length; i++){
                            if(frm.ctnt_type[i].checked) {
                                srvc_type = parseInt(srvc_type) + parseInt(frm.ctnt_type[i].value);
                            }
                        }
            <%
                if ((_cmd.equals("add")
                        && (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                        || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()))
                        || (_cmd.equals("edit") && Integer.parseInt(_oper_id) == CARRIER.AIS.getId())) {
            %>
            <%
                if (_cmd.equals("add")) {
            %>
                        if (frm.oper_id.options[frm.oper_id.selectedIndex].value == <%=CARRIER.AIS.getId()%>)
            <%          }
            %>
                        for(var i=0; i < frm.sss_type.length; i++){
                            if(frm.sss_type[i].checked) {
                                srvc_type = parseInt(srvc_type) + parseInt(frm.sss_type[i].value);
                            }
                        }
            <%
                }
            %>
                    
                        // MTCHARGE flag -> srvc_type
                        if (<%=_srvc_chrg%> != <%=SRVC_CHRG.PER_MESSAGE.getId()%>
                            && frm.chrg_leg.options[frm.chrg_leg.selectedIndex].value == "MT") {
                            srvc_type = parseInt(srvc_type) + <%=SERVICE_TYPE.MTCHARGE.getId()%>;
                        }

                        frm.srvc_type.value = srvc_type;

            <%
                if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) {
            %>
            <%
                if (_cmd.equals("add")) {
            %>
                        if (frm.oper_id.options[frm.oper_id.selectedIndex].value == <%=CARRIER.AIS.getId()%>)
            <%          }
            %>
                        frm.srvc_id_non_chrg.value = frm.srvc_id.value;
            <%
                }
            %>

            <%
                if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                        || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
            %>
                        var dro_flag = 0;
                        for(var i=0; i < frm.dro_event.length; i++){
                            if(frm.dro_event[i].checked) {
                                dro_flag = parseInt(dro_flag) + parseInt(frm.dro_event[i].value);
                            }
                        }
                        frm.dro_flag.value = dro_flag;
            <%            }
            %>

                        // chrg_flg
                        frm.chrg_flg.value = "MT";
                        if (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SUBSCRIPTION.getId()%>
                            && <%=_srvc_chrg%> != <%=SRVC_CHRG.PER_MESSAGE.getId()%> ) {
                            frm.chrg_flg.value = "MO";
                        } else if (frm.service_type.options[frm.service_type.selectedIndex].value==<%=SERVICE_TYPE.SMSDOWNLOAD.getId()%> ) {
                            frm.chrg_flg.value = frm.chrg_leg.options[frm.chrg_leg.selectedIndex].value;
                        }

                        // test
                        //return false;
                    }

                    window.onload=function() {
            <%
                if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                        || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) {
                    if (_cmd.equals("add")) {
                        out.println("hideUnhideCct();");
                        out.println("hideUnhideAisLegacyCommandDiv();");
                        out.println("hideUnhideServiceIdMoDiv();");
                        out.println("hideUnhideServiceIdNonchargeDiv();");
                        out.println("hideUnhideSSSTypeDiv();");
                        out.println("hideUnhideSrvcIdMtDiv();");
                        out.println("hideUnhideCpValidateSelectDiv();");
                        out.println("hideUnhideSftpSelectDiv();");
                    }
                }
            %>
            <%
                if (Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                        || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) {
                    if (_cmd.equals("add")) {
                        out.println("hideUnhideSrvcIdMtDiv();");
                    }
                }
            %>
                        changeSSSType();
                        hideUnhideSubscriptionDiv();
                        hideUnhideSubscriptionCounterDiv();
                        hideUnhideNoCssDiv();
                        hideUnhideUssdDirectReplyDiv();
                        hideUnhideDbcodeDiv();
                        hideUnhideMessageDiv();
                        hideUnhideChrgflgDiv();
                        hideUnhideAisCpValidateDiv();
                        
                        chainQuiz_OnChange(getElement('chainq'));

                        if(NiftyCheck())Rounded("div#content","#C0CDF2","#377CB1");
                        if(NiftyCheck())Rounded("div#content2","#377CB1","#FFF");
                    }
        </script>
    </head>
    <body class="content">
        <div id="servicesSubEdit" style="width:100%;">
            <form target="ctFrame" name="servicesSubEditFrm" method="post" action="services_sub_edit.jsp"
                  onsubmit="return _onsubmit();">
                <input type='hidden' name='cmd' value='submit'>
                <input type='hidden' name='subcmd' value='<%=_cmd%>'>
                <input type='hidden' name='srvc_main_id' value='<%=_srvc_main_id%>'>
                <input type='hidden' name='srvc_type' value=0>
                <%
                    if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                            || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
                %>
                <input type='hidden' name='dro_flag' value=0>
                <%                            }
                %>
                <div id="content" style="width:100%">
                    <h2><%=(_cmd.equals("edit")) ? "Edit" : "Create"%> sub-service for <%=_srvc_name%></h2><hr>
                    <blockquote style='width: 100%; height: 570px; overflow-y: scroll; padding: 0; margin: 0;'>
                        <div id="content2" style="width:95%; text-align:center; background-color:#FFF; margin:10px; padding:0px;">
                            <table align="center" class="table4" style="width:90%; margin:20px 0 20px 0; padding:0;">
                                <tr>
                                    <th style="border-bottom:0px;" width="30%">Create Service for</th>
                                    <td width="25%">
                                        <div><b>
                                                <%
                                                    if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                                                            || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) {
                                                        if (_cmd.equals("edit")) {
                                                %>
                                                <%=(Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) ? "AIS-CDG" : "AIS-LEGACY"%>
                                                <input type='hidden' name='oper_id' value='<%=_oper_id%>'>
                                                <%
                                                }       else {
                                                %>
                                                <select name="oper_id" onchange="operid_onchange()">
                                                    <option value="<%=CARRIER.AIS_LEGACY.getId()%>"<%=(Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) ? " selected" : ""%>>AIS-LEGACY</option>
                                                    <option value="<%=CARRIER.AIS.getId()%>"<%=(Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) ? " selected" : ""%>>AIS-CDG</option>
                                                </select>
                                                <%
                                                        }
                                                %>
                                                <%
                                                    } else if (Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                                                            || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) {
                                                        if (_cmd.equals("edit")) {
                                                %>
                                                <%=(Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()) ? "DTAC-CPA" : "DTAC-SDP"%>
                                                <input type='hidden' name='oper_id' value='<%=_oper_id%>'>
                                                <%
                                                }       else {
                                                %>
                                                <select name="oper_id" onchange="operid_onchange()">
                                                    <option value="<%=CARRIER.DTAC.getId()%>"<%=(Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()) ? " selected" : ""%>>DTAC-CPA</option>
                                                    <option value="<%=CARRIER.DTAC_SDP.getId()%>"<%=(Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) ? " selected" : ""%>>DTAC-SDP</option>
                                                </select>
                                                <%
                                                        }
                                                %>
                                                <%
                                                } else {
                                                %>
                                                <%=CARRIER.fromId(Integer.parseInt(_oper_id)).name()%>
                                                <input type='hidden' name='oper_id' value='<%=_oper_id%>'>
                                                <%
                                                    }
                                                %>
                                            </b></div>
                                    </td>
                                    <th style="border-bottom:0px;" width="20%">
                                        Status:
                                    </th>
                                    <td width="30%">
                                        <%
                                            if (_cmd.equals("edit")) {
                                        %>
                                        <select name="status">
                                            <option value="<%=SERVICE_STATUS.OFF.getDbId()%>"<%=(_status == SERVICE_STATUS.OFF.getDbId()) ? " selected" : ""%>>
                                                <%=SERVICE_STATUS.OFF.toString()%></option>
                                            <option value="<%=SERVICE_STATUS.ON.getDbId()%>"<%=(_status == SERVICE_STATUS.ON.getDbId()) ? " selected" : ""%>>
                                                <%=SERVICE_STATUS.ON.toString()%></option>
                                            <option value="<%=SERVICE_STATUS.TEST.getDbId()%>"<%=(_status == SERVICE_STATUS.TEST.getDbId()) ? " selected" : ""%>>
                                                <%=SERVICE_STATUS.TEST.toString()%></option>
                                        </select>
                                        <%
                                        } else {
                                        %>
                                        Testing
                                        <input type="hidden" name="status" value="<%=SERVICE_STATUS.TEST.getDbId()%>">
                                        <%
                                            }
                                        %>
                                    </td>
                                </tr>
                                <tr>
                                    <th style="border-bottom:0px;">Service ID(Shortcode)</th>
                                    <td>
                                        <input class="mediumtext" type="text" name="srvc_id" value="<%=_srvc_id%>"
                                               onkeypress='return filter_digit_char(event)'
                                               <%
                                                   if (Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                                                   || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) {
                                               %>
                                               onkeyup='return copyServiceId()'
                                               <%                                        }
                                               %>

                                               <%
                                                   if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) {
                                               %>
                                               onkeyup='return copyServiceIdMo()'
                                               <%                                        }
                                               %>
                                               >
                                    </td>
                                    <th style="border-bottom:0px;">
                                        Owner:
                                    </th>
                                    <td>
                                        <%
                                            if (_cmd.equals("edit")) {
                                                out.println("<select name='uid'>");
                                                for (int i = 0; i < userList.size(); i++) {
                                                    out.println("<option value=" + userList.get(i).getUid() + ((userList.get(i).getUid() == _uid) ? " selected" : "") + ">" + userList.get(i).getName() + "</option>");
                                                }
                                                out.println("</select>");
                                            } else {
                                                out.println("<input type=hidden name='uid' value=" + user.getUid() + ">" + user.getName());
                                            }
                                        %>
                                    </td>
                                </tr>
                                <tr>
                                    <th style="border-bottom:0px;">Test Service ID(Shortcode)</th>
                                    <td>
                                        <input class="mediumtext" type="text" name="srvc_id_mo_test" value="<%=_srvc_id_mo_test%>" onkeypress='return filter_digit_char(event)'>
                                    </td>
                                    <td rowspan="7" colspan="2">
                                        <fieldset>
                                            <legend>Link Configure:</legend>
                                            <table style="font-size:1.5em;">
                                                <tr>
                                                    <th style="border-bottom:0px;">
                                                        Normal(charge) <img title="add link" src="./images/new2.gif" style=" vertical-align: middle" border="0" onclick="javascript:doAddLink('conf_id');">
                                                    </th>
                                                    <td>
                                                        <%
                                                            boolean link_valid = false;
                                                            try {
                                                                OperConfig lc = new OperConfig(_conf_id);
                                                                if (lc != null && lc.conf_name != null) {
                                                                    link_valid = true;
                                                                }
                                                            } catch (Exception e) {
                                                            }
                                                            out.println("<select name='conf_id' onchange='hide2(\"conf_id_div\");"
                                                                    + ((Integer.parseInt(_oper_id) != CARRIER.TRUE.getId() && Integer.parseInt(_oper_id) != CARRIER.TRUEH.getId()) ? "copyConfigId();" : "") + "'>");
                                                            for (int i = 0; i < linkList.size(); i++) {
                                                                out.println("<option value=" + linkList.get(i).conf_id
                                                                        + ((linkList.get(i).conf_id == _conf_id) ? " selected" : "")
                                                                        + ">" + linkList.get(i).conf_name + "</option>");
                                                            }
                                                            out.println("</select>");
                                                        %>

                                                    </td>
                                                    <td><span id="conf_id_div" style="color:red;display:<%=(!link_valid && _cmd.equals("edit")) ? "block" : "none"%>">link removed!!</span></td>
                                                </tr>
                                                <tr>
                                                    <th style="border-bottom:0px;">
                                                        Test <img title="add link" src="./images/new2.gif" style=" vertical-align: middle" border="0" onclick="javascript:doAddLink('conf_id_test');">
                                                    </th>
                                                    <td>
                                                        <%
                                                            link_valid = false;
                                                            try {
                                                                OperConfig lc = new OperConfig(_conf_id_test);
                                                                if (lc != null && lc.conf_name != null) {
                                                                    link_valid = true;
                                                                }
                                                            } catch (Exception e) {
                                                            }
                                                            out.println("<select name='conf_id_test' onchange='hide2(\"conf_id_test_div\")'>");
                                                            for (int i = 0; i < linkList.size(); i++) {
                                                                out.println("<option value=" + linkList.get(i).conf_id
                                                                        + ((linkList.get(i).conf_id == _conf_id_test) ? " selected" : "")
                                                                        + ">" + linkList.get(i).conf_name + "</option>");
                                                            }
                                                            out.println("</select>");
                                                        %>
                                                    </td>
                                                    <td><span id="conf_id_test_div" style="color:red;display:<%=(!link_valid && _cmd.equals("edit")) ? "block" : "none"%>">link removed!!</span></td>
                                                </tr>
                                                <tr>
                                                    <th style="border-bottom:0px;">
                                                        Non-charge <img title="add link" src="./images/new2.gif" style=" vertical-align: middle" border="0" onclick="javascript:doAddLink('conf_id_non_chrg');">
                                                    </th>
                                                    <td>
                                                        <%
                                                            link_valid = false;
                                                            try {
                                                                OperConfig lc = new OperConfig(_conf_id_non_chrg);
                                                                if (lc != null && lc.conf_name != null) {
                                                                    link_valid = true;
                                                                }
                                                            } catch (Exception e) {
                                                            }
                                                            out.println("<select name='conf_id_non_chrg' onchange='hide2(\"conf_id_non_chrg_div\")'>");
                                                            for (int i = 0; i < linkList.size(); i++) {
                                                                out.println("<option value=" + linkList.get(i).conf_id
                                                                        + ((linkList.get(i).conf_id == _conf_id_non_chrg) ? " selected" : "")
                                                                        + ">" + linkList.get(i).conf_name + "</option>");
                                                            }
                                                            out.println("</select>");
                                                        %>
                                                    </td>
                                                    <td><span id="conf_id_non_chrg_div" style="color:red;display:<%=(!link_valid && _cmd.equals("edit")) ? "block" : "none"%>">link removed!!</span></td>
                                                </tr>
                                            </table>
                                        </fieldset>
                                        <%
                                            if ((_cmd.equals("add")
                                                    && (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                                                    || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()))
                                                    || (_cmd.equals("edit") && Integer.parseInt(_oper_id) == CARRIER.AIS.getId())) {
                                        %>

                                        <fieldset id="ssstypediv">
                                            <legend>SSS Type:</legend>
                                            <table style="font-size:1.5em;">
                                                <tr>
                                                    <th>
                                                        <input type="radio" id="ssstype1" name="sss_type" value="<%=SSS_TYPE.TYPE_A.getId()%>" onclick="hideUnhideMessageDiv();hideUnhideSubscriptionCounterDiv()" <%=(_srvc_type & SSS_TYPE.TYPE_A.getId()) > 0 ? " CHECKED" : ""%>>
                                                        <label for="ssstype1"><span style="vertical-align:text-bottom;">Type A</span></label>
                                                    </th><th>
                                                        <input type="radio" id="ssstype2" name="sss_type" value="<%=SSS_TYPE.TYPE_B.getId()%>" onclick="hideUnhideMessageDiv();hideUnhideSubscriptionCounterDiv()" <%=(_srvc_type & SSS_TYPE.TYPE_B.getId()) > 0 ? " CHECKED" : ""%>>
                                                        <label for="ssstype2"><span style="vertical-align:text-bottom;">Type B</span></label>
                                                    </th><th>
                                                        <input type="radio" id="ssstype3" name="sss_type" value="<%=SSS_TYPE.TYPE_L.getId()%>" onclick="hideUnhideMessageDiv();hideUnhideSubscriptionCounterDiv()" <%=(_srvc_type & SSS_TYPE.TYPE_L.getId()) > 0 ? " CHECKED" : ""%>>
                                                        <label for="ssstype3"><span style="vertical-align:text-bottom;">Type L</span></label>
                                                    </th><th>
                                                        <input type="radio" id="ssstype4" name="sss_type" value="<%=SSS_TYPE.TYPE_L_PLUS.getId()%>" onclick="hideUnhideMessageDiv();hideUnhideSubscriptionCounterDiv()" <%=(_srvc_type & SSS_TYPE.TYPE_L_PLUS.getId()) > 0 ? " CHECKED" : ""%>>
                                                        <label for="ssstype4"><span style="vertical-align:text-bottom;">Type L+</span></label>
                                                    </th></tr><tr><th colspan="3">
                                                        <input type="radio" id="ssstype5" name="sss_type" value="<%=SSS_TYPE.TYPE_SMS_DOWNLOAD.getId()%>" <%=(_srvc_type & SSS_TYPE.TYPE_SMS_DOWNLOAD.getId()) > 0 ? " CHECKED" : ""%>>
                                                        <label for="ssstype5"><span style="vertical-align:text-bottom;">SMS Download</span></label>
                                                    </th></tr>
                                            </table>

                                        </fieldset>
                                        <%
                                            }
                                        %>
                                    </td>
                                </tr>





                                <%
                                    if ((_cmd.equals("edit") && Integer.parseInt(_oper_id) == CARRIER.AIS.getId())) {
                                        out.println("<input type=\"hidden\" name=\"srvc_id_non_chrg\" value=\"" + _srvc_id_non_chrg + "\">");
                                    }
                                    if (Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) {
                                %>
                                <tr id="serviceidmodiv">
                                    <th style="border-bottom:0px;">Service ID (MO leg)</th>
                                    <td>
                                        <input class="mediumtext" type="text" name="srvc_id_mo" value="<%=_srvc_id_mo%>" 
                                               onkeypress='return filter_digit_char(event)'
                                               <%
                                                   if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                                                           || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
                                               %>
                                               onkeyup="copyServiceIdTruemove()"
                                               <%                                                                                }
                                               %>
                                               >
                                    </td>
                                </tr>
                                <%
                                    }
                                %>

                                <%
                                    if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()
                                            || (_cmd.equals("add") && Integer.parseInt(_oper_id) == CARRIER.AIS.getId())
                                            || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) {
                                %>
                                <tr id="serviceidnonchargediv">
                                    <th style="border-bottom:0px;">Non-Charge Service ID(Shortcode)</th>
                                    <td>
                                        <input class="mediumtext" type="text" name="srvc_id_non_chrg" value="<%=_srvc_id_non_chrg%>" onkeypress='return filter_digit_char(event)'>
                                    </td>
                                </tr>
                                <%
                                    }
                                %>

                                <%
                                    if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) {
                                %>
                                <tr id="srvcidmtdiv">
                                    <th style="border-bottom:0px;">Service ID (MT leg)</th>
                                    <td>
                                        Noncharge:<input class="mediumtext" type="text" name="srvc_id_mt" value="<%=_srvc_id_mt%>" onkeypress='return filter_digit_char(event)'>
                                        <br>
                                        Charge:<input class="mediumtext" type="text" name="srvc_id_mt_chrg" value="<%=_srvc_id_mt_chrg%>" onkeypress='return filter_digit_char(event)'>
                                    </td>
                                </tr>
                                <%
                                    }
                                %>

                                <%
                                    if (Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()) {
                                %>
                                <tr>
                                    <th style="border-bottom:0px;">Broadcast Service ID</th>
                                    <td>
                                        <input class="mediumtext" type="text" name="bcast_srvc_id" value="<%=_bcast_srvc_id%>" onkeypress='return filter_digit_char(event)'>
                                    </td>
                                </tr>
                                <%
                                    }
                                %>

                                <%
                                    if (Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) {
                                %>
                                <tr>
                                    <th style="border-bottom:0px;">Product ID</th>
                                    <td>
                                        <input class="mediumtext" type="text" name="bcast_srvc_id" value="<%=_bcast_srvc_id%>" onkeypress='return filter_digit_char(event)'>
                                    </td>
                                </tr>
                                <%
                                    }
                                %>
                                <tr>
                                    <th style="border-bottom:0px;">Service Type</th>
                                    <td>
                                        <select name="service_type" onchange="javascript:hideUnhideSubscriptionDiv();hideUnhideSubscriptionCounterDiv();hideUnhideDbcodeDiv();hideUnhideMessageDiv();hideUnhideNoCssDiv();changeSSSType();hideUnhideChrgflgDiv()">
                                            <option value="1" <%=(_srvc_type & SERVICE_TYPE.SUBSCRIPTION.getId()) > 0 ? "selected" : ""%>>Subscription</option>
                                            <option value="2" <%=(_srvc_type & SERVICE_TYPE.SMSDOWNLOAD.getId()) > 0 ? "selected" : ""%>>SmsDownload/Forward</option>
                                            <option value="4" <%=(_srvc_type & SERVICE_TYPE.RESERVED.getId()) > 0 ? "selected" : ""%>>Reserved</option>
                                        </select><br>
                                        <input type="checkbox" name="ctnt_type" value="<%=SERVICE_TYPE.SMS.getId()%>"
                                               <%=(_srvc_type & SERVICE_TYPE.SMS.getId()) > 0 ? "checked" : ""%>>SMS
                                        <input type="checkbox" name="ctnt_type" value="<%=SERVICE_TYPE.WAP.getId()%>"
                                               <%=(_srvc_type & SERVICE_TYPE.WAP.getId()) > 0 ? "checked" : ""%>>WAP
                                        <input type="checkbox" name="ctnt_type" value="<%=SERVICE_TYPE.MMS.getId()%>"
                                               <%=(_srvc_type & SERVICE_TYPE.MMS.getId()) > 0 ? "checked" : ""%>>MMS
                                    </td>
                                </tr>
                                <tr id="chrgflgdiv" style="display:none;">
                                    <th style="border-bottom:0px;">Charging Leg</th>
                                    <td>
                                        <select name="chrg_leg">
                                            <option value="MT" <%=(_chrg_flg.equals("MT") || ((_srvc_type & SERVICE_TYPE.MTCHARGE.getId()) > 0)) ? "selected" : ""%>>MT</option>
                                            <option value="MO" <%=(!(_chrg_flg.equals("MT") || ((_srvc_type & SERVICE_TYPE.MTCHARGE.getId()) > 0))) ? "selected" : ""%>>MO</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <th style="border-bottom:0px;">Sender</th>
                                    <td>
                                        <input class="mediumtext" type="text" name="sender" value="<%=_sender%>">
                                    </td>
                                </tr>
                                <tr id="dbcodediv">
                                    <th style="border-bottom:0px;">DB_CODE</th>
                                    <td colspan="2">
                                        <textarea name="dbcode" rows="3" style="width:200px"><%=_dbcode%></textarea><br>use Comma(,) to separate each of DB_CODE
                                    </td>
                                </tr>
                                <tr>
                                    <td></td>
                                    <td colspan="2">
                                        <span id="cpvalidateSelectDiv">
                                            <%
                                                if (((Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                                                        || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) && _cmd.equals("add"))
                                                        || ((Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) && _cmd.equals("edit"))) {
                                            %>
                                            <br><input type="checkbox" name="ctnt_type" id="cpvalidate" 
                                                       value="<%=SERVICE_TYPE.CPVALIDATE.getId()%>" onclick="hideUnhideAisCpValidateDiv();"
                                                       <%=(_srvc_type & SERVICE_TYPE.CPVALIDATE.getId()) > 0 ? " checked" : ""%>>
                                            <label for="cpvalidate" id="cpvalidate_label" >CP Validate</label>
                                            <%
                                                }
                                            %>
                                        </span>
                                        <span id="sftpSelectDiv">
                                            <%
                                                if (((Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                                                        || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) && _cmd.equals("add"))
                                                        || ((Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) && _cmd.equals("edit"))) {
                                            %>
                                            <br><input type="checkbox" name="ctnt_type" id="sftp" value="<%=SERVICE_TYPE.SFTP.getId()%>"
                                                       <%=(_srvc_type & SERVICE_TYPE.SFTP.getId()) > 0 ? "checked" : ""%>>
                                            <label for="sftp" id="sftp_label" >Broadcast Message via SFTP</label>
                                            <%
                                                }
                                            %>
                                        </span>
                                        <%
                                            if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                                                    || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) {
                                        %>
                                        <br><input type="checkbox" name="ctnt_type" id="chainq" 
                                                   value="<%=SERVICE_TYPE.CHAINQUIZ.getId()%>" onclick="chainQuiz_OnChange(this);"
                                                   <%=(_srvc_type & SERVICE_TYPE.CHAINQUIZ.getId()) > 0 ? "checked" : ""%>>
                                        <label for="chainq" id="chainq_label" >Chain-Quiz Service</label>
                                        <%
                                            }
                                        %>
                                        <%
                                            if (Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) {
                                        %>
                                        <br><input type="checkbox" name="ctnt_type" id="ds" value="<%=SERVICE_TYPE.DDS.getId()%>"
                                                   <%=(_srvc_type & SERVICE_TYPE.DDS.getId()) > 0 ? "checked" : ""%>>
                                        <label for="ds" id="ds_label" >Push MT 1-1</label>
                                        <br><input type="checkbox" name="ctnt_type" id="usebc" value="<%=SERVICE_TYPE.BCSERVICEID.getId()%>"
                                                   <%=(_srvc_type & SERVICE_TYPE.BCSERVICEID.getId()) > 0 ? "checked" : ""%>>
                                        <label for="usebc" id="usebc_label" >Use B/C service ID for MT charge</label>
                                        <%
                                            }
                                        %>
                                        <%
                                            if (Integer.parseInt(_oper_id) == CARRIER.DTAC.getId()
                                                    || Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()
                                                    || Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                                                    || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
                                        %>
                                        <br><input type="checkbox" name="ctnt_type" id="dcc" value="<%=SERVICE_TYPE.CONTROLCMD.getId()%>" onclick="hideUnhideMessageDiv();"
                                                   <%=(_srvc_type & SERVICE_TYPE.CONTROLCMD.getId()) > 0 ? "checked" : ""%>>
                                        <label for="dcc" id="dcc_label" >Control Command Service</label>
                                        <%
                                            }
                                        %>
                                        <%
                                            if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                                                    || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
                                        %>
                                        <br><input type="checkbox" name="ctnt_type" id="nocss" value="<%=SERVICE_TYPE.NOCSS.getId()%>"
                                                   <%=(_srvc_type & SERVICE_TYPE.NOCSS.getId()) > 0 ? "checked" : ""%>>
                                        <label for="nocss" id="nocss_label" >Subscript with no update to CSS</label>
                                        <br><input type="checkbox" name="ctnt_type" id="ussddirectreply" value="<%=SERVICE_TYPE.USSDDIRECTREPLY.getId()%>" onclick="hideUnhideMessageDiv();"
                                                   <%=(_srvc_type & SERVICE_TYPE.USSDDIRECTREPLY.getId()) > 0 ? "checked" : ""%>>
                                        <label for="ussddirectreply" id="ussddirectreply_label" >USSD Direct Reply</label>
                                        <%
                                            }
                                        %>
                                    </td>
                                </tr>
                                <%
                                    if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) {
                                %>
                                <tr id="cctdiv">
                                    <td colspan="4">
                                        <fieldset>
                                            <legend>CCT/CPAction:</legend>
                                            <table style=" font-size: 1.5em;">
                                                <tr>
                                                    <td>REGISTER</td>
                                                    <td>
                                                        <input class="shorttext" type="text" name="cct_register" value="<%=_cct_register%>"> /
                                                        <input class="mediumtext" type="text" name="cpaction_register" value="<%=_cpaction_register%>">
                                                    </td>
                                                    <td>CANCEL</td>
                                                    <td>
                                                        <input class="shorttext" type="text" name="cct_cancel" value="<%=_cct_cancel%>">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>WARNING</td>
                                                    <td>
                                                        <input class="shorttext" type="text" name="cct_warning" value="<%=_cct_warning%>">
                                                    </td>
                                                    <td>RECURRING</td>
                                                    <td>
                                                        <input class="shorttext" type="text" name="cct_recurring" value="<%=_cct_recurring%>">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>NON-CHARGE</td>
                                                    <td>
                                                        <input class="shorttext" type="text" name="cct_broadcast" value="<%=_cct_broadcast%>">
                                                    </td>
                                                    <td>CHARGE</td>
                                                    <td>
                                                        <input class="shorttext" type="text" name="cct_charge" value="<%=_cct_charge%>">
                                                    </td>
                                                    <td>INVALID</td>
                                                    <td>
                                                        <input class="shorttext" type="text" name="cct_invalid" value="<%=_cct_invalid%>">
                                                    </td>
                                                </tr>
                                            </table>
                                        </fieldset>
                                    </td>
                                </tr>
                                <%
                                    }
                                %>
                                <tr id="subscriptionDiv">
                                    <td colspan="4">
                                        <fieldset>
                                            <legend>Subscription:</legend>
                                            <table style=" font-size: 1.5em;">
                                                <tr>
                                                    <th>IVR Sub/Unsub</th>
                                                    <td>
                                                        <input class="mediumtext" type="text" name="ivr_register" value="<%=_ivr_register%>" onkeypress="return filter_digit_char(event, '~`#[]^$*?!+-\\/.()=_><,:;\'{}|')">
                                                        <input class="mediumtext" type="text" name="ivr_unregister" value="<%=_ivr_unregister%>" onkeypress="return filter_digit_char(event, '~`#[]^$*?!+-\\/.()=_><,:;\'{}|')">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>SMS Sub/Unsub</th>
                                                    <td>
                                                        <input class="mediumtext" type="text" name="sms_register" value="<%=_sms_register%>">
                                                        <input class="mediumtext" type="text" name="sms_unregister" value="<%=_sms_unregister%>">
                                                    </td>
                                                </tr>
                                                <%
                                                    if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                                                            || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
                                                %>
                                                <tr>
                                                    <th>3rd Party Sub/Unsub</th>
                                                    <td>
                                                        <input class="mediumtext" type="text" name="thrd_prty_register" value="<%=_thrd_prty_register%>">
                                                        <input class="mediumtext" type="text" name="thrd_prty_unregister" value="<%=_thrd_prty_unregister%>">
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                %>
                                                <%
                                                    if (Integer.parseInt(_oper_id) == CARRIER.AIS.getId()) {
                                                %>
                                                <tr id="aisCpValidateDiv">
                                                    <th title="especial use for Hybrid or CPvalidate">Sub/UnSub command</th>
                                                    <td>
                                                        <input class="mediumtext" type="text" name="thrd_prty_register" value="<%=_thrd_prty_register%>">
                                                        <input class="mediumtext" type="text" name="thrd_prty_unregister" value="<%=_thrd_prty_unregister%>">
                                                    </td>
                                                </tr>
                                                <%
                                                    }
                                                %>
                                            </table>
                                        </fieldset>
                                    </td>
                                </tr>
                                <%
                                    if (Integer.parseInt(_oper_id) == CARRIER.DTAC_SDP.getId()) {
                                %>
                                <tr id="dtacRealShortcodeDiv">
                                    <th title="Map to AIS shortcode">Real Shortcode</th>
                                    <td>
                                        <input class="mediumtext" type="text" name="thrd_prty_register" value="<%=_thrd_prty_register%>">
                                    </td>
                                </tr>
                                <%
                                    }
                                %>
                                <%
                                    if (((Integer.parseInt(_oper_id) == CARRIER.AIS.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) && _cmd.equals("add"))
                                            || ((Integer.parseInt(_oper_id) == CARRIER.AIS_LEGACY.getId()) && _cmd.equals("edit"))) {
                                %>
                                <tr id="aisLegacyCommandDiv">
                                    <td colspan="4">
                                        <fieldset>
                                            <legend>AIS Legacy Command Set:</legend>
                                            <table style=" font-size: 1.5em;" width="100%">
                                                <tr>
                                                    <th width="20%">MT Charge</th>
                                                    <td width="30%">
                                                        <input class="longtext" type="text" name="mt_chrg_cmd" value="<%=_mt_chrg_cmd%>">
                                                    </td>
                                                    <th width="20%">MT Non-Charge</th>
                                                    <td width="30%">
                                                        <input class="longtext" type="text" name="mt_non_chrg_cmd" value="<%=_mt_non_chrg_cmd%>">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <th>Warning</th>
                                                    <td>
                                                        <input class="longtext" type="text" name="mt_warn_cmd" value="<%=_mt_warn_cmd%>">
                                                    </td>
                                                    <th>Sub/Unsub</th>
                                                    <td>
                                                        <input class="longtext" type="text" name="mt_sub_cmd" value="<%=_mt_sub_cmd%>">
                                                        <input class="longtext" type="text" name="mt_unsub_cmd" value="<%=_mt_unsub_cmd%>">
                                                    </td>
                                                </tr>
                                            </table>
                                        </fieldset>
                                    </td>
                                </tr>
                                <%
                                    }
                                %>
                                <tr>
                                    <td colspan="4">
                                        <fieldset>
                                            <legend>Counter:</legend>
                                            <table style=" font-size: 1.5em;">
                                                <tr>
                                                    <th>
                                                        Free Trial Period(day)
                                                    </th>
                                                    <td>
                                                        <input class="number" type="text" name="free_trial" value="<%=_free_trial%>" onkeypress='return filter_digit_char(event)'>
                                                    </td>
                                                    <th>Max. Content Download (0:no limited)</th>
                                                    <td>
                                                        <input class="number" type="text" name="ctnt_ctr" value="<%=_ctnt_ctr%>" onkeypress='return filter_digit_char(event)'>
                                                    </td>
                                                    <th>Priority (0-6)</th>
                                                    <td>
                                                        <input class="number" type="text" name="priority" value="<%=_priority%>" onkeypress='return filter_digit_char(event)'>
                                                    </td>
                                                </tr>
                                                <tr id="subscriptionCounterDiv">
                                                    <th>Warning Message(day)</th>
                                                    <td>
                                                        <input class="number" type="text" name="rmdr_ctr" value="<%=_rmdr_ctr%>" onkeypress='return filter_digit_char(event)'>
                                                    </td>
                                                    <th>Retry Charge(day)</th>
                                                    <td>
                                                        <input class="number" type="text" name="rchg_ctr" value="<%=_rchg_ctr%>" onkeypress='return filter_digit_char(event)'>
                                                    </td>
                                                </tr>
                                            </table>
                                        </fieldset>
                                    </td>
                                </tr>
                                <input type="hidden" name="chrg_flg" value="MO">
                                <tr id="MessageDiv">
                                    <td colspan="4">
                                        <fieldset>
                                            <legend>Message:</legend>
                                            <table style=" font-size: 1.5em;">
                                                <tr><th>Register(Free trial):</th><td><input class="message" type="text" name="msg_sub_ft" value="<%=_msg_sub_ft%>"></td></tr>
                                                <tr><th>Unregister(Free trial):</th><td><input class="message" type="text" name="msg_usub_ft" value="<%=_msg_usub_ft%>"></td></tr>
                                                <tr><th>Warning(Free trial):</th><td><input class="message" type="text" name="msg_warn_ft" value="<%=_msg_warn_ft%>"></td></tr>
                                                <tr><th>Renew/Recurring:</th><td><input class="message" type="text" name="msg_sub_nm" value="<%=_msg_sub_nm%>"></td></tr>
                                                <tr><th>Unregister:</th><td><input class="message" type="text" name="msg_usub_nm" value="<%=_msg_usub_nm%>"></td></tr>
                                                <tr><th>Warning:</th><td><input class="message" type="text" name="msg_warn_nm" value="<%=_msg_warn_nm%>"></td></tr>
                                                <tr><th>Error no service:</th><td><input class="message" type="text" name="msg_err_no_srvc" value="<%=_cmd.equals("new") ? "ขออภัยค่ะ คุณกดบริการไม่ถูกต้องสอบถามโทร${CallCenter}" : _msg_err_no_srvc%>"></td></tr>
                                                <tr><th>Error duplicated:</th><td><input class="message" type="text" name="msg_err_dup" value="<%=_cmd.equals("new") ? "ขออภัยค่ะ คุณได้สมัครใช้บริการไว้แล้วสอบถามโทร${CallCenter}" : _msg_err_dup%>"></td></tr>
                                            </table>
                                        </fieldset>
                                    </td>
                                </tr>
                                <%
                                    if (Integer.parseInt(_oper_id) == CARRIER.TRUE.getId()
                                            || Integer.parseInt(_oper_id) == CARRIER.TRUEH.getId()) {
                                %>
                                <tr id="DroConfigureDiv">
                                    <td colspan="4">
                                        <fieldset>
                                            <legend>DR Flag Configure:</legend>
                                            <span style="padding-left: 10px;" onclick="dro_checkall(true);">check all</span> | <span onclick="dro_checkall(false);">uncheck all</span>
                                            <table style=" font-size: 1.5em;">
                                                <tr>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_subft" value="<%=DRO_EVENT_TYPE.SUB_FT.getDbId()%>"
                                                               <%=(_dro_configure.getSub_ft() == 1) ? "checked" : ""%>><label for="dro_subft">register(Freetrial)</label>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_subnm" value="<%=DRO_EVENT_TYPE.SUB_NM.getDbId()%>"
                                                               <%=(_dro_configure.getSub_nm() == 1) ? "checked" : ""%>><label for="dro_subnm">register</label>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_subrenewft" value="<%=DRO_EVENT_TYPE.SUB_RENEW_FT.getDbId()%>"
                                                               <%=(_dro_configure.getSub_renew_ft() == 1) ? "checked" : ""%>><label for="dro_subrenewft">renew(Freetrial)</label>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_subrenewnm" value="<%=DRO_EVENT_TYPE.SUB_RENEW_NM.getDbId()%>"
                                                               <%=(_dro_configure.getSub_renew_nm() == 1) ? "checked" : ""%>><label for="dro_subrenewnm">renew</label>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_unregister" value="<%=DRO_EVENT_TYPE.UNREGISTER.getDbId()%>"
                                                               <%=(_dro_configure.getUnregister() == 1) ? "checked" : ""%>><label for="dro_unregister">unregister</label>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_warning" value="<%=DRO_EVENT_TYPE.WARNING.getDbId()%>"
                                                               <%=(_dro_configure.getWarning() == 1) ? "checked" : ""%>><label for="dro_warning">warning</label>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_recurring" value="<%=DRO_EVENT_TYPE.RECURRING.getDbId()%>"
                                                               <%=(_dro_configure.getRecurring() == 1) ? "checked" : ""%>><label for="dro_recurring">recurring</label>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_broadcast" value="<%=DRO_EVENT_TYPE.BROADCAST.getDbId()%>"
                                                               <%=(_dro_configure.getBroadcast() == 1) ? "checked" : ""%>><label for="dro_broadcast">broadcast</label>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_smsdownload" value="<%=DRO_EVENT_TYPE.SMSDOWNLOAD.getDbId()%>"
                                                               <%=(_dro_configure.getPullsms() == 1) ? "checked" : ""%>><label for="dro_smsdownload">sms download</label>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_forward" value="<%=DRO_EVENT_TYPE.FORWARD.getDbId()%>"
                                                               <%=(_dro_configure.getForward() == 1) ? "checked" : ""%>><label for="dro_forward">forward</label>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_subdup" value="<%=DRO_EVENT_TYPE.SUB_DUP.getDbId()%>"
                                                               <%=(_dro_configure.getSub_dup() == 1) ? "checked" : ""%>><label for="dro_subdup">duplicated sub</label>
                                                    </td>
                                                    <td>
                                                        <input type="checkbox" name="dro_event" id="dro_error" value="<%=DRO_EVENT_TYPE.ERROR.getDbId()%>"
                                                               <%=(_dro_configure.getError() == 1) ? "checked" : ""%>><label for="dro_error">error</label>
                                                    </td>
                                                </tr>
                                            </table>
                                        </fieldset>
                                    </td>
                                </tr>
                                <%
                                    }
                                %>
                            </table>
                            <div style="margin: 20px 0 20px 50px;">
                                <input id="submit" type="submit" class="button" value="Submit">
                                <input id="cancel" type="reset" class="button" value="Cancel">
                            </div>
                        </div>
                    </blockquote>
                </div>
            </form>
        </div>
    </body>
</html>
