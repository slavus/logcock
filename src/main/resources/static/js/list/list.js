'use strict';
define(function(require) {
  /*

     $start.onclick = () => {
          const url = 'https://d8d913s460fub.cloudfront.net/videoserver/cat-test-video-320x240.mp4'
          const fileStream = streamSaver.createWriteStream('cat.mp4')

          fetch(url).then(res => {
            const readableStream = res.body

            // more optimized
            if (window.WritableStream && readableStream.pipeTo) {
              return readableStream.pipeTo(fileStream)
                .then(() => console.log('done writing'))
            }

            window.writer = fileStream.getWriter()

            const reader = res.body.getReader()
            const pump = () => reader.read()
              .then(res => res.done
                ? writer.close()
                : writer.write(res.value).then(pump))

            pump()
          })
        }
  */

  var $ = require('jquery');
  $(document).ready(function() {
    console.log("radi");
    $(".masked-download").click(function(e) {
      e.preventDefault();

      var count = 0;
      var parts = 0;
      var url = this.href;

      $.get(url, function(d) {
        console.log(d);
        var fileName = atob(d.name);
        parts = d.parts;
        var size = d.size;
        console.log(fileName);

        const fileStream = streamSaver.createWriteStream(fileName, {
          size: size // Makes the procentage visiable in the download
        });
        var encode = TextEncoder.prototype.encode.bind(new TextEncoder);
        var writer = fileStream.getWriter();

        var repeatDownload = function(r) {
          var data = encode(atob(r.token));
          data && writer.write(data)
          count++;

          if (count < parts) {
            console.log("Sad je " + count + " od " +parts)
            $.get(url + "?part=" + count, repeatDownload);
          } else {
            console.log("zatvaram writera")
            writer.close();
          }
        }
        $.get(url + "?part=" + count, repeatDownload);
      });


    });


  });

});