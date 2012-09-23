package com.kulinr.backofflbs;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BackOffLBSActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button button = (Button) findViewById(R.id.button_get_location);
		button.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_get_location:
			Location currentLocation = getCurrentLocation();
			Toast.makeText(
					this,
					"lat: " + currentLocation.getLatitude() + " , Lng: "
							+ currentLocation.getLongitude(), Toast.LENGTH_LONG)
					.show();

			break;

		default:
			break;
		}
	}
}
