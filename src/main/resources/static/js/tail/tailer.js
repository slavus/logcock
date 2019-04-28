'use strict';
define(function (require) {
    var Stimulus = require('stimulus');
    require("ace-builds");

    var editor = ace.edit("output");
    editor.setTheme("ace/theme/tomorrow_night_eighties");
    Stimulus.Application.start().register("tailer", class extends Stimulus.Controller {
        static get targets() {
            return ["pause", "follow", "output"]
        }
        connect() {
            this.wsConnect();
        }

        wsConnect() {
            this.clientWebSocket = new WebSocket("ws://127.0.0.1:8080" + wsPath);
            var follow = this.followTarget;
        	var pause = this.pauseTarget;


            var session = editor.session;
            this.clientWebSocket.onmessage = function (msg) {
                session.insert({ row: session.getLength(), column: 0 }, "\n" + msg.data);
                if (follow.checked) {
                	editor.gotoLine(session.getLength(), 0);
                }
            };
        	this.pauseTarget.textContent = "ok";

            this.clientWebSocket.onopen = function() {
            	pause.textContent = "Stop tailing";
            }
            this.clientWebSocket.onclose = function(msg) {
            	pause.textContent = "Tail file";
            }
        }

        toggle() {
            if (this.clientWebSocket.readyState != 3) {
                this.clientWebSocket.close();
            } else {
                this.wsConnect();
            }

        }
    })
});