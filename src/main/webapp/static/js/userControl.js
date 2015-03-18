$(function() {
	
	$("#sheet-list li a").click(function(event) {
		$(this).tab("show");
		var sheetName = $(this).attr("sheet-name");
		$(".setting-sheet").addClass("hidden");
		$("#" + sheetName).removeClass("hidden");
	});

	$("#save-profile").click(
			function(event) {
				event.preventDefault();
			});

	$("#save-password").click(function(event) {
		event.preventDefault();
	});
});

function checkNewPasswordIdentical() {
	var newPassword = $("#new-password").val();
	var newPassword2 = $("#new-password2").val();
	return newPassword == newPassword2;
}

function checkNewPasswordInput() {
	var password = $("#new-password").val();
	var reg = /^[0-9]+$/;
	return (password.length >= 10)  && (password.length <= 20) && (!reg.test(password));
}

function checkNicknameInput() {
	var nickname = $("#nickname").val();
	return nickname != null && nickname != "";
}

function show(target, time) {
	$(target).removeClass("hidden");
	setTimeout(function () {
		$(target).addClass("hidden");
	}, time);
}