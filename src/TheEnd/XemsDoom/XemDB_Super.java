package TheEnd.XemsDoom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Copyright (c) 2012, Luca Moser <moser.luca@gmail.com> All rights reserved.
 */
public class XemDB_Super{

    private String path;
    private String name;
    private File db;
    private XemDB_Sub xemsub;

    public XemDB_Super(String path, String name, XemDB_Sub sub, File db) {
        this.path = path;
        this.name = name;
        this.xemsub = sub;
        this.db = db;
    }

    /**
     * Adds a new entry with its index and value to the file.<br>
     * Overrides already existing entries, so the newest value gets written to
     * the file.
     * 
     * @param index
     * <br>
     *            the index of the entry
     * @param value
     * <br>
     *            the value of the entry respectively of the index
     */
    public void addEntry(String index, String value) {

        LinkedList<String> filecontent = new LinkedList<String>();
        boolean hasindex = false;
        Scanner reader = null;

        try{
            reader = new Scanner(this.db);
        }catch (IOException ex){
            ex.printStackTrace();
        }

        // Reading and eleminating duplicated indexes
        while(reader.hasNextLine()){
            String line = reader.nextLine();
            String[] li = line.split(" ");
            if(li[0].equalsIgnoreCase(index)){
                filecontent.add(index + " " + value);
                hasindex = true;
            }else{
                filecontent.add(line);
            }
        }

        reader.close();

        if(!hasindex)
            filecontent.add(index + " " + value);

        xemsub.writeFile(filecontent, this.db);

    }

    /**
     * Removes the entry with the passed index out of the database
     * 
     * @param index
     * <br>
     *            the index of the removed entry
     */
    public void removeEntry(String index) {

        LinkedList<String> filecontent = new LinkedList<String>();
        Scanner reader = null;

        try{
            reader = new Scanner(this.db);
        }catch (IOException ex){
            ex.printStackTrace();
        }

        while(reader.hasNextLine())
            filecontent.add(reader.nextLine());

        reader.close();

        if(filecontent.isEmpty())
            return;

        for(String s : filecontent){
            String[] line = s.split(" ");
            if(index.equalsIgnoreCase(line[0])){
                filecontent.remove(s);
                break;
            }
        }

        xemsub.writeFile(filecontent, this.db);
    }

    /**
     * Gets a value of an entry via index
     * 
     * @param index
     * <br>
     *            the index of the wanted value
     */
    public String getValue(String index) {

        LinkedList<String> filecontent = new LinkedList<String>();
        Scanner reader = null;

        try{
            reader = new Scanner(this.db);
        }catch (IOException ex){
            ex.printStackTrace();
        }

        while(reader.hasNextLine())
            filecontent.add(reader.nextLine());

        reader.close();

        if(filecontent.isEmpty())
            return null;

        for(String s : filecontent){
            String[] line = s.split(" ");
            if(index.equalsIgnoreCase(line[0])){
                return xemsub.buildValue(line);
            }
        }
        return null;
    }

    /**
     * Gets all values out of the database
     * 
     * @return
     * 
     */
    public ArrayList<String> getAllValues() {

        LinkedList<String> values = new LinkedList<String>();
        ArrayList<String> modified = new ArrayList<String>();
        Scanner reader = null;

        try{
            reader = new Scanner(this.db);
        }catch (IOException ex){
            ex.printStackTrace();
        }

        while(reader.hasNextLine())
            values.add(reader.nextLine());

        reader.close();

        if(values.isEmpty())
            return modified;

        for(String s : values){
            String[] line = s.split(" ");
            String modify = xemsub.buildValue(line);
            modified.add(modify);
        }
        return modified;
    }

    /**
     * Checks if the index exists in the database
     * 
     * @param index
     * <br>
     *            The index to check if it exists
     * @return true if it exists, false if not
     */
    public boolean hasIndex(String index) {

        LinkedList<String> lines = new LinkedList<String>();
        Scanner reader = null;

        try{
            reader = new Scanner(this.db);
        }catch (IOException ex){
            ex.printStackTrace();
        }

        while(reader.hasNextLine())
            lines.add(reader.nextLine());

        if(lines.isEmpty())
            return false;

        reader.close();

        for(String s : lines){
            String[] line = s.split(" ");
            if(line[0].equalsIgnoreCase(index))
                return true;
        }

        return false;
    }

    /**
     * Cleares the entire database
     */
    public void clear() {

        try{
            File file = new File(path, name + ".xem");
            if(file.exists())
                file.delete();

            file.createNewFile();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
