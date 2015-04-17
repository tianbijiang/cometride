package utdallas.ridetrackers.server.datatypes.admin;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class UserData {

    private String userName;
    private String userPassword;
    private List<String> userRoles;

    public UserData() {}

    public UserData(String userName, String userPassword, List<String> userRoles) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRoles = userRoles;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }
}
