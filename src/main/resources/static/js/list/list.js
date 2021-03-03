'use strict';
define(function(require) {

  var $ = require('jquery');
  $(document).ready(function() {
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
          count++;
          console.log("Downloaded part: " + count + " from: " + parts)
          var data = encode(r.token);
          data && writer.write(data)
          if (count < parts) {
            $.get(url + "?part=" + count, repeatDownload);
          } else {
            console.log("Download finished. Closing writer.")
            writer.close();
          }

        }
        $.get(url + "?part=" + count, repeatDownload);
      });


    });


  });

});