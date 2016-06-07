/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var xm = 0;
var x_offset = -255;
var ym = 0;
var y_offset = -115;
var nav = (document.layers);
if(nav) document.captureEvents(Event.MOUSEMOVE);
document.onmousemove = get_mouse; 

function get_mouse(e)
{

    if (!e)
        var e = window.event||window.Event;
    xm = (nav) ? e.pageX : e.clientX+document.body.scrollLeft;
    ym = (nav) ? e.pageY : e.clientY+document.body.scrollTop;
    xm += (xm + x_offset) > 0 ? x_offset : 5;
    ym += (ym + y_offset) > 0 ? y_offset : 15;
}

function setPosition(containerid)
{
    /*
	obox=id;
	dt=new Date();
	odt=new Date(dt);
	dotimer(id);
	*/
    getElement(containerid).style.top = ym;
    getElement(containerid).style.left = xm;
}

