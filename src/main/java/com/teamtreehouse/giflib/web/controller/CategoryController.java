package com.teamtreehouse.giflib.web.controller;

import com.teamtreehouse.giflib.exceptions.CategoryNotEmptyException;
import com.teamtreehouse.giflib.model.Category;
import com.teamtreehouse.giflib.service.CategoryService;
import com.teamtreehouse.giflib.service.GifService;
import com.teamtreehouse.giflib.web.Color;
import com.teamtreehouse.giflib.web.FlashMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // Index of all categories
    @RequestMapping("/categories")
    public String listCategories(Model model) {

        List<Category> categories = categoryService.findAll();

        model.addAttribute("categories", categories);
        return "category/index";
    }

    // Single category page
    @RequestMapping("/categories/{categoryId}")
    public String category(@PathVariable Long categoryId, Model model) {
        // Get the category given by categoryId
        Category category = categoryService.findById(categoryId);

        model.addAttribute("category", category);
        return "category/details";
    }

    // Form for adding a new category
    @RequestMapping("categories/add")
    public String formNewCategory(Model model) {
        // TODO: Add model attributes needed for new form
        if(!model.containsAttribute("category")) {
            model.addAttribute("category", new Category());
        }
        model.addAttribute("colors", Color.values());
        model.addAttribute("action", "/categories");
        model.addAttribute("heading", "New Category");
        model.addAttribute("submit", "Add");

        return "category/form";
    }

    // Form for editing an existing category
    @RequestMapping("categories/{categoryId}/edit")
    public String formEditCategory(@PathVariable Long categoryId, Model model) {
        // TODO: Add model attributes needed for edit form
        if(!model.containsAttribute("category")) {
            model.addAttribute("category", categoryService.findById(categoryId));
        }
        model.addAttribute("colors", Color.values());
        model.addAttribute("action", String.format("/categories/%s", categoryId));
        model.addAttribute("heading", "Edit Category");
        model.addAttribute("submit", "Update");

        return "category/form";
    }

    // Update an existing category
    @RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.POST)
    public String updateCategory(@Valid Category category, BindingResult result, RedirectAttributes redirectAttributes) {
        // Update category if valid data was received
        if (result.hasErrors()) {
            // Include validations errors upon redirect
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.category", result);

            // Add the category object if invalid data was received
            // so that we can have that object available after the redirect
            // and preserve the invalid data the user has entered
            redirectAttributes.addFlashAttribute("category", category);
            // Redirect back to the form for updating a category
            return String.format("redirect:/categories/%s/edit", category.getId());
        }
        categoryService.save(category);

        // add a flash message
        redirectAttributes.addFlashAttribute(
                "flash",
                new FlashMessage("Category successfully updated", FlashMessage.Status.SUCCESS));
        // Redirect browser to /categories
        return "redirect:/categories";
    }

    // Add a category
    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public String addCategory(@Valid Category category, BindingResult result, RedirectAttributes redirectAttributes) {
        // Add category if valid data was received
        if (result.hasErrors()) {
            // Include validations errors upon redirect
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.category", result);

            // Add the category object if invalid data was received
            // so that we can have that object available after the redirect
            // and preserve the invalid data the user has entered
            redirectAttributes.addFlashAttribute("category", category);
            // Redirect back to the form for adding a category
            return "redirect:/categories/add";
        }
        categoryService.save(category);

        // add a flash message
        redirectAttributes.addFlashAttribute(
                "flash",
                new FlashMessage("Category successfully added", FlashMessage.Status.SUCCESS));
        // Redirect browser to /categories
        return "redirect:/categories";
    }

    // Delete an existing category
    @RequestMapping(value = "/categories/{categoryId}/delete", method = RequestMethod.POST)
    public String deleteCategory(@PathVariable Long categoryId, RedirectAttributes redirectAttributes) {
        Category category = categoryService.findById(categoryId);

        // Delete category if it contains no GIFs
        try {
            categoryService.delete(category);
        } catch (CategoryNotEmptyException cne) {
            redirectAttributes.addFlashAttribute(
                    "flash",
                    new FlashMessage("Only empty categories can be deleted.", FlashMessage.Status.FAILURE));
            return String.format("redirect:/categories/%s/edit", categoryId);
        }
        redirectAttributes.addFlashAttribute(
                "flash",
                new FlashMessage("Category deleted!", FlashMessage.Status.SUCCESS));

        // Redirect browser to /categories
        return "redirect:/categories";
    }
}
