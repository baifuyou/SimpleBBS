$(function() {
	$("#save-password").click(function(event) {
		event.preventDefault();
		if (!checkNewPasswordInput()) {
			$("#password-format-error").removeClass("hidden");
			return;
		} else {
			$("#password-format-error").addClass("hidden");
		}

		if (!checkNewPasswordIdentical()) {
			$("#two-password-not-identical").removeClass("hidden");
			return;
		} else {
			$("#two-password-not-identical").addClass("hidden");
		}
		var newPassword = $("#new-password").val();
		var authCode = $("#auth-code").val();
		var emailRecognitionCode = $("#email-recognition-code").val();
		var path = "doResetPassword";
		$.post(path, {
			"newPassword" : newPassword,
			"emailRecognitionCode" : emailRecognitionCode,
			"authCode" : authCode
		}, function(data) {
			if (data.state == "success") {
				$("#reset-password-info").removeClass();
				$("#reset-password-info").addClass("info-success");
				$("#reset-password-info").html("密码重置成功");
			} else {
				$("#reset-password-info").removeClass();
				$("#reset-password-info").addClass("info-error");
				$("#reset-password-info").html("密码重置失败");
			}
		}, "json");
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
	return (password.length >= 10) && (password.length <= 20)
			&& (!reg.test(password));
}