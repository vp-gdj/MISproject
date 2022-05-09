/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.File;
import model.Filetags;
import model.Tags;

/**
 *
 * @author Charlotte
 */
public class FiletagsJpaController implements Serializable {

    public FiletagsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Filetags filetags) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            File file = filetags.getFile();
            if (file != null) {
                file = em.getReference(file.getClass(), file.getId());
                filetags.setFile(file);
            }
            Tags tag = filetags.getTag();
            if (tag != null) {
                tag = em.getReference(tag.getClass(), tag.getId());
                filetags.setTag(tag);
            }
            em.persist(filetags);
            if (file != null) {
                file.getFiletagsCollection().add(filetags);
                file = em.merge(file);
            }
            if (tag != null) {
                tag.getFiletagsCollection().add(filetags);
                tag = em.merge(tag);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Filetags filetags) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Filetags persistentFiletags = em.find(Filetags.class, filetags.getId());
            File fileOld = persistentFiletags.getFile();
            File fileNew = filetags.getFile();
            Tags tagOld = persistentFiletags.getTag();
            Tags tagNew = filetags.getTag();
            if (fileNew != null) {
                fileNew = em.getReference(fileNew.getClass(), fileNew.getId());
                filetags.setFile(fileNew);
            }
            if (tagNew != null) {
                tagNew = em.getReference(tagNew.getClass(), tagNew.getId());
                filetags.setTag(tagNew);
            }
            filetags = em.merge(filetags);
            if (fileOld != null && !fileOld.equals(fileNew)) {
                fileOld.getFiletagsCollection().remove(filetags);
                fileOld = em.merge(fileOld);
            }
            if (fileNew != null && !fileNew.equals(fileOld)) {
                fileNew.getFiletagsCollection().add(filetags);
                fileNew = em.merge(fileNew);
            }
            if (tagOld != null && !tagOld.equals(tagNew)) {
                tagOld.getFiletagsCollection().remove(filetags);
                tagOld = em.merge(tagOld);
            }
            if (tagNew != null && !tagNew.equals(tagOld)) {
                tagNew.getFiletagsCollection().add(filetags);
                tagNew = em.merge(tagNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = filetags.getId();
                if (findFiletags(id) == null) {
                    throw new NonexistentEntityException("The filetags with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Filetags filetags;
            try {
                filetags = em.getReference(Filetags.class, id);
                filetags.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The filetags with id " + id + " no longer exists.", enfe);
            }
            File file = filetags.getFile();
            if (file != null) {
                file.getFiletagsCollection().remove(filetags);
                file = em.merge(file);
            }
            Tags tag = filetags.getTag();
            if (tag != null) {
                tag.getFiletagsCollection().remove(filetags);
                tag = em.merge(tag);
            }
            em.remove(filetags);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Filetags> findFiletagsEntities() {
        return findFiletagsEntities(true, -1, -1);
    }

    public List<Filetags> findFiletagsEntities(int maxResults, int firstResult) {
        return findFiletagsEntities(false, maxResults, firstResult);
    }

    private List<Filetags> findFiletagsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Filetags.class));
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

    public Filetags findFiletags(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Filetags.class, id);
        } finally {
            em.close();
        }
    }

    public int getFiletagsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Filetags> rt = cq.from(Filetags.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
