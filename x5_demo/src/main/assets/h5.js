 document.addEventListener("DOMContentLoaded", function(event) {
    console.log("文档已加载完成，可以操作DOM元素");
	function gotoNative() {
		document.getElementById("content").innerHTML = "HHHH"
	}
	 document.getElementById("btn") .addEventListener("click", function() {
		gotoNative();
		alert("hhh")
	});
}); 