package task.test.kataryna.dmytro.match.otto;


import task.test.kataryna.dmytro.match.model.Person;

/**
 * Created by dmytroKataryna on 20.01.16.
 */
public class PersonChangedEvent {

    public final Person person;

    public PersonChangedEvent(Person person) {
        this.person = person;
    }

}
