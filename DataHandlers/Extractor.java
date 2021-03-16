package DataHandlers;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;

public class Extractor {
    final static int[] getTestingRows = {1, 352};
    final static int[] validationRows = {353, 470};
    final static int[] testingRows = {471, 588};
    private XSSFSheet sheet1;
    private XSSFSheet sheet2;
    public XSSFWorkbook workbook3;
    public XSSFWorkbook workbook4;

    public Extractor() throws IOException {
        // get path of excel sheets
        String path1 = "C:\\Users\\jakub\\OneDrive\\Documents\\NeuralNetworks\\Data\\Data.xlsx";
        String path2 = "C:\\Users\\jakub\\OneDrive\\Documents\\NeuralNetworks\\Data\\Random.xlsx";
        String path3 = "C:\\Users\\jakub\\OneDrive\\Documents\\NeuralNetworks\\Data\\MSE.xlsx";

        // instantiate input stream from file
        FileInputStream inputStream1 = new FileInputStream(path1);
        FileInputStream inputStream2 = new FileInputStream(path2);
        FileInputStream inputStream3 = new FileInputStream(path3);

        // instantiate workbooks from input streams
        XSSFWorkbook workbook1 = new XSSFWorkbook(inputStream1);
        XSSFWorkbook workbook2 = new XSSFWorkbook(inputStream2);
        this.workbook3 = new XSSFWorkbook(inputStream3);

        // get sheets from workbooks
        this.sheet1 = workbook1.getSheet("Sheet1");
        this.sheet2 = workbook2.getSheet("Sheet1");
    }

    /**
     * returns a 2D array of [rows][columns] from excel data
     * @param start first row to extract
     * @param end last row to extract
     * @return
     */
    public double[][] getData(int start, int end){
        // iterate over rows and columns, extracting numeric value from cells
        double[][] arr = new double[end-start+1][9];
        for(int rowIndex=start; rowIndex<=end; rowIndex++)
        for(int columnIndex=0; columnIndex<9; columnIndex++){
            double val = (double) sheet1.getRow(rowIndex).getCell(columnIndex).getNumericCellValue();
            arr[rowIndex-start][columnIndex] = val;
        }
        return arr;
    }

    /**
     * returns an array containing weights/ biases at a given layer
     * @param layer which layer of weights/ biases to extract
     * @param start first row to extract
     * @param end last row to extract
     * @return
     */
    public double[] getWeightsBiases(int layer, int start, int end){
        // iterate over rows for a given layer (column)
        double[] arr = new double[end-start+1];
        for(int rowIndex=start; rowIndex<=end; rowIndex++){
            double val = (double) sheet2.getRow(rowIndex).getCell(layer).getNumericCellValue();
            arr[rowIndex-start] = val;
        }
        return arr;
    }
}