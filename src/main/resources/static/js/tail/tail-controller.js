// tail-controller.js
(() => {
  const application = Stimulus.Application.start()
  application.register("tail", class extends Stimulus.Controller {
    static get targets() {
      return [ "file" ]
    }
    tailIt() {
    	var clientWebSocket = new WebSocket("ws://127.0.0.1:8080"+wsPath);
    	var fileTarget1 = this.fileTarget;

    	clientWebSocket.onmessage = function(msg) {
    		fileTarget1.append(msg.data + "\n");
    		$($("#file")).scrollTop($("#file")[0].scrollHeight);

        }
    }

    connect() {
        console.log("Hello, Stimulus!", this.element)
    }

  })
})()