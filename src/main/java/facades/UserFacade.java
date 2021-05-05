package facades;

import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public User createUser(User user) {

        EntityManager em = emf.createEntityManager();



        System.out.println(user.toString());
        User userforPersist = new User(user.getUsername(), user.getPassword(), user.getRecoveryquestion(), user.getAnswer());




        em.getTransaction().begin();

        Role userRole = new Role("user");

        userforPersist.addRole(userRole);


        em.persist(userforPersist);


        em.getTransaction().commit();
        return userforPersist;
    }
    public User findUserByUsername(String username){
        EntityManager em = emf.createEntityManager();

        User userFound;
        try {
        em.getTransaction().begin();
        TypedQuery<User> user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        user.setParameter("username", username);
        em.getTransaction().commit();

        userFound = user.getSingleResult();}
        finally{
        em.close();
        }

        return userFound;
    }

    }

