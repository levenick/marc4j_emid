package jrl2016;

/**
 * @author levenick Jun 14, 2016 3:36:04 PM
 */

class ExtraTitle {

    private String title;
    private final String titleType;
    private final int createdBy;

    ExtraTitle(String title, String titleType, int createdBy) {
        this.title = title;
        this.titleType = titleType;
        this.createdBy = createdBy;
    }

    public String toString() {
        String returnMe="";
        
        returnMe += " title=" + title;
        returnMe += " titleType=" + titleType;
        returnMe += " createdBy=" + createdBy;
        
        return returnMe;
    }
    
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the titleType
     */
    public String getTitleType() {
        return titleType;
    }

    /**
     * @return the createdBy
     */
    public int getCreatedBy() {
        return createdBy;
    }

    void setTitle(String s) {
        title = s;
    }

}
