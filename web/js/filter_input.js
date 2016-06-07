var SINGLE_IP = 0;
var CLASSLESS_IP_NETMASK = 1;
var CLASSLESS_IP = 2;
var RANGE_IP = 3;
var SINGLE_PORT = 0;
var RANGE_PORT = 1;
var UNKNOWN = -1;

function getKeyPress(event)
{
    var keynum = 0;
    try 
    {
        keynum = window.event.keyCode;
    }
    catch (e) 
    {
        try 
        {
            keynum = event.charCode;
        }
        catch (e2) 
        {
            try
            {
                keynum = event.which;
            }
            catch (e3)
            {
            // Not supported browser
            }
        }
    }

    // test
    //alert(keynum);

    return keynum;
}

function isCtrlPadHeld(event)
{
    var held = false;
	
    try 
    {
        held = window.event.ctrlKey;
    }
    catch (e) 
    {
        try 
        {
            held = event.ctrlKey;
        }
        catch (e2) 
        {
        // Not supported browser
        }
    }

    return held;
}

function isCtrlChar(keynum)
{
    return keynum<32;
}

function isEnterKey(keynum)
{
    return keynum==13 || keynum==10;
}

function isMac(str)
{
    var regExp = /^[0-9A-Fa-f]{1,2}\x3A[0-9A-Fa-f]{1,2}\x3A[0-9A-Fa-f]{1,2}\x3A[0-9A-Fa-f]{1,2}\x3A[0-9A-Fa-f]{1,2}\x3A[0-9A-Fa-f]{1,2}/;
    var match = str.match(regExp);
    return (match==str);
}

function isMsisdn(str)
{
    var regExp = /^66[689]\d{8}/;
    var match = str.match(regExp);
    return (match==str);
}

function isIp(str)
{
    var regExp = /^\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2E\d{1,3}/;
    var match = str.match(regExp);
    return (match==str);
}

function isIpWithNetmask(str)
{
    var regExp = /^\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2F\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2E\d{1,3}/;
    var match = str.match(regExp);
    return (match==str);
}

function isClasslessIp(str)
{
    var regExp = /^\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2F\d{1,2}/;
    var match = str.match(regExp);
    return (match==str);
}

function isRangeIp(str)
{
    var regExp = /^\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2D\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2E\d{1,3}/;
    var match = str.match(regExp);
    return (match==str);
}

function isSinglePort(str)
{
    var regExp = /[-,]/g;
    var match = str.match(regExp);
    return (match==null);
}

function isRangePort(str)
{
    var regExp = /[-,]/g;
    var match = str.match(regExp);
    return (match!=null);
}

function whichIpType(str)
{
    if (isIp(str)) {
        return SINGLE_IP;
    }
    else if (isIpWithNetmask(str)) {
        return CLASSLESS_IP_NETMASK;
    }
    else if (isClasslessIp(str)) {
        return CLASSLESS_IP;
    }
    else if (isRangeIp(str)) {
        return RANGE_IP;
    }
    else
        return UNKNOWN;
}

function whichPortType(str)
{
    if (isSinglePort(str)) {
        return SINGLE_PORT;
    }
    else if (isRangePort(str)) {
        return RANGE_PORT;
    }
    else
        return UNKNOWN;
}

function isDigit(str)
{
    var regExp = /[^\d{1,}]/g;
    var match = str.match(regExp);
    //return regExp.test(str);

    if (match) {
        return false;
    }
    else
    {
        return true;
    }
}

function isHexDigit(str)
{
    var regExp = /^[0-9A-Fa-f]{1,}/g;
    var match = str.match(regExp);

    if (match == null) {
        return false;
    }
    else
    {
        return true;
    }
}

function isHexDigitWithPrefix(str)
{
    // e.g. 0x1234
    var regExp = /^0x[0-9A-F]{1,}/gi;
    var match = str.match(regExp);

    if (match == null) {
        return false;
    }
    else
        return true;
}

