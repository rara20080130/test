/*
 *Pinjing Zhao
 *pinjingz
 *Nov 28,2012
 *08-600 
 */
package webapp.hw8.databean;

public class UserBean {
	private int    id;
	private String email;
	private String firstname;
	private String lastname;
	private String password;
	
	public int    getId()            { return id;       }
    public String getEmail()         { return email;    }
    public String getFirstname()     { return firstname;}
    public String getLastname()      { return lastname; }
    public String getPassword()      { return password; }

	public void   setId(int i)           { id = i;          }
	public void   setEmail(String s)     { email = s;       }
	public void   setFirstname(String s) { firstname = s;   }
	public void   setLastname(String s)  { lastname = s;    }
	public void   setPassword(String s)  { password = s;    }
}
