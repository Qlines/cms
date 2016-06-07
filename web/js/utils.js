var browserType;

if (document.layers) {
    browserType = "nn4"
}
if (document.all) {
    browserType = "ie"
}
if (window.navigator.userAgent.toLowerCase().match("gecko")) {
    browserType = "gecko"
}

//// create Array.indexOf function supported for IE6
//if (!Array.prototype.indexOf)
//{
//  Array.prototype.indexOf = function(elt /*, from*/)
//  {
//    var len = this.length;
//
//    var from = Number(arguments[1]) || 0;
//    from = (from < 0)
//         ? Math.ceil(from)
//         : Math.floor(from);
//    if (from < 0)
//      from += len;
//
//    for (; from < len; from++)
//    {
//      if (from in this &&
//          this[from] === elt)
//        return from;
//    }
//    return -1;
//  };
//}
function findArrayIndex(array, obj) {
    for (var i = 0; i < array.length; i++) {
        if (array[i] == obj) {
            return i;
        }
    }
    return -1;
}

// Array prototype definition for indexOf
if (!Array.prototype.indexOf)
{
    Array.prototype.indexOf = function(elt /*, from*/)
    {
        var len = this.length;

        var from = Number(arguments[1]) || 0;
        from = (from < 0)
                ? Math.ceil(from)
                : Math.floor(from);
        if (from < 0)
            from += len;

        for (; from < len; from++)
        {
            if (from in this &&
                    this[from] === elt)
                return from;
        }
        return -1;
    };
}

// Array prototype definition for lastIndexOf
if (!Array.prototype.lastIndexOf)
{
    Array.prototype.lastIndexOf = function(elt /*, from*/)
    {
        var len = this.length;

        var from = Number(arguments[1]);
        if (isNaN(from))
        {
            from = len - 1;
        }
        else
        {
            from = (from < 0)
                    ? Math.ceil(from)
                    : Math.floor(from);
            if (from < 0)
                from += len;
            else if (from >= len)
                from = len - 1;
        }

        for (; from > -1; from--)
        {
            if (from in this &&
                    this[from] === elt)
                return from;
        }
        return -1;
    };
}

// Support the Query String
var qsParm = new Array();
var parms_len;
function qs() {
    var query = window.location.search.substring(1);
    var parms = query.split('&');
    parms_len = parms.length;
    for (var i = 0; i < parms_len; i++) {
        var pos = parms[i].indexOf('=');
        if (pos > 0) {
            var key = parms[i].substring(0, pos);
            var val = parms[i].substring(pos + 1);
            qsParm[key] = val;

            //document.write(key + "=" + val + "<br>");
            //alert(key + "=" + val + "<br>");
        }
    }
}

function urlfix(url) {
    //		alert(url);
    var fixed = url.split("?");
    //		alert(fixed[0]);
    return fixed[0];
}

function hide(id) {
    if (browserType == "gecko")
        document.poppedLayer =
                eval('document.getElementById(id)');
    else if (browserType == "ie")
        document.poppedLayer =
                eval('document.getElementById(id)');
    else
        document.poppedLayer =
                eval('document.layers[id]');
    if (!document.poppedLayer) {
        return;
    }
    document.poppedLayer.style.visibility = "hidden";
}

function show(id) {
    if (browserType == "gecko")
        document.poppedLayer =
                eval('document.getElementById(id)');
    else if (browserType == "ie")
        document.poppedLayer =
                eval('document.getElementById(id)');
    else
        document.poppedLayer =
                eval('document.layers[id]');
    if (!document.poppedLayer) {
        return;
    }
    document.poppedLayer.style.visibility = "visible";
}

function hide2(id) {
    if (browserType == "gecko") {
        document.poppedLayer =
                eval('document.getElementById(id)');
    }
    else if (browserType == "ie") {
        document.poppedLayer =
                eval('document.getElementById(id)');
    }
    else {
        document.poppedLayer =
                eval('document.layers[id]');
    }
    if (!document.poppedLayer) {
        return;
    }
    document.poppedLayer.style.display = 'none';
}
function show2(id) {
    if (browserType == "gecko")
        document.poppedLayer =
                eval('document.getElementById(id)');
    else if (browserType == "ie")
        document.poppedLayer =
                eval('document.getElementById(id)');
    else
        document.poppedLayer =
                eval('document.layers[id]');
    if (!document.poppedLayer) {
        return;
    }
    document.poppedLayer.style.display = "inline";
}