function isEmail(str)
{
    /*
     * /^[a-zA-Z0-9._-]+:  
	 *	Means that the email address must begin with alpha-numeric 
	 *	characters (both lowercase and uppercase characters are allowed). 
	 *	It may have periods,underscores and hyphens.
	 *
	 * @:   There must be a �@� symbol after initial characters.
	 *
	 * [a-zA-Z0-9.-]+: 
	 * After the �@� sign there must be some alpha-numeric characters. 
	 * It can also contain period (�.') and and hyphens(�-').
	 *
	 * \.: 
	 * After the second group of characters there must be a period (�.'). 
	 * This is to separate domain and subdomain names.
	 *
	 * [a-zA-Z]{2,4}$/: 
	 * Finally, the email address must end with two to four alphabets. 
	 * Having a-z and A-Z means that both lowercase and uppercase letters are allowed. 
	 * {2,4} indicates the minimum and maximum number of characters. 
	 * This will allow domain names with 2, 3 and 4 characters e.g.; us, tx, org, com, net, wxyz).
	 */
    var regExp = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    var match = str.match(regExp);

    return (match==str);
}

function isUrl(str)
{
    /*
     * /^[a-zA-Z0-9._-]+:  
	 *	Means that the email address must begin with alpha-numeric 
	 *	characters (both lowercase and uppercase characters are allowed). 
	 *	It may have periods,underscores and hyphens.
	 *
	 * @:   There must be a �@� symbol after initial characters.
	 *
	 * [a-zA-Z0-9.-]+: 
	 * After the �@� sign there must be some alpha-numeric characters. 
	 * It can also contain period (�.') and and hyphens(�-').
	 *
	 * \.: 
	 * After the second group of characters there must be a period (�.'). 
	 * This is to separate domain and subdomain names.
	 *
	 * [a-zA-Z]{2,4}$/: 
	 * Finally, the email address must end with two to four alphabets. 
	 * Having a-z and A-Z means that both lowercase and uppercase letters are allowed. 
	 * {2,4} indicates the minimum and maximum number of characters. 
	 * This will allow domain names with 2, 3 and 4 characters e.g.; us, tx, org, com, net, wxyz).
	 */
    var regExp = /^(((ht|f)tp(s?))\:\/\/)?((www.|[a-zA-Z].)[a-zA-Z0-9\-\.]+\.(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk|co.th|in.th)|(\d{1,3}\x2E\d{1,3}\x2E\d{1,3}\x2E\d{1,3}))(\:\d{2,5})*(\/($|[a-zA-Z0-9\.\,\;\?\'\\\+&%\$#\=~_\-\}\{]+))*$/i;

    return (regExp.test(str));
}

function isEnglishText(txt) {
    var ret = true;
    for (i = 0; i < txt.length; i++) {
        code = txt.charAt(i);
        if (('ก' <= code)) {
            ret = false;
            break;
        }
    }

    return ret;
}

function trimEmailList(str)
{
    var paired = str.split(';');
    var tmp = '';

    for (var i=paired.length;i>=0;i--) {
        if (!paired[i]) {
            // Delete this item
            paired.splice(i, 1);
            continue;
        }
    }

    for (var i=0;i<paired.length;i++) {
        if (paired[i].trim()=='') { // trim
            continue;
        }

        tmp += paired[i].trim();
        if (i<paired.length-1) {
            tmp += ';';
        }
    }
    return tmp;
}

function filter_hex_char(event)
{
    var keychar;
    var keynum;
    var strPat = /[0-9A-F]/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;

    keychar = String.fromCharCode(keynum);

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_hex_char_with_prefix(event)
{
    var keychar;
    var keynum;
    var strPat = /[x0-9A-F]/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;

    keychar = String.fromCharCode(keynum);

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_digit_char(event, exceptChars)
{
    var keychar;
    var keynum;
    var strPat = /\d{1}/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;

    keychar = String.fromCharCode(keynum);
    if (exceptChars && exceptChars.indexOf(keychar)!=-1) {
        return true; // Additional char accepted
    }

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_port_range_char(event)
{
    var keychar;
    var keynum;
    var strPat = /\d{1}/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)
        || keynum == 44 // ','
        || keynum == 45 // '-'
            )
        return true;

    keychar = String.fromCharCode(keynum);

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_url_ip_char(event, exceptChars)
{
    var keychar;
    var keynum;
    var strPat = /(\d|\x2E|[a-z])/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;

    keychar = String.fromCharCode(keynum);
    if (exceptChars && exceptChars.indexOf(keychar)!=-1) {
        return true; // Additional char accepted
    }

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_ip_char(event)
{
    var keychar;
    var keynum;
    var strPat = /(\d|\x2E)/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;

    keychar = String.fromCharCode(keynum);

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_spcl_ip_char(event)
{
    var keychar;
    var keynum;
    var strPat = /(\d|\x2E|\x2F)/i; // supported multiple ip [digit | . | - | /]

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;

    keychar = String.fromCharCode(keynum);

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_mac_char(event)
{
    var keychar;
    var keynum;
    var strPat = /(\d|[A-F]|\x3A)/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;

    keychar = String.fromCharCode(keynum);

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_spcl_char(event, exceptChars)
{
    // Allow key [0-9a-zA-Z_] only
    var keychar;
    var keynum;
    var strPat = /([a-z0-9])/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;
    // Allow key lists
    if (
        keynum==95 // '_' key
            //|| keynum==32 // space
            //|| keynum==46 // '.'
            )
        return true;

    /*
	if (exceptChars && exceptChars.length>0) {
		for (var i=0;i<exceptChars.length;i++) {
			if (ascii_value(exceptChars.charAt(i))==keynum) {
				return true;
			}
		}
	}
	*/
	
    keychar = String.fromCharCode(keynum);
    if (exceptChars && exceptChars.indexOf(keychar)!=-1) {
        return true; // Additional char accepted
    }

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_passwd_char(event)
{
    var keychar;
    var keynum;
    //var strPat = /([a-z0-9])/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;

    // Not allow following key(s)
    if (
        keynum==32 // space
            )
        return false;

    return true; // accept all
}

function filter_email_char(event)
{
    // Allow key [0-9a-zA-Z_@.] only
    var keychar;
    var keynum;
    var strPat = /([a-z0-9])/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;
    // Allow key lists
    if (
        keynum==95 // '_' key
        || keynum==46 // '.'
        || keynum==64 // '@'
            )
        return true;
	
    keychar = String.fromCharCode(keynum);

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function filter_email_list_char(event)
{
    // Allow key [0-9a-zA-Z_@.;] only
    var keychar;
    var keynum;
    var strPat = /([a-z0-9])/i;

    keynum = getKeyPress(event);

    if (isCtrlChar(keynum) || isCtrlPadHeld(event)) return true;
    // Allow key lists
    if (
        keynum==95 // '_' key
        || keynum==46 // '.'
        || keynum==64 // '@'
        || keynum==59 // ';'
            )
        return true;
	
    keychar = String.fromCharCode(keynum);

    var match = keychar.match(strPat);

    if (match == null)
    {
        return false;
    }
}

function check_port_range_fmt(str)
{
    switch (whichPortType(str)) {
        case SINGLE_PORT:
            if (eval(str)>65535) {
                return false;
            }
            break;
        case RANGE_PORT:
            var paired = str.split(',');
            var portArry = new Array();
            var usedPort = new Array();
            for (var i=0;i<paired.length;i++) {
                var sPort, ePort;
                switch (whichPortType(paired[i])) {
                    case SINGLE_PORT:
                        if (eval(paired[i])>65535) {
                            return false;
                        }
                        sPort = ePort = eval(paired[i]);
                        break;
                    case RANGE_PORT:
                        portArry[i] = paired[i].split('-');
                        if (!portArry[i][0] || !portArry[i][1]) {
                            return false;
                        }
                        sPort = eval(portArry[i][0]);
                        ePort = eval(portArry[i][1]);
                        break;
                }

                // Check invalid sequence port
                if (sPort > ePort) {
                    return false;
                }

                // Check maximum port number
                if (sPort>65535 || ePort>65535) {
                    return false;
                }

                // Check used port
                for (var p=sPort;p<=ePort;p++) {
                    if (usedPort[p]==null) {
                        usedPort[p] = 1;
                    }
                    else
                        return false;
                }
            }
            break;
        default:
            return false;
    }
    return true;
}

function explain_port_range(str)
{
    switch (whichPortType(str)) {
        case SINGLE_PORT:
            return str + ';';
            break;
        case RANGE_PORT:
            var paired = str.split(',');
            var portArry = new Array();
            var tmp = '';

            for (var i=0;i<paired.length;i++) {
                var sPort, ePort;
                switch (whichPortType(paired[i])) {
                    case SINGLE_PORT:
                        sPort = ePort = eval(paired[i]);
                        break;
                    case RANGE_PORT:
                        portArry[i] = paired[i].split('-');
                        sPort = eval(portArry[i][0]);
                        ePort = eval(portArry[i][1]);
                }
                for (var p=sPort;p<=ePort;p++) {
                    tmp += p + ';';
                }
            }
            return tmp;
    }
}

function isUsedPort(port, expPort)
{
    var portArry = expPort.split(';');
    for (var i=0;i<portArry.length;i++) {
        if (eval(port)==eval(portArry[i])) {
            return false;
        }
    }
    return true;
}

function check_ip_class_D_fmt(obj)
{
    // We assume the input should be IP characters(0-9|.) only.
    var str = obj.value;
    var ipArry = str.split('.');
    var ret=true;
	
    // Check range
    if (ipArry.length!=4)
        ret=false;
    // Check ip element definition
    else if (ipArry[0]==null || ipArry[1]==null ||
        ipArry[2]==null || ipArry[3]==null )
        ret=false;
    // Check ip element valid
    else if (ipArry[0]=='' || ipArry[1]=='' ||
        ipArry[2]=='' || ipArry[3]=='' )
        ret=false;
    // Check Start IP Class D
    else if (ipArry[0]<=223 || ipArry[1]<0 ||
        ipArry[2]<0 || ipArry[3]<0)
        ret=false;
    // Check End IP Class D
    else if (ipArry[0]>239 || ipArry[1]>255 ||
        ipArry[2]>255 || ipArry[3]>255)
        ret=false;
		
    if (!ret) {
        alert("An invalid Class D IP address [" + str + "] has been entered.\n" +
            "Please correct to format \"xxx.xxx.xxx.xxx\"\n" +
            "And Between 224.0.0.0 to 240.0.0.0, eg. 224.0.0.1");
        obj.focus();
        obj.select();
    }
	
    return ret;
}

function check_ip_fmt(str)
{
    // We assume the input should be IP characters(0-9|.) only.
    var ipArry = str.split('.');
    var ret=true;
	
    // Check range
    if (ipArry.length!=4)
        ret=false;
    // Check ip element definition
    else if (ipArry[0]==null || ipArry[1]==null ||
        ipArry[2]==null || ipArry[3]==null )
        ret=false;
    // Check ip element valid
    else if (ipArry[0]=='' || ipArry[1]=='' ||
        ipArry[2]=='' || ipArry[3]=='' )
        ret=false;
    // Check IP Network
    else if (ipArry[0]<=0 || ipArry[1]<0 ||
        ipArry[2]<0 || ipArry[3]<=0)
        ret=false;
    // Check Broadcast Address
    else if (ipArry[0]>=255 || ipArry[1]>=255 ||
        ipArry[2]>=255 || ipArry[3]>=255)
        ret=false;
		
    /*
	if (!ret) {
		alert("An invalid IP address [" + str + "] has been entered.\n" +
			"Please correct to format \"xxx.xxx.xxx.xxx\", eg. 192.168.0.1");
		obj.focus();
		obj.select();
	}
	*/
	
    return ret;
}

function check_spcl_ip_fmt(str)
{
    switch (whichIpType(str)) {
        case SINGLE_IP:
            return check_single_ip_fmt(str);
            break;
        case CLASSLESS_IP_NETMASK:
            return check_classless_ip_netmask_fmt(str);
            break;
        case CLASSLESS_IP:
            return check_classless_ip_fmt(str);
            break;
        case RANGE_IP:
            return check_range_ip_fmt(str);
            break;
        default:
            return false;
    }
}

function check_single_ip_fmt(str)
{
    var ipArry = str.split('.');
    var ret=true;
	
    // Check range
    if (ipArry.length!=4)
        ret=false;
    // Check ip element definition
    else if (ipArry[0]==null || ipArry[1]==null ||
        ipArry[2]==null || ipArry[3]==null )
        ret=false;
    // Check ip element valid
    else if (ipArry[0]=='' || ipArry[1]=='' ||
        ipArry[2]=='' || ipArry[3]=='' )
        ret=false;
    // Check IP Network
    else if (ipArry[0]<0 || ipArry[1]<0 ||
        ipArry[2]<0 || ipArry[3]<0)
        ret=false;
    // Check Broadcast Address
    else if (ipArry[0]>255 || ipArry[1]>255 ||
        ipArry[2]>255 || ipArry[3]>255)
        ret=false;
		
    return ret;
}

function check_classless_ip_netmask_fmt(str)
{
    //e.g. 10.0.0.0/255.255.255.0
    var ipArry = str.split('/');

    return (check_single_ip_fmt(ipArry[0]) && check_netmask_fmt(ipArry[1]));
}

function check_classless_ip_fmt(str)
{
    //e.g. 10.0.0.0/24
    var ipArry = str.split('/');

    return (check_single_ip_fmt(ipArry[0]) && (ipArry[1]>=0) && (ipArry[1]<=32));
}

function check_range_ip_fmt(str)
{
    //e.g. 10.0.0.0-10.0.0.100
    var ipArry = str.split('-');

    return (check_single_ip_fmt(ipArry[0]) && check_single_ip_fmt(ipArry[1]));
}

function check_netmask_fmt(str)
{
    // We assume the input should be IP characters(0-9|.) only.
    var ipArry = str.split('.');
    var ret=true;
	
    // Check range
    if (ipArry.length!=4)
        ret=false;
    // Check ip element definition
    else if (ipArry[0]==null || ipArry[1]==null ||
        ipArry[2]==null || ipArry[3]==null )
        ret=false;
    // Check ip element valid
    else if (ipArry[0]=='' || ipArry[1]=='' ||
        ipArry[2]=='' || ipArry[3]=='' )
        ret=false;
    // Check IP Network
    else if (ipArry[0]<0 || ipArry[1]<0 ||
        ipArry[2]<0 || ipArry[3]<0)
        ret=false;
    // Check Broadcast Address
    else if (ipArry[0]>255 || ipArry[1]>255 ||
        ipArry[2]>255 || ipArry[3]>255)
        ret=false;
		
    /*
	if (!ret) {
		alert("An invalid netmask [" + str + "] has been entered.\n" +
			"Please correct to format \"xxx.xxx.xxx.xxx\", eg. 255.255.255.0");
		obj.focus();
		obj.select();
	}
	*/
	
    return ret;
}

function check_mac_fmt(str)
{
    if (str.length!=17) {
        return false;
    }
    if (!isMac(str)) {
        return false;
    }
    // We assume the input should be MAC characters(0-9|A-F|a-f|:) only.
    var macArry = str.split(':');
    var ret=true;
	
    // Check range
    if (macArry.length!=6)
        ret=false;
    // Check MAC element definition
    else if (macArry[0]==null || macArry[1]==null ||
        macArry[2]==null || macArry[3]==null ||
        macArry[4]==null || macArry[5]==null )
        ret=false;
    // Check MAC element valid
    else if (macArry[0]=='' || macArry[1]=='' ||
        macArry[2]=='' || macArry[3]=='' ||
        macArry[4]=='' || macArry[5]=='' )
        ret=false;
    // Check MAC element length
    else if (macArry[0].length!=2 || macArry[1].length!=2 ||
        macArry[2].length!=2 || macArry[3].length!=2 ||
        macArry[4].length!=2 || macArry[5].length!=2 )
        ret=false;
		
    /*
	if (!ret) {
		alert("An invalid MAC address [" + str + "] has been entered.\n" +
			"Please correct to format \"XX:XX:XX:XX:XX:XX\", eg. 01:23:45:AB:CD:EF");
		//if (obj.name!='') {
//			obj.focus();
//			obj.select();
		//}
	}
	*/
	
    return ret;
}

