/*
 *Pinjing Zhao
 *pinjingz
 *Nov 28,2012
 *08-600 
 */
package webapp.hw8.dao;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import webapp.hw8.databean.FavoriteBean;

import webapp.hw8.dao.MyDAOException;
import webapp.hw8.databean.FavoriteBean;

public class FavoriteDAO {
	private List<Connection> connectionPool = new ArrayList<Connection>();

	private String jdbcDriver;
	private String jdbcURL;
	private String tableName;
	
	public FavoriteDAO(String jdbcDriver, String jdbcURL, String tableName) throws MyDAOException {
		this.jdbcDriver = jdbcDriver;
		this.jdbcURL    = jdbcURL;
		this.tableName  = tableName;
		
		if (!tableExists()) createTable();
	}
	
	private synchronized Connection getConnection() throws MyDAOException {
		if (connectionPool.size() > 0) {
			return connectionPool.remove(connectionPool.size()-1);
		}
		
        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new MyDAOException(e);
        }

        try {
            return DriverManager.getConnection(jdbcURL);
        } catch (SQLException e) {
            throw new MyDAOException(e);
        }
	}
	
	private synchronized void releaseConnection(Connection con) {
		connectionPool.add(con);
	}

	public void create(FavoriteBean favorite) throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();
        	con.setAutoCommit(false);

            PreparedStatement pstmt = con.prepareStatement(
            		"INSERT INTO " + tableName + " (id,url,comments,count) VALUES (?,?,?,?)");
        //  pstmt.setInt(1,favorite.getFavoriteId());
            pstmt.setInt(1,favorite.getId());
            pstmt.setString(2, favorite.getUrl());
            pstmt.setString(3, favorite.getComments());
            pstmt.setInt(4, favorite.getCount());
            pstmt.executeUpdate();
            pstmt.close();

            con.commit();
            con.setAutoCommit(true);
            
            releaseConnection(con);
         
    	} catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
	}
	

	public void delete(int favoriteId) throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            stmt.executeUpdate("DELETE FROM " + tableName + " WHERE favoriteId=" + favoriteId);
            stmt.close();
            releaseConnection(con);
    	} catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
	}

	public FavoriteBean[] getUserFavorites(int id) throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE id=" + id);
            
            List<FavoriteBean> list = new ArrayList<FavoriteBean>();
            while (rs.next()) {
            	FavoriteBean bean = new FavoriteBean();
            	bean.setFavoriteId(rs.getInt("favoriteId"));
            	bean.setId(rs.getInt("id"));
            	bean.setUrl(rs.getString("url"));
            	bean.setComments(rs.getString("comments"));
            	bean.setCount(rs.getInt("count"));
            	
            	list.add(bean);
            }
            stmt.close();
            releaseConnection(con);
            
            return list.toArray(new FavoriteBean[list.size()]);
    	} catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
	}
	
	public int updateCount(int favoriteId) throws MyDAOException {
		Connection con = null;
    	try {
        	con = getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count FROM " + tableName + " WHERE favoriteId = " + favoriteId);
            rs.next();
            int count = rs.getInt("count");
            ++count;
            //stmt.executeQuery("UPDATE " + tableName + "SET count = " + count + " WHERE favoriteId = " + favoriteId);
            PreparedStatement pstmt = con.prepareStatement(
            		"UPDATE " + tableName + " SET count = " + count + " WHERE favoriteId = ?");
            pstmt.setInt(1, favoriteId);
            pstmt.executeUpdate();
            pstmt.close();

            stmt.close();
            releaseConnection(con);
            
            return count;

    	} catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
		}
	}
	
	private boolean tableExists() throws MyDAOException {
		Connection con = null;
        try {
        	con = getConnection();
        	DatabaseMetaData metaData = con.getMetaData();
        	ResultSet rs = metaData.getTables(null, null, tableName, null);
        	
        	boolean answer = rs.next();
        	
        	rs.close();
        	releaseConnection(con);
        	
        	return answer;

        } catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
    }

	private void createTable() throws MyDAOException {
    	Connection con = getConnection();
    	try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate("CREATE  TABLE " + tableName + " (favoriteId INT NOT NULL AUTO_INCREMENT ,id INT NULL ," +
            		"url TEXT NULL ,comments TEXT NULL ,count INT NULL ,PRIMARY KEY (favoriteId) ,FOREIGN KEY (id )" +
            		"REFERENCES pinjingz_user(id));");
            stmt.close();
            releaseConnection(con);
        } catch (SQLException e) {
            try { if (con != null) con.close(); } catch (SQLException e2) { /* ignore */ }
        	throw new MyDAOException(e);
        }
    }
}

