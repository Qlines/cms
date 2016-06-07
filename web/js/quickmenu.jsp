<%--
    Document   : quickmenu
    Created on : 25 ต.ค. 2552, 23:16:12
    Author     : nack_ki
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="hippoping.smsgw.api.db.User" %>
<%@page import="hippoping.smsgw.api.db.User.USER_TYPE" %>

<%
    User user = (User)session.getAttribute("USER");
    USER_TYPE policy = USER_TYPE.GUEST;
    if (user!=null) {
        policy = user.getType();
    }
%>

//Compressed CSS Styles
//document.write('<!--%%%%%%%%%%%% QuickMenu Styles [Keep in head for full validation!] %%%%%%%%%%%--><style type="text/css">/*!!!!!!!!!!! QuickMenu Core CSS [Do Not Modify!] !!!!!!!!!!!!!*/.qmmc .qmdivider{display:block;font-size:1px;border-width:0px;border-style:solid;position:relative;z-index:1;}.qmmc .qmdividery{float:left;width:0px;}.qmmc .qmtitle{display:block;cursor:default;white-space:nowrap;position:relative;z-index:1;}.qmclear {font-size:1px;height:0px;width:0px;clear:left;line-height:0px;display:block;float:none !important;}.qmmc {position:relative;zoom:1;z-index:10;}.qmmc a, .qmmc li {float:left;display:block;white-space:nowrap;position:relative;z-index:1;}.qmmc div a, .qmmc ul a, .qmmc ul li {float:none;}.qmsh div a {float:left;}.qmmc div{visibility:hidden;position:absolute;}.qmmc .qmcbox{cursor:default;display:block;position:relative;z-index:1;}.qmmc .qmcbox a{display:inline;}.qmmc .qmcbox div{float:none;position:static;visibility:inherit;left:auto;}/*!!!!!!!!!!! QuickMenu Styles [Please Modify!] !!!!!!!!!!!*//* QuickMenu 0 */#qm0{width:200px;background-color:transparent;}#qm0 a{padding:5px 5px 5px 8px;color:#3c3c3c;font-family:Arial;font-size:12px;text-decoration:none;font-weight:bold;}#qm0 a:hover{text-decoration:underline;}#qm0 li:hover>a{text-decoration:underline;}body #qm0 .qmactive, body #qm0 .qmactive:hover{text-decoration:none;font-weight:bold;}#qm0 div{padding:5px 0px;}#qm0 div a{padding:2px 0px 2px 15px;margin:0px 5px;background-image:none;color:#3c3c3c;font-size:11px;font-weight:normal;}#qm0 div a:hover{text-decoration:underline;}#qm0 div a:hover{text-decoration:underline;}body #qm0 div .qmactive, body #qm0 div .qmactive:hover{text-decoration:none;font-weight:bold;}#qm0 .qmtitle{background-image:url(images/logo.gif);font-size:11px;}#qm0 .qmdividerx{border-top-width:1px;}#qm0 .qmcbox{width:150px;padding:5px;background-color:#ffffa2;font-family:Arial;font-size:12px;border-width:1px;border-style:dashed;border-color:#111111;}#qm0 .qmbox{border-width:1px;border-style:solid;border-color:#999999;}#qm0 .qmhoverfill{background-color:#91ed99;}#qm0 div .qmhoverfill{background-color:#91e599;border-width:1px;border-style:solid;border-color:#555555;}ul#qm0{border-width:1px;border-style:solid;border-color:#3c3c3c;}ul#qm0 ul{border-width:1px;border-style:solid;border-color:#3c3c3c;}ul#qm0 ul a{padding:2px 20px 2px 5px;}ul#qm0 .qmparent{background-image:url(qmimages/arrow_0.gif);background-repeat:no-repeat;background-position:95% 55%;}</style>');

//Add-On Core Code (Remove when not using any add-on's)
document.write('<style type="text/css">.qmfv{visibility:visible !important;}.qmfh{visibility:hidden !important;}</style><script type="text/javascript">qmad=new Object();qmad.bvis="";qmad.bhide="";</script>');
document.write('<script type="text/javascript" src="/js/utils.js"></script>');

/*******  Menu 0 Add-On Settings *******/
var a = qmad.qm0 = new Object();

// Item Bullets Add On
a.ibullets_apply_to = "parent";
//	a.ibullets_main_image = "images/color_plus.gif";
//	a.ibullets_main_image_active = "images/color_minus.gif";
//	a.ibullets_main_image_width = 11;
//	a.ibullets_main_image_height = 11;
//	a.ibullets_main_position_x = -16;
//	a.ibullets_main_position_y = -5;
a.ibullets_main_image = "images/sub_plus.gif";
a.ibullets_main_image_hover = "images/sub_plus_hover.gif";
a.ibullets_main_image_active = "images/sub_plus_active.gif";
a.ibullets_main_image_width = 5;
a.ibullets_main_image_height = 5;
a.ibullets_main_position_x = -9;
a.ibullets_main_position_y = -5;
//	a.ibullets_main_align_x = "right";
a.ibullets_main_align_x = "left";
a.ibullets_main_align_y = "middle";
//	a.ibcss_main_type = "arrow-v";
//	a.ibcss_main_direction = "right";
//	a.ibcss_main_size = 4;
//	a.ibcss_main_border_color = "#797979";
//	a.ibcss_main_border_color_active = "#dd3300";
//	a.ibcss_main_position_x = -9;
//	a.ibcss_main_position_y = -3;
	
a.ibullets_sub_image = "images/sub_plus.gif";
a.ibullets_sub_image_hover = "images/sub_plus_hover.gif";
a.ibullets_sub_image_active = "images/sub_plus_active.gif";
a.ibullets_sub_image_width = 5;
a.ibullets_sub_image_height = 5;
a.ibullets_sub_position_x = -12;
a.ibullets_sub_position_y = -2;
a.ibullets_sub_align_x = "left";
a.ibullets_sub_align_y = "middle";

// Tree Menu Add On
a.tree_enabled = true;
a.tree_sub_sub_indent = 15;
a.tree_hide_focus_box = true;
a.tree_expand_animation = 2;
a.tree_expand_step_size = 15;
a.tree_collapse_animation = 3;
a.tree_collapse_step_size = 20;

// Keyboard Access Add On
a.keyboard_access_active = true;

// Show Subs Onload Add On
a.showload_active = true;
a.showload_auto_close = true;


