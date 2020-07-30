// Data.aidl
package com.example.javaaidlserver;
import com.example.javaaidlserver.Person;
// Declare any non-default types here with import statements

interface Data {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int Sum(int a, int b);
    List<String> getData();
    List<Person> getPerson();
}
