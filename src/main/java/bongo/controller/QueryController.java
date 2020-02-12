package bongo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import bongo.TableUtils;
import bongo.model.CustomQueryModel;
import bongo.model.SelectTableModel;
import bongo.service.DataBaseService;

import javax.servlet.http.HttpSession;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/query")
public class QueryController
{

    @Autowired
    DataBaseService dataBaseService;

    @GetMapping
    public String getQuery(Model model, HttpSession session) throws URISyntaxException, SQLException
    {
        if (session.getAttribute("email") == null)
            return "redirect:/";
        ArrayList<String> tables = dataBaseService.getAllTableNames();
        model.addAttribute("tables", tables);
        model.addAttribute("selectTableModel", new SelectTableModel());
        model.addAttribute("customQueryModel", new CustomQueryModel());
        return "query";
    }

    @PostMapping("/select")
    public String postQuery(@Validated @ModelAttribute SelectTableModel selectTableModel, BindingResult result, Model model) throws URISyntaxException, SQLException
    {
        ResultSet rs = dataBaseService.getResultSet(("SELECT * FROM " + selectTableModel.getName()));
        ArrayList<ArrayList<String>> rows = TableUtils.getRows(rs);
        ArrayList<String> columnNames = TableUtils.getColumnNames(rs);
        model.addAttribute("columnNames", columnNames);
        model.addAttribute("content", rows);
        model.addAttribute("tableName", selectTableModel.getName());
        model.addAttribute("isCustom", false);
        return "table";
    }

    @PostMapping("/custom")
    public String postCustomQuery(@Validated @ModelAttribute CustomQueryModel customQueryModel, BindingResult result, Model model, RedirectAttributes redirectAttributes) throws SQLException
    {
        if(isQueryForbidden(customQueryModel.getCustomQuery()))
            return "redirect:/query";
        if (customQueryModel.getCustomQuery().toLowerCase().contains("insert") || customQueryModel.getCustomQuery().toLowerCase().contains("update") || customQueryModel.getCustomQuery().toLowerCase().contains("drop")) {
            try {
                dataBaseService.insertUpdateQuery(customQueryModel.getCustomQuery());
                redirectAttributes.addFlashAttribute("updateSuccess", "Table update success");
                return "redirect:/query";
            } catch (SQLException e) {
                return handleErrors(model, result, e);
            }
        } else if (customQueryModel.getCustomQuery().toLowerCase().contains("create table")) {
            if (customQueryModel.getCustomQuery().toLowerCase().contains("primary key")) {
                try {
                    dataBaseService.insertUpdateQuery(customQueryModel.getCustomQuery());
                    redirectAttributes.addFlashAttribute("updateSuccess", "Table creation success");
                    return "redirect:/query";
                } catch (SQLException e) {
                    return handleErrors(model, result, e);
                }
            }
            result.addError(new ObjectError("customQuery", "Table must contain primary key"));
            model.addAttribute("selectTableModel", new SelectTableModel());
            return "query";
        } else {
            try {
                ResultSet rs = dataBaseService.getResultSet((customQueryModel.getCustomQuery()));
                ArrayList<ArrayList<String>> rows = TableUtils.getRows(rs);
                ArrayList<String> columnNames = TableUtils.getColumnNames(rs);
                model.addAttribute("columnNames", columnNames);
                model.addAttribute("content", rows);
                model.addAttribute("isCustom", true);
                return "table";

            } catch (Exception e) {
                return handleErrors(model, result, e);
            }
        }
    }

    private boolean isQueryForbidden(String query)
    {
        Pattern regex = Pattern.compile("select.*from.*users");
        Matcher matcher = regex.matcher(query);
        return matcher.matches();
    }

    String handleErrors(Model model, BindingResult result, Exception e) throws SQLException
    {
        ArrayList<String> tables = dataBaseService.getAllTableNames();
        model.addAttribute("tables", tables);
        model.addAttribute("selectTableModel", new SelectTableModel());
        result.addError(new ObjectError("customQuery", e.getMessage()));
        return "query";
    }

}
