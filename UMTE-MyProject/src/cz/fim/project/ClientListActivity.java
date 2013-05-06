/**
 * 
 */
package cz.fim.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.j256.ormlite.dao.ForeignCollection;

import cz.fim.project.data.Clients;
import cz.fim.project.data.DatabaseManager;
import cz.fim.project.data.MyService;
import cz.fim.project.R;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

/**
 * @author Roman
 * 
 */
@EActivity(R.layout.client_list)
public class ClientListActivity extends Activity {

	public static final String CLIENT_ID = "clietnID";

	int serviceId;

	MyService ms;
	List<Clients> listOfClients;

	@ViewById(R.id.client_list)
	ListView clientList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.klient_activity);
		serviceId = getIntent().getIntExtra(MyConstants.keyMyServiceId, 0);
		findServiceById(serviceId);
		Log.i("App", "" + serviceId);
	}
	
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		loadClientsFromDatabase();
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Log.i("App", "Click Add");
			Intent intent = new Intent(this, AddClientActivity_.class);
			intent.putExtra("ServiceKey", ms.getId());
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void findServiceById(int myId) {
		if (myId != 0) {
			ms = DatabaseManager.getInstance().getMyServiceWithId(myId);
		}

	}

	@UiThread
	void loadClientsFromDatabase() {
		final ForeignCollection<Clients> clients = ms.getClients();
		listOfClients = new ArrayList<Clients>(clients);
		clientList.setAdapter(new CustomClientAdapter(listOfClients,
				getApplicationContext()));
		registerForContextMenu(clientList);

	}
	

	@ItemClick(R.id.client_list)
	void clientListItemClicked(Clients client) {
		Intent intent = new Intent(this,ClientDetailActivity_.class);
		intent.putExtra("ClientKey", client.getId());
		startActivity(intent);
	}



	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Clients client = this.listOfClients.get(info.position);
		switch (item.getItemId()) {
		case R.id.menu_edit:
			Intent intent = new Intent(ClientListActivity.this,AddClientActivity_.class);
			intent.putExtra("ServiceKey", ms.getId());
			intent.putExtra(CLIENT_ID, client.getId());
			startActivity(intent);
			break;
		case R.id.menu_delete:
			this.listOfClients.remove(info.position);
			DatabaseManager.getInstance().removeClient(client);
			loadClientsFromDatabase();
			break;
		case R.id.menu_export:
			exportToJson(this.listOfClients.get(info.position));
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}


	@Background
	@SuppressWarnings({ "unchecked", "rawtypes" })
	void exportToJson(Clients c) {
		Collection collection = new ArrayList();
		collection.add(c.getFirstname());
		collection.add(c.getLastname());
		collection.add(c.getCity());
		collection.add(c.getAddress());
		collection.add(c.getPostalcode());
		collection.add(c.getMyPhonenumber());
		collection.add(c.getObrSign());
		collection.add(ms.getName());
		Gson gs = new Gson();
		String json = gs.toJson(collection);
		Log.i("JSON Export", json + "");
	}



	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.basic_context_menu, menu);
		menu.setHeaderTitle("Choose an Option....");
	}
	
	

}
