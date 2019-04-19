package comp5703.sydney.edu.au.kinderfoodfinder.Model;

public class Plus_code {
    private String global_code;
    private String compound_code;


    public String getCompound_code ()
    {
        return compound_code;
    }

    public void setCompound_code (String compound_code)
    {
        this.compound_code = compound_code;
    }

    public String getGlobal_code ()
    {
        return global_code;
    }

    public void setGlobal_code (String global_code)
    {
        this.global_code = global_code;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [compound_code = "+compound_code+", global_code = "+global_code+"]";
    }
}
