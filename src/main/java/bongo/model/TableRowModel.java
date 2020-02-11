package bongo.model;

import java.util.LinkedHashMap;

public class TableRowModel
{
    LinkedHashMap<String, String> values = new LinkedHashMap<>();
    private String tableName;
    private int id;


    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public LinkedHashMap<String, String> getValues()
    {
        return values;
    }

    public void setValues(LinkedHashMap<String, String> values)
    {
        this.values = values;
    }
}
