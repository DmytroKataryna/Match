package task.test.kataryna.dmytro.match.otto;

import java.util.List;

import task.test.kataryna.dmytro.match.model.Person;

/**
 * Created by dmytroKataryna on 21.01.16.
 */
public class PersonsAddedEvent {

    public final List<Person> persons;

    public PersonsAddedEvent(List<Person> persons) {
        this.persons = persons;
    }

}
