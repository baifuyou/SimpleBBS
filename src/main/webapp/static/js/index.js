$(function() {
    $(".pageIndex li").click(function(event){
        $(".pageIndex li").removeClass("active")
        $(this).addClass("active")
    })
})
function renderPostList(postList) {
    console.log("postList: " + postList)
    console.log(JSON.stringify(postList))
    //console.log("postList[0]: " + postList[0])
}

/*function getIndex() {
    return $(".pageIndex .active a").attr("index")
}*/