/*
 *Pinjing Zhao
 *pinjingz
 *Nov 28,2012
 *08-600 
 */
package webapp.hw8.formbean;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class RegisterForm {
	private String firstname;
    private String lastname;
    private String confirmPassword;
    private String button;
	
    public RegisterForm(HttpServletRequest request) {
    	firstname       = request.getParameter("firstname");
    	lastname        = request.getParameter("lastname");
    	confirmPassword = request.getParameter("confirmPassword");
    	button          = request.getParameter("button");
    }
    public String getFirstname()       { return firstname; }
    public String getLastname()        { return lastname; }
    public String getConfirmPassword() { return confirmPassword; }
    public String getButton()          { return button; }
    
    public boolean isPresent()   { return button != null; }

    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<String>();

        if (firstname == null || firstname.length() == 0) errors.add("User's first name is required");
        if (lastname == null || lastname.length() == 0) errors.add("User's last name is required");
        if (confirmPassword == null || confirmPassword.length() == 0) errors.add("Comfirm password is required");
        if (button == null) errors.add("Button is required");

        if (errors.size() > 0) return errors;

        if (!button.equals("Complete Registration")) errors.add("Invalid button");
        if (firstname.matches(".*[<>\"].*")) errors.add("User's first name may not contain angle brackets or quotes");
		if (lastname.matches(".*[<>\"].*")) errors.add("User's last name may not contain angle brackets or quotes");
	
        return errors;
    }
}