function swap(id) {
    if (browserType == "gecko")
        document.poppedLayer =
                eval('document.getElementById(id)');
    else if (browserType == "ie")
        document.poppedLayer =
                eval('document.getElementById(id)');
    else
        document.poppedLayer =
                eval('document.layers[id]');
    if (!document.poppedLayer) {
        return;
    }

    if (document.poppedLayer.style.display == "none") {
        document.poppedLayer.style.display = "inline";
    }
    else {
        document.poppedLayer.style.display = "none";
    }
}

function getElement(id) {
    if (browserType == "gecko")
        obj = eval('document.getElementById(id)');
    else if (browserType == "ie")
        obj = eval('document.getElementById(id)');
    else
        obj = eval('document.layers[id]');
    return obj;
}

function getCookie(c_name)
{
    if (document.cookie.length > 0)
    {
        c_start = document.cookie.indexOf(c_name + "=");
        if (c_start != -1)
        {
            c_start = c_start + c_name.length + 1;
            c_end = document.cookie.indexOf(";", c_start);
            if (c_end == -1)
                c_end = document.cookie.length;
            return unescape(document.cookie.substring(c_start, c_end));
        }
    }
    return "";
}

function setCookie(c_name, value, expiredays)
{
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + expiredays);
    document.cookie = c_name + "=" + escape(value) +
            ((expiredays == null) ? "" : ";expires=" + exdate.toGMTString());
}

function chgColor(objid, col) {
    var x = getElement(objid);
    if (x) {
        x.style.color = col;
    }
}

function chgImage(objid, src) {
    document[objid].src = src;
}

function createOptions(titles, values, name, disabled, selectedId)
{
    var tmp = "";
    tmp += "<select id='" + name + "' name='" + name + "'" + ((disabled) ? " disabled" : "") + ">";
    if (titles && values)
        for (var i = 0; i < titles.length; i++) {
            tmp += "<option value=" + values[i] + ((selectedId == i) ? " selected" : "") + ">" + titles[i] + "</option>";
        }
    tmp += "</select>";

    return tmp;
}

function addOption(selectbox, value, text, selected)
{
    var optn = document.createElement("OPTION");
    optn.text = text;
    optn.value = value;
    selectbox.options.add(optn);
    if (selected)
        selectbox.options[selectbox.options.length - 1].selected = selected;
}

/**
 * Add options with 2 dimensions array, [value][text]
 */
function addOption_list(selectbox, arry2d, defaultValue) {
    for (i = 0; i < arry2d.length; i++) {
        if (arry2d[i][0] && arry2d[i][1]) {
            addOption(selectbox, arry2d[i][0], arry2d[i][1], defaultValue == arry2d[i][0]);
        }
    }
}

function removeAllOptions(selectbox)
{
    var i;
    if (!selectbox)
        return;
    for (i = selectbox.options.length - 1; i >= 0; i--) {
        selectbox.remove(i);
    }
}

function removeSelectedOptions(selectbox)
{
    var i;
    for (i = selectbox.options.length - 1; i >= 0; i--) {
        if (selectbox.options[i].selected)
            selectbox.remove(i);
    }
}

/**
 * updateServiceOptions(raw, obj)
 * input parameters
 * raw - item string -> [v1]|[t1];[v2]|[t2];...
 */
function updateServiceOptions(raw, obj) {
    var optionsArry = new Array();
    var tmp = raw.split(';');
    // load services name
    for (i = 0; i < tmp.length; i++) {
        optionsArry[i] = tmp[i].split('|');
    }

    removeAllOptions(obj);
    addOption_list(obj, optionsArry);
}

if (!String.prototype.trim) {
    String.prototype.trim = function() {
        return this.replace(/^\s+|\s+$/g, '');
    };
}

if (!String.prototype.isEmpty) {
    String.prototype.isEmpty = function() {
        return this == '';
    };
}

