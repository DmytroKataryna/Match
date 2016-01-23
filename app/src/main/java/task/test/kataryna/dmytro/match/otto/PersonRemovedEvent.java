package task.test.kataryna.dmytro.match.otto;

import task.test.kataryna.dmytro.match.model.Person;

/**
 * Created by dmytroKataryna on 21.01.16.
 */
public class PersonRemovedEvent {

    public final Person person;

    public PersonRemovedEvent(Person person) {
        this.person = person;
    }
}
