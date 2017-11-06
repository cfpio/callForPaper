package io.cfp.entity;

import org.hibernate.validator.constraints.Email;

import javax.persistence.*;

@Entity
@Table(name = "humanity")
public class Humanity {

    private int id;
    private String email;

    /**
     * password if user have a local account
     */
    private String password;

    /**
     * token to verify local e-mail address, empty when e-mail verified
     */
    private String verifyToken;

    /**
     * superAdmin flag
     */
    private boolean superAdmin = false;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    @Email
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Column(name = "verify_token")
    public String getVerifyToken() {
        return verifyToken;
    }

    @Column(name = "super_admin")
    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVerifyToken(String verifyToken) {
        this.verifyToken = verifyToken;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

}
