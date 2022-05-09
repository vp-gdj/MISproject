package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.File;
import model.Person;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2022-05-09T18:09:06")
@StaticMetamodel(Doctor.class)
public class Doctor_ { 

    public static volatile SingularAttribute<Doctor, Integer> inami;
    public static volatile CollectionAttribute<Doctor, File> fileCollection;
    public static volatile SingularAttribute<Doctor, Person> person;
    public static volatile SingularAttribute<Doctor, Integer> id;

}