package entities;



public class ResetPasswordDTO {

    private String email;

    private String answerToSecurityQuestion;

    public ResetPasswordDTO(String email, String answerToSecurityQuestion) {
        this.email = email;
        this.answerToSecurityQuestion = answerToSecurityQuestion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAnswerToSecurityQuestion() {
        return answerToSecurityQuestion;
    }

    public void setAnswerToSecurityQuestion(String answerToSecurityQuestion) {
        this.answerToSecurityQuestion = answerToSecurityQuestion;
    }

    @Override
    public String toString() {
        return "ResetPasswordDTO{" +
                "email='" + email + '\'' +
                ", answerToSecurityQuestion='" + answerToSecurityQuestion + '\'' +
                '}';
    }
}
