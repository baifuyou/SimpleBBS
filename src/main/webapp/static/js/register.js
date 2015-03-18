$(function() {
	$("#reinputPassword").focusout(function(event) {
		if (validateTwicePassword()) {
			hiddenElement($("#passwordNotMatch"));
			enableElement($("#submit"));
		} else {
			showElement($("#passwordNotMatch"));
			disableElement($("#submit"));
		}

	});

	$("#inputPassword").focusout(function(event) {
		if (checkPassword()) {
			hiddenElement($("#passwordNotInput"));
			enableElement($("#submit"));
		} else {
			showElement($("#passwordNotInput"));
			disableElement($("#submit"));
		}
	});

	$("#inputNickname").focusout(function(event) {
		if (checkNickname()) {
			hiddenElement($("#nicknameNotInput"));
			enableElement($("#submit"));
		} else {
			showElement($("#nicknameNotInput"));
			disableElement($("#submit"));
		}
	});

	$("#submit").click(
			function(event) {
				if (validateTwicePassword() && validateEmail()
						&& checkNickname() && checkPassword()) {
					enableElement($("#submit"));
				} else {
					disableElement($("#submit"));
				}
			});
});

function checkEmail(isRegistered) {
    console.log("isRegistered: " + isRegistered)
    if (validateEmail()) {
    	$("#emailInvalid").removeClass("show").addClass("hidden");
    	enableElement($("#submit"));
    			// 验证email是否已被注册
        console.log("isRegistered: " + isRegistered)
    	if (isRegistered) {
    		showElement($("#emailUnusable"));
    		disableElement($("#submit"));
    	} else {
    		hiddenElement($("#emailUnusable"));
    		enableElement($("#submit"));
    	}
    } else {
    	console.log("emailInvalid")
    	showElement($("#emailInvalid"));
    	disableElement($("#submit"));
    }
}

function enableElement(element) {
	element.removeAttr("disabled");
}

function disableElement(element) {
	element.attr("disabled", "disabled");
}

function hiddenElement(element) {
	element.removeClass("show").addClass("hidden");
}

function showElement(element) {
	element.removeClass("hidden").addClass("show");
}

function checkPassword() {
	var pwd = $("#inputPassword").val();
	var reg = /^[0-9]+$/;
	return pwd != undefined && pwd != null && pwd != ""
			&& (pwd.length >= 10) && (pwd.length <= 20)
			&& (!reg.test(pwd));
}

function checkNickname() {
	var nickname = $("#inputNickname").val();
	return nickname != undefined && nickname != null && nickname != "";
}

function validateTwicePassword() {
	var fPassword = $("#inputPassword").val();
	var sPassword = $("#reinputPassword").val();
	return fPassword == sPassword;
}

function validateEmail() {
	var email = $("#inputEmail").val();
	var reg = /^[a-zA-Z0-9_\.-]+@[a-zA-Z0-9_\.-]+\.[a-zA-Z]+$/;
	return reg.test(email);
}