//Core QuickMenu Code
var qm_si,qm_li,qm_lo,qm_tt,qm_th,qm_ts,qm_la,qm_ic,qm_ib;var qp="parentNode";var qc="className";var qm_t=navigator.userAgent;var qm_o=qm_t.indexOf("Opera")+1;var qm_s=qm_t.indexOf("afari")+1;var qm_s2=qm_s&&qm_t.indexOf("ersion/2")+1;var qm_s3=qm_s&&qm_t.indexOf("ersion/3")+1;var qm_n=qm_t.indexOf("Netscape")+1;var qm_v=parseFloat(navigator.vendorSub);;function qm_create(sd,v,ts,th,oc,rl,sh,fl,ft,aux,l){
    var w="onmouseover";var ww=w;var e="onclick";if(oc){
        if(oc=="all"||(oc=="lev2"&&l>=2)){
            w=e;ts=0;
        }if(oc=="all"||oc=="main"){
            ww=e;th=0;
        }
        }if(!l){
        l=1;qm_th=th;sd=document.getElementById("qm"+sd);if(window.qm_pure)sd=qm_pure(sd);sd[w]=function(e){
            qm_kille(e)
            };document[ww]=qm_bo;if(oc=="main"){
            qm_ib=true;sd[e]=function(event){
                qm_ic=true;qm_oo(new Object(),qm_la,1);qm_kille(event)
                };document.onmouseover=function(){
                qm_la=null;clearTimeout(qm_tt);qm_tt=null;
            };
        }sd.style.zoom=1;if(sh)x2("qmsh",sd,1);if(!v)sd.ch=1;
    }else  if(sh)sd.ch=1;if(oc)sd.oc=oc;if(sh)sd.sh=1;if(fl)sd.fl=1;if(ft)sd.ft=1;if(rl)sd.rl=1;sd.style.zIndex=l+""+1;var lsp;var sp=sd.childNodes;for(var i=0;i<sp.length;i++){
        var b=sp[i];if(b.tagName=="A"){
            lsp=b;b[w]=qm_oo;if(w==e)b.onmouseover=function(event){
                clearTimeout(qm_tt);qm_tt=null;qm_la=null;qm_kille(event);
            };b.qmts=ts;if(l==1&&v){
                b.style.styleFloat="none";b.style.cssFloat="none";
            }
            }else  if(b.tagName=="DIV"){
            if(window.showHelp&&!window.XMLHttpRequest)sp[i].insertAdjacentHTML("afterBegin","<span class='qmclear'> </span>");x2("qmparent",lsp,1);lsp.cdiv=b;b.idiv=lsp;if(qm_n&&qm_v<8&&!b.style.width)b.style.width=b.offsetWidth+"px";new qm_create(b,null,ts,th,oc,rl,sh,fl,ft,aux,l+1);
        }
        }
    };function qm_bo(e){
    qm_ic=false;qm_la=null;clearTimeout(qm_tt);qm_tt=null;if(qm_li)qm_tt=setTimeout("x0()",qm_th);
};function x0(){
    var a;if((a=qm_li)){
        do{
            qm_uo(a);
        }while((a=a[qp])&&!qm_a(a))
    }qm_li=null;
};function qm_a(a){
    if(a[qc].indexOf("qmmc")+1)return 1;
};function qm_uo(a,go){
    if(!go&&a.qmtree)return;if(window.qmad&&qmad.bhide)eval(qmad.bhide);a.style.visibility="";x2("qmactive",a.idiv);
};;function qa(a,b){
    return String.fromCharCode(a.charCodeAt(0)-(b-(parseInt(b/2)*2)));
}eval("");;function qm_oo(e,o,nt){
    if(!o)o=this;if(qm_la==o&&!nt)return;if(window.qmv_a&&!nt)qmv_a(o);if(window.qmwait){
        qm_kille(e);return;
    }clearTimeout(qm_tt);qm_tt=null;qm_la=o;if(!nt&&o.qmts){
        qm_si=o;qm_tt=setTimeout("qm_oo(new Object(),qm_si,1)",o.qmts);return;
    }var a=o;if(a[qp].isrun){
        qm_kille(e);return;
    }if(qm_ib&&!qm_ic)return;var go=true;while((a=a[qp])&&!qm_a(a)){
        if(a==qm_li)go=false;
    }if(qm_li&&go){
        a=o;if((!a.cdiv)||(a.cdiv&&a.cdiv!=qm_li))qm_uo(qm_li);a=qm_li;while((a=a[qp])&&!qm_a(a)){
            if(a!=o[qp]&&a!=o.cdiv)qm_uo(a);else break;
        }
        }var b=o;var c=o.cdiv;if(b.cdiv){
        var aw=b.offsetWidth;var ah=b.offsetHeight;var ax=b.offsetLeft;var ay=b.offsetTop;if(c[qp].ch){
            aw=0;if(c.fl)ax=0;
        }else {
            if(c.ft)ay=0;if(c.rl){
                ax=ax-c.offsetWidth;aw=0;
            }ah=0;
        }if(qm_o){
            ax-=b[qp].clientLeft;ay-=b[qp].clientTop;
        }if(qm_s2&&!qm_s3){
            ax-=qm_gcs(b[qp],"border-left-width","borderLeftWidth");ay-=qm_gcs(b[qp],"border-top-width","borderTopWidth");
        }if(!c.ismove){
            c.style.left=(ax+aw)+"px";c.style.top=(ay+ah)+"px";
        }x2("qmactive",o,1);if(window.qmad&&qmad.bvis)eval(qmad.bvis);c.style.visibility="inherit";qm_li=c;
    }else  if(!qm_a(b[qp]))qm_li=b[qp];else qm_li=null;qm_kille(e);
};function qm_gcs(obj,sname,jname){
    var v;if(document.defaultView&&document.defaultView.getComputedStyle)v=document.defaultView.getComputedStyle(obj,null).getPropertyValue(sname);else  if(obj.currentStyle)v=obj.currentStyle[jname];if(v&&!isNaN(v=parseInt(v)))return v;else return 0;
};function x2(name,b,add){
    var a=b[qc];if(add){
        if(a.indexOf(name)==-1)b[qc]+=(a?' ':'')+name;
    }else {
        b[qc]=a.replace(" "+name,"");b[qc]=b[qc].replace(name,"");
    }
    };function qm_kille(e){
    if(!e)e=event;e.cancelBubble=true;if(e.stopPropagation&&!(qm_s&&e.type=="click"))e.stopPropagation();
}

