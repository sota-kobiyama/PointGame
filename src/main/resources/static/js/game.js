/**
 * 
 */

let gdto = {}

//初期画面表示処理
document.addEventListener("DOMContentLoaded", function() {
	formActivation(true);
	data = document.getElementById("data")
	gdto = JSON.parse(data.dataset.gdto);
	if (gdto.success == true) {
		renderView();
	} else {
		formActivation(false);
		$("#err_msg").text(gdto.err_msg || "");
	}
	return;
});

$("#input_form").submit(function(event) {
	event.preventDefault();

	let input_num0 = String($("#0").val());
	let input_num1 = String($("#1").val());
	let input_num2 = String($("#2").val());
	let input_num = input_num0 + input_num1 + input_num2;
	//入力数字の桁数に応じてカーソルセット
	let digit_id
	if (input_num.length > 2) {
		digit_id = "2";
	} else {
		digit_id = String(input_num.length)
	}
	moveDigit(digit_id, 0);

	if (!((input_num).match(/^[0-9]*$/))) {
		$("#err_msg").text("半角数字で入力してください");
		formActivation(true);
		return;
	} else if ((input_num).length != 3) {
		$("#err_msg").text("3桁の数字を入力してください");
		formActivation(true);
		return;
	} else if (input_num0 == input_num1 || input_num0 == input_num2 || input_num1 == input_num2) {
		$("#err_msg").text("異なる桁に同じ数字は入力できません");
		formActivation(true);
		return;
	}
	formActivation(false);

	$.post("/game", {
		input_num: input_num,
		hide_num: gdto.hide_num,
		game_count: gdto.game_count,
		point: gdto.point
	}, function(response) {
		formActivation(true);
		if (response.success == true) {
			gdto = response;
			renderView();
			//バックエンド処理異常時
		} else {
			formActivation(false);
			$("#err_msg").text(gdto.err_msg);
			return gdto;
		}
	}, "json");
});

//ポップアップを閉じる
$(document).on("click", ".popup-close", function() {
	$("#point").text(gdto.point);
});

//ログアウトボタン押下時
$(document).on("click", ".logout", function() {
	$("#logout-dialog").remove();
	$("body").append(`
		<div class="modal fade" id="logout-dialog" tabindex="-1" aria-hidden="true">
			<div class="modal-dialog modal-dialog-centered modal-md">
				<div class="modal-content">
	                <div class="row justify-content-center text-center">
	                    <div class="mt-4 mb-4">ログアウトしますか？</div>
	                    <div class="d-flex justify-content-center align-content-center mb-3">
	                    	<button class="btn btn-danger me-2" id="confirm">ログアウト</button>
	                   		<button class="btn btn-secondary" id="cancel" type="button" data-bs-dismiss="modal">キャンセル</button>
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
        `);
	setTimeout(() => {
		let logoutModal = new bootstrap.Modal(document.getElementById('logout-dialog'));
		logoutModal.show();
	}, 100);

	$("#confirm").on("click", function() {
		window.location.href = "/logout";
	});
});

//数字入力欄活性化/非活性化
function formActivation(abled) {
	$('#input_form input').prop('disabled', !abled);
	$('#input_form button').prop('disabled', !abled);
}

//数字入力欄カーソル移動
function moveDigit(digit_id, destination) {
	let moved_digit_id = String(parseInt(digit_id) + destination);
	let moved_digit = document.getElementById(moved_digit_id);
	if (moved_digit) {
		moved_digit.focus();
	}
}

function renderView() {
	$("#point").text(gdto.point);
	$(".input_num").val('');
	document.getElementById('0').focus();
	$("#err_msg").empty();
	$("#tbody").empty();
	for (let i = 0; i < 10; i++) {
		let row = `<tr>
								<td>${i + 1}回目</td>
								<td>${gdto.resultList[i]?.input_num || ""}</td>
								<td>${gdto.resultList[i]?.result || ""}</td>
								</tr>`;
		$("#tbody").append(row);
	}
	if (gdto.game_act_flg === 1) {
		formActivation(false);
		displayPopup();
	}
}

function displayPopup() {
	$.get("/html/popup.html", function(html_data) {
		$("#popup").html(html_data);
		$("#popup_msg").text(gdto.popup_msg);
		$("#point").text(gdto.point);
		new bootstrap.Modal(document.getElementById('popup-contents')).show();
	});
}

function inputNum(event, current_digit) {
	//半角数字チェック
	if (current_digit.value != "" && !(current_digit.value.match(/[0-9]/))) {
		current_digit.value = "";
		$("#err_msg").text("半角数字で入力してください");
		return;
	}
	//バックスペース：カーソルを前に移動
	//数字キー：数字を入力しカーソルを後ろに移動
	if (event.key === "Backspace") {
		moveDigit(current_digit.id, -1);
	} else if (event.key.match(/[0-9]/) && current_digit.value.match(/[0-9]/)) {
		current_digit.value = event.key;
		moveDigit(current_digit.id, 1);
		$("#err_msg").text("");
	}
}