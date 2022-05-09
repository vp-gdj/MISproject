package model;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import model.Filetags;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2022-05-09T18:09:06")
@StaticMetamodel(Tags.class)
public class Tags_ { 

    public static volatile SingularAttribute<Tags, Integer> id;
    public static volatile CollectionAttribute<Tags, Filetags> filetagsCollection;
    public static volatile SingularAttribute<Tags, String> tagName;

}