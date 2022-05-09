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
import model.Doctor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Patient;
import model.Person;

/**
 *
 * @author Charlotte
 */
public class PersonJpaController implements Serializable {

    public PersonJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Person person) {
        if (person.getDoctorCollection() == null) {
            person.setDoctorCollection(new ArrayList<Doctor>());
        }
        if (person.getPatientCollection() == null) {
            person.setPatientCollection(new ArrayList<Patient>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Doctor> attachedDoctorCollection = new ArrayList<Doctor>();
            for (Doctor doctorCollectionDoctorToAttach : person.getDoctorCollection()) {
                doctorCollectionDoctorToAttach = em.getReference(doctorCollectionDoctorToAttach.getClass(), doctorCollectionDoctorToAttach.getId());
                attachedDoctorCollection.add(doctorCollectionDoctorToAttach);
            }
            person.setDoctorCollection(attachedDoctorCollection);
            Collection<Patient> attachedPatientCollection = new ArrayList<Patient>();
            for (Patient patientCollectionPatientToAttach : person.getPatientCollection()) {
                patientCollectionPatientToAttach = em.getReference(patientCollectionPatientToAttach.getClass(), patientCollectionPatientToAttach.getId());
                attachedPatientCollection.add(patientCollectionPatientToAttach);
            }
            person.setPatientCollection(attachedPatientCollection);
            em.persist(person);
            for (Doctor doctorCollectionDoctor : person.getDoctorCollection()) {
                Person oldPersonOfDoctorCollectionDoctor = doctorCollectionDoctor.getPerson();
                doctorCollectionDoctor.setPerson(person);
                doctorCollectionDoctor = em.merge(doctorCollectionDoctor);
                if (oldPersonOfDoctorCollectionDoctor != null) {
                    oldPersonOfDoctorCollectionDoctor.getDoctorCollection().remove(doctorCollectionDoctor);
                    oldPersonOfDoctorCollectionDoctor = em.merge(oldPersonOfDoctorCollectionDoctor);
                }
            }
            for (Patient patientCollectionPatient : person.getPatientCollection()) {
                Person oldPersonOfPatientCollectionPatient = patientCollectionPatient.getPerson();
                patientCollectionPatient.setPerson(person);
                patientCollectionPatient = em.merge(patientCollectionPatient);
                if (oldPersonOfPatientCollectionPatient != null) {
                    oldPersonOfPatientCollectionPatient.getPatientCollection().remove(patientCollectionPatient);
                    oldPersonOfPatientCollectionPatient = em.merge(oldPersonOfPatientCollectionPatient);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Person person) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Person persistentPerson = em.find(Person.class, person.getId());
            Collection<Doctor> doctorCollectionOld = persistentPerson.getDoctorCollection();
            Collection<Doctor> doctorCollectionNew = person.getDoctorCollection();
            Collection<Patient> patientCollectionOld = persistentPerson.getPatientCollection();
            Collection<Patient> patientCollectionNew = person.getPatientCollection();
            List<String> illegalOrphanMessages = null;
            for (Doctor doctorCollectionOldDoctor : doctorCollectionOld) {
                if (!doctorCollectionNew.contains(doctorCollectionOldDoctor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Doctor " + doctorCollectionOldDoctor + " since its person field is not nullable.");
                }
            }
            for (Patient patientCollectionOldPatient : patientCollectionOld) {
                if (!patientCollectionNew.contains(patientCollectionOldPatient)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Patient " + patientCollectionOldPatient + " since its person field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Doctor> attachedDoctorCollectionNew = new ArrayList<Doctor>();
            for (Doctor doctorCollectionNewDoctorToAttach : doctorCollectionNew) {
                doctorCollectionNewDoctorToAttach = em.getReference(doctorCollectionNewDoctorToAttach.getClass(), doctorCollectionNewDoctorToAttach.getId());
                attachedDoctorCollectionNew.add(doctorCollectionNewDoctorToAttach);
            }
            doctorCollectionNew = attachedDoctorCollectionNew;
            person.setDoctorCollection(doctorCollectionNew);
            Collection<Patient> attachedPatientCollectionNew = new ArrayList<Patient>();
            for (Patient patientCollectionNewPatientToAttach : patientCollectionNew) {
                patientCollectionNewPatientToAttach = em.getReference(patientCollectionNewPatientToAttach.getClass(), patientCollectionNewPatientToAttach.getId());
                attachedPatientCollectionNew.add(patientCollectionNewPatientToAttach);
            }
            patientCollectionNew = attachedPatientCollectionNew;
            person.setPatientCollection(patientCollectionNew);
            person = em.merge(person);
            for (Doctor doctorCollectionNewDoctor : doctorCollectionNew) {
                if (!doctorCollectionOld.contains(doctorCollectionNewDoctor)) {
                    Person oldPersonOfDoctorCollectionNewDoctor = doctorCollectionNewDoctor.getPerson();
                    doctorCollectionNewDoctor.setPerson(person);
                    doctorCollectionNewDoctor = em.merge(doctorCollectionNewDoctor);
                    if (oldPersonOfDoctorCollectionNewDoctor != null && !oldPersonOfDoctorCollectionNewDoctor.equals(person)) {
                        oldPersonOfDoctorCollectionNewDoctor.getDoctorCollection().remove(doctorCollectionNewDoctor);
                        oldPersonOfDoctorCollectionNewDoctor = em.merge(oldPersonOfDoctorCollectionNewDoctor);
                    }
                }
            }
            for (Patient patientCollectionNewPatient : patientCollectionNew) {
                if (!patientCollectionOld.contains(patientCollectionNewPatient)) {
                    Person oldPersonOfPatientCollectionNewPatient = patientCollectionNewPatient.getPerson();
                    patientCollectionNewPatient.setPerson(person);
                    patientCollectionNewPatient = em.merge(patientCollectionNewPatient);
                    if (oldPersonOfPatientCollectionNewPatient != null && !oldPersonOfPatientCollectionNewPatient.equals(person)) {
                        oldPersonOfPatientCollectionNewPatient.getPatientCollection().remove(patientCollectionNewPatient);
                        oldPersonOfPatientCollectionNewPatient = em.merge(oldPersonOfPatientCollectionNewPatient);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = person.getId();
                if (findPerson(id) == null) {
                    throw new NonexistentEntityException("The person with id " + id + " no longer exists.");
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
            Person person;
            try {
                person = em.getReference(Person.class, id);
                person.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The person with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Doctor> doctorCollectionOrphanCheck = person.getDoctorCollection();
            for (Doctor doctorCollectionOrphanCheckDoctor : doctorCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Person (" + person + ") cannot be destroyed since the Doctor " + doctorCollectionOrphanCheckDoctor + " in its doctorCollection field has a non-nullable person field.");
            }
            Collection<Patient> patientCollectionOrphanCheck = person.getPatientCollection();
            for (Patient patientCollectionOrphanCheckPatient : patientCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Person (" + person + ") cannot be destroyed since the Patient " + patientCollectionOrphanCheckPatient + " in its patientCollection field has a non-nullable person field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(person);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Person> findPersonEntities() {
        return findPersonEntities(true, -1, -1);
    }

    public List<Person> findPersonEntities(int maxResults, int firstResult) {
        return findPersonEntities(false, maxResults, firstResult);
    }

    private List<Person> findPersonEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Person.class));
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

    public Person findPerson(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Person.class, id);
        } finally {
            em.close();
        }
    }

    public int getPersonCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Person> rt = cq.from(Person.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
