package task.test.kataryna.dmytro.match.helpers;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import task.test.kataryna.dmytro.match.model.Person;

/**
 * Created by dmytroKataryna on 21.01.16.
 */
public class Utils {

    public static LatLng getLatLng(String location) {
        String[] arr = location.split(",");
        double lat = Double.parseDouble(arr[0]);
        double lon = Double.parseDouble(arr[1]);
        return new LatLng(lat, lon);
    }

    public static Person getPersonFromJSON(String person) {
        return new Gson().fromJson(person, Person.class);
    }


    public static List<Person> getPersonsFromJSON(String persons) {
        return new Gson().fromJson(persons,
                new TypeToken<List<Person>>() {
                }.getType());
    }
}
