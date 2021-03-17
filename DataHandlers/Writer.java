package DataHandlers;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Writer {
    private XSSFWorkbook workbook1;
    private XSSFWorkbook workbook2;
    private XSSFSheet sheet1;
    private XSSFSheet sheet2;
    private Extractor extractor;

    public Writer(Extractor extractor) throws IOException {
        // import extractor
        this.extractor = extractor;

        // get sheet from Extractor
        this.workbook1 = this.extractor.workbook3;
        this.sheet1 =  workbook1.getSheet("Sheet1");

        this.workbook2 = new XSSFWorkbook();
        this.sheet2 = workbook2.createSheet("Sheet1");
    }

    public void writeError(double[] data, int column) throws IOException {
        // get path of excel sheet and instantiate output stream from path
        String path1 = ".\\Data\\MSE.xlsx";
        FileOutputStream outputStream = new FileOutputStream(path1);

        // iterate over rows
        for(int i=0; i<data.length; i++){
            // create cell from given row and column
            XSSFCell cell;
            try{
                XSSFRow row = sheet1.getRow(i);
                cell = row.createCell(column);
            }
            catch(NullPointerException e){
                XSSFRow row = sheet1.createRow(i);
                cell = row.createCell(column);
            }

            // set cell value
            double val = data[i];
            cell.setCellValue(val);
        }
        // write to excel and close file
        workbook1.write(outputStream);
        outputStream.close();
    }

    public void writePrediction(double[][] data) throws IOException {
        // get path of excel sheet and instantiate output stream from path
        String path = ".\\Data\\Predictions.xlsx";
        FileOutputStream outputStream = new FileOutputStream(path);

        for(int i=0; i<data.length; i++)
        for(int j=0; j<data[i].length; j++){
            XSSFCell cell;
            try{
                cell = sheet2.getRow(i+1).createCell(j);
            }
            catch(NullPointerException e){
                cell = sheet2.createRow(i+1).createCell(j);
            }
            cell.setCellValue(data[i][j]);
        }
        workbook2.write(outputStream);
        outputStream.close();
    }
}
