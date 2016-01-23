package task.test.kataryna.dmytro.match.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import task.test.kataryna.dmytro.match.model.Person;

/**
 * Created by dmytroKataryna on 21.01.16.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "match_app.db";
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<Person, Integer> simpleRuntimeDao = null;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static volatile DataBaseHelper instance;

    public static DataBaseHelper getInstance(Context context) {
        DataBaseHelper localInstance = instance;
        if (localInstance == null) {
            synchronized (DataBaseHelper.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DataBaseHelper(context);
                }
            }
        }
        return localInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DataBaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Person.class);
        } catch (SQLException e) {
            Log.e(DataBaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DataBaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Person.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DataBaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public RuntimeExceptionDao<Person, Integer> getSimpleDataDao() {
        if (simpleRuntimeDao == null) {
            simpleRuntimeDao = getRuntimeExceptionDao(Person.class);
        }
        return simpleRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        simpleRuntimeDao = null;
    }
}

