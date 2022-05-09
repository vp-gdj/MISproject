/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Charlotte
 */
@Entity
@Table(name = "persong")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Persong.findAll", query = "SELECT p FROM Persong p"),
    @NamedQuery(name = "Persong.findByIDperson", query = "SELECT p FROM Persong p WHERE p.iDperson = :iDperson"),
    @NamedQuery(name = "Persong.findByName", query = "SELECT p FROM Persong p WHERE p.name = :name"),
    @NamedQuery(name = "Persong.findByFirstname", query = "SELECT p FROM Persong p WHERE p.firstname = :firstname"),
    @NamedQuery(name = "Persong.findByDateOfBirth", query = "SELECT p FROM Persong p WHERE p.dateOfBirth = :dateOfBirth")})
public class Persong implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDperson")
    private Integer iDperson;
    @Basic(optional = false)
    @Column(name = "Name")
    private String name;
    @Basic(optional = false)
    @Column(name = "Firstname")
    private String firstname;
    @Basic(optional = false)
    @Column(name = "DateOfBirth")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfBirth;

    public Persong() {
    }

    public Persong(Integer iDperson) {
        this.iDperson = iDperson;
    }

    public Persong(Integer iDperson, String name, String firstname, Date dateOfBirth) {
        this.iDperson = iDperson;
        this.name = name;
        this.firstname = firstname;
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getIDperson() {
        return iDperson;
    }

    public void setIDperson(Integer iDperson) {
        this.iDperson = iDperson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iDperson != null ? iDperson.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Persong)) {
            return false;
        }
        Persong other = (Persong) object;
        if ((this.iDperson == null && other.iDperson != null) || (this.iDperson != null && !this.iDperson.equals(other.iDperson))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return name.toUpperCase() + " " + firstname + " (" + fmt.format(dateOfBirth) + ")";
    }
    
}
