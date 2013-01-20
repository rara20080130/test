/*
 *Pinjing Zhao
 *pinjingz
 *Nov 28,2012
 *08-600 
 */
package webapp.hw8;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import webapp.hw8.dao.FavoriteDAO;
import webapp.hw8.dao.MyDAOException;
import webapp.hw8.dao.UserDAO;
import webapp.hw8.databean.FavoriteBean;
import webapp.hw8.databean.UserBean;
import webapp.hw8.formbean.FavoriteForm;
import webapp.hw8.formbean.RegisterForm;
import webapp.hw8.formbean.LoginForm;

public class hw8 extends HttpServlet {
private static final long serialVersionUID = 1L;
	
	private FavoriteDAO hw8DAO;
	private UserDAO userDAO;
	
	public void init() throws ServletException {
		String jdbcDriverName = getInitParameter("jdbcDriver");
		String jdbcURL        = getInitParameter("jdbcURL");
		
		try {
			userDAO  = new UserDAO(jdbcDriverName,jdbcURL,"pinjingz_user");
			hw8DAO   = new FavoriteDAO(jdbcDriverName,jdbcURL,"pinjingz_favorite");
		} catch (MyDAOException e) {
			throw new ServletException(e);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
        	login(request,response);
        } else {
        	favorite(request,response,(UserBean)session.getAttribute("user"));
        }
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doGet(request,response);
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   		List<String> errors = new ArrayList<String>();
   		
   		LoginForm loginForm = new LoginForm(request);
   		RegisterForm registerForm = new RegisterForm(request);
    	
    	if (!loginForm.isPresent()) {
    		outputLoginPage(response,loginForm,null);
    		return;
    	}
    	
   		errors.addAll(loginForm.getValidationErrors());
       	if (errors.size() != 0) {
    		outputLoginPage(response,loginForm,errors);
    		return;
    	}

        try {
            UserBean user;

       		if (loginForm.getButton().equals("Register")) {
       			user = userDAO.lookup(loginForm.getEmail());
       			if(user != null) {
       				errors.add("Exsiting user");
       				outputLoginPage(response,loginForm,errors);
       				return;
       			}
       			else {
       				outputCompletePage(response,loginForm,registerForm,errors);
       				return;
       			}
       		} else if(loginForm.getButton().equals("Login")){
		       	user = userDAO.lookup(loginForm.getEmail());
		       	if (user == null) {
		       		errors.add("No such user");
		    		outputLoginPage(response,loginForm,errors);
		    		return;
		       	}
		       	
		       	if (!loginForm.getPassword().equals(user.getPassword())) {
		       		errors.add("Incorrect password");
		    		outputLoginPage(response,loginForm,errors);
		    		return;
		       	}
       		} else {
       			errors.addAll(registerForm.getValidationErrors());
       	       	if (errors.size() != 0) {
       	    		outputCompletePage(response,loginForm,registerForm,errors);
       	    		return;
       	    	}
       			if(registerForm.getConfirmPassword().equals(loginForm.getPassword())) {
       				user = new UserBean();
       				user.setEmail(loginForm.getEmail());
       				user.setPassword(loginForm.getPassword());
       				user.setFirstname(registerForm.getFirstname());
       				user.setLastname(registerForm.getLastname());
       				userDAO.create(user);
       				user = userDAO.lookup(user.getEmail());
       			}
       			else {
       				errors.add("Incorrect password");
       				outputCompletePage(response,loginForm,registerForm,errors);
       				return;
       			}
       		}
	    	
	       	HttpSession session = request.getSession();
	       	session.setAttribute("user",user);
	       	favorite(request,response,user);
       	} catch (MyDAOException e) {
       		errors.add(e.getMessage());
       		outputLoginPage(response,loginForm,errors);
       	}
	}


