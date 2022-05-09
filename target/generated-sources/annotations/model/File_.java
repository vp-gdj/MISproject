package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Doctor;
import model.Filetags;
import model.Patient;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2022-05-09T18:09:06")
@StaticMetamodel(File.class)
public class File_ { 

    public static volatile SingularAttribute<File, Doctor> doctor;
    public static volatile SingularAttribute<File, String> path;
    public static volatile SingularAttribute<File, Patient> patient;
    public static volatile SingularAttribute<File, Integer> id;
    public static volatile CollectionAttribute<File, Filetags> filetagsCollection;
    public static volatile SingularAttribute<File, String> type;

}