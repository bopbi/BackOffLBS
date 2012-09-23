package com.kulinr.backofflbs;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

public class BackOffLBSActivity extends Activity implements
		LocationListener {
	private LocationManager lm;
	final long minTime = 5 * 6000; // 5min
	final float minDistance = 1000; // 1000m

	private String bestProvider;
	private String coarseProvider;
	protected float accuracy;

	private LocationListener lbounce = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			runUpdateLocation();
			if (location.getAccuracy() < 10) {
				lm.removeUpdates(lbounce);
				lm.removeUpdates(lcoarse);
			}

		}
	};

	private LocationListener lcoarse = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			runUpdateLocation();
			lm.removeUpdates(lcoarse);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!LocationServiceActive(this)) {
		} else if (!checkInternetConnection(this)) {
		} else {
			getCurrentLocation();
			runUpdateLocation();
		}
	}

	protected Location getCurrentLocation() {

		Location location = null;
		Location currentBestLocation = null;

		bestProvider = lm
				.getBestProvider(LocationUtils.getBestProvider(), true);
		coarseProvider = lm.getBestProvider(LocationUtils.getCoarseProvider(),
				true);

		try {
			location = lm.getLastKnownLocation(coarseProvider);
			currentBestLocation = lm.getLastKnownLocation(bestProvider);
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), "Location Service Not Available",
					10).show();
		}

		if (LocationUtils.isBetterLocation(location, currentBestLocation)) {
			return location;
		}

		return currentBestLocation;
	}

	protected void runUpdateLocation() {
		lm.requestLocationUpdates(bestProvider, minTime, minDistance,
				(LocationListener) this);
		lm.requestLocationUpdates(coarseProvider, 0, 0, lcoarse);
		lm.requestLocationUpdates(bestProvider, 0, 0, lbounce);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Stop updates to save power while app paused
		if (!LocationServiceActive(this)) {
		} else if (!checkInternetConnection(this)) {
		} else {
			lm.removeUpdates(this);
			lm.removeUpdates(lbounce);
			lm.removeUpdates(lcoarse);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		runUpdateLocation();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(getBaseContext(), "Location Service Not Available",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	
	public static boolean LocationServiceActive(Context context) {
		boolean hasActiveLocationProvider = false;
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(true);
		for (String providerName : providers) {
			if (providerName.equals(LocationManager.GPS_PROVIDER)
					|| providerName.equals(LocationManager.NETWORK_PROVIDER)) {
				hasActiveLocationProvider = true;
			}
		}
		return hasActiveLocationProvider;
	}
	
	public static boolean checkInternetConnection(Context context) {

		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET
		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			return true;

		} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
			// System.out.println("Not Connected");
			return false;
		}

		return false;
	}
}
