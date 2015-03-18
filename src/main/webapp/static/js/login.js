$(function() {
	$("#request-reset-password").click(function (event){
		event.preventDefault();
		var email = $("#email").val();
		if (!checkEmail()) {
			showErrorInfo("该邮件地址未注册");
		} else {
			var path = "requestResetPassword?email=" + email;
			$.getJSON(path, function(data) {
				if (data.state == "success") {
					showSuccessInfo("邮件发送成功");
				} else {
					showErrorInfo("邮件发送失败");
				}
			});
		}
	});
	$("#email-form").submit(function (event) {
		event.preventDefault();
	});
});

function showErrorInfo(message) {
	$("#info").removeClass("info-success");
	$("#info").addClass("info-error");
	$("#info").removeClass("hidden");
	$("#info").html(message);
}

function showSuccessInfo(message) {
	$("#info").removeClass("info-error");
	$("#info").addClass("info-success");
	$("#info").removeClass("hidden");
	$("#info").html(message);
}

function checkEmail() {
	var path = "checkEmailIsExisted";
	var emailIsExisted = false;
	var email = $("#email").val();
	$.ajax({
		type : "post",
		url : path,
		async: false,
        dataType: "json",
		data: {"email" : email},
		success : function(data, textStatus, jqXHR) {
			console.log(JSON.stringify(data));
			if (data.state == "existed") {
				emailIsExisted = true;
			} else {
				emailIsExisted = false;
			}
		}
	});
	return emailIsExisted;
}