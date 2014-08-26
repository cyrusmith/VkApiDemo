package ru.interosite.vkapidemo;

import org.joda.time.DateTime;

/**
 * Created by cyrusmith
 * All rights reserved
 * http://interosite.ru
 * info@interosite.ru
 */
public class User {

    private final String name;
    private final DateTime birthDate;

    public User(String name, DateTime birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    public DateTime getBirthDate() {
        return birthDate;
    }

    public String getName() {
        return name;
    }
}
