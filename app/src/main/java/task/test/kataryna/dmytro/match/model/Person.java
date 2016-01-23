package task.test.kataryna.dmytro.match.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dmytroKataryna on 20.01.16.
 */

@DatabaseTable(tableName = "persons")
public class Person {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private String location;

    @DatabaseField
    private String status;

    @DatabaseField
    private String photo;

    public Person() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
