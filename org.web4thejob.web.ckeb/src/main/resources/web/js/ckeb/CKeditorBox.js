/*
 * Copyright (c) 2012-2013 Veniamin Isaias.
 *
 * This file is part of web4thejob.
 *
 * Web4thejob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * Web4thejob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with web4thejob.  If not, see <http://www.gnu.org/licenses/>.
 */
ckeb.CKeditorBox=zk.$extends(zul.Widget,{_value:"",$define:{value:[function(e){return!e?"":e},function(e){var t=this.getEditor();if(t)t.setData(e)}],flush:function(e){var t=this.getEditor();if(t&&t.checkDirty()){this._value=t.getData();t.resetDirty();this.fire("onFlush",{value:this._value},{toServer:true});t.on("change",this.onChange)}},focus:function(e){var t=this.getEditor();if(t){setTimeout(function(){t.focus()},50)}}},redraw:function(e){e.push('<div id="',this.uuid,'-ckcontainer"','style="height: 100%;"','><textarea id="',this.uuid,'-cnt">',this._value,"</textarea></div>")},getEditor:function(){return this._editor},bind_:function(){this.$supers("bind_",arguments);var e=this.findHostWindow();if(e){e.wgt=this;zWatch.listen({onSize:[e,this.onSize]})}this._init()},unbind_:function(e){if(this._editor){try{this._editor.destroy(true)}catch(t){CKEDITOR.tools.removeFunction(this._editor._.frameLoadedHandler);this._editor.fire("contentDomUnload");this._editor.status="destroyed";this._editor.fire("destroy");this._editor.removeAllListeners();CKEDITOR.remove(this._editor);CKEDITOR.fire("instanceDestroyed",null,this._editor)}this._editor=null}var n=this.findHostWindow();if(n){zWatch.unlisten({onSize:[n,this.onSize]})}this.$supers("unbind_",arguments)},_init:function(){var e=this.uuid,t=this.configPath;this._editor=CKEDITOR.replace(e+"-cnt",{startupFocus:true,height:600,tabIndex:1,customConfig:t});this._editor.wgt=this;this._editor.on("instanceReady",function(e){var t=document.getElementById(e.editor.wgt.uuid+"-ckcontainer");e.editor.resize(null,t.clientHeight);e.editor.focus()});this._editor.on("change",this.onChange)},onSize:function(){if(this.wgt._editor.status=="ready"){var e=this.$n(),t=this.wgt._editor;setTimeout(function(){t.resize(null,e.clientHeight-(70+13));t.focus()},50)}},findHostWindow:function(){var e=this.parent;while(e){if(e.$instanceof(zul.wnd.Window)){return e}e=e.parent}},onChange:function(e){var t=e.editor.wgt;if(t){e.removeListener();t._value=e.editor.getData();t.fire("onChange",{value:t._value},{toServer:true})}}})