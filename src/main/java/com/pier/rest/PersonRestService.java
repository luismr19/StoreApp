package com.pier.rest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pier.rest.model.Person;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PersonRestService {
    private static final List<Person> people;

    static {
    	people = new ArrayList<>();
    	people.add(new Person("Hello", "World"));
    	people.add(new Person("Foo", "Bar"));
    }

    @RequestMapping(value = "/persons", method = RequestMethod.GET)
    public static List<Person> getPersons() {
        return people;
    }

    @RequestMapping(value = "/persons/{name}", method = RequestMethod.GET)
    public static Person getPerson(@PathVariable("name") String name) {
        return people.stream()
                .filter(person -> name.equalsIgnoreCase(person.getName()))
                .findAny().orElse(null);
    }
}