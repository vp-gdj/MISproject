/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Person;
import model.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Patient;

/**
 *
 * @author Charlotte
 */
public class PatientJpaController implements Serializable {

    public PatientJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Patient patient) {
        if (patient.getFileCollection() == null) {
            patient.setFileCollection(new ArrayList<File>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person person = patient.getPerson();
            if (person != null) {
                person = em.getReference(person.getClass(), person.getId());
                patient.setPerson(person);
            }
            Collection<File> attachedFileCollection = new ArrayList<File>();
            for (File fileCollectionFileToAttach : patient.getFileCollection()) {
                fileCollectionFileToAttach = em.getReference(fileCollectionFileToAttach.getClass(), fileCollectionFileToAttach.getId());
                attachedFileCollection.add(fileCollectionFileToAttach);
            }
            patient.setFileCollection(attachedFileCollection);
            em.persist(patient);
            if (person != null) {
                person.getPatientCollection().add(patient);
                person = em.merge(person);
            }
            for (File fileCollectionFile : patient.getFileCollection()) {
                Patient oldPatientOfFileCollectionFile = fileCollectionFile.getPatient();
                fileCollectionFile.setPatient(patient);
                fileCollectionFile = em.merge(fileCollectionFile);
                if (oldPatientOfFileCollectionFile != null) {
                    oldPatientOfFileCollectionFile.getFileCollection().remove(fileCollectionFile);
                    oldPatientOfFileCollectionFile = em.merge(oldPatientOfFileCollectionFile);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Patient patient) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Patient persistentPatient = em.find(Patient.class, patient.getId());
            Person personOld = persistentPatient.getPerson();
            Person personNew = patient.getPerson();
            Collection<File> fileCollectionOld = persistentPatient.getFileCollection();
            Collection<File> fileCollectionNew = patient.getFileCollection();
            List<String> illegalOrphanMessages = null;
            for (File fileCollectionOldFile : fileCollectionOld) {
                if (!fileCollectionNew.contains(fileCollectionOldFile)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain File " + fileCollectionOldFile + " since its patient field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (personNew != null) {
                personNew = em.getReference(personNew.getClass(), personNew.getId());
                patient.setPerson(personNew);
            }
            Collection<File> attachedFileCollectionNew = new ArrayList<File>();
            for (File fileCollectionNewFileToAttach : fileCollectionNew) {
                fileCollectionNewFileToAttach = em.getReference(fileCollectionNewFileToAttach.getClass(), fileCollectionNewFileToAttach.getId());
                attachedFileCollectionNew.add(fileCollectionNewFileToAttach);
            }
            fileCollectionNew = attachedFileCollectionNew;
            patient.setFileCollection(fileCollectionNew);
            patient = em.merge(patient);
            if (personOld != null && !personOld.equals(personNew)) {
                personOld.getPatientCollection().remove(patient);
                personOld = em.merge(personOld);
            }
            if (personNew != null && !personNew.equals(personOld)) {
                personNew.getPatientCollection().add(patient);
                personNew = em.merge(personNew);
            }
            for (File fileCollectionNewFile : fileCollectionNew) {
                if (!fileCollectionOld.contains(fileCollectionNewFile)) {
                    Patient oldPatientOfFileCollectionNewFile = fileCollectionNewFile.getPatient();
                    fileCollectionNewFile.setPatient(patient);
                    fileCollectionNewFile = em.merge(fileCollectionNewFile);
                    if (oldPatientOfFileCollectionNewFile != null && !oldPatientOfFileCollectionNewFile.equals(patient)) {
                        oldPatientOfFileCollectionNewFile.getFileCollection().remove(fileCollectionNewFile);
                        oldPatientOfFileCollectionNewFile = em.merge(oldPatientOfFileCollectionNewFile);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = patient.getId();
                if (findPatient(id) == null) {
                    throw new NonexistentEntityException("The patient with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Patient patient;
            try {
                patient = em.getReference(Patient.class, id);
                patient.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The patient with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<File> fileCollectionOrphanCheck = patient.getFileCollection();
            for (File fileCollectionOrphanCheckFile : fileCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Patient (" + patient + ") cannot be destroyed since the File " + fileCollectionOrphanCheckFile + " in its fileCollection field has a non-nullable patient field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Person person = patient.getPerson();
            if (person != null) {
                person.getPatientCollection().remove(patient);
                person = em.merge(person);
            }
            em.remove(patient);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Patient> findPatientEntities() {
        return findPatientEntities(true, -1, -1);
    }

    public List<Patient> findPatientEntities(int maxResults, int firstResult) {
        return findPatientEntities(false, maxResults, firstResult);
    }

    private List<Patient> findPatientEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Patient.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Patient findPatient(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Patient.class, id);
        } finally {
            em.close();
        }
    }

    public int getPatientCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Patient> rt = cq.from(Patient.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
