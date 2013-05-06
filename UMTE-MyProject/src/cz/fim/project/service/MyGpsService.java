/**
 * 
 */
package cz.fim.project.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author Roman
 *
 */
public class MyGpsService extends Service {

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	
	public static final String GPS_ACTION = "GPS_LOCATION";
    private static final String TAG = "MyProject gps";
	
    private LocationManager lm;
    
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		super.onCreate();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		lm.removeUpdates(locListener);
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, locListener);
		return START_STICKY;
	}
	
	private void publishLocation(Location location){
		if(location != null){
			Intent broadcastIntent = new Intent(GPS_ACTION);
			broadcastIntent.putExtra("lat", location.getLatitude());
			broadcastIntent.putExtra("lng", location.getLongitude());
			sendBroadcast(broadcastIntent);
		}
	}
	
	private final LocationListener locListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			publishLocation(location);
		}
	};

}