    private void favorite(HttpServletRequest request, HttpServletResponse response,UserBean user) throws ServletException, IOException {
   		List<String> errors = new ArrayList<String>();
   		
   		FavoriteForm form = new FavoriteForm(request);

    	if (!form.isPresent() && form.getFavoriteId() == null) {
    		outputList(response,form,null,user);
    		return;
    	}
    	
   		errors.addAll(form.getValidationErrors());
       	if (errors.size() != 0) {
    		outputList(response,form,errors,user);
    		return;
    	}

        try {
        	FavoriteBean bean;
            if(form.isPresent() && form.getButton().equals("Add Favorite")) {
            	bean = new FavoriteBean();
            	bean.setId(user.getId());
            	bean.setUrl(form.getUrl());
            	bean.setComments(form.getComments());
            	bean.setCount(0);
            	hw8DAO.create(bean);
            	outputList(response,form,errors,user);
            } else if(form.isPresent() && form.getButton().equals("Logout")) {
            	HttpSession session = request.getSession();
    	       	session.removeAttribute("user");
    	       	login(request,response);
            }
            else {
            	hw8DAO.updateCount(Integer.parseInt(form.getFavoriteId()));
            	outputList(response,form,errors,user);
            }

       	} catch (MyDAOException e) {
       		errors.add(e.getMessage());
       		outputList(response,form,errors,user);
       	}
	}
    
    // Methods that generate & output HTML
    
    private void generateHead(PrintWriter out) {
	    out.println("  <head>");
	    out.println("    <meta http-equiv=\"cache-control\" content=\"no-cache\">");
	    out.println("    <meta http-equiv=\"pragma\" content=\"no-cache\">");
	    out.println("    <meta http-equiv=\"expires\" content=\"0\">");
	    out.println("    <title>User Login & Register</title>");
	    out.println("  </head>");
    }
    
