package comp5703.sydney.edu.au.kinderfoodfinder.Model;

public class Photos {
    private String width;

    private String[] html_attributions;

    private String height;

    private String photo_reference;

    public String getPhoto_reference ()
    {
        return photo_reference;
    }

    public void setPhoto_reference (String photo_reference)
    {
        this.photo_reference = photo_reference;
    }

    public String getWidth ()
    {
        return width;
    }

    public void setWidth (String width)
    {
        this.width = width;
    }

    public String[] getHtml_attributions ()
    {
        return html_attributions;
    }

    public void setHtml_attributions (String[] html_attributions)
    {
        this.html_attributions = html_attributions;
    }

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [photo_reference = "+photo_reference+", width = "+width+", html_attributions = "+html_attributions+", height = "+height+"]";
    }
}
