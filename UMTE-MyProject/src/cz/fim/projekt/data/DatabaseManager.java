package cz.fim.projekt.data;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.ForeignCollection;

import android.content.Context;
import android.util.Log;

public class DatabaseManager {

	static private DatabaseManager instance;

	private DatabaseHelper helper;

	public DatabaseManager(Context ctx) {
		helper = new DatabaseHelper(ctx);
	}

	static public void init(Context ctx) {
		if (null == instance) {
			instance = new DatabaseManager(ctx);
		}
	}

	public List<MyService> getAllMyService() {
		List<MyService> myService = null;
		try {
			myService = getHelper().getMyServiceDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return myService;
	}

	static public DatabaseManager getInstance() {
		return instance;
	}

	private DatabaseHelper getHelper() {
		return helper;
	}

	public MyService getMyServiceWithId(int myServiceId) {
		MyService myService = null;
		try {
			myService = getHelper().getMyServiceDao().queryForId(myServiceId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return myService;
	}

	public void addMyService(MyService addNewService) {
		try {
			getHelper().getMyServiceDao().create(addNewService);
			Log.i("DB", "Created service id:" + addNewService.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateMyService(MyService ms) {
		try {
			getHelper().getMyServiceDao().update(ms);
			Log.i("DB", "Update service id:" + ms.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addClient(Clients addNewClient) {
		try {
			getHelper().getClientsDao().create(addNewClient);
			Log.i("DB", "Create client id: " + addNewClient.getId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateClient(Clients cl){
			Log.i("DB", "Update client id: " + cl.getId() + "GPS" + cl.getGpsLongitude() + " " + cl.getGpsLatitude() + " " + cl.getMyPhonenumber());
			try {
				getHelper().getClientsDao().update(cl);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//getHelper().getClientsDao().updateId(cl, cl.getId());
		
	}

	public void removeService(MyService service) {
		try {
			ForeignCollection<Clients> clients = service.getClients();
			for (Clients cl : clients) {
				getHelper().getClientsDao().delete(cl);
			}
			getHelper().getMyServiceDao().delete(service);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Clients getClientWithID(int id) {
		Clients myClient = null;
		try {
			myClient = getHelper().getClientsDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return myClient;
	}

	public void removeClient(Clients client) {
		try {
			getHelper().getClientsDao().delete(client);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
