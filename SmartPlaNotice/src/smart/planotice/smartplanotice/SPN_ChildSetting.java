package smart.planotice.smartplanotice;

import smart.planotice.smartplanotice.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SPN_ChildSetting extends Activity {
	
	Button btn1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.childsetting);
		btn1 = (Button)findViewById(R.id.btnchildsetting01);
		
		btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SPN_ChildSetting.this, SPN_ChildAdd.class);
				startActivity(intent);
			}
		});
		
	}
	
}
