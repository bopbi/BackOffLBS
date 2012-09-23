package com.kulinr.backofflbs;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;

public class LocationUtils {

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	public static String ConvertLocationToAddress(Context context,
			Location location) {
		int timeout = 10;
		StringBuffer address = new StringBuffer();
		Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 10);

			int loop = 0;
			while (loop < timeout) {
				addresses = geoCoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 10);
				loop++;
				if (addresses.size() > 0) {
					break;
				}
			}

			if (addresses.size() > 0) {
				int maxLoop = addresses.get(0).getMaxAddressLineIndex();
				for (int index = 0; index < maxLoop; index++) {
					address.append(addresses.get(0).getAddressLine(index));
					if (index != maxLoop - 1) {
						address.append(" ");
					}
				}
				if (addresses.get(0).getCountryName() != null) {
					address.append(", ");
					address.append(addresses.get(0).getCountryName());
				}
			}
		} catch (IOException e) {
			return "NearBy";

		}

		return address.toString();
	}

	public static String ConvertLocationToCityCountry(Context context,
			Location location) {
		int timeout = 10;
		StringBuffer address = new StringBuffer();
		Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 10);

			int loop = 0;
			while (loop < timeout) {
				addresses = geoCoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 10);
				loop++;
				if (addresses.size() > 0) {
					break;
				}
			}

			if (addresses.size() > 0) {
				if (addresses.get(0).getAdminArea() != null) {
					address.append(addresses.get(0).getAdminArea());
				}
				if (addresses.get(0).getCountryName() != null) {
					address.append(", ");
					address.append(addresses.get(0).getCountryName());
				}

			}
		} catch (IOException e) {
			return "Nearby";
		}

		return address.toString();
	}

	public static Criteria getBestProvider() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;

	}

	public static Criteria getCoarseProvider() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	public static boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	public static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

}
