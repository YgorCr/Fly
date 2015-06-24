
package flygui;

public class CSVparser {
    private String[][] matriz;
    
    public CSVparser(String f){
        String[] lines = f.split("\n");
        this.matriz = new String[lines.length][lines[0].split(",").length];
        
        for (int i = 0; i < lines.length; ++i) {
            this.matriz[i] = lines[i].split(",");
        }
    }
    
    public CSVparser(int lines, int columns){
        this.matriz = new String[lines][columns];
    }
    
    public String[] getLine(int lineNumber){
        return this.matriz[lineNumber];
    }
    
    public void setLine(String[] line, int lineNumber){
        this.matriz[lineNumber] = line;
    }
    
    public String[] getColumn(int columnNumber){
        String[] result = new String[this.matriz.length];
        
        for (int i = 0; i < result.length; ++i) {
            result[i] = this.matriz[i][columnNumber];
        }
        
        return result;
    }
    
    public String getCell(int lineNumber, int columnNumber){
        return this.matriz[lineNumber][columnNumber];
    }
    
    public void setCell(int lineNumber, int columnNumber, String newValue){
        this.matriz[lineNumber][columnNumber] = newValue;
    }
    
    public int getNumberOfColumns(){
        return this.matriz[0].length;
    }
    
    public int getNumberOfLines(){
        return this.matriz.length;
    }
    
    public void addColumn(String newColumn[]) throws Exception{
        if(this.matriz.length != newColumn.length){
            throw new Exception("Unequal number of lines!!!");
        }
        
        String[][] newMatriz = new String[this.matriz.length][this.matriz[0].length+1];
        
        for (int i = 0; i < this.matriz.length; ++i) {
            int j;
            for (j = 0; j < this.matriz[0].length; ++j) {
                newMatriz[i][j] = this.matriz[i][j];
            }
            newMatriz[i][j] = newColumn[i];
        }
        
        this.matriz = newMatriz;
    }
    
    public void addLine(String[] newLine) throws Exception{
        if(this.matriz[0].length != newLine.length){
            throw new Exception("Unequal number of columns!!!");
        }
        
        String[][] newMatriz = new String[this.matriz.length+1][this.matriz[0].length];
        
        for (int i = 0; i < this.matriz.length; ++i) {
            newMatriz[i] = this.matriz[i];
        }
        
        newMatriz[newMatriz.length-1] = newLine;
        
        this.matriz = newMatriz;
    }
    
    public void insertLine(String[] newLine, int position) throws Exception{
        if(this.matriz[0].length != newLine.length){
            throw new Exception("Unequal number of columns!!!");
        }
        
        String[][] newMatriz = new String[this.matriz.length+1][this.matriz[0].length];
        
        for (int i = 0, k = 0; i < newMatriz.length; ++i) {
            if(i == position){
                newMatriz[i] = newLine;
                continue;
            }
            newMatriz[i] = this.matriz[k++];
        }
        
        this.matriz = newMatriz;
    }
    
    public void insertColumn(String[] newColumn, int position) throws Exception{
        if(this.matriz.length != newColumn.length){
            throw new Exception("Unequal number of lines!!!");
        }
        
        String[][] newMatriz = new String[this.matriz.length][this.matriz[0].length+1];
        
        for (int i = 0 ; i < newMatriz.length; ++i) {
            for (int j = 0, k = 0; j < newMatriz[0].length; j++) {
                if(j == position){
                    newMatriz[i][j] = newColumn[i];
                    continue;
                }
                newMatriz[i][j] = this.matriz[i][k++];
            }
        }
        
        this.matriz = newMatriz;
    }
    
    public void removeColumn(int columnNumber){
        String[][] newMatriz = new String[this.matriz.length][this.matriz[0].length-1];
        
        for (int i = 0; i < this.matriz.length; i++) {
            for (int j = 0, k = 0; j < this.matriz[0].length; j++) {
                if(j == columnNumber)
                    continue;
                newMatriz[i][k++] = this.matriz[i][j];
            }
        }
        
        this.matriz = newMatriz;
    }
    
    public void removeLine(int lineNumber){
        String[][] newMatriz = new String[this.matriz.length-1][this.matriz[0].length];
        
        for (int i = 0, k = 0; i < this.matriz.length; i++) {
            if(i == lineNumber)
                continue;
            newMatriz[k++] = this.matriz[i];
        }
        
        this.matriz = newMatriz;
    }
    
    public String toString(){
        String result = "";
        
        for (int i = 0; i < this.matriz.length; i++) {
            result = result + this.matriz[i][0];
            for (int j = 1; j < this.matriz[0].length; j++) {
                result = result + "," + this.matriz[i][j];
            }
            result = result + "\n";
        }
        
        return result;
    }
}
