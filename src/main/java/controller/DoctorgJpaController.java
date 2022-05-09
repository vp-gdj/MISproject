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
import model.Doctorg;

/**
 *
 * @author Charlotte
 */
public class DoctorgJpaController implements Serializable {

    public DoctorgJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Doctorg doctorg) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(doctorg);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Doctorg doctorg) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            doctorg = em.merge(doctorg);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = doctorg.getIDdoctor();
                if (findDoctorg(id) == null) {
                    throw new NonexistentEntityException("The doctorg with id " + id + " no longer exists.");
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
            Doctorg doctorg;
            try {
                doctorg = em.getReference(Doctorg.class, id);
                doctorg.getIDdoctor();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The doctorg with id " + id + " no longer exists.", enfe);
            }
            em.remove(doctorg);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Doctorg> findDoctorgEntities() {
        return findDoctorgEntities(true, -1, -1);
    }

    public List<Doctorg> findDoctorgEntities(int maxResults, int firstResult) {
        return findDoctorgEntities(false, maxResults, firstResult);
    }

    private List<Doctorg> findDoctorgEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Doctorg.class));
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

    public Doctorg findDoctorg(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Doctorg.class, id);
        } finally {
            em.close();
        }
    }

    public int getDoctorgCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Doctorg> rt = cq.from(Doctorg.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
