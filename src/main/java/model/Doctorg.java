/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Charlotte
 */
@Entity
@Table(name = "doctorg")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Doctorg.findAll", query = "SELECT d FROM Doctorg d"),
    @NamedQuery(name = "Doctorg.findByIDdoctor", query = "SELECT d FROM Doctorg d WHERE d.iDdoctor = :iDdoctor"),
    @NamedQuery(name = "Doctorg.findByInami", query = "SELECT d FROM Doctorg d WHERE d.inami = :inami")})
public class Doctorg implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDdoctor")
    private Integer iDdoctor;
    @Basic(optional = false)
    @Column(name = "Inami")
    private int inami;
    @JoinColumn(name = "person", referencedColumnName = "IDperson")
    @ManyToOne(optional = false)
    private Persong person;

    public Doctorg() {
    }

    public Doctorg(Integer iDdoctor) {
        this.iDdoctor = iDdoctor;
    }

    public Doctorg(Integer iDdoctor, int inami) {
        this.iDdoctor = iDdoctor;
        this.inami = inami;
    }

    public Integer getIDdoctor() {
        return iDdoctor;
    }

    public void setIDdoctor(Integer iDdoctor) {
        this.iDdoctor = iDdoctor;
    }

    public int getInami() {
        return inami;
    }

    public void setInami(int inami) {
        this.inami = inami;
    }

    public Persong getPerson() {
        return person;
    }

    public void setPerson(Persong person) {
        this.person = person;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDdoctor != null ? iDdoctor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Doctorg)) {
            return false;
        }
        Doctorg other = (Doctorg) object;
        if ((this.iDdoctor == null && other.iDdoctor != null) || (this.iDdoctor != null && !this.iDdoctor.equals(other.iDdoctor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return person.toString();
    }

    public Object getIDperson() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
