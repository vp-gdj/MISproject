package model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Doctor;
import model.Patient;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2022-05-09T18:09:06")
@StaticMetamodel(Person.class)
public class Person_ { 

    public static volatile SingularAttribute<Person, String> firstname;
    public static volatile SingularAttribute<Person, String> name;
    public static volatile SingularAttribute<Person, Date> dateOfBirth;
    public static volatile CollectionAttribute<Person, Doctor> doctorCollection;
    public static volatile CollectionAttribute<Person, Patient> patientCollection;
    public static volatile SingularAttribute<Person, Integer> id;

}