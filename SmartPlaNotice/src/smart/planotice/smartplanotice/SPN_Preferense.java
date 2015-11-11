package smart.planotice.smartplanotice;

public class SPN_Preferense {

	public static final String ParentState = "ParentState";
	public static final String ChildState = "ChildState";
	public static final String EmptyState = "EmptyState";
	public static final String NothingState = "NothingState";
	public static final String OkState = "OkState";
	public static final String CheckAddress = "CheckAddress";
	public static final String RegisterPhone = "RegisterPhone";
	public static final String LogOut = "LogOut";
	public static final String AddChildState = "AddChildState";
	public static final String CheckChildState = "CheckChildState";
	public static final String SuccessState ="SuccessState";
	public static final String FailState = "FailState";
	public static final String BackAccreditState = "BackAccreditState";
	public static final String AddVector = "AddVector";
	public static final String ReqOneMoreState = "ReqOneMoreState";
	public static final String ReqMyGpsState = "ReqMyGpsState";
	public static final String AddChildGpsState = "AddChildGpsState";
	public static final String TransChildGpsState = "TransChildGpsState";
	public static final String ReqGpsState01 = "ReqGpsState01"; // 자녀위치추적
	public static final String NoReqGpsState01 = "NoReqGpsState01"; // 자녀위치추적취소
	public static final String ReqGpsState02 = "ReqGpsState02";// 자녀경로추적
	public static final String NoReqGpsState02 = "NoReqGpsState02"; //자녀경로 추적취소
	public static final String ReqGpsState03 = "ReqGpsState03";//자녀경로추적해서 자녀폰이 부모폰에게 알림.
	public static final String ReqGpsChild01 = "ReqGpsChild01"; //자녀위치추적에 대한 자녀가 서버에 보내는 메시지
	
	//에러관련
	public static final String ErrorState01 = "ErrorState01";// 회원가입 에러
	public static final String ErrorState02 = "ErrorState02";// 내 정보를 입력할 시
	public static final String ErrorState03 = "ErrorState03";// 연결안되어서 백그라운드 알람(인증번호) 못보낼때
}
