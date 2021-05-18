package entities;

public class UserDTO {
    private  String username;
    private  String recoveryquestion;

    public UserDTO(String username, String recoveryquestion) {
        this.username = username;
        this.recoveryquestion = recoveryquestion;
    }
}
