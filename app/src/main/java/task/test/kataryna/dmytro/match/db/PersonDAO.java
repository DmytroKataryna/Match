package task.test.kataryna.dmytro.match.db;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import task.test.kataryna.dmytro.match.model.Person;

/**
 * Created by dmytroKataryna on 21.01.16.
 */
public class PersonDAO {

    private RuntimeExceptionDao<Person, Integer> dao;

    public PersonDAO(RuntimeExceptionDao<Person, Integer> dao) {
        this.dao = dao;
    }

    public void savePersons(List<Person> persons) {
        for (Person p : persons) {
            savePerson(p);
        }
    }

    public void savePerson(Person p) {
        dao.createOrUpdate(p);
    }

    public Person getPersonById(Integer id) {
        QueryBuilder qb = dao.queryBuilder();
        Person person = null;
        try {
            person = (Person) qb.where().eq("id", id).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }

    public List<Person> getAllPersons() {
        return dao.queryForAll();
    }

    public void deleteAll() {
        dao.delete(getAllPersons());
    }

    public void deleteById(Integer id) {
        dao.deleteById(id);
    }

    public void deletePerson(Person person) {
        deleteById(person.getId());
    }

    public interface PersonsCallback {
        void onResult(List<Person> persons);
    }
}