if (!String.prototype.stripHtml) {
    String.prototype.stripHtml = function() {
        return this.replace(/(<([^>]+)>)/gi, '');
    };
}

if (!String.prototype.splitCsv) {
    String.prototype.splitCsv = function(d, encap) {
        var isEncapOpen = false;
        var isWordStarted = false;
        var list = new Array();
        var tmp = '';

        var type; // 0: normal, 1:eow, 2:soe
        var l = 0;

        for (var i = 0; i < this.length; i++) {
            var a = this.substring(i, i + 1);
            if (a === encap) {
                if (!isEncapOpen && !isWordStarted) {
                    type = 2; // start encap
                } else if (isEncapOpen) {
                    type = 1; // end of encapsulate
                } else {
                    type = 0;
                }
            } else if (a === d && !isEncapOpen) { // end of token
                type = 1;
            } else {
                type = 0;
            }

            switch (type) {
                case 0:
                    tmp += a;
                    isWordStarted = true;
                    break;
                case 1:
                    if (!tmp.trim().isEmpty())
                        list[l++] = tmp.trim();
                    tmp = '';
                    isWordStarted = false;
                    isEncapOpen = false;
                    break;
                case 2:
                    isEncapOpen = true;
                    break;
            }
        }
        
        if (!tmp.trim().isEmpty())
            list[l++] = tmp.trim();

        return list;
    };
}

function open_page(url, frame)
{
    window.scrollTo(0, 0);
    top.frames[frame].location = url;
}

function wait_div_gen(id, msg)
{
    div_width = 400;
    div_height = 200;
    if (!msg) {
        msg = "\
			Please wait for a while...\
			 <hr style='height:0.5px;color:#999'>\
			"
    }
    document.write(" \
				   <div id=" + id + " style=\"\
				   z-index:1009;\
				   display:none;\
				   position:absolute;\
				   text-align:center;\
				   width:" + div_width + ";\
				   height:" + div_height + "px;\
				   left:" + (600 - div_width) / 2 + "px;\
				   top:" + (400 - div_height) / 2 + "px;\
				   background-color:#444444;\
				   padding:10px;\
				   border-right:1px solid #000000;\
				   border-bottom:1px solid #000000;\
				   font-family:arial;\
				   font-size:24px;\
				   font-weight:normal;\
				   line-height:1.6em;\
				   color:#999999;\
				   filter:alpha(opacity=90);-moz-opacity:.90;opacity:.90;\"> \
						<div id=wait02 style=\"text-align:center;width:" + (div_width - 20) + "\"></div>"
            + msg + "</div>");

    def_width = 100;
    def_height = 100;
    zoom = 1;
    width = def_width * zoom;
    height = def_height * zoom;

    FlashReplace.replace("wait02", "/flash/wait02.swf", "wait02_obj", width, height,
            7,
            {
                wmode: "transparent",
                quality: "high",
                bgcolor: "#ffffff"
            }
    );
}

function hideAllSelect()
{
    svn = document.getElementsByTagName("SELECT");
    for (a = 0; a < svn.length; a++) {
        var vs = isIE();
        if (vs && vs <= 6) {
            svn[a].style.visibility = "hidden";
        }
    }
}

function hideAllSelect(div)
{
    svn = document.getElementsByTagName("SELECT");
    for (var a in svn) {
        var vs = isIE();
        if (vs && vs <= 6) {
            if (svn[a]) {
                //alert(svn[a].parentNode.id);
                //if (svn[a].parentNode.id == div)
                svn[a].style.visibility = "hidden";
            }
        }
    }
}

function isIE()
{
    // Return Value: return IE version otherwise return 0
    if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)) {
        return new Number(RegExp.$1);
    }
    return 0;
}

