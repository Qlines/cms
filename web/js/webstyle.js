/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function genMenu(tabs, titles, len)
{
    document.write("<div id='menu'><ul id='nav'>");
    for ( var i=0; i<len ; i++ ) {
        document.write("<li id='"+ tabs[i] +"'><a href='javascript:changeTab(tabs, " + i + ", " + len + ");'>" + titles[i] + "</a></li>");
    }
    document.write("</ul></div>");
}

function genIncludePage(tabs, links, len)
{
    document.write("<div class='contentbox' style='overflow: auto; height:557px; padding: 15px 0 0 20px;'>");
    for ( var i=0; i<len ; i++ ) {
        document.write("<div id='" + tabs[i] + "Div' style='display:none;'>" +
            "<jsp:include page='" + links[i] + "'></jsp:include>" +
            "</div>");
    }
    document.write("<div>");
}

function genFrame(tabs, links, len)
{
    document.write("<div class='contentbox' style='overflow: auto; height:607px; padding: 15px 15px 20px 20px;'>");
    for ( var i=0; i<len ; i++ ) {
        document.write("<iframe id='" + tabs[i] + "Div' src='" + links[i] + "' height=\"100%\" width=\"100%\" frameborder=\"0\" style=\"display:none; overflow:auto;\"></iframe>");
    }
    document.write("<div>");
}

function changeTab(tabs, showid, len)
{
    for ( var i=0; i<len ; i++ ) {
        if (i!=showid) {
            hide2(tabs[i] + 'Div');
            getElement(tabs[i]).className = "";
        }
        else {
            show2(tabs[i] + 'Div');
            getElement(tabs[i]).className = "activelink";
        }
    }
}

function genMenu2(tabs, links, titles, len)
{
    document.write("<div id='menu'><ul id='nav'>");
    for ( var i=0; i<len ; i++ ) {
        document.write("<li id='"+ tabs[i] +"'><a href='javascript:changeTab2(tabs, \"" + links[i] + "\", " + i + ");'>" + titles[i] + "</a></li>");
    }
    document.write("</ul></div>");
}

function genIncludePage2(tabs, links, len)
{
    document.write("<div class='contentbox' style='overflow: auto; height:557px; padding: 15px 0 0 20px;'>");
    for ( var i=0; i<len ; i++ ) {
        document.write("<div id='shareDiv' style='display:none;'>" +
            "<jsp:include page='" + links[i] + "'></jsp:include>" +
            "</div>");
    }
    document.write("<div>");
}

function genFrame2()
{
    document.write("<div class='contentbox' style='overflow: auto; height:607px; padding: 15px 15px 20px 20px;'>");
    document.write("<iframe id='shareDiv' height=\"100%\" width=\"100%\" frameborder=\"0\" style=\"display:block; overflow:auto;\"></iframe>");
    document.write("<div>");
}

function changeTab2(tabs, link, showid)
{
    var obj = getElement('shareDiv');
    if (obj) {
        obj.src = link;
    }
    for (t in tabs) {
        try {
            getElement(tabs[t]).className = "";
        } catch (e) {}
    }
    getElement(tabs[showid]).className = "activelink";
}