//Add-On Code: Keyboard Access
if(!qmad.keyaccess){
    qmad.keyaccess=new Object();if(window.attachEvent)window.attachEvent("onload",qm_kb_init);else  if(window.addEventListener)window.addEventListener("load",qm_kb_init,1);if(window.attachEvent)document.attachEvent("onclick",qm_kc_hover_off);else  if(window.addEventListener)document.addEventListener("click",qm_kc_hover_off,1);
};function qm_kb_init(){
    if(window.qmv)return;qm_ts=1;var q=qmad.tabs;var a;for(var i=0;i<10;i++){
        if(a=document.getElementById("qm"+i)){
            var ss=qmad[a.id];if(ss&&ss.keyboard_access_active){
                var at=a.getElementsByTagName("A");for(var j=0;j<at.length;j++){
                    if(at[j].tagName=="A"){
                        if(at[j].attachEvent)at[j].attachEvent("onkeydown",qm_kb_press);else  if(at[j].addEventListener)at[j].addEventListener("keypress",qm_kb_press,1);
                    }
                    }
                }
            }
        }
    };function qm_kb_press(e){
    e=window.event||e;var kc=e.keyCode;var targ=e.srcElement||e.target;while(targ.tagName!="A")targ=targ[qp];var na;var ish=false;var c1;if(document.defaultView&&document.defaultView.getComputedStyle)c1=document.defaultView.getComputedStyle(targ,null).getPropertyValue("float");else  if(targ.currentStyle)c1=targ.currentStyle.styleFloat;if(c1&&c1.toLowerCase()=="left")ish=true;if(kc==13){
        if(targ.cdiv){
            qm_kc_fnl(targ);if(window.showHelp){
                e.cancelBubble=true;return false;
            }
            }
        }else  if(kc==40){
        if(targ.cdiv&&ish){
            qm_kc_fnl(targ);
        }else {
            na=qm_kc_getnp(targ,"next");if(na){
                na.focus();qm_kc_hover(na);
            }
            }
        }else  if(kc==38){
        na=qm_kc_getnp(targ,"previous");if(na){
            na.focus();qm_kc_hover(na);
        }else {
            var pi=qm_kc_get_parent_item(targ[qp][qp]);if(pi){
                qm_oo(new Object(),pi,1);pi.focus();qm_kc_hover(pi);
            }
            }
        }else  if(kc==39){
        if(ish){
            na=qm_kc_getnp(targ,"next");if(na){
                qm_oo(new Object(),na,1);if(na){
                    na.focus();qm_kc_hover(na);
                }
                }
            }else  if(targ.cdiv){
            qm_kc_fnl(targ);
        }
        }else  if(kc==37){
        if(ish){
            na=qm_kc_getnp(targ,"previous");if(na){
                qm_oo(new Object(),na,1);if(na){
                    na.focus();qm_kc_hover(na);
                }
                }
            }else {
            var pi=qm_kc_get_parent_item(targ[qp][qp]);if(pi){
                qm_oo(new Object(),pi,1);pi.focus();qm_kc_hover(pi);
            }
            }
        }
    };function qm_kc_hover_off(){
    if(qmad.keyaccess.lasthover)x2("qmkeyboardaccess",qmad.keyaccess.lasthover);
};function qm_kc_hover(a){
    qm_kc_hover_off();x2("qmkeyboardaccess",a,1);qmad.keyaccess.lasthover=a;
};function qm_kc_fnl(t){
    var na=t.cdiv.getElementsByTagName("A")[0];qm_oo(new Object(),t,1);na.focus();qm_kc_hover(na);
};function qm_kc_get_parent_item(d){
    var dc=d.childNodes;for(var i=0;i<dc.length;i++){
        if(dc[i].cdiv&&dc[i].cdiv.style.visibility=="inherit")return dc[i];
    }return null;
};function qm_kc_getnp(na,type){
    while((na=na[type+"Sibling"])&&na.tagName!="A")continue;return na;
}

//Add-On Code: Item Images
qmad.image=new Object();qmad.image.preload=new Array();if(qmad.bvis.indexOf("qm_image_switch(b,1);")==-1){
    qmad.bvis+="qm_image_switch(b,1);";qmad.bhide+="qm_image_switch(a.idiv,false,1);";if(window.attachEvent){
    window.attachEvent("onload",qm_image_preload);document.attachEvent("onmouseover",qm_image_off);
}else  if(window.addEventListener){
    window.addEventListener("load",qm_image_preload,1);document.addEventListener("mouseover",qm_image_off,false);
}document.write('<style type="text/css">.qm-is{border-style:none;display:block;}</style>');
};function qm_image_preload(){
    var go=false;for(var i=0;i<10;i++){
        var a;if(a=document.getElementById("qm"+i)){
            var ai=a.getElementsByTagName("IMG");for(var j=0;j<ai.length;j++){
                if(ai[j].className.indexOf("qm-is")+1){
                    ai[j].style.visibility="inherit";go=true;var br=qm_image_base(ai[j]);if(ai[j].className.indexOf("qm-ih")+1)qm_image_preload2(br[0]+"_hover."+br[1]);if(ai[j].className.indexOf("qm-ia")+1)qm_image_preload2(br[0]+"_active."+br[1]);ai[j].setAttribute("qmvafter",1);if((z=window.qmv)&&(z=z.addons)&&(z=z.image))z["on"+i]=true;
                }
                }if(go){
                ai=a.getElementsByTagName("A");for(var j=0;j<ai.length;j++){
                    if(window.attachEvent)ai[j].attachEvent("onmouseover",qmv_image_hover);else  if(window.addEventListener)ai[j].addEventListener("mouseover",qmv_image_hover,1);
                }
                }if(go)a.onmouseover=function(e){
                qm_kille(e)
                };
        }
        }
    };function qmv_image_hover(e){
    e=e||window.event;var targ=e.srcElement||e.target;while(targ&&targ.tagName!="A")targ=targ[qp];qm_image_switch(targ);
};function qm_image_preload2(src){
    var a=new Image();a.src=src;qmad.image.preload.push(a);
};function qm_image_base(a,full){
    var br=qm_image_split_ext_name(a.getAttribute("src",2));br[0]=br[0].replace("_hover","");br[0]=br[0].replace("_active","");if(full)return br[0]+"."+br[1];else return br;
};function qm_image_off(){
    if(qmad.image.la&&qmad.image.la.className.indexOf("qmactive")==-1){
        qm_image_switch(qmad.image.la,false,1);qmad.image.la=null;
    }
    };function qm_image_switch(a,active,hide,force){
    if((z=window.qmv)&&(z=z.addons)&&(z=z.image)&&!z["on"+qm_index(a)])return;if(!active&&!hide&&qmad.image.la &&qmad.image.la!=a&&qmad.image.la.className.indexOf("qmactive")==-1)qm_image_switch(qmad.image.la,false,1);var img=a.getElementsByTagName("IMG");for(var i=0;i<img.length;i++){
        var iic=img[i].className;if(iic&&iic.indexOf("qm-is")+1){
            var br=qm_image_base(img[i]);if(!active&&!hide&&iic.indexOf("qm-ih")+1&&(a.className.indexOf("qmactive")==-1||force)){
                qmad.image.la=a;img[i].src=br[0]+"_hover."+br[1];continue;
            }if(active){
                if(iic.indexOf("qm-ia")+1)img[i].src=br[0]+"_active."+br[1];else  if(iic.indexOf("qm-ih")+1)img[i].src=br[0]+"_hover."+br[1];continue;
            }if(hide)img[i].src=br[0]+"."+br[1];
        }
        }
    };function qm_image_split_ext_name(s){
    var ext=s.split(".");ext=ext[ext.length-1];var fn=s.substring(0,s.length-(ext.length+1));return new Array(fn,ext);
}

//Add-On Code: Tree Menu
qmad.br_navigator=navigator.userAgent.indexOf("Netscape")+1;qmad.br_version=parseFloat(navigator.vendorSub);qmad.br_oldnav=qmad.br_navigator&&qmad.br_version<7.1;qmad.tree=new Object();if(qmad.bvis.indexOf("qm_tree_item_click(b.cdiv);")==-1){
    qmad.bvis+="qm_tree_item_click(b.cdiv);";qm_tree_init_styles();
}if(window.attachEvent)window.attachEvent("onload",qm_tree_init);else  if(window.addEventListener)window.addEventListener("load",qm_tree_init,1);;

