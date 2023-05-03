$(document).ready(function(){
	var isIdChecked=false; //중복체크했을 땐 true로 변경
	$("#idcheck").on("click",function(){
		var querydata = {"id":$("#id").val()}
		
		$.ajax({
			url:"/carrot/member/singup3_idcheck",
			type:"get",
			data:querydata,
			success:success_run,
			error:error_run
		});//end ajx
		
	})//end click

// id중복체크확인버튼 이벤트
    //Id값 바뀔 때마다 
    var id = document.getElementById('id')
	id.addEventListener('change', function(e){
    	isIdChecked=false;
    	$("#result").html("<a style='color: red;'>아이디 중복체크를 해주세요</a>");
    });

    //제출버튼 클릭시 중복체크여부
    var fin = document.getElementById('fin')
    fin.addEventListener('click',function(e) {
        if(isIdChecked==false){
	    	e.preventDefault();
        	alert("id 중복체크를 해주세요");
        }
    });// 다음버튼
})//end ready
	
//ajax요청을 성공하면 .ajax메소드 내부에서 success속성에 설정한 함수를 호출하면서 ajax처리 결과를 자동으로 함수의 매개변수로 전달
	function success_run(txt){
		if(txt=="사용가능한 아이디입니다."){
			isIdChecked=true
			$("#result").html("<a style='color: green;'>"+txt+"</a>");
		}else{
			$("#id").val('');
			$("#result").html("<a style='color: red;'>"+txt+"</a>");
		}
	}
	//obj - ajax통신을 하는 객체, msg -응답메시지,statusmsg - 에러메시지
	function error_run(obj,msg,statusmsg){
		alert("오류;"+obj+","+msg+","+statusmsg);
	}
