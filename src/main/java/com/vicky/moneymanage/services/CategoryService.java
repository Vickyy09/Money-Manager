package com.vicky.moneymanage.services;

import com.vicky.moneymanage.dto.CategoryDTO;
import com.vicky.moneymanage.dto.ProfileDTO;
import com.vicky.moneymanage.entity.Category;
import com.vicky.moneymanage.entity.Profile;
import com.vicky.moneymanage.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        Profile profile=profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(),profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Category with this name already exist");
        }
        Category newCategory= toEntity(categoryDTO,profile);
        newCategory = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }



    public List<CategoryDTO> getCategoriesForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        List<Category> categories=categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        Profile currentProfile=profileService.getCurrentProfile();
        List<Category> entities=categoryRepository.findByTypeAndProfileId(type, currentProfile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId,CategoryDTO dto){
        Profile profile=profileService.getCurrentProfile();
        Category existing=categoryRepository.findByIdAndProfileId(categoryId,profile.getId())
                .orElseThrow(()->new RuntimeException("Category not found"));
        existing.setName(dto.getName());
        existing.setIcon(dto.getIcon());
        existing=categoryRepository.save(existing);
        return toDTO(existing);
    }

    public Category toEntity(CategoryDTO categoryDTO, Profile profile){
        return Category.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .type(categoryDTO.getType())
                .profile((profile))
                .build();
    }

    public CategoryDTO toDTO(Category category){
        return CategoryDTO.builder()
                .id(category.getId())
                .profileId(category.getProfile() !=null ? category.getProfile().getId(): null)
                .name(category.getName())
                .icon(category.getIcon())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .type(category.getType())
                .build();

    }
}