function qm_tree_init_styles(){
    var a,b;if(qmad){
        var i;for(i in qmad){
            if(i.indexOf("qm")!=0||i.indexOf("qmv")+1)continue;var ss=qmad[i];if(ss.tree_width)ss.tree_enabled=true;if(ss&&ss.tree_enabled){
                var az="";if(window.showHelp)az="zoom:1;";var a2="";if(qm_s2)a2="display:none;position:relative;";var wv='<style type="text/css">.qmistreestyles'+i+'{} #'+i+'{position:relative !important;} #'+i+' a{float:none !important;white-space:normal !important;position:static !important}#'+i+' div{width:auto !important;left:0px !important;top:0px !important;overflow:hidden !important;'+a2+az+'margin-left:0px !important;margin-top:0px !important;}';if(ss.tree_sub_sub_indent)wv+='#'+i+' div div{padding-left:'+ss.tree_sub_sub_indent+'px}';document.write(wv+'</style>');
            }
            }
        }
    };

function qm_tree_init(event,spec){
    var q=qmad.tree;var a,b;var i;for(i in qmad){
        if(i.indexOf("qm")!=0||i.indexOf("qmv")+1||i.indexOf("qms")+1||(!isNaN(spec)&&spec!=i))continue;var ss=qmad[i];if(ss&&ss.tree_enabled){
            q.estep=ss.tree_expand_step_size;if(!q.estep)q.estep=1;q.cstep=ss.tree_collapse_step_size;if(!q.cstep)q.cstep=1;q.acollapse=ss.tree_auto_collapse;q.no_focus=ss.tree_hide_focus_box;q.etype=ss.tree_expand_animation;if(q.etype)q.etype=parseInt(q.etype);if(!q.etype)q.etype=0;q.ctype=ss.tree_collapse_animation;if(q.ctype)q.ctype=parseInt(q.ctype);if(!q.ctype)q.ctype=0;if(qmad.br_oldnav){
                q.etype=0;q.ctype=0;
            }qm_tree_init_items(document.getElementById(i));
        }i++;
    }
    };

function qm_tree_init_items(a,sub){
    var w,b;var q=qmad.tree;var aa;aa=a.childNodes;for(var j=0;j<aa.length;j++){
        if(aa[j].tagName=="A"){
            if(aa[j].cdiv){
                aa[j].cdiv.ismove=1;aa[j].cdiv.qmtree=1;
            }if(!aa[j].onclick){
                aa[j].onclick=aa[j].onmouseover;aa[j].onmouseover=null;
            }if(q.no_focus){
                aa[j].onfocus=function(){
                    this.blur();
                };
            }if(aa[j].cdiv)new qm_tree_init_items(aa[j].cdiv,1);if(aa[j].getAttribute("qmtreeopen"))qm_oo(new Object(),aa[j],1)
                }
        }
    };

function qm_tree_item_click(a,close)
{
    var z;

    if(!a.qmtree&&!((z=window.qmv)&&z.loaded))
    {
        var id=qm_get_menu(a).id;
        //alert('test1');
        if(window.qmad&&qmad[id]&&qmad[id].tree_enabled)
            x2("qmfh",a,1);
        return;
    }
    //alert('interupt');
    if((z=window.qmv)&&(z=z.addons)&&(z=z.tree_menu)&&!z["on"+qm_index(a)])
    {
        //alert('out here');
        return;
    }
    x2("qmfh",a);
    var q=qmad.tree;
    if(q.timer)
    {
        //alert('timer out here');
        return;
    }
    qm_la=null;
    q.co=new Object();
    var levid="a"+qm_get_level(a);
    var ex=false;
    var cx=false;
    if(q.acollapse)
    {
        //alert('test2');
        var mobj=qm_get_menu(a);
        var ds=mobj.getElementsByTagName("DIV");
        for(var i=0;i<ds.length;i++)
        {
            if(ds[i].style.position=="relative"&&ds[i]!=a)
            {
                var go=true;
                var cp=a[qp];
                while(!qm_a(cp))
                {
                    if(ds[i]==cp)
                        go=false;
                    cp=cp[qp];
                }
                if(go)
                {
                    cx=true;
                    q.co["a"+i]=ds[i];
                    qm_uo(ds[i],1);
                }
            }
        }
    }
    //alert('check relative='+a.style.position);
    if(a.style.position=="relative")
    {
        //alert('test3');
        cx=true;
        q.co["b"]=a;
        var d=a.getElementsByTagName("DIV");
        for(var i=0;i<d.length;i++)
        {
            if(d[i].style.position=="relative")
            {
                q.co["b"+i]=d[i];
                qm_uo(d[i],1);
            }
        }
        a.qmtreecollapse=1;
        qm_uo(a,1);
        if(window.qm_ibullets_hover)
            qm_ibullets_hover(null,a.idiv);
    }
    else 
    {
        //alert('test4');
        ex=true;
        if(qm_s2)
            a.style.display="block";
        a.style.position="relative";
        q.eh=a.offsetHeight;
        a.style.height="0px";
        x2("qmfv",a,1);
        x2("qmfh",a);
        a.qmtreecollapse=0;
        q.eo=a;
    }
    qmwait=true;
    qm_tree_item_expand(ex,cx,levid);
};

function qm_tree_item_expand(expand,collapse,levid)
{
    var q=qmad.tree;
    var go=false;
    var cs=1;
    if(collapse)
    {
        for(var i in q.co)
        {
            if(!q.co[i].style.height&&q.co[i].style.position=="relative")
            {
                q.co[i].style.height=(q.co[i].offsetHeight)+"px";
                q.co[i].qmtreeht=parseInt(q.co[i].style.height);
            }
            cs=parseInt((q.co[i].offsetHeight/parseInt(q.co[i].qmtreeht))*q.cstep);
            if(q.ctype==1)
                cs=q.cstep-cs+1;
            else  if(q.ctype==2)
                cs=cs+1;
            else  if(q.ctype==3)cs=q.cstep;
            if(q.ctype&&parseInt(q.co[i].style.height)-cs>0)
            {
                q.co[i].style.height=parseInt(q.co[i].style.height)-cs+"px";
                go=true;
            }
            else 
            {
                q.co[i].style.height="";
                q.co[i].style.position="";
                if(qm_s2)
                    q.co[i].style.display="";
                x2("qmfh",q.co[i],1);
                x2("qmfv",q.co[i]);
                q.co[i].style.visibility="inherit";
            }
        }
    }

    if(expand)
    {
        cs=parseInt((q.eo.offsetHeight/q.eh)*q.estep);
        if(q.etype==2)
            cs=q.estep-cs;
        else  if(q.etype==1)
            cs=cs+1;
        else  if(q.etype==3)
            cs=q.estep;
        if(q.etype&&q.eo.offsetHeight<(q.eh-cs))
        {
            q.eo.style.height=parseInt(q.eo.style.height)+cs+"px";
            go=true;
            if(window.qmv_position_pointer)
                qmv_position_pointer();
        }
        else 
        {
            q.eo.qmtreeh=q.eo.style.height;q.eo.style.height="";
            if(window.qmv_position_pointer)
                qmv_position_pointer();
        }
    }
    if(go)
    {
        //alert('settimeout');
        q.timer=setTimeout("qm_tree_item_expand("+expand+","+collapse+",'"+levid+"')",10);
    }
    else 
    {
        //alert('qmwait=false\nq.timer=null');
        qmwait=false;
        q.timer=null;
    }
};

