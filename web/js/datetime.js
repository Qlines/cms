/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var MonthArry = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

function showDate(day, name)
{
    document.write("<select size='1' name='" + name + "'>");
    for (i = 1; i <= 31; i++)
        document.write("<option value=" + i + ((i == day) ? " selected" : "") + ">" + ((i < 10) ? "0" : "") + i + "</option>");
    document.write("</select>");
}

function showMonth(month, name)
{
    document.write("<select size='1' name='" + name + "'>");
    for (i = 0; i < MonthArry.length; i++)
        document.write("<option value=" + (i + 1) + ((i == month) ? " selected" : "") + ">" + MonthArry[i] + "</option>");
    document.write("</select>");
}

function showYear(year, name)
{
    document.write("<select size='1' name='" + name + "'>");
    for (i = year - 3; i <= year + 3; i++)
        document.write("<option value=" + i + ((i == year) ? " selected" : "") + ">" + i + "</option>");
    document.write("</select>");
}

function showHour(hour, name)
{
    document.write("<select size='1' name='" + name + "'>");
    for (i = 0; i < 24; i++)
        document.write("<option value=" + (i + 1) + ((i == hour) ? " selected" : "") + ">" + ((i < 10) ? "0" : "") + i + "</option>");
    document.write("</select>");
}

function showMinute(minute, name)
{
    document.write("<select size='1' name='" + name + "'>");
    for (i = 0; i < 60; i++)
        document.write("<option value=" + (i + 1) + ((i == minute) ? " selected" : "") + ">" + ((i < 10) ? "0" : "") + i + "</option>");
    document.write("</select>");
}

function showSecond(second, name)
{
    document.write("<select size='1' name='" + name + "'>");
    for (i = 0; i < 60; i++)
        document.write("<option value=" + (i + 1) + ((i == second) ? " selected" : "") + ">" + ((i < 10) ? "0" : "") + i + "</option>");
    document.write("</select>");
}

function isValidDate(dd, mm, yy)
{
    var dt = new Date(yy, mm - 1, dd);
    if (dd == dt.getDate() && mm - 1 == dt.getMonth() && yy == dt.getFullYear())
        return true;
    return false;
}

function isValidTime(str)
{
    var regExp = /^([0-1]?[0-9]|2[0-4]):([0-5][0-9])(:[0-5][0-9])?$/;
    var match = str.match(regExp);
    return (match == str);
}

function checkDateFormat(str) {
    var regExp;
    var match;

    var formation = false;
    var valid = false;

    // yyyy-mm-dd HH:MM:SS
    regExp = /^2\\d{3}[-/](0[1-9]|1[012]|[1-9])[-/](0[1-9]|[1-9]|[12][0-9]|3[01]) ((0|1)\\d{1}|2[0-3]|[0-9]):([0-5]\\d{1}|[0-9])(:([0-5]\\d{1})$|$)/;
    match = str.match(regExp);
    return (match == str);
}

var DateTimeChecker = (function() {
    // private static
    var dt = "";
    var type = "";
    var pattern = null;
    var err = "OK";

    // constructor
    var cls = function(str) {
        this.dt = str;
        this.err = "Formation error.";

        // find match pattern
        for (var p in patterns) {
            if (patterns[p][1](str, patterns[p][3])) {
                this.type = p;
                this.pattern = patterns[p][0];
                this.err = "OK";

                // Is validate
                this.validate = patterns[p][2](str, patterns[p][3]);
                if (!this.validate)
                    this.err = "DateTime is invalid.";

                break;
            }
        }

    };

    var isValid = function()
    {
        dd = arguments[0].match(/0?(\d{1,2})/)[1];
        mm = arguments[1].match(/0?(\d{1,2})/)[1];
        yy = arguments[2].match(/0?(\d{1,4})/)[1];
        var dt = new Date(yy, mm - 1, dd);
        if (dd == dt.getDate() && mm - 1 == dt.getMonth() && yy == dt.getFullYear())
            return true;

        return false;
    };

    var is_valid_a = function(str, pat) {
        var match = str.match(pat);
        return isValid(match[3], match[2], match[1]);
    };

    var is_valid_b = function(str, pat) {
        var match = str.match(pat);
        return isValid(match[1], match[2], match[3]);
    };

    var is_pat = function(str, pat) {
        var match = str.match(pat);
        //console.log(match);
        if (!match)
            return false;

        isValid(match[3], match[2], match[1]);

        return pat.test(str);
    };

    var patterns = {
        a: ['yyyy/mm/dd HH:MM:SS', function(s, p) {
                return is_pat(s, p);
            }, function(s, p) {
                return is_valid_a(s, p);
            }
            , /^(\d{4})[-/](0[1-9]|1[012]|[1-9])[-/](0[1-9]|[1-9]|[12][0-9]|3[01]) (0\d{1}|1\d{1}|2[0-3]|[0-9]):([0-5]\d{1}|[0-9]):?([0-5]\d{1}|[0-9])?$/]
        , b: ['dd/mm/yyyy HH:MM:SS', function(s, p) {
                return is_pat(s, p);
            }, function(s, p) {
                return is_valid_b(s, p);
            }
            , /^(0[1-9]|[1-9]|[12][0-9]|3[01])[-/](0[1-9]|1[012]|[1-9])[-/](\d{4}) (0\d{1}|1\d{1}|2[0-3]|[0-9]):([0-5]\d{1}|[0-9]):?([0-5]\d{1}|[0-9])?$/]
        , c: ['yyyy/mm/dd', function(s, p) {
                return is_pat(s, p);
            }, function(s, p) {
                return is_valid_a(s, p);
            }
            , /^(\d{4})[-/](0[1-9]|1[012]|[1-9])[-/](0[1-9]|[1-9]|[12][0-9]|3[01])$/]
        , d: ['dd/mm/yyyy', function(s, p) {
                return is_pat(s, p);
            }, function(s, p) {
                return is_valid_b(s, p);
            }
            , /^(0[1-9]|[1-9]|[12][0-9]|3[01])[-/](0[1-9]|1[012]|[1-9])[-/](\d{4})$/]
    };

    cls.prototype.getPattern = function() {
        return this.pattern;
    };
    cls.prototype.isValidate = function() {
        return this.validate;
    };
    cls.prototype.getError = function() {
        return this.err;
    };
    cls.prototype.getType = function() {
        return this.type;
    };

    return cls;

})();

var DateFormatter = (function() {
    // private static
    var formatString = "";
    var mthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    var dayNames = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

    // constructor
    var cls = function(fmt) {
        this.formatString = fmt
    }

    // private static function
    var zeroPad = function(number) {
        return ("0" + number).substr(-2, 2);
    }

    var dateMarkers = {
        d: ['getDate', function(v) {
                return zeroPad(v)
            }],
        m: ['getMonth', function(v) {
                return zeroPad(v + 1)
            }],
        n: ['getMonth', function(v) {
                return mthNames[v];
            }],
        w: ['getDay', function(v) {
                return dayNames[v];
            }],
        y: ['getFullYear'],
        H: ['getHours', function(v) {
                return zeroPad(v)
            }],
        M: ['getMinutes', function(v) {
                return zeroPad(v)
            }],
        S: ['getSeconds', function(v) {
                return zeroPad(v)
            }],
        i: ['toISOString']
    };

    cls.prototype.format = function(date) {
        var dateTxt = this.formatString.replace(/%(.)/g,
                function(m, p) {
                    var rv = date[(dateMarkers[p])[0]]()
                    if (dateMarkers[p][1] != null)
                        rv = dateMarkers[p][1](rv);

                    return rv
                });

        return dateTxt;
    };

    return cls;
})();