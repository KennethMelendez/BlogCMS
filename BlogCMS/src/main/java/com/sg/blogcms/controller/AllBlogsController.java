/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.blogcms.controller;

import com.sg.blogcms.model.Category;
import com.sg.blogcms.model.Entity;
import com.sg.blogcms.model.Posts;
import com.sg.blogcms.model.StaticPages;
import com.sg.blogcms.model.postsTags;
import com.sg.blogcms.service.CategoriesService;
import com.sg.blogcms.service.EntityService;
import com.sg.blogcms.service.PostsService;
import com.sg.blogcms.service.StaticPagesService;
import com.sg.blogcms.service.TagsService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author kmlnd
 */
@Controller
public class AllBlogsController {
    
    PostsService postsService;
    CategoriesService serviceCat;
    EntityService EntityServiceDao;
    StaticPagesService servicePage;
    TagsService serviceTag;
    
    @Inject
    public AllBlogsController(PostsService postsService, CategoriesService serviceCat,
            EntityService EntityServiceDao, StaticPagesService servicePage, TagsService serviceTag) {
        this.postsService = postsService;
        this.serviceCat = serviceCat;
        this.EntityServiceDao = EntityServiceDao;
        this.servicePage = servicePage;
        this.serviceTag = serviceTag;
    }
    
    @RequestMapping(value = "/allBlogs", method = RequestMethod.GET)
    public String allBlogs(Model model) {
        List<Category> categories = serviceCat.getAllCategories();
        List<Posts> posts = postsService.getAllPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("categories", categories);

        // get a list from the service with all pages
        List<StaticPages> pages = servicePage.getAllStaticPages();
        // add it to the model
        model.addAttribute("pagesList", pages);
        
        return "allBlogs";
    }
    
    @RequestMapping(value = "/displayChosenBlogPost", method = RequestMethod.GET)
    public String displayChosenBlogPost(HttpServletRequest request, Model model) {
        String currentIdForPost = request.getParameter("currentPostId");
        if ("".equals(currentIdForPost) || "0".equals(currentIdForPost)) {
            return "redirect:homepage";
        } else {
            Posts currentPost = postsService.getPostsById(Integer.parseInt(currentIdForPost));
            Category currentCategory = serviceCat.getCategoryById(currentPost.getCategoryId());
            model.addAttribute("currentCategory", currentCategory);
            model.addAttribute("currentPost", currentPost);

            // get a list from the service with all pages
            List<StaticPages> pages = servicePage.getAllStaticPages();
            // add it to the model
            model.addAttribute("pagesList", pages);
            
            model.addAttribute("tagList", serviceTag.getAllTagsByPosts(Integer.parseInt(currentIdForPost)));
            
            return "displayChosenBlogPost";
        }
    }
    
    @RequestMapping(value = "/deletePost", method = RequestMethod.GET)
    public String deletePost(HttpServletRequest request) {
        String id = request.getParameter("currentPost");
        postsService.removePostsById(Integer.parseInt(id));
        return "redirect:dashboard";
    }
    
    @RequestMapping(value = "/updatePost", method = RequestMethod.GET)
    public String updatePost(HttpServletRequest request, Model model) {
        String id = request.getParameter("currentId");
        Posts post = postsService.getPostsById(Integer.parseInt(id));
        List<Category> categories = serviceCat.getAllCategories();
        Category c = serviceCat.getCategoryById(post.getCategoryId());
        model.addAttribute("currentCategory", c);
        model.addAttribute("post", post);
        model.addAttribute("cList", categories);

        // get a list from the service with all pages
        List<StaticPages> pages = servicePage.getAllStaticPages();
        // add it to the model
        model.addAttribute("pagesList", pages);

        //going to get tags from the database
        model.addAttribute("tagList", serviceTag.getLast10Tags());
        return "updatePost";
    }

    // wont work
    @RequestMapping(value = "/submitUpdatedPost", method = RequestMethod.POST)
    public String submitPost(@ModelAttribute("post") Posts post, HttpServletRequest request) {
        Entity entity = EntityServiceDao.getEntityByUserName(request.getRemoteUser());
        post.setUserId(entity.getRecordId());
        //requesting and getting the parameter of the title
        String title = request.getParameter("postTitle");
        post.setPostTitle(title);
        //requesting and getting the parameter of the post body
        String body = request.getParameter("postBody");
        post.setPostBody(body);
        post.setPostBody(request.getParameter("postBody"));
        
        String id = request.getParameter("chooseCategory");
        //get category by id

        Category c = serviceCat.getCategoryById(Integer.parseInt(id));

        // this references categories (is category id)
        post.setCategoryId(c.getRecordId());
        
        postsService.updatePost(post);

        //untill we add an update method using this for loop in order to clear out current tags
        List<postsTags> currentTags = serviceTag.getAllTagsByPosts(post.getRecordId());
        for (postsTags currentPostsTags : currentTags) {
            serviceTag.removeTag(currentPostsTags);
        }
        
        String[] chosenTags = request.getParameterValues("chooseTags");
        
        List<postsTags> postTags = new ArrayList<>();
        
        for (String tag : chosenTags) {
            postsTags pt = new postsTags();
            pt.setTag(tag);
            pt.setPostId(post.getRecordId());
            postTags.add(pt);
            
        }

        // NEED TO CREATE A UPDATE POST TAGS
        serviceTag.savePostTags(postTags);
        
        return "redirect:allBlogs";
    }
    
    @RequestMapping(value = "/displayBlogsByTag", method = RequestMethod.GET)
    public String displayBlogsByTag(HttpServletRequest request, Model model) {
        // get a list from the service with all pages
        List<StaticPages> pages = servicePage.getAllStaticPages();
        // add it to the model
        model.addAttribute("pagesList", pages);
        String id = request.getParameter("currentTagId");
        List<Posts> postsByTag = serviceTag.getPostsByTag(Integer.parseInt(id));
        model.addAttribute("postsByTag", postsByTag);
        return "displayBlogsByTag";
    }
    
}
