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
import model.Patient;
import model.Doctor;
import model.Filetags;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.File;

/**
 *
 * @author Charlotte
 */
public class FileJpaController implements Serializable {

    public FileJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(File file) {
        if (file.getFiletagsCollection() == null) {
            file.setFiletagsCollection(new ArrayList<Filetags>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Patient patient = file.getPatient();
            if (patient != null) {
                patient = em.getReference(patient.getClass(), patient.getId());
                file.setPatient(patient);
            }
            Doctor doctor = file.getDoctor();
            if (doctor != null) {
                doctor = em.getReference(doctor.getClass(), doctor.getId());
                file.setDoctor(doctor);
            }
            Collection<Filetags> attachedFiletagsCollection = new ArrayList<Filetags>();
            for (Filetags filetagsCollectionFiletagsToAttach : file.getFiletagsCollection()) {
                filetagsCollectionFiletagsToAttach = em.getReference(filetagsCollectionFiletagsToAttach.getClass(), filetagsCollectionFiletagsToAttach.getId());
                attachedFiletagsCollection.add(filetagsCollectionFiletagsToAttach);
            }
            file.setFiletagsCollection(attachedFiletagsCollection);
            em.persist(file);
            if (patient != null) {
                patient.getFileCollection().add(file);
                patient = em.merge(patient);
            }
            if (doctor != null) {
                doctor.getFileCollection().add(file);
                doctor = em.merge(doctor);
            }
            for (Filetags filetagsCollectionFiletags : file.getFiletagsCollection()) {
                File oldFileOfFiletagsCollectionFiletags = filetagsCollectionFiletags.getFile();
                filetagsCollectionFiletags.setFile(file);
                filetagsCollectionFiletags = em.merge(filetagsCollectionFiletags);
                if (oldFileOfFiletagsCollectionFiletags != null) {
                    oldFileOfFiletagsCollectionFiletags.getFiletagsCollection().remove(filetagsCollectionFiletags);
                    oldFileOfFiletagsCollectionFiletags = em.merge(oldFileOfFiletagsCollectionFiletags);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(File file) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            File persistentFile = em.find(File.class, file.getId());
            Patient patientOld = persistentFile.getPatient();
            Patient patientNew = file.getPatient();
            Doctor doctorOld = persistentFile.getDoctor();
            Doctor doctorNew = file.getDoctor();
            Collection<Filetags> filetagsCollectionOld = persistentFile.getFiletagsCollection();
            Collection<Filetags> filetagsCollectionNew = file.getFiletagsCollection();
            List<String> illegalOrphanMessages = null;
            for (Filetags filetagsCollectionOldFiletags : filetagsCollectionOld) {
                if (!filetagsCollectionNew.contains(filetagsCollectionOldFiletags)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Filetags " + filetagsCollectionOldFiletags + " since its file field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (patientNew != null) {
                patientNew = em.getReference(patientNew.getClass(), patientNew.getId());
                file.setPatient(patientNew);
            }
            if (doctorNew != null) {
                doctorNew = em.getReference(doctorNew.getClass(), doctorNew.getId());
                file.setDoctor(doctorNew);
            }
            Collection<Filetags> attachedFiletagsCollectionNew = new ArrayList<Filetags>();
            for (Filetags filetagsCollectionNewFiletagsToAttach : filetagsCollectionNew) {
                filetagsCollectionNewFiletagsToAttach = em.getReference(filetagsCollectionNewFiletagsToAttach.getClass(), filetagsCollectionNewFiletagsToAttach.getId());
                attachedFiletagsCollectionNew.add(filetagsCollectionNewFiletagsToAttach);
            }
            filetagsCollectionNew = attachedFiletagsCollectionNew;
            file.setFiletagsCollection(filetagsCollectionNew);
            file = em.merge(file);
            if (patientOld != null && !patientOld.equals(patientNew)) {
                patientOld.getFileCollection().remove(file);
                patientOld = em.merge(patientOld);
            }
            if (patientNew != null && !patientNew.equals(patientOld)) {
                patientNew.getFileCollection().add(file);
                patientNew = em.merge(patientNew);
            }
            if (doctorOld != null && !doctorOld.equals(doctorNew)) {
                doctorOld.getFileCollection().remove(file);
                doctorOld = em.merge(doctorOld);
            }
            if (doctorNew != null && !doctorNew.equals(doctorOld)) {
                doctorNew.getFileCollection().add(file);
                doctorNew = em.merge(doctorNew);
            }
            for (Filetags filetagsCollectionNewFiletags : filetagsCollectionNew) {
                if (!filetagsCollectionOld.contains(filetagsCollectionNewFiletags)) {
                    File oldFileOfFiletagsCollectionNewFiletags = filetagsCollectionNewFiletags.getFile();
                    filetagsCollectionNewFiletags.setFile(file);
                    filetagsCollectionNewFiletags = em.merge(filetagsCollectionNewFiletags);
                    if (oldFileOfFiletagsCollectionNewFiletags != null && !oldFileOfFiletagsCollectionNewFiletags.equals(file)) {
                        oldFileOfFiletagsCollectionNewFiletags.getFiletagsCollection().remove(filetagsCollectionNewFiletags);
                        oldFileOfFiletagsCollectionNewFiletags = em.merge(oldFileOfFiletagsCollectionNewFiletags);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = file.getId();
                if (findFile(id) == null) {
                    throw new NonexistentEntityException("The file with id " + id + " no longer exists.");
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
            File file;
            try {
                file = em.getReference(File.class, id);
                file.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The file with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Filetags> filetagsCollectionOrphanCheck = file.getFiletagsCollection();
            for (Filetags filetagsCollectionOrphanCheckFiletags : filetagsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This File (" + file + ") cannot be destroyed since the Filetags " + filetagsCollectionOrphanCheckFiletags + " in its filetagsCollection field has a non-nullable file field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Patient patient = file.getPatient();
            if (patient != null) {
                patient.getFileCollection().remove(file);
                patient = em.merge(patient);
            }
            Doctor doctor = file.getDoctor();
            if (doctor != null) {
                doctor.getFileCollection().remove(file);
                doctor = em.merge(doctor);
            }
            em.remove(file);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<File> findFileEntities() {
        return findFileEntities(true, -1, -1);
    }

    public List<File> findFileEntities(int maxResults, int firstResult) {
        return findFileEntities(false, maxResults, firstResult);
    }

    private List<File> findFileEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(File.class));
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

    public File findFile(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(File.class, id);
        } finally {
            em.close();
        }
    }

    public int getFileCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<File> rt = cq.from(File.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
