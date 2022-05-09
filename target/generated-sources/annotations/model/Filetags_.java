package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.File;
import model.Tags;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2022-05-09T18:09:06")
@StaticMetamodel(Filetags.class)
public class Filetags_ { 

    public static volatile SingularAttribute<Filetags, File> file;
    public static volatile SingularAttribute<Filetags, Integer> id;
    public static volatile SingularAttribute<Filetags, Tags> tag;
    public static volatile SingularAttribute<Filetags, String> value;

}