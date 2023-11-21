import java.io.*;
import java.util.Scanner;

class CustomFile {
    private int cols = 0, rows = 0;
    public String filePath;
    public String[][] pos; //The position in the file matrix

    CustomFile(String filePath) {
        this.filePath = filePath;
        try {
            Scanner rowScanner = new Scanner(new File(filePath));
            rowScanner.useDelimiter("\n");
            String headerRow = rowScanner.nextLine();
            if (!headerRow.isEmpty())
                rows++;
            else
                throw new RuntimeException("File is empty!");
            Scanner headerScanner = new Scanner(headerRow);
            headerScanner.useDelimiter(",");
            while (headerScanner.hasNext()) {
                cols++;
                headerScanner.next();
            }
            while (rowScanner.hasNext()) {
                rows++;
                rowScanner.next();
            }
            pos = new String[rows][cols];
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found! Please check the file's path and try again.");
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error! Please try again.");
        }

        try {
            Scanner fileScanner = new Scanner(new File(filePath));
            fileScanner.useDelimiter("[,\\r]");
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++) {
                    //assert pos != null;
                    pos[i][j] = fileScanner.next();
                }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found! Please check the file's path and try again.");
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error! Please try again.");
        }
    }

    public void printFile() {
        System.out.println();
        for (int i = 0; i < rows; i++) {
            String row = "";
            for (int j = 0; j < cols; j++)
                row += pos[i][j] + ",";
            row = row.substring(0, row.length() - 1);
            System.out.print(row);
        }
        System.out.println();
    }

    public static void printCommonHeader(CustomFile file1, CustomFile file2) {
        System.out.println();
        System.out.print("Common header : ");
        for (String col1 : file1.pos[0])
            for (String col2 : file2.pos[0])
                if (col1.equals(col2))
                    System.out.print(col2 + ", ");
        System.out.println();
        System.out.println();
    }

    public static void createFiles(CustomFile file1, CustomFile file2) {

        int[][] cC = new int[Math.min(file1.cols, file2.cols)][2]; //commonCols
        int cC_index = -1;
        int[][] cR = new int[Math.min(file1.rows, file2.rows)][2]; //commonRows
        int cR_index = -1;

        try {
            //Get current path
            String userDirectoryPath = System.getProperty("user.dir");
            //Creating the common file
            File common = new File(userDirectoryPath + "\\common.csv");
            System.out.println("common.csv file's path : " + userDirectoryPath + "\\common.csv");
            //Creating the diff1 file
            File diff1 = new File(userDirectoryPath + "\\diff1.csv");
            System.out.println("diff1.csv file's path : " + userDirectoryPath + "\\diff1.csv");
            //Creating the diff2 file
            File diff2 = new File(userDirectoryPath + "\\diff2.csv");
            System.out.println("diff2.csv file's path : " + userDirectoryPath + "\\diff2.csv");
            System.out.println();
            //Setting up the writers
            FileWriter commonWriter = new FileWriter(common);
            FileWriter diff1Writer = new FileWriter(diff1);
            FileWriter diff2Writer = new FileWriter(diff2);

            //Getting the common cols
            for (int i = 0; i < file1.cols; i++)
                for (int j = 0; j < file2.cols; j++)
                    if (file1.pos[0][i].equals(file2.pos[0][j])) {
                        cC_index++;
                        cC[cC_index][0] = i;
                        cC[cC_index][1] = j;
                    }

            int isEqual, i, j;
            //Getting the common rows
            for (i = 1; i < file1.rows; i++) {
                for (j = 1; j < file2.rows; j++) {
                    isEqual = 1;
                    int k;
                    for (k = 0; k <= cC_index; k++) {
                        if (!file1.pos[i][cC[k][0]].trim().equals(file2.pos[j][cC[k][1]].trim())) {
                            isEqual = 0;
                        }
                    }
                    if (isEqual == 1) {
                        cR_index++;
                        cR[cR_index][0] = i;
                        cR[cR_index][1] = j;
                    }
                }
            }

            //Writing the common file
            System.out.print("Common cols are : ");
            for (i = 0; i <= cC_index; i++)
                System.out.print("(" + cC[i][0] + ", " + cC[i][1] + "), ");
            System.out.println();
            System.out.print("Common rows are : ");
            for (i = 0; i <= cR_index; i++)
                System.out.print("(" + cR[i][0] + ", " + cR[i][1] + "), ");
            //Printing the header
            String headerRow = "";
            //Printing from file 1
            for (i = 0; i < file1.cols; i++)
                headerRow += file1.pos[0][i] + ",";
            //Printing what isn't common from file 2
            for (i = 0; i < file2.cols; i++) {
                int isCommon = 0;
                for (j = 0; j <= cC_index; j++)
                    if (i == cC[j][1])
                        isCommon = 1;
                if (isCommon == 0)
                    headerRow += file2.pos[0][i] + ",";
            }
            headerRow = headerRow.substring(0, headerRow.length() - 1);
            commonWriter.write(headerRow);
            //Printing the body
            for (int r = 0; r <= cR_index; r++) {
                String row = "";
                //Printing from file 1
                for (i = 0; i < file1.cols; i++)
                    row += file1.pos[cR[r][0]][i] + ",";
                //Printing only what is not common from file 2
                for (i = 0; i < file2.cols; i++) {
                    int isCommon = 0;
                    for (j = 0; j <= cC_index; j++)
                        if (i == cC[j][1])
                            isCommon = 1;
                    if (isCommon == 0)
                        row += file2.pos[cR[r][1]][i] + ",";
                }
                row = row.substring(0, row.length() - 1);
                commonWriter.write(row);
            }
            commonWriter.close();

            //Writing the diff1 file
            System.out.println();
            for (i = 0; i < file1.rows; i++) {
                String row = "";
                int isCommon = 0;
                for (int r = 0; r <= cR_index; r++)
                    if (i == cR[r][0])
                        isCommon = 1;
                if (isCommon == 0) {
                    for (j = 0; j < file1.cols; j++)
                        row += file1.pos[i][j] + ",";
                    row = row.substring(0, row.length() - 1);
                    diff1Writer.write(row);
                }
            }
            diff1Writer.close();

            //Writing the diff2 file
            System.out.println();
            for (i = 0; i < file2.rows; i++) {
                String row = "";
                int isCommon = 0;
                for (int r = 0; r <= cR_index; r++)
                    if (i == cR[r][1])
                        isCommon = 1;
                if (isCommon == 0) {
                    for (j = 0; j < file2.cols; j++)
                        row += file2.pos[i][j] + ",";
                    row = row.substring(0, row.length() - 1);
                    diff2Writer.write(row);
                }
            }
            diff2Writer.close();

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error! Please try again.");
        }
    }
}

public class Main {
    public static void main(String[] args) {

        Scanner consoleReader = new Scanner(System.in);
        //Getting first file's path
        System.out.print("First file's path : ");
        String file1Path = consoleReader.nextLine();
        //Getting second file's path
        System.out.print("First file's path : ");
        String file2Path = consoleReader.nextLine();

        CustomFile file1 = new CustomFile(file1Path);
        CustomFile file2 = new CustomFile(file2Path);

        //file1.printFile();
        //file2.printFile();

        CustomFile.printCommonHeader(file1, file2);
        CustomFile.createFiles(file1, file2);
    }
}