function qm_get_level(a){
    lev=0;while(!qm_a(a)&&(a=a[qp]))lev++;return lev;
};function qm_get_menu(a){
    while(!qm_a(a)&&(a=a[qp]))continue;return a;
}

//Add-On Code: Item Bullets
qmad.br_navigator=navigator.userAgent.indexOf("Netscape")+1;qmad.br_version=parseFloat(navigator.vendorSub);qmad.br_oldnav6=qmad.br_navigator&&qmad.br_version<7;if(!qmad.br_oldnav6){
    if(!qmad.ibullets)qmad.ibullets=new Object();if(qmad.bvis.indexOf("qm_ibullets_active(o,false);")==-1){
        qmad.bvis+="qm_ibullets_active(o,false);";qmad.bhide+="qm_ibullets_active(a,1);";if(window.attachEvent)window.attachEvent("onload",qm_ibullets_init);else  if(window.addEventListener)window.addEventListener("load",qm_ibullets_init,1);if(window.attachEvent)document.attachEvent("onmouseover",qm_ibullets_hover_off);else  if(window.addEventListener)document.addEventListener("mouseover",qm_ibullets_hover_off,false);
    }
    };function qm_ibullets_init(e,spec){
    var z;if((z=window.qmv)&&(z=z.addons)&&(z=z.item_bullets)&&(!z["on"+qmv.id]&&z["on"+qmv.id]!=undefined&&z["on"+qmv.id]!=null))return;qm_ts=1;var q=qmad.ibullets;var a,b,r,sx,sy;z=window.qmv;for(i=0;i<10;i++){
        if(!(a=document.getElementById("qm"+i))||(!isNaN(spec)&&spec!=i))continue;var ss=qmad[a.id];if(ss&&(ss.ibullets_main_image||ss.ibullets_sub_image)){
            q.mimg=ss.ibullets_main_image;if(q.mimg){
                q.mimg_a=ss.ibullets_main_image_active;if(!z)qm_ibullets_preload(q.mimg_a);q.mimg_h=ss.ibullets_main_image_hover;if(!z)qm_ibullets_preload(q.mimg_a);q.mimgwh=eval("new Array("+ss.ibullets_main_image_width+","+ss.ibullets_main_image_height+")");r=q.mimgwh;if(!r[0])r[0]=9;if(!r[1])r[1]=6;sx=ss.ibullets_main_position_x;sy=ss.ibullets_main_position_y;if(!sx)sx=0;if(!sy)sy=0;q.mpos=eval("new Array('"+sx+"','"+sy+"')");q.malign=eval("new Array('"+ss.ibullets_main_align_x+"','"+ss.ibullets_main_align_y+"')");r=q.malign;if(!r[0])r[0]="right";if(!r[1])r[1]="center";
            }q.simg=ss.ibullets_sub_image;if(q.simg){
                q.simg_a=ss.ibullets_sub_image_active;if(!z)qm_ibullets_preload(q.simg_a);q.simg_h=ss.ibullets_sub_image_hover;if(!z)qm_ibullets_preload(q.simg_h);q.simgwh=eval("new Array("+ss.ibullets_sub_image_width+","+ss.ibullets_sub_image_height+")");r=q.simgwh;if(!r[0])r[0]=6;if(!r[1])r[1]=9;sx=ss.ibullets_sub_position_x;sy=ss.ibullets_sub_position_y;if(!sx)sx=0;if(!sy)sy=0;q.spos=eval("new Array('"+sx+"','"+sy+"')");q.salign=eval("new Array('"+ss.ibullets_sub_align_x+"','"+ss.ibullets_sub_align_y+"')");r=q.salign;if(!r[0])r[0]="right";if(!r[1])r[1]="middle";
            }q.type=ss.ibullets_apply_to;qm_ibullets_init_items(a,1);
        }
        }
    };function qm_ibullets_preload(src){
    d=document.createElement("DIV");d.style.display="none";d.innerHTML="<img src="+src+" width=1 height=1>";document.body.appendChild(d);
};function qm_ibullets_init_items(a,main){
    var q=qmad.ibullets;var aa,pf;aa=a.childNodes;for(var j=0;j<aa.length;j++){
        if(aa[j].tagName=="A"){
            if(window.attachEvent)aa[j].attachEvent("onmouseover",qm_ibullets_hover);else  if(window.addEventListener)aa[j].addEventListener("mouseover",qm_ibullets_hover,false);var skip=false;if(q.type!="all"){
                if(q.type=="parent"&&!aa[j].cdiv)skip=true;if(q.type=="non-parent"&&aa[j].cdiv)skip=true;
            }if(!skip){
                if(main)pf="m";else pf="s";if(q[pf+"img"]){
                    var ii=document.createElement("IMG");ii.setAttribute("src",q[pf+"img"]);ii.setAttribute("width",q[pf+"imgwh"][0]);ii.setAttribute("height",q[pf+"imgwh"][1]);ii.style.borderWidth="0px";ii.style.position="absolute";var ss=document.createElement("SPAN");var s1=ss.style;s1.display="block";s1.position="relative";s1.fontSize="1px";s1.lineHeight="0px";s1.zIndex=1;ss.ibhalign=q[pf+"align"][0];ss.ibvalign=q[pf+"align"][1];ss.ibiw=q[pf+"imgwh"][0];ss.ibih=q[pf+"imgwh"][1];ss.ibposx=q[pf+"pos"][0];ss.ibposy=q[pf+"pos"][1];qm_ibullets_position(aa[j],ss);ss.appendChild(ii);aa[j].qmibullet=aa[j].insertBefore(ss,aa[j].firstChild);aa[j]["qmibullet"+pf+"a"]=q[pf+"img_a"];aa[j]["qmibullet"+pf+"h"]=q[pf+"img_h"];aa[j].qmibulletorig=q[pf+"img"];ss.setAttribute("qmvbefore",1);ss.setAttribute("isibullet",1);if(aa[j].className.indexOf("qmactive")+1)qm_ibullets_active(aa[j]);
                }
                }if(aa[j].cdiv)new qm_ibullets_init_items(aa[j].cdiv);
        }
        }
    };function qm_ibullets_position(a,b){
    if(b.ibhalign=="right")b.style.left=(a.offsetWidth+parseInt(b.ibposx)-b.ibiw)+"px";else  if(b.ibhalign=="center")b.style.left=(parseInt(a.offsetWidth/2)-parseInt(b.ibiw/2)+parseInt(b.ibposx))+"px";else b.style.left=b.ibposx+"px";if(b.ibvalign=="bottom")b.style.top=(a.offsetHeight+parseInt(b.ibposy)-b.ibih)+"px";else  if(b.ibvalign=="middle")b.style.top=parseInt((a.offsetHeight/2)-parseInt(b.ibih/2)+parseInt(b.ibposy))+"px";else b.style.top=b.ibposy+"px";
};function qm_ibullets_hover(e,targ){
    e=e||window.event;if(!targ){
        var targ=e.srcElement||e.target;while(targ.tagName!="A")targ=targ[qp];
    }var ch=qmad.ibullets.lasth;if(ch&&ch!=targ){
        qm_ibullets_hover_off(new Object(),ch);
    }if(targ.className.indexOf("qmactive")+1)return;var wo=targ.qmibullet;var ma=targ.qmibulletmh;var sa=targ.qmibulletsh;if(wo&&(ma||sa)){
        var ti=ma;if(sa&&sa!=undefined)ti=sa;if(ma&&ma!=undefined)ti=ma;wo.firstChild.src=ti;qmad.ibullets.lasth=targ;
    }if(e)qm_kille(e);
};
function qm_ibullets_hover_off(e,o){
    //alert('qm_ibullets_hover_off()');
    if(!o)o=qmad.ibullets.lasth;if(o&&o.className.indexOf("qmactive")==-1){
        var os=o.getElementsByTagName("SPAN");for(var i=0;i<os.length;i++){
            if(os[i].getAttribute("isibullet"))os[i].firstChild.src=o.qmibulletorig;
        }
        }
    };
