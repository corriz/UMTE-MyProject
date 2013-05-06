/**
 * 
 */
package cz.fim.projekt;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import cz.fim.project.service.MyGpsService;
import cz.fim.projekt.R;
import cz.fim.projekt.data.Clients;
import cz.fim.projekt.data.DatabaseManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author Roman
 * 
 */
@EActivity(R.layout.client_detail)
public class ClientDetailActivity extends Activity {

	@ViewById(R.id.tvClientDetail_name)
	TextView name;

	@ViewById(R.id.tvClientDetail_address)
	TextView address;

	@ViewById(R.id.tvClientDetail_phoneNumber)
	TextView phoneNumber;

	@ViewById(R.id.tvClientDetail_gpsLat)
	TextView gpsLat;

	@ViewById(R.id.tvClientDetail_gpsLng)
	TextView gpsLng;
	

	private int clientID;
	private Clients client;
	
	double gpsLatFormat;
	double gpsLngFormat;

	static public final String EXTRA_CLIENT_OBJECT = "ExtraClientObject";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@UiThread
	void setupDetail() {
		Intent in = getIntent();
		clientID = in.getIntExtra("ClientKey", 0);
		findClientByid(clientID);
		name.setText(client.getFirstname().toString() + " "	+ client.getLastname().toString());
		address.setText(client.getAddress().toString() + "\n"+ client.getCity().toString() + "\n"+ client.getPostalcode().toString());
		phoneNumber.setText(client.getMyPhonenumber());
		
		if (client.getGpsLatitude() != 0 && client.getGpsLongitude() != 0) {
			gpsLat.setText("Lat: " + "\n" + client.getGpsLatitude());
			gpsLng.setText("Lng: " + "\n" + client.getGpsLongitude());
		}
	}

	private void findClientByid(int id) {
		if(id != 0){
			client = DatabaseManager.getInstance().getClientWithID(id);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		setupDetail();
		registerReceiver(updateReceiver, gpsFilter);
	}

	@Click(R.id.btClientDetail_OK)
	void clikOk() {
		finish();
	}

	@Click(R.id.btClientDetail_Edit)
	void clickEdit() {
		Intent intent = new Intent(ClientDetailActivity.this,AddClientActivity_.class);
		intent.putExtra(ClientListActivity.CLIENT_ID, clientID);
		startActivity(intent);
	}

	@Click(R.id.btClientDetail_getGPS)
	void clickGetGPS() {
		Intent service = new Intent(ClientDetailActivity.this,
				MyGpsService.class);
		startService(service);
		
		clientUpdate();
	}

	@Click(R.id.btClientDetail_SignIn)
	void clickSignature() {
		Intent intent = new Intent(this, SignatureActivity.class);
		intent.putExtra(EXTRA_CLIENT_OBJECT, clientID);
		startActivity(intent);
	}

	@Click(R.id.btClientDetail_call)
	void clickMakeCall() {
		Intent i = new Intent(android.content.Intent.ACTION_DIAL);
		i.setData(Uri.parse("tel:" + phoneNumber.toString()));
		startActivity(i);
	}
@Background
	void clientUpdate(){
		client.setGpsLatitude(gpsLatFormat);
		client.setGpsLongitude(gpsLngFormat);
		DatabaseManager.getInstance().updateClient(client);
		finish();
		startActivity(getIntent());
	}
	
	private BroadcastReceiver updateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context paramContext, Intent i) {
			publishLocation(i.getDoubleExtra("lat", 0),
					i.getDoubleExtra("lng", 0));
		}
	};

	private IntentFilter gpsFilter = new IntentFilter(MyGpsService.GPS_ACTION);

	/**
	 * @param location
	 */
	protected void publishLocation(double lat, double lng) {
		gpsLatFormat = lat;
		gpsLngFormat = lng;
	}

}
