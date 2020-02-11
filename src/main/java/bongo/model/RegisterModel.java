package bongo.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class RegisterModel
{
    @Email
    @NotBlank(message = "You must provide an email")
    String email;
    @NotBlank(message = "Password can't be blank")
    String password;
    @NotBlank(message = "Password confirmation can't be blank")
    String rePassword;

    public String getRePassword()
    {
        return rePassword;
    }

    public void setRePassword(String rePassword)
    {
        this.rePassword = rePassword;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}

