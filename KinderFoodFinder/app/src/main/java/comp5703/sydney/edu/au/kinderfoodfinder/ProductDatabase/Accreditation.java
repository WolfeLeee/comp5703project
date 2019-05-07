package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class Accreditation {

    @Id(autoincrement = true)
    private  Long id;
    @Index(unique = true)
    private  String sid;
    @Index(unique = true)
    private Long parentId;
    private  String Accreditation;
    private  String Rating;
    @Generated(hash = 1501738056)
    public Accreditation(Long id, String sid, Long parentId, String Accreditation,
            String Rating) {
        this.id = id;
        this.sid = sid;
        this.parentId = parentId;
        this.Accreditation = Accreditation;
        this.Rating = Rating;
    }
    @Generated(hash = 1290250304)
    public Accreditation() {
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAccreditation() {
        return Accreditation;
    }

    public void setAccreditation(String accreditation) {
        Accreditation = accreditation;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }


}
