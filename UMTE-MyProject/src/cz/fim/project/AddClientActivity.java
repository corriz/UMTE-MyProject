package cz.fim.project;

import java.util.Date;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import cz.fim.project.data.Clients;
import cz.fim.project.data.DatabaseManager;
import cz.fim.project.data.MyService;
import cz.fim.projekt.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

@EActivity(R.layout.add_client)
public class AddClientActivity extends Activity {

	@ViewById(R.id.etFirstName)
	EditText firstName;

	@ViewById(R.id.etLastName)
	EditText lastName;

	@ViewById(R.id.etCity)
	EditText city;

	@ViewById(R.id.etAddress)
	EditText address;

	@ViewById(R.id.etPostalCode)
	EditText postalCode;

	@ViewById(R.id.etPhoneNumber)
	EditText phoneNumber;
	
	MyService myService;
	private int clientID = 0;
	Clients client;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	

	@Override
	protected void onStart() {
		clientID = getIntent().getIntExtra(ClientListActivity.CLIENT_ID, 0);
		if(clientID != 0){
			client = DatabaseManager.getInstance().getClientWithID(clientID);
			firstName.setText(client.getFirstname());
			lastName.setText(client.getLastname());
			city.setText(client.getCity());
			address.setText(client.getAddress());
			postalCode.setText(client.getPostalcode());
			phoneNumber.setText(client.getMyPhonenumber());
		}
		super.onStart();
	}



	@Click(R.id.btSubmitClient)
	void submitClient() {

		Intent in = getIntent();
		int mId = in.getIntExtra("ServiceKey",0);
		findMyServiceByID(mId);

		String fname = firstName.getText().toString();
		String lname = lastName.getText().toString();
		String addCity = city.getText().toString();
		Date date = new Date();
		String addAddress = address.getText().toString();
		String addPostalCode = postalCode.getText().toString();
		String addPhoneNumber = phoneNumber.getText().toString();
		Log.i("Debug", "" + addPhoneNumber);
		createClient(fname, lname, addCity, addAddress, addPostalCode, addPhoneNumber, date);
		finish();
	}

	private void findMyServiceByID(int mId) {
		if(mId != 0){
			myService = DatabaseManager.getInstance().getMyServiceWithId(mId);
		}
	}

	@Background
	void createClient(String fname, String lname, String addCity,
			String addAddress, String addPostalCode, String addPhoneNumber, Date date) {
		Log.i("App", "creating client");
		if(clientID == 0){
			Clients addNewClient = new Clients(fname,lname,addCity,addAddress,addPostalCode,addPhoneNumber,date,myService);
			DatabaseManager.getInstance().addClient(addNewClient);
		}else {
			client.setFirstname(fname);
			client.setLastname(lname);
			client.setCity(addCity);
			client.setAddress(addAddress);
			client.setPostalcode(addPostalCode);
			client.setMyPhonenumber(addPhoneNumber);
			DatabaseManager.getInstance().updateClient(client);
		}
	}
}
