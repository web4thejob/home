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
ckeb.CKeditorBox = zk.$extends(zul.Widget, {
    _value: '',
    $define: {
        value: [

            function (v) {
                return !v ? '' : v;
            },
            function (v) {
                var editor = this.getEditor();
                if (editor)
                    editor.setData(v);
            }
        ],
        flush: function (v) {
            var editor = this.getEditor();
            if (editor && editor.checkDirty()) {
                this._value = editor.getData();
                editor.resetDirty();
                this.fire('onFlush', {
                    value: this._value
                }, {
                    toServer: true
                });
                editor.on('change', this.onChange);
            }
        },
        focus: function (v) {
            var editor = this.getEditor();
            if (editor) {
                setTimeout(function () {
                    editor.focus();
                }, 50);
            }
        }
    },

    redraw: function (out) {
        out.push('<div id="', this.uuid, '-ckcontainer"', 'style="height: 100%;"', '><textarea id="', this.uuid, '-cnt">', this._value, '</textarea></div>');
    },

    getEditor: function () {
        return this._editor;
    },

    bind_: function () {
        this.$supers('bind_', arguments);

        var wnd = this.findHostWindow();
        if (wnd) {
            wnd.wgt = this;
            zWatch.listen({
                onSize: [wnd, this.onSize]
            });
        }
        this._init();
    },

    unbind_: function (evt) {
        if (this._editor) {
            try {
                this._editor.destroy(true);
            } catch (err) {
                // finish detaching ckeditor
                CKEDITOR.tools.removeFunction(this._editor._.frameLoadedHandler);
                this._editor.fire('contentDomUnload');

                // finish destroying ckeditor
                this._editor.status = 'destroyed';
                this._editor.fire('destroy');
                this._editor.removeAllListeners();
                CKEDITOR.remove(this._editor);
                CKEDITOR.fire('instanceDestroyed', null, this._editor);
            }

            this._editor = null;
        }
        var wnd = this.findHostWindow();
        if (wnd) {
            zWatch.unlisten({
                onSize: [wnd, this.onSize]
            });
        }
        this.$supers('unbind_', arguments);

    },

    _init: function () {
        var uuid = this.uuid;
        this._editor = CKEDITOR.replace(uuid + '-cnt', {
            startupFocus: true,
            height: 600,
            tabIndex: 1,
            customConfig: this.configPath
        });

        this._editor.wgt = this;
        this._editor.on('instanceReady', function (event) {
            var e = document.getElementById(event.editor.wgt.uuid + '-ckcontainer')
            event.editor.resize(null, e.clientHeight);
            event.editor.focus();
        });
        this._editor.on('change', this.onChange);
    },

    onSize: function () {
        if (this.wgt._editor.status == 'ready') {
            var e = this.$n(),
                editor = this.wgt._editor;
            setTimeout(function () {
                editor.resize(null, e.clientHeight - (70+13));
                editor.focus();
            }, 50);
        }
    },

    findHostWindow: function () {
        var p = this.parent;
        while (p) {
            if (p.$instanceof(zul.wnd.Window)) {
                return p;
            }
            p = p.parent;
        }
    },

    onChange: function (evt) {
        var wgt = evt.editor.wgt;
        if (wgt) {
            evt.removeListener();
            wgt._value = evt.editor.getData();
            wgt.fire('onChange', {
                value: wgt._value
            }, {
                toServer: true
            });
        }
    },

});