    private void outputLoginPage(HttpServletResponse response, LoginForm form, List<String> errors) throws IOException {
		response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	
	    out.println("<html>");
	
	    generateHead(out);
	
	    out.println("<body>");
	    out.println("<h2>Login & Register</h2>");
	    
	    if (errors != null && errors.size() > 0) {
	    	for (String error : errors) {
	        	out.println("<p style=\"font-size: large; color: red\">");
	        	out.println(error);
	        	out.println("</p>");
	    	}
	    }
	
	    // Generate an HTML <form> to get data from the user
        out.println("<form method=\"POST\">");
        out.println("    <table/>");
        out.println("        <tr>");
        out.println("            <td style=\"font-size: x-large\">E-mail Address:</td>");
        out.println("            <td>");
        out.println("                <input type=\"text\" name=\"email\"");
        if (form != null && form.getEmail() != null) {
        	out.println("                    value=\""+form.getEmail()+"\"");
        }
        out.println("                />");
        out.println("            <td>");
        out.println("        </tr>");
        out.println("        <tr>");
        out.println("            <td style=\"font-size: x-large\">Password:</td>");
        out.println("            <td><input type=\"password\" name=\"password\" /></td>");
        out.println("        </tr>");
        out.println("        <tr>");
        out.println("            <td colspan=\"2\" align=\"center\">");
        out.println("                <input type=\"submit\" name=\"button\" value=\"Login\" />");
        out.println("                <input type=\"submit\" name=\"button\" value=\"Register\" />");
        out.println("            </td>");
        out.println("        </tr>");
        out.println("    </table>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
	}
    
    private void outputCompletePage(HttpServletResponse response, LoginForm loginForm, RegisterForm registerForm, List<String> errors) throws IOException {
		response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	
	    out.println("<html>");
	
	    generateHead(out);
	
	    out.println("<body>");
	    out.println("<h2>Complete Registration</h2>");
	    
	    if (errors != null && errors.size() > 0) {
	    	for (String error : errors) {
	        	out.println("<p style=\"font-size: large; color: red\">");
	        	out.println(error);
	        	out.println("</p>");
	    	}
	    }
	
	    // Generate an HTML <form> to get data from the user
        out.println("<form method=\"POST\">");
        out.println("                <input type=\"hidden\" name=\"email\"");
        out.println("                    value=\""+loginForm.getEmail()+"\"");
        out.println("                />");
        out.println("                <input type=\"hidden\" name=\"password\"");
        out.println("                    value=\""+loginForm.getPassword()+"\"");
        out.println("                />");
        out.println("    <table/>");
        out.println("        <tr>");
        out.println("            <td style=\"font-size: x-large\">First Name:</td>");
        out.println("            <td>");
        out.println("                <input type=\"text\" name=\"firstname\"");
        if (registerForm != null && registerForm.getFirstname() != null) {
        	out.println("                    value=\""+registerForm.getFirstname()+"\"");
        }
        out.println("                />");
        out.println("            <td>");
        out.println("        </tr>");
        out.println("        <tr>");
        out.println("            <td style=\"font-size: x-large\">Last Name:</td>");
        out.println("            <td>");
        out.println("                <input type=\"text\" name=\"lastname\"");
        if (registerForm != null && registerForm.getLastname() != null) {
        	out.println("                    value=\""+registerForm.getLastname()+"\"");
        }
        out.println("                />");
        out.println("            <td>");
        out.println("        </tr>");
        out.println("        <tr>");
        out.println("            <td style=\"font-size: x-large\">Confirm Password:</td>");
        out.println("            <td><input type=\"password\" name=\"confirmPassword\" /></td>");
        out.println("        </tr>");
        out.println("        <tr>");
        out.println("            <td colspan=\"1\" align=\"center\">");
        out.println("                <input type=\"submit\" name=\"button\" value=\"Complete Registration\" />");
        out.println("            </td>");
        out.println("        </tr>");
        out.println("    </table>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
	}
  
    private void outputList(HttpServletResponse response,FavoriteForm form,List<String> messages,UserBean user) throws IOException {
    	// Get the list of items to display at the end
    	FavoriteBean[] favorite;
        try {
        	favorite = hw8DAO.getUserFavorites(user.getId());
        	
        } catch (MyDAOException e) {
        	// If there's an access error, add the message to our list of messages
        	messages.add(e.getMessage());
        	favorite = new FavoriteBean[0];
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html>");

        generateHead(out);

        out.println("<body>");
        out.println("<h2>Favorites for " + user.getFirstname() + " " + user.getLastname());
        out.println("</h2>");

        // Generate an HTML <form> to get data from the user
        out.println("<form method=\"POST\">");
        out.println("    <table>");
        out.println("        <tr><td colspan=\"3\"><hr/></td></tr>");
        out.println("        <tr>");
        out.println("            <td style=\"font-size: large\">URL:</td>");
        out.println("            <td colspan=\"2\"><input type=\"text\" size=\"40\" name=\"url\"/></td>");
        out.println("        </tr>");
        out.println("        <tr>");
        out.println("            <td style=\"font-size: large\">Comment:</td>");
        out.println("            <td colspan=\"2\"><input type=\"text\" size=\"40\" name=\"comments\"/></td>");
        out.println("        </tr>");
        out.println("        <tr>");
        out.println("            <td/>");
        out.println("            <td colspan=\"2\"><input type=\"submit\" name=\"what\" value=\"Add Favorite\"/>");
        out.println("                              <input type=\"submit\" name=\"what\" value=\"Logout\"/>");
        out.println("            </td>");
        out.println("        </tr>");
        out.println("    </table>");
        out.println("</form>");

        if(messages != null) {
        	for (String message : messages) {
            	out.println("<p style=\"font-size: large; color: red\">");
            	out.println(message);
            	out.println("</p>");
            }
        }
 
        out.println("<p style=\"font-size: x-large\">The list now has "+favorite.length+" Favorites.</p>");
        out.println("<table>");
        if(favorite != null) {
    		for(int i=0;i<favorite.length;i++) {
        		out.println("<table width=\"200\">");
        		out.println("  <tr>");
        		out.println("    <td><a href=\"HW8?favoriteId=" + favorite[i].getFavoriteId() + "\">" + favorite[i].getUrl() + "</a></td>");
        		out.println("  </tr>");
        		out.println("  <tr>");
        		out.println("    <td>" + favorite[i].getComments() + "</td>");
        		out.println("  </tr>");
        		out.println("  <tr>");
        		out.println("    <td>" + favorite[i].getCount() + " clicks</td>");
        		out.println("  </tr>");
        		out.println("</table>");
        		out.println("</br>");
    	    }
    	}

        out.println("</table>");

        out.println("</body>");
        out.println("</html>");
    }

}
