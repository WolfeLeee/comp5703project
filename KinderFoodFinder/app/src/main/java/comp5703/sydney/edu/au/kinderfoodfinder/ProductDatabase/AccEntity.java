package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class AccEntity {

    @Id
    private long id;
    @Index(unique = true)
    private String sid;
    private String parentId;
    private String accreditation;
    private String rating;

    @Generated(hash = 1637818586)
    public AccEntity(long id, String sid, String parentId, String accreditation, String rating) {
        this.id = id;
        this.sid = sid;
        this.parentId = parentId;
        this.accreditation = accreditation;
        this.rating = rating;
    }

    @Generated(hash = 1852916788)
    public AccEntity() {
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(String accreditation) {
        this.accreditation = accreditation;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
