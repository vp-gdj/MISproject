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
@Table(name = "patientg")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Patientg.findAll", query = "SELECT p FROM Patientg p"),
    @NamedQuery(name = "Patientg.findByIDpatient", query = "SELECT p FROM Patientg p WHERE p.iDpatient = :iDpatient"),
    @NamedQuery(name = "Patientg.findBySocialSec", query = "SELECT p FROM Patientg p WHERE p.socialSec = :socialSec")})
public class Patientg implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDpatient")
    private Integer iDpatient;
    @Basic(optional = false)
    @Column(name = "SocialSec")
    private int socialSec;
    @JoinColumn(name = "person", referencedColumnName = "IDperson")
    @ManyToOne(optional = false)
    private Persong person;

    public Patientg() {
    }

    public Patientg(Integer iDpatient) {
        this.iDpatient = iDpatient;
    }

    public Patientg(Integer iDpatient, int socialSec) {
        this.iDpatient = iDpatient;
        this.socialSec = socialSec;
    }

    public Integer getIDpatient() {
        return iDpatient;
    }

    public void setIDpatient(Integer iDpatient) {
        this.iDpatient = iDpatient;
    }

    public int getSocialSec() {
        return socialSec;
    }

    public void setSocialSec(int socialSec) {
        this.socialSec = socialSec;
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
        hash += (iDpatient != null ? iDpatient.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Patientg)) {
            return false;
        }
        Patientg other = (Patientg) object;
        if ((this.iDpatient == null && other.iDpatient != null) || (this.iDpatient != null && !this.iDpatient.equals(other.iDpatient))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Patientg[ iDpatient=" + iDpatient + " ]";
    }
    
}
