/**
 * 
 */

//入力会員情報チェック
$("#login").submit(function(event) {
	event.preventDefault();
	
	$('#login button').prop('disabled', true);

	let member_id = $("#member_id").val();
	let password = $("#password").val();
	
	if (member_id == null || member_id == "") {
		$("#err_msg").text("会員IDを入力してください");
		document.getElementById("member_id").focus();
		$('#login button').prop('disabled', false);
		return;
	}
	
	if (!(member_id.match(/^[0-9]*$/))) {
		$("#err_msg").text("会員IDは半角数字以外は入力できません");
		document.getElementById("member_id").focus();
		$('#login button').prop('disabled', false);
		return;
	}
	if (member_id.length != 6) {
		$("#err_msg").text("会員IDは6桁で入力してください");
		document.getElementById("member_id").focus();
		$('#login button').prop('disabled', false);
		return;
	}
	
	if (password == null || password == "") {
		$("#err_msg").text("パスワードを入力してください");
		document.getElementById("password").focus();
		$('#login button').prop('disabled', false);
		return;
	}
	
	if (!(password.match(/^[A-Za-z0-9]*$/))) {
		$("#err_msg").text("パスワードは半角英数字以外は入力できません");
		document.getElementById("password").focus();
		$('#login button').prop('disabled', false);
		return;
	}
	if (password.length != 8) {
		$("#err_msg").text("パスワードは8文字で入力してください");
		document.getElementById("password").focus();
		$('#login button').prop('disabled', false);
		return;
	}

	//会員情報送信時
	$.post("/login", { member_id: member_id, password: password }, function(mdto) {
		$('#login button').prop('disabled', false);
		//成功時、数当てゲーム画面に遷移
		if (mdto.success) {
			window.location.href = "/game";
		//失敗時
		} else {
			document.getElementById("password").focus();
			$("#err_msg").text(mdto.err_msg);
			return;
		}
	}, "json");


});