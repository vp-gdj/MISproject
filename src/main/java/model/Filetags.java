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
@Table(name = "filetags")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Filetags.findAll", query = "SELECT f FROM Filetags f"),
    @NamedQuery(name = "Filetags.findById", query = "SELECT f FROM Filetags f WHERE f.id = :id"),
    @NamedQuery(name = "Filetags.findByValue", query = "SELECT f FROM Filetags f WHERE f.value = :value")})
public class Filetags implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Value")
    private String value;
    @JoinColumn(name = "File", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private File file;
    @JoinColumn(name = "Tag", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Tags tag;

    public Filetags() {
    }

    public Filetags(Integer id) {
        this.id = id;
    }

    public Filetags(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Tags getTag() {
        return tag;
    }

    public void setTag(Tags tag) {
        this.tag = tag;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Filetags)) {
            return false;
        }
        Filetags other = (Filetags) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Filetags[ id=" + id + " ]";
    }
    
}