function isFirefox()
{
    // Return Value: return Firefox version otherwise return 0
    if (/Firefox[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
        return new Number(RegExp.$1);
    }
    return 0;
}

function isOpera()
{
    // Return Value: return Opera version otherwise return 0
    if (/Opera[\/\s](\d+\.\d+)/.test(navigator.userAgent)) {
        return new Number(RegExp.$1);
    }
    return 0;
}

function ascii_value(c)
{
    // restrict input to a single character
    c = c.charAt(0);

    // loop through all possible ASCII values
    var i;
    for (i = 0; i < 256; ++i)
    {
        // convert i into a 2-digit hex string
        var h = i.toString(16);
        if (h.length == 1)
            h = "0" + h;

        // insert a % character into the string
        h = "%" + h;

        // determine the character represented by the escape code
        h = unescape(h);

        // if the characters match, we've found the ASCII value
        if (h == c)
            break;
    }
    return i;
}

function parseURLstr(str)
{
    return unescape(str);
}

var imgs = new Array('./images/expand_white.gif', './images/collapse_white.gif');
var hints = new Array('expand', 'collapse');

function showBox(id)
{
    var ostate = (getCookie(id) == '') ? 1 : parseInt(getCookie(id));
    var imgobj = getElement(id);

    imgobj.src = imgs[ostate];
    imgobj.alt = hints[ostate];
    if (hints[ostate] == 'expand') {
        hide2(id + 'Div');
    } else {
        show2(id + 'Div');
    }
}

function hideUnhideBox(id)
{
    var cstate = (getCookie(id) == '') ? 1 : parseInt(getCookie(id));
    var imgobj = getElement(id);

    cstate = ((cstate + 1) % 2);
    imgobj.src = imgs[cstate];
    imgobj.alt = hints[cstate];
    if (hints[cstate] == 'expand') {
        hide2(id + 'Div');
    } else {
        show2(id + 'Div');
    }

    setCookie(id, cstate + '');
}

function collapseBox(id)
{
    var cstate = (getCookie(id) == '') ? 1 : parseInt(getCookie(id));
    var imgobj = getElement(id);

    cstate = ((cstate + 1) % 2);
    imgobj.src = imgs[cstate];
    imgobj.alt = hints[cstate];
    if (hints[cstate] == 'expand') {
        hide2(id + 'Div');
    } else {
        show2(id + 'Div');
    }

    setCookie(id, cstate + '');
}

function ajaxPost(url, method, user, password) {
    var objHTTP;
    var async = false;
    var mt = 'GET';
    if (browserType == "gecko") {
        mt = (method) ? method : mt;
        objHTTP = new XMLHttpRequest();
        if (user && password) {
            objHTTP.open(mt, url, async, user, password);
        } else {
            objHTTP.open(mt, url, async);
        }
    } else {
        mt = (method) ? method : 'POST';
        objHTTP = new ActiveXObject('Microsoft.XMLHTTP');
        if (user && password) {
            objHTTP.open(mt, url, async, user, password);
        } else {
            objHTTP.open(mt, url, async);
        }
        objHTTP.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    }

    objHTTP.send(null);
    return objHTTP.responseText;
}

var loadedobjects = "";

function loadobjs() {
    if (!document.getElementById)
        return
    for (i = 0; i < arguments.length; i++) {
        var file = arguments[i]
        var fileref = ""
        if (loadedobjects.indexOf(file) == -1) { //Check to see if this object has not already been added to page before proceeding
            if (file.indexOf(".js") != -1) { //If object is a js file
                fileref = document.createElement('script')
                fileref.setAttribute("type", "text/javascript");
                fileref.setAttribute("src", file);
            }
            else if (file.indexOf(".css") != -1) { //If object is a css file
                fileref = document.createElement("link")
                fileref.setAttribute("rel", "stylesheet");
                fileref.setAttribute("type", "text/css");
                fileref.setAttribute("href", file);
            }
        }
        if (fileref != "") {
            document.getElementsByTagName("head").item(0).appendChild(fileref)
            loadedobjects += file + " " //Remember this object as being already added to page
        }
    }
}

function findRadioValue(radioObj) {
    if (!radioObj)
        return "";
    var radioLength = radioObj.length;
    if (radioLength == undefined)
        if (radioObj.checked)
            return radioObj.value;
        else
            return "";
    for (var i = 0; i < radioLength; i++) {
        if (radioObj[i].checked) {
            return radioObj[i].value;
        }
    }
    return "";
}

function randomHex(len) {
    var hex = "";
    while (--len >= 0) {
        hex += Math.round(Math.random() * 14).toString(16);
    }
    return hex;
}

