package smart.planotice.smartplanotice;

import smart.planotice.smartplanotice.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class SPN_ChkAccredit extends Activity {

	Intent getResult;
	EditText et1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 자동 생성된 메소드 스텁
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chk_accredit);
		et1 = (EditText) findViewById(R.id.etaccredit01);
		getResult = getIntent();
		
		Log.e(getResult.getStringExtra("ChkAccreditNumber"), getResult.getStringExtra("ChkAccreditNumber"));
		et1.setText(getResult.getStringExtra("ChkAccreditNumber"));
		
	}

}
