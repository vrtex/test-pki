package bongo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import bongo.TableUtils;
import bongo.model.TableRowModel;
import bongo.service.DataBaseService;

import javax.servlet.http.HttpSession;
import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

@Controller
public class TableController
{

    @Autowired
    DataBaseService dataBaseService;

    @GetMapping("query/table")
    public String viewTable(Model model, HttpSession session) throws SQLException, URISyntaxException
    {
        if (session.getAttribute("email") == null)
            return "redirect:/";
        return "table";
    }

    @GetMapping({"query/remove", "query/table/remove"})
    public String remove(Model model, @RequestParam("id") int id, @RequestParam("tableName") String tableName, RedirectAttributes redirectAttributes) throws SQLException, URISyntaxException
    {
        ResultSet rs = dataBaseService.getResultSet("SELECT * FROM " + tableName);
        for (int i = 1; ; i++) {
            if (!rs.next())
                break;
            if (id == i)
                rs.deleteRow();
        }
        rs.close();
        rs = dataBaseService.getResultSet("SELECT * FROM " + tableName);
        ArrayList<ArrayList<String>> rows = TableUtils.getRows(rs);
        ArrayList<String> columnNames = TableUtils.getColumnNames(rs);
        redirectAttributes.addFlashAttribute("columnNames", columnNames);
        redirectAttributes.addFlashAttribute("content", rows);
        redirectAttributes.addFlashAttribute("tableName", tableName);
        redirectAttributes.addFlashAttribute("isCustom", false);
        return "redirect:/query/table/";
    }

    @GetMapping({"query/edit", "query/table/edit"})
    public String edit(
            Model model,
            @RequestParam("id")
                    int id,
            @RequestParam("tableName")
                    String tableName, RedirectAttributes redirectAttributes) throws SQLException, URISyntaxException
    {


        ResultSet rs = dataBaseService.getResultSet("SELECT * FROM " + tableName);
        ArrayList<ArrayList<String>> row = TableUtils.getRows(rs);
        ArrayList<String> columnNames = TableUtils.getColumnNames(rs);
        model.addAttribute("tableName", tableName);
        TableRowModel tableRowModel = new TableRowModel();
        for (int i = 0; i < columnNames.size(); i++) {
            tableRowModel.getValues().put(columnNames.get(i), row.get(id - 1).get(i));
        }
        tableRowModel.setId(id);
        tableRowModel.setTableName(tableName);
        model.addAttribute("tableRowModel", tableRowModel);
        model.addAttribute("action", "editPost");
        rs.close();
        return "edit";
    }

    @PostMapping({"query/table/editPost", "query/editPost"})
    public String editPost(
            @ModelAttribute("tableRowModel")
                    TableRowModel tableRowModel, RedirectAttributes redirectAttributes, Model model) throws SQLException,
            URISyntaxException
    {
        ResultSet rs = dataBaseService.getResultSet("SELECT * FROM " + tableRowModel.getTableName());

        for (int i = 1; ; i++) {
            if (!rs.next())
                break;
            if (tableRowModel.getId() == i)
                break;
        }
        try {
            editAddRow(rs, tableRowModel);
            rs.updateRow();
            rs.close();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("tableRowModel", tableRowModel);
            model.addAttribute("tableName", tableRowModel.getTableName());
            return "edit";
        }
        rs = dataBaseService.getResultSet("SELECT * FROM " + tableRowModel.getTableName());
        ArrayList<ArrayList<String>> rows = TableUtils.getRows(rs);
        ArrayList<String> columnNames = TableUtils.getColumnNames(rs);
        redirectAttributes.addFlashAttribute("columnNames", columnNames);
        redirectAttributes.addFlashAttribute("content", rows);
        redirectAttributes.addFlashAttribute("tableName", tableRowModel.getTableName());
        redirectAttributes.addFlashAttribute("isCustom", false);
        return "redirect:/query/table/";
    }

    @GetMapping({"query/add", "query/table/add"})
    public String add(
            Model model,
            @RequestParam("tableName")
                    String tableName, RedirectAttributes redirectAttributes) throws SQLException, URISyntaxException
    {

        ResultSet rs = dataBaseService.getResultSet("SELECT * FROM " + tableName);
        ArrayList<String> columnNames = TableUtils.getColumnNames(rs);
        model.addAttribute("tableName", tableName);
        TableRowModel tableRowModel = new TableRowModel();
        for (int i = 0; i < columnNames.size(); i++) {
            tableRowModel.getValues().put(columnNames.get(i), null);
        }
        tableRowModel.setTableName(tableName);
        model.addAttribute("tableRowModel", tableRowModel);
        model.addAttribute("action", "addPost");
        rs.close();
        return "edit";
    }

    @PostMapping({"query/table/addPost", "query/addPost"})
    public String addPost(@ModelAttribute("tableRowModel") TableRowModel tableRowModel, RedirectAttributes redirectAttributes, Model model) throws SQLException,
            URISyntaxException
    {
        ResultSet rs = dataBaseService.getResultSet("SELECT * FROM " + tableRowModel.getTableName());
        rs.moveToInsertRow();
        try {
            editAddRow(rs, tableRowModel);
            rs.insertRow();
            rs.close();
        } catch (Exception e) {
            model.addAttribute("error", e.getLocalizedMessage());
            model.addAttribute("tableRowModel", tableRowModel);
            model.addAttribute("tableName", tableRowModel.getTableName());
            return "edit";
        }
        rs = dataBaseService.getResultSet("SELECT * FROM " + tableRowModel.getTableName());
        ArrayList<ArrayList<String>> rows = TableUtils.getRows(rs);
        ArrayList<String> columnNames = TableUtils.getColumnNames(rs);
        redirectAttributes.addFlashAttribute("columnNames", columnNames);
        redirectAttributes.addFlashAttribute("content", rows);
        redirectAttributes.addFlashAttribute("tableName", tableRowModel.getTableName());
        redirectAttributes.addFlashAttribute("isCustom", false);
        return "redirect:/query/table/";
    }

    void editAddRow(ResultSet rs, TableRowModel tableRowModel) throws SQLException
    {
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String type = metaData.getColumnClassName(i);
            String name = metaData.getColumnName(i);
            if (tableRowModel.getValues().get(name).isEmpty()) {
                rs.updateNull(name);
                continue;
            }
            if (type.equals("java.lang.String")) {
                rs.updateString(name, tableRowModel.getValues().get(name));
                continue;
            }
            if (type.equals("java.lang.Integer")) {
                rs.updateInt(name, Integer.parseInt(tableRowModel.getValues().get(name)));
                continue;
            }
            if (type.equals("java.lang.Float")) {
                rs.updateFloat(name, Float.parseFloat(tableRowModel.getValues().get(name)));
                continue;
            }
            if (type.equals("java.sql.Date")) {
                rs.updateDate(name, Date.valueOf(tableRowModel.getValues().get(name)));
                continue;
            }
        }
    }
}




