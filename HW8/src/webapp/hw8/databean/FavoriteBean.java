/*
 *Pinjing Zhao
 *pinjingz
 *Nov 28,2012
 *08-600 
 */
package webapp.hw8.databean;

public class FavoriteBean {
    private int favoriteId;
    private int id;
    private String url;
    private String comments;
    private int count;

    public int getFavoriteId()         { return favoriteId; }
    public int getId()                 { return id;         }
    public String getUrl()             { return url;        }
    public String getComments()         { return comments;    }
    public int getCount()              { return count;      }

    public void setFavoriteId(int i)   { favoriteId = i;    }
    public void setId(int i)           { id = i;            }
    public void setUrl(String s)       { url = s;           }
    public void setComments(String s)   { comments = s;       }
    public void setCount(int i)        { count = i;         }

}
