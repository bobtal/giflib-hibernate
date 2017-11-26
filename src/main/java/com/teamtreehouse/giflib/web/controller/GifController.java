package com.teamtreehouse.giflib.web.controller;

import com.teamtreehouse.giflib.model.Gif;
import com.teamtreehouse.giflib.service.CategoryService;
import com.teamtreehouse.giflib.service.GifService;
import com.teamtreehouse.giflib.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GifController {
    @Autowired
    private GifService gifService;

    @Autowired
    private CategoryService categoryService;

    // Home page - index of all GIFs
    @RequestMapping("/")
    public String listGifs(Model model) {
        // Get all gifs
        List<Gif> gifs = gifService.findAll();

        model.addAttribute("gifs", gifs);
        return "gif/index";
    }

    // Single GIF page
    @RequestMapping("/gifs/{gifId}")
    public String gifDetails(@PathVariable Long gifId, Model model) {
        // Get gif whose id is gifId
        Gif gif = gifService.findById(gifId);

        model.addAttribute("gif", gif);
        return "gif/details";
    }

    // GIF image data
    @RequestMapping("/gifs/{gifId}.gif")
    @ResponseBody
    public byte[] gifImage(@PathVariable Long gifId) {
        // TODO: Return image data as byte array of the GIF whose id is gifId
        return gifService.findById(gifId).getBytes();
    }

    // Favorites - index of all GIFs marked favorite
    @RequestMapping("/favorites")
    public String favorites(Model model) {
        // TODO: Get list of all GIFs marked as favorite
        List<Gif> faves = new ArrayList<>();

        model.addAttribute("gifs",faves);
        model.addAttribute("username","Chris Ramacciotti"); // Static username
        return "gif/favorites";
    }

    // Upload a new GIF
    @RequestMapping(value = "/gifs", method = RequestMethod.POST)
    public String addGif(@Valid Gif gif, BindingResult result, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        // TODO: Upload new GIF if data is valid
        if (result.hasErrors()) {
            // Include validations errors upon redirect
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.gif", result);

            // Add the gif object if invalid data was received
            // so that we can have that object available after the redirect
            // and preserve the invalid data the user has entered
            // the attribute name should match the attribute name above ...BindingResult.gif <- this "gif" part
            redirectAttributes.addFlashAttribute("gif", gif);
            // Redirect back to the form for adding new gifs
            return "redirect:/upload";
        }

        gifService.save(gif, file);

        // Add a flash message for success
        redirectAttributes.addFlashAttribute(
                "flash",
                new FlashMessage("GIF successfully uploaded", FlashMessage.Status.SUCCESS));

        // TODO: Redirect browser to new GIF's detail view
        return String.format("redirect:/gifs/%s", gif.getId());
    }

    // Form for uploading a new GIF
    @RequestMapping("/upload")
    public String formNewGif(Model model) {
        // TODO: Add model attributes needed for new GIF upload form
        if (!model.containsAttribute("gif")){
            model.addAttribute("gif", new Gif());
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("action", "/gifs");
        model.addAttribute("heading", "Upload");
        model.addAttribute("submit", "Upload");

        return "gif/form";
    }

    // Form for editing an existing GIF
    @RequestMapping(value = "/gifs/{gifId}/edit")
    public String formEditGif(@PathVariable Long gifId, Model model) {
        // TODO: Add model attributes needed for edit form
        if (!model.containsAttribute("gif")) {
            model.addAttribute("gif", gifService.findById(gifId));
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("action", String.format("/gifs/%s", gifId));
        model.addAttribute("heading", "Edit");
        model.addAttribute("submit", "Update");

        return "gif/form";
    }

    // Update an existing GIF
    @RequestMapping(value = "/gifs/{gifId}", method = RequestMethod.POST)
    public String updateGif(@Valid Gif gif, BindingResult result, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        // Update GIF if data is valid
        if (result.hasErrors()) {
            // Include validations errors upon redirect
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.gif", result);

            // Add the gif object if invalid data was received
            // so that we can have that object available after the redirect
            // and preserve the invalid data the user has entered
            // the attribute name should match the attribute name above ...BindingResult.gif <- this "gif" part
            redirectAttributes.addFlashAttribute("gif", gif);
            // Redirect back to the form for updating gifs
            return String.format("redirect:/gifs/%s/edit", gif.getId());
        }
        gifService.save(gif, file);

        // Add a flash message for success
        redirectAttributes.addFlashAttribute(
                "flash",
                new FlashMessage("GIF successfully updated", FlashMessage.Status.SUCCESS));
        // TODO: Redirect browser to updated GIF's detail view
        return String.format("redirect:/gifs/%s", gif.getId());
    }

    // Delete an existing gif
    @RequestMapping(value = "/gifs/{gifId}/delete", method = RequestMethod.POST)
    public String deleteGif(@PathVariable Long gifId, RedirectAttributes redirectAttributes) {
        // Delete the GIF whose id is gifId
        Gif gif = gifService.findById(gifId);
        gifService.delete(gif);
        redirectAttributes.addFlashAttribute(
                "flash",
                new FlashMessage("Gif successfully deleted.", FlashMessage.Status.SUCCESS));

        // Redirect to app root
        return "redirect:/";
    }

    // Mark/unmark an existing GIF as a favorite
    @RequestMapping(value = "gifs/{gifId}/favorite", method = RequestMethod.POST)
    public String toggleFavorite(@PathVariable Long gifId, HttpServletRequest request) {
        // With GIF whose id is gifId, toggle the favorite field
        Gif gif = gifService.findById(gifId);
        gifService.toggleFavorite(gif);

        // Redirect to wherever the request came from using the referer header from the request
        return String.format("redirect:%s", request.getHeader("referer"));
        // WARNING: client specified values can be dangerous. The client browser might be compromised
        // so that malicious software could set the referer to a malicious site and this web app
        // could then redirect to that malicious site when using the referer header
    }

    // Search results
    @RequestMapping("/search")
    public String searchResults(@RequestParam String q, Model model) {
        // TODO: Get list of GIFs whose description contains value specified by q
        List<Gif> gifs = new ArrayList<>();

        model.addAttribute("gifs",gifs);
        return "gif/index";
    }

}