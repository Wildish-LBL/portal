//Matt Kruse-javascripttoolbox.com-MIT/GPL
//version from: 31.02.2007
//maneo (maneo@man.poznan.pl) added lines 143 and 144 to prevent parsing errors in IE6/7
//version from: 09.10.2008
eval(function(p,a,c,k,e,d){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--){d[e(c)]=k[c]||e(c)}k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1};while(c--){if(k[c]){p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c])}}return p}('6 P=(g(){g q(s){3(8.n&&8.n(s)!=9){a 8.n(s)}w 3(8.l&&8.l[s]!=9){a 8.l[s]}w 3(8.e&&8.e.v&&8.e.v>0&&8.e[0].x){Q(6 i=0;i<8.e.v;i++){3(8.e[i].O==s){a 8.e[i]}}}}6 b={};b.$S=1.0;b.R=g(o,5,7){3(u(o)=="D"){o=q(o)}3(o==9||!o.t){a J}3(u(5)=="X"){6 b=5;5=b.5;7=b.7}o.t.5=5+"I";o.t.7=7+"I";a p};b.F=g(o){6 H=p;3(u(o)=="D"){o=q(o)}3(o==9){a 9}6 5=0;6 7=0;6 h=0;6 j=0;6 k=9;6 d=9;d=o.d;6 f=o;6 4=o;T(4.k!=9){4=4.k;3(4.d==9){}w{6 m=p;3(H&&N.U){3(4==f.k||4.A=="W"){m=J}}3(m){3(4.y&&4.y>0){7-=4.y}3(4.r&&4.r>0){5-=4.r}}}3(4==d){5+=o.B;3(4.G&&4.A!="K"){5+=4.G}7+=o.z;3(4.L&&4.A!="K"){7+=4.L}o=4;3(o.d==9){3(o.B){5+=o.B}3(o.z){7+=o.z}}d=o.d}}3(f.C){h=f.C}3(f.E){j=f.E}a{\'5\':5,\'7\':7,\'h\':h,\'j\':j}};b.V=g(o){6 c=M.F(o);3(c==9){a 9}c.5=c.5+(c.h/2);c.7=c.7+(c.j/2);a c};a b})();',60,60,'|||if|el|left|var|top|document|null|return|pos||offsetParent|anchors|originalObject|function|width||height|parentNode|all|considerScroll|getElementById||true|resolveObject|scrollLeft||style|typeof|length|else||scrollTop|offsetTop|nodeName|offsetLeft|offsetWidth|string|offsetHeight|get|clientLeft|fixBrowserQuirks|px|false|TABLE|clientTop|this|window|name|Position|for|set|VERSION|while|opera|getCenter|TR|object'.split('|'),0,{}))


