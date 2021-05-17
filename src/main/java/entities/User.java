package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name = "users")
@NamedQuery(name = "User.deleteAllRows", query = "DELETE from User")
public class User implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "user_name", length = 25)
  private String username;


  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "user_pass")
  private String password;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "recovery_question")
  private String recoveryquestion;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "recovery_answer")
  private String answer;


  @JoinTable(name = "user_roles", joinColumns = {
    @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
    @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
  @ManyToMany
  private List<Role> roleList = new ArrayList<>();

  //JOIN 3 tables HERE (NOTI, STOCK, USER)

  @JoinTable(name = "user_stock_choices", joinColumns = {
          @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
          @JoinColumn(name = "stock_ticker", referencedColumnName = "stock_ticker")
  })
  @ManyToMany
  private List<Stock> stockList = new ArrayList<Stock>();

  @JoinTable(name = "user_notifications", joinColumns = {
          @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
          @JoinColumn(name = "message_id", referencedColumnName = "message_id")
  })
  @ManyToMany
  private List<Notifications> notiList = new ArrayList<Notifications>();



  public List<String> getRolesAsStrings() {
    if (roleList.isEmpty()) {
      return null;
    }
    List<String> rolesAsStrings = new ArrayList<>();
    roleList.forEach((role) -> {
        rolesAsStrings.add(role.getRoleName());
      });
    return rolesAsStrings;
  }

  public User() {}

  //TODO Change when password is hashed
   public boolean verifyPassword(String pw){
        return( BCrypt.checkpw(pw, password));
    }

  public User(String username, String password) {
    this.username = username;
    this.password = BCrypt.hashpw(password, BCrypt.gensalt());

  }

  public User( String username,String password, String recoveryquestion, String answer) {
    this.username = username;
    this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    this.recoveryquestion = recoveryquestion;
    this.answer = BCrypt.hashpw(answer, BCrypt.gensalt());
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String userName) {
    this.username = userName;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String userPass) {
    this.password = userPass;
  }

  public List<Role> getRoleList() {
    return roleList;
  }

  public void setRoleList(List<Role> roleList) {
    this.roleList = roleList;
  }

  public void addRole(Role userRole) {
    roleList.add(userRole);
  }

  public void addStock(Stock userStock) {
    stockList.add(userStock);
  }

  public void addNoti(Notifications noti){
    notiList.add(noti);
  }

  public List<Notifications> getNotiList() {
    return notiList;
  }

  public void setNotiList(List<Notifications> notiList) {
    this.notiList = notiList;
  }

  public String getRecoveryquestion() {
    return recoveryquestion;
  }

  public void setRecoveryquestion(String recoveryquestion) {
    this.recoveryquestion = recoveryquestion;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public List<Stock> getStockList() {
    return stockList;
  }

  public void setStockList(List<Stock> stockList) {
    this.stockList = stockList;
  }

  @Override
  public String toString() {
    return "User{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", recoveryquestion='" + recoveryquestion + '\'' +
            ", answer='" + answer + '\'' +

            '}';
  }
}