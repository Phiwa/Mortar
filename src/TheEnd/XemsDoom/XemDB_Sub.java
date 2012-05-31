package TheEnd.XemsDoom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Copyright (c) 2012, Luca Moser <moser.luca@gmail.com> All rights reserved.
 */
public class XemDB_Sub{

    private String path;
    private String name;
    private String linesp = System.getProperty("line.separator");

    public XemDB_Sub(String path, String name) {
        this.path = path;
        this.name = name;
    }

    /**
     * Creates a file and the path if it does not exist
     * 
     * @param path
     * <br>
     *            the path in which the file gets created
     * @param name
     * <br>
     *            the name of the file
     * @return the file and otherwhise null
     */
    public File createDB() {
        try{
            File di = new File(this.path);
            di.mkdirs();
            File file = new File(this.path, this.name + ".xem");
            if(file.exists())
                return file;
            else{
                file.createNewFile();
                return file;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Writes the content of the passed LinkedList to the file
     * 
     * @param filecontent
     * <br>
     *            the LinkedList with the file content
     */
    public void writeFile(LinkedList<String> filecontent, File db) {

        FileWriter writer = null;
        try{
            writer = new FileWriter(db);
        }catch (IOException ex){
            ex.printStackTrace();
        }

        int counter = 0;
        int size = filecontent.size();

        for(String s : filecontent){
            try{
                if(counter != size - 1){
                    writer.write(s + linesp);
                    counter++;
                }else
                    writer.write(s);

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        try{
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Builds the value out of the line-arraylist
     * 
     * @param line
     * <br>
     *            the line String which contains the wanted value
     * @return the value of the line
     */
    public String buildValue(String[] line) {

        StringBuilder build = new StringBuilder();

        for(int counter = 0; counter <= line.length - 1; counter++){

            if(counter != 0)
                build.append(line[counter] + " ");
        }
        return build.toString();
    }
}