if(typeof a2a_init=='undefined'){
	var a2a_init=1, a2a_n=0;

	window['a2a'+a2a_n+'_srch_focused']	=false;
	window['a2a'+a2a_n+'_show_all']		=false;
	
	function a2a_get_this_button_index(t){ 		var a=document.getElementsByName('a2a_dd');
		for(i=0;i<a.length;i++){
			if(t==a[i]) return i;
		}
	}
	function a2a_disp(w,v){if(w)document.getElementById(w).style.display=v;}
	function a2a_onMouseOut_delay(){
		if(!window['a2a'+a2a_n+'_srch_focused'])
			window['a2a'+a2a_n+'_delay']=setTimeout("a2a_toggle_dropdown('none')",501);
	}
	function a2a_onMouseOver_stay() {
		if(typeof window['a2a'+a2a_n+'_delay']!="undefined")clearTimeout(window['a2a'+a2a_n+'_delay']);
	}
	function a2a_toggle_dropdown(t){
		document.getElementById('a2a'+a2a_n+'_dropdown').style.display=t;
	}
	function a2a_getDocDims(d){
		w=0;
		h=0;
		if(typeof(window.innerWidth)=='number'){
			w=window.innerWidth; //Non-IE
			h=window.innerHeight;
		}
		else if(document.documentElement&&(document.documentElement.clientWidth||document.documentElement.clientHeight)){
			w=document.documentElement.clientWidth; //IE 6+ in standards compliant mode
			h=document.documentElement.clientHeight;
		}
		else if(document.body&&(document.body.clientWidth||document.body.clientHeight)){
			w=document.body.clientWidth; //IE 4
			h=document.body.clientHeight;
		}
		if(d=='w')return w;else return h;
	}
	function a2a_showAll(t){
		if(!window['a2a'+a2a_n+'_show_all']){
			srvcs=document.getElementById('a2a'+a2a_n+'_dropdown').getElementsByTagName('a');
			for(i=0;i<srvcs.length;i++)
				a2a_disp(srvcs[i].id,'');
			t.innerHTML="Add to Any";
			
			window['a2a'+a2a_n+'_show_all']=true;
			return false;
		}
	}
	function a2a_show_srch(a){
		if(a.value=="Find service..."){
			a.value="";
			a.style.color="#000";
			a.focus();
		}
	}
	function a2a_do_srch(curr_search){
		srvcs=document.getElementById('a2a'+a2a_n+'_dropdown').getElementsByTagName('a');
		colm1_lnks=document.getElementById('a2a'+a2a_n+'_colm1').getElementsByTagName('a');
		colm2_lnks=document.getElementById('a2a'+a2a_n+'_colm2').getElementsByTagName('a');
		if(document.getElementById('a2a'+a2a_n+'_srch').value!=''){
			for(i=0;i<srvcs.length;i++)
			{
				s=srvcs[i].id;
				
				s_fix=(s.substr(4+(String(a2a_n).length)).toLowerCase()).replace('_',' '); 				if(s_fix.indexOf(curr_search.toLowerCase())!=-1)
					a2a_disp(s,'');
				else a2a_disp(s,'none');
			}
		}
		else
		{
			
			for(i=0;i<colm1_lnks.length;i++) 
				if(i<6)					a2a_disp(colm1_lnks[i].id,'');
				else a2a_disp(colm1_lnks[i].id,'none');
			for(i=0;i<colm2_lnks.length;i++) 
				if(i<6)					a2a_disp(colm2_lnks[i].id,'');
				else a2a_disp(colm2_lnks[i].id,'none');
					}
	}
	function a2a_show_dropdown(o){
		a2a_onMouseOver_stay();
		a2a_toggle_dropdown('none');
		a2a_n = a2a_get_this_button_index(o);
		
		a2a_onMouseOver_stay();
		a2a_toggle_dropdown('');
		docWidth=a2a_getDocDims('w');
		docHeight=a2a_getDocDims('h');
		ddPos=Position.get(document.getElementById('a2a'+a2a_n+'_dropdown'));
		pos=Position.get(o);
		x=91;
		if(pos.left + x + ddPos.width > docWidth) { pos.left = pos.left+x - ddPos.width; }		if(pos.top + 16 + ddPos.height > docHeight && ddPos.height < pos.top) { pos.top = pos.top-16 - ddPos.height; }		Position.set('a2a'+a2a_n+'_dropdown',pos.left,pos.top+16);
	}

}


	function a2a_create_dropdown(){
		// This last index of A2A buttons, so far
		a2a_t = document.getElementsByName('a2a_dd').length - 1;
		
		a2a_c='#a2a'+a2a_t+'_dropdown{margin:0;padding:2px;border:1px solid #CCC;background-color:#FFF;overflow:hidden;z-index:9999;position:absolute;}';
		a2a_c+='#a2a'+a2a_t+'_dropdown table{border-collapse:collapse;width:auto;margin:0;padding:0;border:0;font:12px Arial, Helvetica, sans-serif}';
		a2a_c+='#a2a'+a2a_t+'_dropdown table td{margin:0;padding:0;}';
		a2a_c+='#a2a'+a2a_t+'_dropdown a{color:#00F;text-decoration:none;font:12px Arial,Helvetica,sans-serif;height:auto;width:auto}';
		a2a_c+='#a2a'+a2a_t+'_dropdown a:visited{color:#00F}';
		a2a_c+='#a2a'+a2a_t+'_dropdown a:hover{color:#000;border:1px solid #CCC}';
		a2a_c+='#a2a'+a2a_t+'_srch{color:#CCC;font:12px;padding:2px 0 2px 2px;border:1px solid;background-color:#FFF;width:100%}';
		a2a_c+='.a2a_srch_container{padding:9px;margin-bottom:2px;border:0;background-color:#EEE}';
		a2a_c+='.a2a_i{display:block;padding:6px;border:1px solid #FFF}';
		a2a_c+='.a2a_i:hover{background-color:#EEE}';
		a2a_c+='.a2a_left{float:left}';
		a2a_c+='.a2a_clear{clear:both}';
		a2a_c+='.a2a_ftr a{display:block;margin-top:2px;background-color:#EEE;border:1px solid #EEE;padding:3px;text-align:center}';
		
		var a2a_s = document.createElement('style');
		a2a_s.setAttribute("type","text/css");
		if(a2a_s.styleSheet) 
		    a2a_s.styleSheet.cssText=a2a_c;		
		else{
			a2a_s_tn=document.createTextNode(a2a_c);			a2a_s.appendChild(a2a_s_tn);
		}
		a2a_doc_head = document.getElementsByTagName('head')[0];
		a2a_doc_head.appendChild(a2a_s);
		
		a2a_d='<div id="a2a'+a2a_t+'_dropdown" onMouseOver="a2a_onMouseOver_stay()" onmouseout="a2a_onMouseOut_delay()" style="display:none">';
		
		
		
		if(!Array.every||!window.getComputedStyle)a2a_d+='<table cellpadding=0><td>';		
		    a2a_d+='<div class="a2a_srch_container"><input id="a2a'+a2a_t+'_srch" type="text" onclick="a2a_show_srch(this)" onKeyUp="a2a_do_srch(this.value)" autocomplete="off" onfocus="window[\'a2a'+a2a_t+'_srch_focused\']=true" onblur="window[\'a2a'+a2a_t+'_srch_focused\']=false;a2a_onMouseOut_delay()" value="Find service..."></div>';
			a2a_d+='<div><div id="a2a'+a2a_t+'_colm1" class="a2a_left">';
			
			encodedUrl = encodeURIComponent(a2a_linkurl); 
			encodedTitle = encodeURIComponent(a2a_linkname);
			
			a2a_d+='<a id="a2a'+a2a_t+'_del.icio.us" rel="srvc" class="a2a_i" href="http://del.icio.us/post?&url='+encodedUrl+'&title='+encodedTitle+'">del.icio.us</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Google_Bookmarks" rel="srvc" class="a2a_i" href="http://www.google.com/bookmarks/mark?op=edit&bkmk='+encodedUrl+'">Google Bookmarks</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_My_Yahoo_Bookmarks" rel="srvc" class="a2a_i" href="http://e.my.yahoo.com/config/edit_bookmark?.src=bookmarks&.folder=1&.name='+encodedTitle+'&.url='+encodedUrl+'&.save=+Save+">My Yahoo Bookmarks</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Windows_Live_Favorites" rel="srvc" class="a2a_i" href="http://favorites.live.com/quickadd.aspx?url='+encodedUrl+'&title='+encodedTitle+'">Windows Live Favorites</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Ask.com_MyStuff" rel="srvc" class="a2a_i" href="http://mystuff.ask.com/mysearch/BookmarkIt?v=1.2&t=webpages&url='+encodedUrl+'&title='+encodedTitle+'">Ask.com MyStuff</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Ma.gnolia" rel="srvc" class="a2a_i" href="http://ma.gnolia.com/bookmarklet/add?url='+encodedUrl+'&title='+encodedTitle+'">Ma.gnolia</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Mister-Wong" rel="srvc" style="display:none" class="a2a_i" href="http://www.mister-wong.com/index.php?action=addurl&bm_url='+encodedUrl+'&bm_description='+encodedTitle+'">Mister-Wong</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_CiteULike" rel="srvc" style="display:none" class="a2a_i" href="http://www.citeulike.org/posturl?url='+encodedUrl+'&title='+encodedTitle+'">CiteULike</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_FunP" rel="srvc" style="display:none" class="a2a_i" href="http://funp.com/portal/addrss.php?url='+encodedUrl+'&via=addtoany">FunP</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Netvouz" rel="srvc" style="display:none" class="a2a_i" href="http://www.netvouz.com/action/submitBookmark?url='+encodedUrl+'&title='+encodedTitle+'&popup=no">Netvouz</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_BibSonomy" rel="srvc" style="display:none" class="a2a_i" href="http://www.bibsonomy.org/BibtexHandler?requTask=upload&url='+encodedUrl+'&description='+encodedTitle+'">BibSonomy</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Taggly" rel="srvc" style="display:none" class="a2a_i" href="http://www.taggly.com/bookmarks/?action=add&address='+encodedUrl+'&title='+encodedTitle+'">Taggly</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Tailrank" rel="srvc" style="display:none" class="a2a_i" href="http://tailrank.com/share/?link_href='+encodedUrl+'&title='+encodedTitle+'">Tailrank</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Blinklist" rel="srvc" style="display:none" class="a2a_i" href="http://www.blinklist.com/index.php?Action=Blink/addblink.php&Url='+encodedUrl+'&Title='+encodedTitle+'">Blinklist</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_YiGG" rel="srvc" style="display:none" class="a2a_i" href="http://yigg.de/neu?exturl='+encodedUrl+'&exttitle='+encodedTitle+'">YiGG</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_YouMob" rel="srvc" style="display:none" class="a2a_i" href="http://youmob.com/mob.aspx?mob='+encodedUrl+'">YouMob</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_RawSugar" rel="srvc" style="display:none" class="a2a_i" href="http://www.rawsugar.com/pages/tagger.faces?turl='+encodedUrl+'&tttl='+encodedTitle+'">RawSugar</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Smarking" rel="srvc" style="display:none" class="a2a_i" href="http://smarking.com/editbookmark/?url='+encodedUrl+'&description='+encodedTitle+'">Smarking</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Shadows" rel="srvc" style="display:none" class="a2a_i" href="http://www.shadows.com/features/tcr.htm?url='+encodedUrl+'&title='+encodedTitle+'">Shadows</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_OnlyWire" rel="srvc" style="display:none" class="a2a_i" href="http://www.onlywire.com/b/?u='+encodedUrl+'&t='+encodedTitle+'">OnlyWire</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Squidoo" rel="srvc" style="display:none" class="a2a_i" href="http://www.squidoo.com/lensmaster/bookmark?'+encodedUrl+'">Squidoo</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Spurl" rel="srvc" style="display:none" class="a2a_i" href="http://www.spurl.net/spurl.php?title='+encodedTitle+'&url='+encodedUrl+'">Spurl</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Oneview" rel="srvc" style="display:none" class="a2a_i" href="http://www.oneview.de:80/quickadd/neu/addBookmark.jsf?URL='+encodedUrl+'&title='+encodedTitle+'">Oneview</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Simpy" rel="srvc" style="display:none" class="a2a_i" href="http://www.simpy.com/simpy/LinkAdd.do?href='+encodedUrl+'&title='+encodedTitle+'&v=6">Simpy</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Small_Things" rel="srvc" style="display:none" class="a2a_i" href="http://smallthings.kilobox.mobi/login.php/?action=add&address='+encodedUrl+'">Small Things</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Wists" rel="srvc" style="display:none" class="a2a_i" href="http://wists.com/r.php?r='+encodedUrl+'&title='+encodedTitle+'">Wists</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Gravee" rel="srvc" style="display:none" class="a2a_i" href="http://www.gravee.com/account/bookmarkpop?u='+encodedUrl+'&t='+encodedTitle+'">Gravee</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Backflip" rel="srvc" style="display:none" class="a2a_i" href="http://www.backflip.com/add_page_pop.ihtml?url='+encodedUrl+'&title='+encodedTitle+'">Backflip</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_SiteJot" rel="srvc" style="display:none" class="a2a_i" href="http://www.sitejot.com/addform.php?iSiteAdd='+encodedUrl+'&iSiteDes='+encodedTitle+'">SiteJot</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Mixx" rel="srvc" style="display:none" class="a2a_i" href="http://www.mixx.com/submit?page_url='+encodedUrl+'">Mixx</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Bitty_Browser" rel="srvc" style="display:none" class="a2a_i" href="http://www.bitty.com/manual/?contenttype=&contentvalue='+encodedUrl+'">Bitty Browser</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Diigo" rel="srvc" style="display:none" class="a2a_i" href="http://www.diigo.com/post?url='+encodedUrl+'&title='+encodedTitle+'">Diigo</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_diglog" rel="srvc" style="display:none" class="a2a_i" href="http://www.diglog.com/submit?act=like&returnurl=true&title='+encodedTitle+'&url='+encodedUrl+'">diglog</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_NowPublic" rel="srvc" style="display:none" class="a2a_i" href="http://view.nowpublic.com/?src='+encodedUrl+'&t='+encodedTitle+'">NowPublic</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_Care2_News" rel="srvc" style="display:none" class="a2a_i" href="http://www.care2.com/news/news_post.html?url='+encodedUrl+'&title='+encodedTitle+'&v=1.3">Care2 News</a>';
			a2a_d+='<a id="a2a'+a2a_t+'_LiveJournal" rel="srvc" style="display:none" class="a2a_i" href="http://www.livejournal.com/update.bml?subject='+encodedTitle+'">LiveJournal</a>';
			a2a_d+='</div>';
			a2a_d+='<div id="a2a'+a2a_t+'_colm2" class="a2a_left">';
			a2a_d+='<a id="a2a'+a2a_t+'_Digg" rel="srvc" class="a2a_i" href="http://digg.com/submit?phase=2&url='+encodedUrl+'&title='+encodedTitle+'">Digg</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Reddit" rel="srvc" class="a2a_i" href="http://reddit.com/submit?url='+encodedUrl+'&title='+encodedTitle+'">Reddit</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_StumbleUpon" rel="srvc" class="a2a_i" href="http://www.stumbleupon.com/submit?url='+encodedUrl+'&title='+encodedTitle+'">StumbleUpon</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Facebook" rel="srvc" class="a2a_i" href="http://www.facebook.com/share.php?src=bm&u='+encodedUrl+'&t='+encodedTitle+'&v=3">Facebook</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_NewsVine" rel="srvc" class="a2a_i" href="http://www.newsvine.com/_tools/seed&save?u='+encodedUrl+'&h='+encodedTitle+'">NewsVine</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Furl" rel="srvc" class="a2a_i" href="http://www.furl.net/storeIt.jsp?u='+encodedUrl+'&t='+encodedTitle+'">Furl</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Technorati_Favorites" rel="srvc" style="display:none" class="a2a_i" href="http://technorati.com/faves?add='+encodedUrl+'">Technorati Favorites</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Hemidemi" rel="srvc" style="display:none" class="a2a_i" href="http://www.hemidemi.com/user_bookmark/new?title='+encodedTitle+'&url='+encodedUrl+'&via=http%3A%2F%2Fwww.addtoany.com%2F">Hemidemi</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Xerpi" rel="srvc" style="display:none" class="a2a_i" href="http://www.xerpi.com/block/add_link_from_extension?url='+encodedUrl+'&title='+encodedTitle+'">Xerpi</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Wink" rel="srvc" style="display:none" class="a2a_i" href="http://www.wink.com/_/tag?url='+encodedUrl+'&doctitle='+encodedTitle+'">Wink</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Jots" rel="srvc" style="display:none" class="a2a_i" href="http://www.jots.com/?cmd=do_post&url='+encodedUrl+'&title='+encodedTitle+'">Jots</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_BlogMarks" rel="srvc" style="display:none" class="a2a_i" href="http://blogmarks.net/my/new.php?mini=1&simple=1&title='+encodedTitle+'&url='+encodedUrl+'">BlogMarks</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Protopage_Bookmarks" rel="srvc" style="display:none" class="a2a_i" href="http://www.protopage.com/add-button-site?url='+encodedUrl+'&label=&type=page">Protopage Bookmarks</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Gabbr" rel="srvc" style="display:none" class="a2a_i" href="http://www.gabbr.com/submitext.php?gurl='+encodedUrl+'">Gabbr</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Segnalo" rel="srvc" style="display:none" class="a2a_i" href="http://segnalo.alice.it/post.html.php?url='+encodedUrl+'&title='+encodedTitle+'">Segnalo</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Slashdot" rel="srvc" style="display:none" class="a2a_i" href="http://slashdot.org/bookmark.pl?url='+encodedUrl+'&title='+encodedTitle+'">Slashdot</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Scuttle" rel="srvc" style="display:none" class="a2a_i" href="http://scuttle.org/login/?action=add&address='+encodedUrl+'&title='+encodedTitle+'">Scuttle</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Jookster" rel="srvc" style="display:none" class="a2a_i" href="http://www.jookster.com/JookThis.aspx?url='+encodedUrl+'">Jookster</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Feedmarker_Bookmarks" rel="srvc" style="display:none" class="a2a_i" href="http://www.feedmarker.com/admin.php?do=bookmarklet_mark&url='+encodedUrl+'&title='+encodedUrl+'">Feedmarker Bookmarks</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_unalog" rel="srvc" style="display:none" class="a2a_i" href="http://unalog.com/my/stack/link?url='+encodedUrl+'&title='+encodedTitle+'">unalog</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Thoof" rel="srvc" style="display:none" class="a2a_i" href="http://thoof.com/submit?link='+encodedUrl+'&title='+encodedTitle+'">Thoof</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Yample" rel="srvc" style="display:none" class="a2a_i" href="http://yample.com/submit.php?url='+encodedUrl+'">Yample</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Linkatopia" rel="srvc" style="display:none" class="a2a_i" href="http://linkatopia.com/add?uri='+encodedUrl+';title='+encodedTitle+'">Linkatopia</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_BuddyMarks" rel="srvc" style="display:none" class="a2a_i" href="http://buddymarks.com/add_bookmark.php?bookmark_url='+encodedUrl+'&bookmark_title='+encodedTitle+'">BuddyMarks</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Maple" rel="srvc" style="display:none" class="a2a_i" href="http://www.maple.nu/bookmarks/bookmarklet?bookmark[url]='+encodedUrl+'&bookmark[description]='+encodedTitle+'">Maple</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Kinja" rel="srvc" style="display:none" class="a2a_i" href="http://kinja.com/id.knj?url='+encodedUrl+'">Kinja</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Connotea" rel="srvc" style="display:none" class="a2a_i" href="http://www.connotea.org/add?uri='+encodedUrl+'&title='+encodedTitle+'">Connotea</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_MyLinkVault" rel="srvc" style="display:none" class="a2a_i" href="http://www.mylinkvault.com/link-page.php?u='+encodedUrl+'&n='+encodedTitle+'">MyLinkVault</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_FeVote" rel="srvc" style="display:none" class="a2a_i" href="http://www.fevote.com/new/?category='+encodedTitle+'">FeVote</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Sphere" rel="srvc" style="display:none" class="a2a_i" href="http://www.sphere.com/search?q=sphereit:'+encodedUrl+'">Sphere</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_dzone" rel="srvc" style="display:none" class="a2a_i" href="http://www.dzone.com/links/add.html?url='+encodedUrl+'&title='+encodedTitle+'">dzone</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Design_Float" rel="srvc" style="display:none" class="a2a_i" href="http://www.designfloat.com/submit.php?url='+encodedUrl+'">Design Float</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Technotizie" rel="srvc" style="display:none" class="a2a_i" href="http://www.technotizie.it/posta_ok?action=f2&title='+encodedTitle+'&url='+encodedUrl+'">Technotizie</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Hugg" rel="srvc" style="display:none" class="a2a_i" href="http://www.hugg.com/submit?url='+encodedUrl+'">Hugg</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Netscape.com" rel="srvc" style="display:none" class="a2a_i" href="http://www.netscape.com/submit/?U='+encodedUrl+'&T='+encodedTitle+'">Netscape.com</a>';	
			a2a_d+='<a id="a2a'+a2a_t+'_Feedo_Style" rel="srvc" style="display:none" class="a2a_i" href="http://www.feedostyle.com/create_digest.aspx?feed='+encodedUrl+'">Feedo Style</a>';	
			a2a_d+='</div></div>';
		a2a_d+='<div class="a2a_clear"></div>';
		a2a_d+='<div class="a2a_ftr"><a href="http://www.addtoany.com/" onClick="return a2a_showAll(this)">&darr;</a></div>';
		if(!Array.every||!window.getComputedStyle)a2a_d+='</td></table>';		a2a_d+='</div>';
		a2a_w=document.createElement('div');
		a2a_w.innerHTML=a2a_d;
		document.body.insertBefore(a2a_w,document.body.firstChild);
	}
	a2a_create_dropdown();