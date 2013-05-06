package cz.fim.project;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import cz.fim.project.data.DatabaseManager;
import cz.fim.project.data.MyService;
import cz.fim.projekt.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

@EActivity(R.layout.add_activity)
public class AddServiceActivity extends Activity {

	@ViewById(R.id.et_nazev)
	EditText et_nazev;
	@ViewById(R.id.et_popis)
	EditText et_popis;
	@ViewById(R.id.et_vydava)
	EditText et_vydava;

	private MyService ms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupMyService();
		setContentView(R.layout.add_activity);
	}

	private void setupMyService() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle && bundle.containsKey(MyConstants.keyMyServiceId)) {
			int myServiceId = bundle.getInt(MyConstants.keyMyServiceId);
			ms = DatabaseManager.getInstance().getMyServiceWithId(myServiceId);
			et_vydava.setText(ms.getName());
			et_popis.setText(ms.getText());
			et_vydava.setText(ms.getCorporation());
		}
	}

	@Click(R.id.submit_sluzba)
	void clickOk() {
		String name = et_nazev.getText().toString();
		String description = et_popis.getText().toString();
		String corporation = et_vydava.getText().toString();

		if ((null != name && name.length() > 0)
				&& (null != corporation && corporation.length() > 0)) {
			if (null != ms) {
				updateMyService(name,description,corporation);
			} else {
				createMyService(name,description,corporation);
			}
			finish();
		} else {
			new AlertDialog.Builder(this)
					.setTitle("Error")
					.setMessage("Invalid name or Corporation")
					.setNegativeButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
		}
	}
@Background
	void createMyService(String name, String description, String corporation) {
	Log.i("App", "creating service");
	MyService addNewService = new MyService(name, description, corporation);
	DatabaseManager.getInstance().addMyService(addNewService);
	}

@Background
	void updateMyService(String name, String description, String corporation) {
	Log.i("App", "updating service");
		MyService myServiceList = new MyService();
		if(null != ms){
			myServiceList.setName(name);
			myServiceList.setText(description);
			myServiceList.setCorporation(corporation);
			DatabaseManager.getInstance().updateMyService(ms);
		}
	}
}
