package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.File;
import model.Person;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2022-05-09T18:09:06")
@StaticMetamodel(Patient.class)
public class Patient_ { 

    public static volatile CollectionAttribute<Patient, File> fileCollection;
    public static volatile SingularAttribute<Patient, Person> person;
    public static volatile SingularAttribute<Patient, Integer> id;
    public static volatile SingularAttribute<Patient, Integer> socialSec;

}