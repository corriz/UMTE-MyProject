package cz.fim.project;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

import cz.fim.project.data.DatabaseManager;
import cz.fim.project.data.MyService;
import cz.fim.projekt.R;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

	@ViewById(R.id.serviceList)
	ListView serviceView;
	
	List<MyService> myService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DatabaseManager.init(this);
		loadListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onStart() {
		super.onStart();
		loadListView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Log.i("App", "Click Add");
			Intent intent = new Intent(this, AddServiceActivity_.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.basic_context_menu, menu);
		menu.setHeaderTitle("Choose an Option...");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		MyService service = this.myService.get(info.position);
		switch (item.getItemId()) {
		case R.id.menu_edit:
			Toast.makeText(this, "Tato funkce ještì není implementována", Toast.LENGTH_LONG).show();
			break;
		case R.id.menu_delete:
			this.myService.remove(info.position);
			DatabaseManager.getInstance().removeService(service);
			loadListView();
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}

	final Activity activity = this;

	@UiThread
	void loadListView() {
		myService = DatabaseManager.getInstance()
				.getAllMyService();

		List<String> titles = new ArrayList<String>();
		for (MyService ms : myService) {
			titles.add(ms.getName());
			Log.i("Data", ms.getName().toString());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, titles);
		serviceView.setAdapter(adapter);
		registerForContextMenu(serviceView);
		serviceView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyService ms = myService.get(position);
				Intent clientListIntent = new Intent(activity,
						ClientListActivity_.class);
				clientListIntent.putExtra(MyConstants.keyMyServiceId,
						ms.getId());
				startActivity(clientListIntent);
			}
		});
	}

}