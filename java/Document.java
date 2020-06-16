public class Document {
    private int widthPage;
    private int heightPage;
    private int column1Size;
    private int column2Size;
    private int column3Size;
    private String column1;
    private String column2;
    private String column3;

    public int getColumn1Size() {
        return column1Size;
    }

    public void setColumn1Size(int column1Size) {
        this.column1Size = column1Size;
    }

    public int getColumn2Size() {
        return column2Size;
    }

    public void setColumn2Size(int column2Size) {
        this.column2Size = column2Size;
    }

    public int getColumn3Size() {
        return column3Size;
    }

    public void setColumn3Size(int column3Size) {
        this.column3Size = column3Size;
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public int getWidthPage() {
        return widthPage;
    }

    public void setWidthPage(int widthPage) {
        this.widthPage = widthPage;
    }

    public int getHeightPage() {
        return heightPage;
    }

    public void setHeightPage(int heightPage) {
        this.heightPage = heightPage;
    }

    @Override
    public String toString() {
        return "Document{" +
                "widthPage=" + widthPage +
                ", heightPage=" + heightPage +
                ", column1Size=" + column1Size +
                ", column2Size=" + column2Size +
                ", column3Size=" + column3Size +
                ", column1='" + column1 + '\'' +
                ", column2='" + column2 + '\'' +
                ", column3='" + column3 + '\'' +
                '}';
    }
}