function qm_ibullets_active(a,hide){
    //alert('qm_ibullets_active()');
    var wo=a.qmibullet;var ma=a.qmibulletma;var sa=a.qmibulletsa;if(!hide&&a.className.indexOf("qmactive")==-1)return;if(hide&&a.idiv){
        var o=a.idiv;var os=o.getElementsByTagName("SPAN");for(var i=0;i<os.length;i++){
            if(os[i].getAttribute("isibullet"))os[i].firstChild.src=o.qmibulletorig;
        }
        }else {
        if(!a.cdiv.offsetWidth)a.cdiv.style.visibility="inherit";qm_ibullets_wait_relative(a);if(a.cdiv){
            var aa=a.cdiv.childNodes;for(var i=0;i<aa.length;i++){
                if(aa[i].tagName=="A"&&aa[i].qmibullet)qm_ibullets_position(aa[i],aa[i].qmibullet);
            }
            }if(wo&&(ma||sa)){
            var ti=ma;if(sa&&sa!=undefined)ti=sa;if(ma&&ma!=undefined)ti=ma;wo.firstChild.src=ti;
        }
        }
    };
function qm_ibullets_wait_relative(a){
    //alert('qm_ibullets_wait_relative()');
    if(!a)a=qmad.ibullets.cura;if(a.cdiv){
        if(a.cdiv.qmtree&&a.cdiv.style.position!="relative"){
            qmad.ibullets.cura=a;setTimeout("qm_ibcss_wait_relative()",10);return;
        }var aa=a.cdiv.childNodes;for(var i=0;i<aa.length;i++){
            if(aa[i].tagName=="A"&&aa[i].qmibullet)qm_ibullets_position(aa[i],aa[i].qmibullet);
        }
        }
    }

//Add-On Code: Show Subs Onload
if(!qmad.sopen){
    qmad.sopen=new Object();qmad.sopen.log=new Array();if(window.attachEvent)window.attachEvent("onload",qm_sopen_init);else  if(window.addEventListener)window.addEventListener("load",qm_sopen_init,1);
};
function qm_sopen_init(e,go){
    if(window.qmv)return;if(!go){
        setTimeout("qm_sopen_init(null,1)",10);return;
    }var auto_close=false;var i;var ql=qmad.sopen.log;for(i=0;i<10;i++){
        var a;if(a=document.getElementById("qm"+i)){
            var ss=qmad[a.id];if(ss&&!ss.showload_active)continue;if(ss&&ss.showload_auto_close)auto_close=true;var dd=a.getElementsByTagName("DIV");for(var j=0;j<dd.length;j++){
                if(dd[j].idiv&&dd[j].idiv.className.indexOf("qm-startopen")+1){
                    ql.push(dd[j].idiv);var f=dd[j][qp];if(!qm_a(f)){
                        var b=false;for(var k=0;k<ql.length;k++){
                            if(ql[k]==f.idiv)ql[k]=null;
                        }ql.push(f.idiv);f=f[qp];
                    }
                    }
                }
            }
        }var se=0;var sc=0;if(qmad.tree){
        se=qmad.tree.etype;sc=qmad.tree.ctype;qmad.tree.etype=0;qmad.tree.ctype=0;
    }for(i=ql.length-1;i>=0;i--){
        if(ql[i]){
            qm_oo(new Object(),ql[i],1);if(!auto_close)qm_li=null;
        }
        }if(qmad.tree){
        qmad.tree.etype=se;qmad.tree.ctype=sc;
    }
    }

//Images for Information boxes
var info_icon = "images/qmv_r_icon_info.gif";
var info_head_icon = "images/info_icon.gif";

function menuObj(mName, mParent)
{
    this.name = mName;
    this.mParent = mParent;
    this.mChild = null;
}

//Create menu object and set its parent menu
var pAdmin_Settings = new menuObj('Admin_Settings', null);
var pContent_Settings = new menuObj('Content_Settings', null);
var pSms_Settings = new menuObj('Sms_Settings', null);
var pReport = new menuObj('Report', null);
var pPush_Message = new menuObj('Push_Message', null);

//Set child menu
//pWireless.mChild = pMobile_AP;

//Menu
var def_expd_menu = new Array(pPush_Message, pReport);
var expd_menu = new Array(
    pAdmin_Settings
    , pContent_Settings
    , pSms_Settings
    , pReport
    , pPush_Message
    );
var dt = null;
var odt = null;
var obox = null;

function dotimer(boxID)
{
    if ( dt==null || boxID!=obox ) {
        return;
    }
	
    if ( dt.getTime()-odt.getTime()>=1000) { // 1 second
        show2(boxID);
        dt=null;
    } else {
        dt.setSeconds(dt.getSeconds()+1);
        xx=setTimeout('dotimer(\'' + boxID + '\')', 1000);
    }
}

var menuHintArry = new Array( 
    'OVERVIEW',      '',
    'REPORT',        '',
    'REPORT_SUBSCRIPTION',        '',
    'REPORT_SMSDOWNLOAD',        '',
    'LOGOUT', '',
    'PUSH_MESSAGE', '',
    'PUSH_MESSAGE_SMS', '',
    'PUSH_MESSAGE_MMS', ''
<%
    if (policy.getId() <= USER_TYPE.SENIOR.getId()) {
%>
    ,'ADMIN_SETTINGS',        ''
<%
    if (policy.getId() <= USER_TYPE.ADMIN.getId()) {
%>
    ,'SERVICES',      ''
<%
    }
%>
    ,'MEMBER',        ''
<%
    }
%>
<%
    if (policy.getId() <= USER_TYPE.ADMIN.getId()) {
%>
    ,
    'CONTENT_SETTINGS',      '',
    'SMS_SETTINGS',  '',
    'TEXT',  '',
    'RINGTONE',      '',
    'LOGO',  '',
    'WAP',   '',
    'MMS',   ''
<%
    }
%>
<%
    if (policy.getId() <= USER_TYPE.SENIOR.getId()) {
%>
    ,
    'BLOCKED_LIST',        ''
<%
    }
%>
    );

