/**
 * 
 */
package cz.fim.project.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * A helper to create database
 * 
 * @author Roman Michalèík
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "MyServiseApp.sqlite";
	private static final int DATABASE_VERSION = 3;

	private Dao<MyService, Integer> myServiceDao = null;
	private Dao<Clients, Integer> clientsDao = null;

	/**
	 * Create helper
	 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
    public void onCreate(SQLiteDatabase database,ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, MyService.class);
            TableUtils.createTable(connectionSource, Clients.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        
    }

	@Override
    public void onUpgrade(SQLiteDatabase db,ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            List<String> allSql = new ArrayList<String>(); 
            switch(oldVersion) 
            {
              case 1: 
                  //allSql.add("alter table AdData add column `new_col` VARCHAR");
                  //allSql.add("alter table AdData add column `new_col2` VARCHAR");
            }
            for (String sql : allSql) {
                db.execSQL(sql);
            }
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "exception during onUpgrade", e);
            throw new RuntimeException(e);
        }
        
    }

	public Dao<MyService, Integer> getMyServiceDao() {
		if (null == myServiceDao) {
			try {
				myServiceDao = getDao(MyService.class);
			} catch (java.sql.SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return myServiceDao;
	}

	public Dao<Clients, Integer> getClientsDao() {
		if (null == clientsDao) {
			try {
				clientsDao = getDao(Clients.class);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return clientsDao;
	}

}
