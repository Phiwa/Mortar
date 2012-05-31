package TheEnd.XemsDoom;

import java.util.ArrayList;

/**
 * Copyright (c) 2012, Luca Moser <moser.luca@gmail.com> All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of Luca Moser nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL Luca Moser BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class XemDB{

    private XemDB_Sub xemsub;
    private XemDB_Super xemsuper;

    /**
     * Initializes a new XemDB with the given name and path.
     * 
     * @param path
     * <br>
     *            the path in which the XemDB gets created
     * @param name
     * <br>
     *            the name of the XemDB(it always ends with .xem)
     */
    public XemDB(String path, String name) {
        this.xemsub = new XemDB_Sub(path, name);
        this.xemsuper = new XemDB_Super(path, name, this.xemsub, xemsub.createDB());
    }

    public static void main(String[] args) {
        XemDB db = new XemDB("plugins", "XemDB");
        XemDB db2 = new XemDB("plugins", "XemDB2");
        
        // Clearing
        db.clear();
        db2.clear();
        System.out.println();

        // XemDB 1

        // Adding first entry
        db.addEntry("DB", "Database-1");
        // Removing the first entry
        db.removeEntry("DB");
        System.out.println("Entry with index \"DB\" is now " + db.getValue("XemsDoom"));

        // Adding second entry
        db.addEntry("DB1-Entry", "DB1-Value1");
        System.out.println(db.getValue("DB1-Entry"));

        // Adding third entry
        db.addEntry("DB1-Entry2", "DB1-Value2");
        System.out.println(db.getValue("DB1-Entry2"));

        // Overriding existing third entry
        db.addEntry("DB1-Entry2", "DB1-Value2-Overrided");
        System.out.println(db.getValue("DB1-Entry2"));

        if(db.hasIndex("DB1-Entry2"))
            System.out.println("True");

        if(!db.hasIndex("DB1-Entry3"))
            System.out.println("false");

        // Getting all values of the db and print them out
        System.out.println(db.getAllValues().toString());

        // XemDB 2
        System.out.println();

        // Adding first entry
        db2.addEntry("DB2", "Database-2");
        // Removing the first entry
        db2.removeEntry("DB2");
        System.out.println("Entry with index \"DB2\" is now " + db2.getValue("DB2"));

        // Adding second entry
        db2.addEntry("DB2-Entry", "DB2-Value1");
        System.out.println(db2.getValue("DB2-Entry"));

        // Adding third entry
        db2.addEntry("DB2-Entry2", "DB2-Value2");
        System.out.println(db2.getValue("DB2-Entry2"));

        // Overriding existing value of third entry
        db2.addEntry("DB2-Entry2", "DB2-Value2-Overrided");
        System.out.println(db2.getValue("DB2-Entry2"));

        if(db2.hasIndex("DB2-Entry2"))
            System.out.println("True");

        if(!db2.hasIndex("DB1-Entry3"))
            System.out.println("false");

        // Getting all values of the db2 and print them out
        System.out.println(db2.getAllValues().toString());

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
        this.xemsuper.addEntry(index, value);
    }

    /**
     * Removes the entry with the passed index out of the database
     * 
     * @param index
     * <br>
     *            the index of the removed entry
     */
    public void removeEntry(String index) {
        this.xemsuper.removeEntry(index);
    }

    /**
     * Gets a value of an entry via index
     * 
     * @param index
     * <br>
     *            the index of the wanted value
     */
    public String getValue(String index) {
        return this.xemsuper.getValue(index);
    }

    /**
     * Gets all values out of the database
     * 
     * @return
     * 
     */
    public ArrayList<String> getAllValues() {
        return this.xemsuper.getAllValues();
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
        return this.xemsuper.hasIndex(index);
    }

    /**
     * Cleares the entire database
     */
    public void clear() {
        this.xemsuper.clear();
    }
}
