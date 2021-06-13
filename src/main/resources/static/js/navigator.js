var baseUrl = "./navigate?path=";

$(document).ready(function(){
    $("button").click(function(){
        let path = $(this).attr("path");
        $.get(baseUrl+path, function(data, status){
            console.log(data);
            populateFiles(data,status);
          });
    });

    $("#navFileList").on('click','.ls', function (event) {
        console.log(this);
        console.log("ls clicked");
        let path = $(this).attr("path");
        let isDir = $(this).attr("isDir");
        console.log(isDir);
        if(isDir == 'true'){
            console.log("fetching directory");
            $.ajax({
                url: baseUrl + path,
                type: "GET",
                dataType: 'json',
                success: function (data) {
                    console.log(data);
                    populateFiles(data);
                }
            });
        } else {
            console.log("downloading file");
            event.preventDefault();
            window.location.href = './download?path='+path;

        }
    });
});

function populateFiles(data){
    let node = $("#navFileList");
    node.empty();
    $("#back").attr("path",data.parentDir);
    let files = data.navFiles;
    for(let i = 0; i < files.length;i++){
        let file = files[i];
        var li = $("<li style='text-decoration: underline;cursor: pointer;margin-bottom:10px'></li>");
        var div = $("<div class='ls'></div>");
        $(div).attr('path', file.entityUrl);
        $(div).attr('isDir',file.isDir);
        div.append(file.entityName);
        li.append(div);
        node.append(li);
    }

}