// Hint box CSS
document.write("<style type='text/css'> " +
    "div.data{" +
    "width:160px;" +
    "background-color: #CCCCFF; " +
    "border-top:1px solid #DDE8FF;" +
    "border-left:1px solid #DDE8FF;" +
    "border-right:1px solid #555;" +
    "border-bottom:1px solid #555;" +
    "margin-bottom:1em ;" +
    "position: absolute;" +
    "display:none;" +
    "z-Index: 1005;" +
    "overflow: hidden;" +
    "padding:.2em .2em .2em .5em;" +
    "font-family: Arial;" +
    "font-weight: bold;" +
    "font-size:xx-small;" +
    "font-color: #8B96A1;" +
    "filter:alpha(opacity=90);-moz-opacity:.90;opacity:.90; " +
    "}" +
    ".clearfix {" +
    "display:block;" +
    "position:relative;" +
    "}" +
    "</style>" );

function createHintBox()
{
    for (var i=0;i<menuHintArry.length;i+=2) {
        if (menuHintArry[i+1]=='') continue;
        document.write('<div class="data" id="' + menuHintArry[i] + '_hint">');
        document.write('<IMG src="' + info_head_icon + '" width=12 height=12 align=left> ' + menuHintArry[i+1] + '</div>');
    }
}

function hideAllHintBox()
{
    for (var i=0;i<menuHintArry.length;i+=2) {
        if (menuHintArry[i+1]=='') continue;
        hide2(menuHintArry[i] + '_hint');

    }
}

var xm = 0;
var x_offset = 5;
var ym = 0;
var y_offset = 15;
var nav = (document.layers);
if(nav) document.captureEvents(Event.MOUSEMOVE);
document.onmousemove = get_mouse; 

function get_mouse(e)
{

    if (!e)
        var e = window.event||window.Event;
    xm = (nav) ? e.pageX : e.clientX+document.body.scrollLeft;
    ym = (nav) ? e.pageY : e.clientY+document.body.scrollTop;
    xm += x_offset;
    ym += y_offset;
}

function showHintBox(id)
{
    /*
	obox=id;
	dt=new Date();
	odt=new Date(dt);
	dotimer(id);
	*/
    show2(id);
    getElement(id).style.top = ym;
    getElement(id).style.left = xm;
}

function hideHintBox(id)
{
    //dt=null;
    hide2(id);
}

