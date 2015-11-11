package smart.planotice.smartplanotice;

import smart.planotice.smartplanotice.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SPN_HELP extends Activity{

	Button btn1,btn2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		btn1 = (Button)findViewById(R.id.btnhelp01);
		btn2 = (Button)findViewById(R.id.btnhelp02);
		
		btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
		btn2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopService(new Intent(SPN_HELP.this, SPN_Connect_BackServ.class));
			}
		});
		
	}

}
