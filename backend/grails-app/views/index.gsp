<!DOCTYPE html>
<html>
	<head>
        <g:javascript library="jquery" plugin="jquery"/>
        <script src="js/plupload.full.js"></script>
        <script>
            var uploader;
            var initUploader = function(){
                uploader = new plupload.Uploader({
                    runtimes: 'html5, html4',
                    browse_button: 'triggerFileBrowse'
                });
                uploader.init();
            };

            var addFile = function(event) {
                event.preventDefault();
                if(!uploader){
                    initUploader();
                }
                uploader.settings.url = $(this).attr('href');
                $("#triggerFileBrowse").trigger('click');

                uploader.bind('FilesAdded', function(up, files) {
                    uploader.start();
                });
                var trigger = $(this);
                uploader.bind('FileUploaded', function(up, file, response){
                    trigger.closest('div').find('.fileContainer').html(response.response);
                });
            };
            $(document).on("click", ".addFile", addFile);
        </script>
	<body>
    <div>
        <g:link class="addFile" controller="importData" action="importData"><h2>Load Excel file</h2></g:link>
        <input id="triggerFileBrowse" style="display: none"/>%{--workaround for triggering plupload filebrowse--}%
    </div>
	</body>
</html>
