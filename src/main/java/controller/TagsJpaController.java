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
import model.Filetags;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Tags;

/**
 *
 * @author Charlotte
 */
public class TagsJpaController implements Serializable {

    public TagsJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tags tags) {
        if (tags.getFiletagsCollection() == null) {
            tags.setFiletagsCollection(new ArrayList<Filetags>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Filetags> attachedFiletagsCollection = new ArrayList<Filetags>();
            for (Filetags filetagsCollectionFiletagsToAttach : tags.getFiletagsCollection()) {
                filetagsCollectionFiletagsToAttach = em.getReference(filetagsCollectionFiletagsToAttach.getClass(), filetagsCollectionFiletagsToAttach.getId());
                attachedFiletagsCollection.add(filetagsCollectionFiletagsToAttach);
            }
            tags.setFiletagsCollection(attachedFiletagsCollection);
            em.persist(tags);
            for (Filetags filetagsCollectionFiletags : tags.getFiletagsCollection()) {
                Tags oldTagOfFiletagsCollectionFiletags = filetagsCollectionFiletags.getTag();
                filetagsCollectionFiletags.setTag(tags);
                filetagsCollectionFiletags = em.merge(filetagsCollectionFiletags);
                if (oldTagOfFiletagsCollectionFiletags != null) {
                    oldTagOfFiletagsCollectionFiletags.getFiletagsCollection().remove(filetagsCollectionFiletags);
                    oldTagOfFiletagsCollectionFiletags = em.merge(oldTagOfFiletagsCollectionFiletags);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tags tags) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tags persistentTags = em.find(Tags.class, tags.getId());
            Collection<Filetags> filetagsCollectionOld = persistentTags.getFiletagsCollection();
            Collection<Filetags> filetagsCollectionNew = tags.getFiletagsCollection();
            List<String> illegalOrphanMessages = null;
            for (Filetags filetagsCollectionOldFiletags : filetagsCollectionOld) {
                if (!filetagsCollectionNew.contains(filetagsCollectionOldFiletags)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Filetags " + filetagsCollectionOldFiletags + " since its tag field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Filetags> attachedFiletagsCollectionNew = new ArrayList<Filetags>();
            for (Filetags filetagsCollectionNewFiletagsToAttach : filetagsCollectionNew) {
                filetagsCollectionNewFiletagsToAttach = em.getReference(filetagsCollectionNewFiletagsToAttach.getClass(), filetagsCollectionNewFiletagsToAttach.getId());
                attachedFiletagsCollectionNew.add(filetagsCollectionNewFiletagsToAttach);
            }
            filetagsCollectionNew = attachedFiletagsCollectionNew;
            tags.setFiletagsCollection(filetagsCollectionNew);
            tags = em.merge(tags);
            for (Filetags filetagsCollectionNewFiletags : filetagsCollectionNew) {
                if (!filetagsCollectionOld.contains(filetagsCollectionNewFiletags)) {
                    Tags oldTagOfFiletagsCollectionNewFiletags = filetagsCollectionNewFiletags.getTag();
                    filetagsCollectionNewFiletags.setTag(tags);
                    filetagsCollectionNewFiletags = em.merge(filetagsCollectionNewFiletags);
                    if (oldTagOfFiletagsCollectionNewFiletags != null && !oldTagOfFiletagsCollectionNewFiletags.equals(tags)) {
                        oldTagOfFiletagsCollectionNewFiletags.getFiletagsCollection().remove(filetagsCollectionNewFiletags);
                        oldTagOfFiletagsCollectionNewFiletags = em.merge(oldTagOfFiletagsCollectionNewFiletags);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = tags.getId();
                if (findTags(id) == null) {
                    throw new NonexistentEntityException("The tags with id " + id + " no longer exists.");
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
            Tags tags;
            try {
                tags = em.getReference(Tags.class, id);
                tags.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tags with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Filetags> filetagsCollectionOrphanCheck = tags.getFiletagsCollection();
            for (Filetags filetagsCollectionOrphanCheckFiletags : filetagsCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tags (" + tags + ") cannot be destroyed since the Filetags " + filetagsCollectionOrphanCheckFiletags + " in its filetagsCollection field has a non-nullable tag field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tags);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Tags> findTagsEntities() {
        return findTagsEntities(true, -1, -1);
    }

    public List<Tags> findTagsEntities(int maxResults, int firstResult) {
        return findTagsEntities(false, maxResults, firstResult);
    }

    private List<Tags> findTagsEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tags.class));
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

    public Tags findTags(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tags.class, id);
        } finally {
            em.close();
        }
    }

    public int getTagsCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tags> rt = cq.from(Tags.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
