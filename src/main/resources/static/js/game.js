/**
 * 
 */

let gdto_tmp = null;
let move_digit_id;
let move_digit;

//数当てゲーム画面ロード時
document.addEventListener("DOMContentLoaded", function() {

	$('#input_form input').prop('disabled', false);
	$('#input_form button').prop('disabled', false);

	//htmlから変数を取得
	let data = document.getElementById("data");
	let gdto = {
		game_act_flg: parseInt(data.dataset.game_act_flg),
		popup_msg: data.dataset.popup_msg,
		point: parseInt(data.dataset.point),
		hide_num: data.dataset.hide_num,
		success: (data.dataset.success === "true"),
		err_msg: data.dataset.err_msg
	};

	gdto_tmp = gdto;

	//エラー時処理
	if (!gdto.success) {
		$('#input_form input').prop('disabled', true);
		$('#input_form button').prop('disabled', true);
		$(".err_msg").empty();
		if (gdto.err_msg != null) {
			let err_msg = `<p>${gdto.err_msg}</p>`;
			$(".err_msg").append(err_msg);
		}
	}

	//当日ゲーム終了時処理
	if (gdto.game_act_flg == 1) {
		$('#input_form input').prop('disabled', true);
		$('#input_form button').prop('disabled', true);
		$.get("/html/popup.html", function(html_data) {
			$("#popup").html(html_data);
			$("#popup_msg").text(gdto.popup_msg);
			$("#point").text(gdto.point);
			new bootstrap.Modal(document.getElementById('popup-contents')).show();
		});
	}
	return;
});



//キー入力時
function inputNum(event, current_digit) {
	//半角数字チェック
	if (current_digit.value != "" && !(current_digit.value.match(/[0-9]/))) {
		current_digit.value = "";
		$("#err_msg").text("半角数字で入力してください");
		return current_digit.value;
	}
	//バックスペースを押した場合
	if (event.key === "Backspace") {
		//カーソルを前に移動
		move_digit_id = String(parseInt(current_digit.id) - 1);
		move_digit = document.getElementById(move_digit_id);
		if (move_digit) {
			move_digit.focus();
		}
		//数字キーを押した場合
	} else if (event.key.match(/[0-9]/) && current_digit.value.match(/[0-9]/)) {
		//数値を置換
		current_digit.value = "";
		current_digit.value = event.key;
		//カーソルを後ろに移動
		move_digit_id = String(parseInt(current_digit.id) + 1);
		move_digit = document.getElementById(move_digit_id);
		if (move_digit) {
			move_digit.focus();
		}
		$("#err_msg").text("");
	}
}

//入力数字チェック
$("#input_form").submit(function(event) {
	event.preventDefault();
	$('#input_form button').prop('disabled', true);

	let input_num0 = String($("#0").val());
	let input_num1 = String($("#1").val());
	let input_num2 = String($("#2").val());
	let input_num = input_num0 + input_num1 + input_num2;

	move_digit_id = String((input_num).length);
	if (move_digit_id > 2) {
		move_digit_id = 2;
	}
	move_digit = document.getElementById(move_digit_id);
	if (move_digit) {
		move_digit.focus();
	}

	if (!((input_num).match(/^[0-9]*$/))) {
		$("#err_msg").text("半角数字で入力してください");
		$('#input_form button').prop('disabled', false);
		return;
	}
	if ((input_num).length != 3) {
		$("#err_msg").text("3桁の数字を入力してください");
		$('#input_form button').prop('disabled', false);
		return;
	}

	if (input_num0 == input_num1 || input_num0 == input_num2 || input_num1 == input_num2) {
		$("#err_msg").text("異なる桁に同じ数字は入力できません");
		$('#input_form button').prop('disabled', false);
		return;
	}

	//Modelに入力数字を送信
	$.post("/game", { input_num: input_num }, function(gdto) {
		$('#input_form button').prop('disabled', false);
		if (gdto.success) {
			$("#err_msg").empty();
			//ゲーム結果リストを表示
			$("#tbody").empty();
			for (let i = 0; i < 10; i++) {
				let resultList = `<tr>
								<td>${i + 1}回目</td>
								<td>${gdto.resultList[i]?.input_num || ""}</td>
								<td>${gdto.resultList[i]?.result || ""}</td>
								</tr>`;
				$("#tbody").append(resultList);

				//入力欄の初期化
				document.querySelectorAll('.input_num').forEach(function(input) {
					input.value = '';
				});
				document.getElementById('0').focus();

			}
			//当日ゲーム終了時、ポップアップを表示
			if (gdto.game_act_flg == 1) {
				$('#input_form input').prop('disabled', true);
				$('#input_form button').prop('disabled', true);
				gdto_tmp = gdto;
				$.get("/html/popup.html", function(html_data) {
					$("#popup").html(html_data);
					$("#popup_msg").text(gdto.popup_msg);
					$("#point").text(gdto.point);
					new bootstrap.Modal(document.getElementById('popup-contents')).show();
				});
			}
		} else {
			$('#input_form input').prop('disabled', true);
			$('#input_form button').prop('disabled', true);
			$("#err_msg").text(gdto.err_msg);
			return gdto;
		}
	}, "json");

});

//ポップアップを閉じる
$(document).on("click", ".popup-close", function() {
	$(".point").empty();
	let point = `<p>${gdto_tmp.point}</p>`;
	$(".point").append(point);
});


//ログアウトダイアログ表示
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