function pairMenu2Hint()
{
    if (browserType == "gecko" ){
    //alert("we recommend you to use IE");
    }

    for (var i=0;i<menuHintArry.length;i+=2) {
        var obj = getElement(menuHintArry[i]);
        if (!obj) continue;
        if (menuHintArry[i+1]=='') continue;
        switch (i/2) {
            case 0:
                obj.onmousemove = function(){
                    showHintBox('OVERVIEW_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('OVERVIEW_hint')
                    };
                break;
            case 1:
                obj.onmousemove = function(){
                    showHintBox('REPORT_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('REPORT_hint')
                    };
                break;
            case 2:
                obj.onmousemove = function(){
                    showHintBox('REPORT_SUBSCRIPTION_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('REPORT_SUBSCRIPTION_hint')
                    };
                break;
            case 3:
                obj.onmousemove = function(){
                    showHintBox('REPORT_SMSDOWNLOAD_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('REPORT_SMSDOWNLOAD_hint')
                    };
                break;
            case 4:
                obj.onmousemove = function(){
                    showHintBox('LOGOUT_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('LOGOUT_hint')
                    };
                break;
            case 5:
                obj.onmousemove = function(){
                    showHintBox('PUSH_MESSAGE_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('PUSH_MESSAGE_hint')
                    };
                break;
            case 6:
                obj.onmousemove = function(){
                    showHintBox('PUSH_MESSAGE_SMS_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('PUSH_MESSAGE_SMS_hint')
                    };
                break;
            case 7:
                obj.onmousemove = function(){
                    showHintBox('PUSH_MESSAGE_MMS_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('PUSH_MESSAGE_MMS_hint')
                    };
                break;
<%
    if (policy.getId() <= USER_TYPE.SENIOR.getId()) {
%>
            case 8:
                obj.onmousemove = function(){
                    showHintBox('ADMIN_SETTINGS_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('ADMIN_SETTINGS_hint')
                    };
                break;
<%
    if (policy.getId() <= USER_TYPE.ADMIN.getId()) {
%>
            case 9:
                obj.onmousemove = function(){
                    showHintBox('SERVICES_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('SERVICES_hint')
                    };
                break;
<%
    }
%>
            case <%=((policy.getId() <= USER_TYPE.ADMIN.getId())?"10":"9")%>:
                obj.onmousemove = function(){
                    showHintBox('MEMBER_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('MEMBER_hint')
                    };
                break;
<%
    }
%>
<%
    if (policy.getId() <= USER_TYPE.ADMIN.getId()) {
%>
            case 11:
                obj.onmousemove = function(){
                    showHintBox('CONTENT_SETTINGS_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('CONTENT_SETTINGS_hint')
                    };
                break;
            case 12:
                obj.onmousemove = function(){
                    showHintBox('SMS_SETTINGS_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('SMS_SETTINGS_hint')
                    };
                break;
            case 13:
                obj.onmousemove = function(){
                    showHintBox('TEXT_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('TEXT_hint')
                    };
                break;
            case 14:
                obj.onmousemove = function(){
                    showHintBox('RINGTONE_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('RINGTONE_hint')
                    };
                break;
            case 15:
                obj.onmousemove = function(){
                    showHintBox('LOGO_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('LOGO_hint')
                    };
                break;
            case 16:
                obj.onmousemove = function(){
                    showHintBox('WAP_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('WAP_hint')
                    };
                break;
            case 17:
                obj.onmousemove = function(){
                    showHintBox('MMS_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('MMS_hint')
                    };
                break;
<%
    }
%>
<%
    if (policy.getId() <= USER_TYPE.SENIOR.getId()) {
%>
            case <%=((policy.getId() <= USER_TYPE.ADMIN.getId())?"18":"10")%>:
                obj.onmousemove = function(){
                    showHintBox('BLOCKED_LIST_hint')
                    };
                obj.onmouseout = function(){
                    hideHintBox('BLOCKED_LIST_hint')
                    };
                break;
<%
    }
%>
        }
    }
}

function getMenuStat(menuObj)
{
    var menu = menuObj.name;
    if ((expd=getCookie(menu)) == "")
    {
        //alert('getCookie: ' + menu + '=' + expd);
        for (i=0;i<def_expd_menu.length;i++)
        {
            if (menu==def_expd_menu[i].name) {
                //alert('The menu [' + menu + '] found in default open.');
                pmenu = menuObj;
                while (pmenu=pmenu.mParent)
                {
                    expdParent = getCookie(menuObj.mParent.name);
                    if (expdParent=='' || expdParent=='0')
                        return '';
                }
                setMenuStat(menuObj, '1');
                return 'class="qm-startopen"';
            }
        }
        setMenuStat(menuObj, '0');
    }
    //alert('getCookie: ' + menu + '=' + expd);
	
    if (expd=='1') {
        // Is expanded menu of parent?
        pmenu = menuObj;
        while (pmenu=pmenu.mParent)
        {
            expdParent = getCookie(menuObj.mParent.name);
            if (expdParent=='' || expdParent=='0')
                return '';
        }
        return 'class="qm-startopen"';
    }
    return '';
}

function setMenuStat(menuObj, expd)
{
    var menu = menuObj.name;
    if (expd==null)
    {
        expd = getCookie(menu);
        if (expd == '' || expd == '0')
            expd = '1';
        else
            expd = '0';
    }
    //alert('setCookie: ' + menu + '=' + expd);
    setCookie(menu, expd, 365);
	
    if (expd == '0') // set collapse to all sub-menu
    {
        var cmenu = menuObj;
        while(cmenu=cmenu.mChild)
        {
            //alert('setCookie: ' + cmenu.name + '=' + expd);
            setCookie(cmenu.name, expd, 365);
        }
    }
}

function qm_expand_all()
{
    for (i=0;i<expd_menu.length;i++)
    {
        var a = getElement(expd_menu[i].name);
        a.style.position = "";
        qm_tree_item_click(a);
        
        //Set expanded flag
        setMenuStat(expd_menu[i], '1');
    }
}

function qm_collapse_all()
{
    for (i=0;i<expd_menu.length;i++)
    {
        //alert(expd_menu[i].name);
        var a = getElement(expd_menu[i].name);
        a.style.position = "relative";
        var q = qmad.tree;
        qmwait = false;
        qmad.tree.timer = null;
        //q.timer=setTimeout("qm_tree_item_expand("+expand+","+collapse+",'"+levid+"')",10);
        var levid = "a"+qm_get_level(a);
        var expand = 'false';
        var collapse = 'true';
        //q.timer=setTimeout("qm_tree_item_expand("+expand+","+collapse+",'"+levid+"')",10);
        qm_tree_item_click(a);
    }
}

function changeText(id, txt) {
    obj = getElement(id);
    if (txt != 'Home')
        txt = '<a href="javascript:changeText(\'sitemap\', \'Home\');open_page(\'overview.jsp\', ' + framename + ');" style="text-decoration:none;">Home</a> &gt; ' + txt;
    obj.innerHTML = txt;
}

framename = "\'ctFrame\'";


// Print out the menu items
document.write(
    '<div id="qm0" class="qmmc" style="top:103px;padding-left:10px;">'
<%
    if (policy.getId() <= USER_TYPE.ADMIN.getId()) {
%>
    + '  <a id=OVERVIEW href="javascript:changeText(\'sitemap\', \'Home\');open_page(\'overview.jsp\', ' + framename + ');">Overview</a>'
<%
    }
%>
<%
    if (policy.getId() <= USER_TYPE.SENIOR.getId()) {
%>
    + '  <a id=ADMIN_SETTINGS ' + getMenuStat(pAdmin_Settings) + ' href="javascript:setMenuStat(pAdmin_Settings, null);void(0)">Admin Settings</a>'
    + '  <div id="Admin_Settings">'
<%
    if (policy.getId() <= USER_TYPE.ADMIN.getId()) {
%>
    + '    <a id=SERVICES href="javascript:changeText(\'sitemap\', \'Admin Settings > Services\');open_page(\'services.jsp\', '+ framename +');">Services</a>'
<%
    }
%>
    + '    <a id=MEMBER href="javascript:changeText(\'sitemap\', \'Admin Settings > Member\');open_page(\'member.jsp\', '+ framename +');">Member</a>'
    + '  </div>'
<%
    }
%>
    + '  <a id=PUSH_MESSAGE ' + getMenuStat(pPush_Message) + ' href="javascript:setMenuStat(pPush_Message, null);void(0)">Push Message</a>'
    + '  <div id="Push_Message">'
    + '    <a id=PUSH_MESSAGE_SMS href="javascript:changeText(\'sitemap\', \'Push Message > SMS\');open_page(\'push_message_sms.jsp\', '+ framename +');">SMS</a>'
    + '    <a id=PUSH_MESSAGE_MMS href="javascript:changeText(\'sitemap\', \'Push Message > MMS\');open_page(\'push_message_mms.jsp\', '+ framename +');">MMS</a>'
    + '  </div>'
<%
    if (policy.getId() <= USER_TYPE.ADMIN.getId()) {
%>
    + '  <a id=CONTENT_SETTINGS ' + getMenuStat(pContent_Settings) + ' href="javascript:setMenuStat(pContent_Settings, null);void(0)">Content Settings</a>'
    + '  <div id="Content_Settings">'
    + '    <a id=SMS_SETTINGS ' + getMenuStat(pSms_Settings) + ' href="javascript:setMenuStat(pSms_Settings, null);void(0)">SMS</a>'
    + '    <div id="Sms_Settings">'
    + '      <a id=TEXT href="javascript:changeText(\'sitemap\', \'Content Settings > SMS > Text Message\');open_page(\'sms_text.jsp\', '+ framename +');">Text Message</a>'
    + '      <a id=RINGTONE href="javascript:changeText(\'sitemap\', \'Content Settings > SMS > Ringtone\');open_page(\'sms_ringtone.jsp\', '+ framename +');">Ringtone</a>'
    + '      <a id=LOGO href="javascript:changeText(\'sitemap\', \'Content Settings > SMS > Logo\');open_page(\'sms_logo.jsp\', '+ framename +');">Logo</a>'
    + '      <a id=WAP href="javascript:changeText(\'sitemap\', \'Content Settings > WAP\');open_page(\'wap.jsp\', '+ framename +');">WAP</a>'
    + '    </div>'
    + '    <a id=MMS href="javascript:changeText(\'sitemap\', \'Content Settings > MMS\');open_page(\'mms.jsp\', '+ framename +');">MMS</a>'
    + '  </div>'
<%
    }
%>
<%
    if (policy.getId() <= USER_TYPE.SENIOR.getId()) {
%>
    + '  <a id=BLOCKED_LIST href="javascript:changeText(\'sitemap\', \'Blocked List\');open_page(\'block_list.jsp\', '+ framename +');"><img src="./images/no_phone_zone_mini.gif" border="0">Blocked List</a>'
<%
    }
%>
    + '  <a id=REPORT ' + getMenuStat(pReport) + ' href="javascript:setMenuStat(pReport, null);void(0)">Report</a>'
    + '  <div id="Report">'
    + '    <a id=REPORT_SUBSCRIPTION href="javascript:changeText(\'sitemap\', \'Report > Subscription\');open_page(\'report_subscription.jsp\', '+ framename +');">Subscription</a>'
    + '    <a id=REPORT_SMSDOWNLOAD href="javascript:changeText(\'sitemap\', \'Report > SMS Download\');open_page(\'report_smsdownload.jsp\', '+ framename +');">SMS Download</a>'
    + '  </div>'
    );

document.write('  <a id=LOGOUT href="javascript:window.location=\'logout\'">Logout</a>');
document.write('</div>');
document.write('<script type="text/javascript">qm_create(0,true,0,500,\'all\',false,false,false,false);</script>');

//Optional Code
createHintBox();
//hideAllHintBox();
pairMenu2Hint();
