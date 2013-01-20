/*
 *Pinjing Zhao
 *pinjingz
 *Nov 28,2012
 *08-600 
 */
package webapp.hw8.formbean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import webapp.hw8.dao.MyDAOException;

public class FavoriteForm {
	private String url;
	private String comments;
	private String favoriteId;

	private String button;
//	private int count;

	
	public FavoriteForm(HttpServletRequest request) {
		url = request.getParameter("url");
		comments = request.getParameter("comments");
		button = request.getParameter("what");
		favoriteId = request.getParameter("favoriteId");
	}
	
	public String getUrl()      { return url; }
	public String getComments() { return comments;}
	public String getFavoriteId() {
		return favoriteId;
	}
	public String getButton() {
		return button;
	}

	public boolean isPresent()  { return button != null;}
	
	public List<String> getValidationErrors() {
		List<String> errors = new ArrayList<String>();

		if(button != null && button.equals("Add Favorite")) {
			if (url == null || url.length() == 0) {
				errors.add("Url is required");
			}
			if (comments == null || comments.length() ==0 ) {
				errors.add("comments is required");
			}
		}



		return errors;
	}

}
