package DataHandlers;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.io.FileOutputStream;
import java.io.IOException;

public class Writer {
    private XSSFWorkbook workbook1;
    private XSSFSheet sheet1;
    private FileOutputStream outputStream1;
    private Extractor extractor;

    public Writer(Extractor extractor) throws IOException {
        // import extractor
        this.extractor = extractor;

        // get path of excel sheet and instantiate output stream from path
        String path1 = ".\\Data\\MSE.xlsx";
        this.outputStream1 = new FileOutputStream(path1);

        // get sheet from Extractor
        this.workbook1 = this.extractor.workbook3;
        this.sheet1 =  workbook1.getSheet("Sheet1");
    }

    public void writeError(double[] data, int column) throws IOException {
        String path1 = ".\\Data\\MSE.xlsx";
        this.outputStream1 = new FileOutputStream(path1);
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
        workbook1.write(outputStream1);
        outputStream1.close();
    }
}
