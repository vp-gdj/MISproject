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
import model.Doctor;

/**
 *
 * @author Charlotte
 */
public class DoctorJpaController implements Serializable {

    public DoctorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Doctor doctor) {
        if (doctor.getFileCollection() == null) {
            doctor.setFileCollection(new ArrayList<File>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person person = doctor.getPerson();
            if (person != null) {
                person = em.getReference(person.getClass(), person.getId());
                doctor.setPerson(person);
            }
            Collection<File> attachedFileCollection = new ArrayList<File>();
            for (File fileCollectionFileToAttach : doctor.getFileCollection()) {
                fileCollectionFileToAttach = em.getReference(fileCollectionFileToAttach.getClass(), fileCollectionFileToAttach.getId());
                attachedFileCollection.add(fileCollectionFileToAttach);
            }
            doctor.setFileCollection(attachedFileCollection);
            em.persist(doctor);
            if (person != null) {
                person.getDoctorCollection().add(doctor);
                person = em.merge(person);
            }
            for (File fileCollectionFile : doctor.getFileCollection()) {
                Doctor oldDoctorOfFileCollectionFile = fileCollectionFile.getDoctor();
                fileCollectionFile.setDoctor(doctor);
                fileCollectionFile = em.merge(fileCollectionFile);
                if (oldDoctorOfFileCollectionFile != null) {
                    oldDoctorOfFileCollectionFile.getFileCollection().remove(fileCollectionFile);
                    oldDoctorOfFileCollectionFile = em.merge(oldDoctorOfFileCollectionFile);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Doctor doctor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Doctor persistentDoctor = em.find(Doctor.class, doctor.getId());
            Person personOld = persistentDoctor.getPerson();
            Person personNew = doctor.getPerson();
            Collection<File> fileCollectionOld = persistentDoctor.getFileCollection();
            Collection<File> fileCollectionNew = doctor.getFileCollection();
            List<String> illegalOrphanMessages = null;
            for (File fileCollectionOldFile : fileCollectionOld) {
                if (!fileCollectionNew.contains(fileCollectionOldFile)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain File " + fileCollectionOldFile + " since its doctor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (personNew != null) {
                personNew = em.getReference(personNew.getClass(), personNew.getId());
                doctor.setPerson(personNew);
            }
            Collection<File> attachedFileCollectionNew = new ArrayList<File>();
            for (File fileCollectionNewFileToAttach : fileCollectionNew) {
                fileCollectionNewFileToAttach = em.getReference(fileCollectionNewFileToAttach.getClass(), fileCollectionNewFileToAttach.getId());
                attachedFileCollectionNew.add(fileCollectionNewFileToAttach);
            }
            fileCollectionNew = attachedFileCollectionNew;
            doctor.setFileCollection(fileCollectionNew);
            doctor = em.merge(doctor);
            if (personOld != null && !personOld.equals(personNew)) {
                personOld.getDoctorCollection().remove(doctor);
                personOld = em.merge(personOld);
            }
            if (personNew != null && !personNew.equals(personOld)) {
                personNew.getDoctorCollection().add(doctor);
                personNew = em.merge(personNew);
            }
            for (File fileCollectionNewFile : fileCollectionNew) {
                if (!fileCollectionOld.contains(fileCollectionNewFile)) {
                    Doctor oldDoctorOfFileCollectionNewFile = fileCollectionNewFile.getDoctor();
                    fileCollectionNewFile.setDoctor(doctor);
                    fileCollectionNewFile = em.merge(fileCollectionNewFile);
                    if (oldDoctorOfFileCollectionNewFile != null && !oldDoctorOfFileCollectionNewFile.equals(doctor)) {
                        oldDoctorOfFileCollectionNewFile.getFileCollection().remove(fileCollectionNewFile);
                        oldDoctorOfFileCollectionNewFile = em.merge(oldDoctorOfFileCollectionNewFile);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = doctor.getId();
                if (findDoctor(id) == null) {
                    throw new NonexistentEntityException("The doctor with id " + id + " no longer exists.");
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
            Doctor doctor;
            try {
                doctor = em.getReference(Doctor.class, id);
                doctor.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The doctor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<File> fileCollectionOrphanCheck = doctor.getFileCollection();
            for (File fileCollectionOrphanCheckFile : fileCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Doctor (" + doctor + ") cannot be destroyed since the File " + fileCollectionOrphanCheckFile + " in its fileCollection field has a non-nullable doctor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Person person = doctor.getPerson();
            if (person != null) {
                person.getDoctorCollection().remove(doctor);
                person = em.merge(person);
            }
            em.remove(doctor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Doctor> findDoctorEntities() {
        return findDoctorEntities(true, -1, -1);
    }

    public List<Doctor> findDoctorEntities(int maxResults, int firstResult) {
        return findDoctorEntities(false, maxResults, firstResult);
    }

    private List<Doctor> findDoctorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Doctor.class));
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

    public Doctor findDoctor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Doctor.class, id);
        } finally {
            em.close();
        }
    }

    public int getDoctorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Doctor> rt = cq.from(Doctor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
