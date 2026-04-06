package com.vicky.moneymanage.services;

import com.vicky.moneymanage.dto.ExpenseDTO;
import com.vicky.moneymanage.dto.IncomeDTO;
import com.vicky.moneymanage.entity.Category;
import com.vicky.moneymanage.entity.Expense;
import com.vicky.moneymanage.entity.Income;
import com.vicky.moneymanage.entity.Profile;
import com.vicky.moneymanage.repository.CategoryRepository;
import com.vicky.moneymanage.repository.IncomeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final IncomeRepo incomeRepo;


    public IncomeDTO saveIncome(IncomeDTO dto){
        Profile profile=profileService.getCurrentProfile();
        Category category=categoryRepository.findById(dto.getCategoryId()).orElseThrow(()->new RuntimeException("Category not found"));
        Income newIncome=toEntity(dto,profile,category);
        newIncome=incomeRepo.save(newIncome);
        return toDTO(newIncome);
    }


    public List<IncomeDTO> getCurrentMonthIncomeForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<Income> list=incomeRepo.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);
        return list.stream().map(this::toDTO).toList();

    }

    public void deleteIncomeById(Long incomeId){
        Profile profile=profileService.getCurrentProfile();
        Income income = incomeRepo.findById(incomeId).orElseThrow(()->new RuntimeException("Expense not found"));
        if(!income.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        incomeRepo.delete(income);
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        List<Income> list=incomeRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal totalIncomeForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        BigDecimal total=incomeRepo.findTotalIncomeByProfileId(profile.getId());
        return total !=null ? total: BigDecimal.ZERO;
    }

    public List<IncomeDTO> filterIncomes(LocalDate startDate,LocalDate endDate,String keyword, Sort sort){
        Profile profile= profileService.getCurrentProfile();
        List<Income> list=incomeRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyword,sort);
        return list.stream().map(this::toDTO).toList();
    }
    private Income toEntity(IncomeDTO dto, Profile profile, Category category){
        return Income.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }
    private IncomeDTO toDTO(Income income){
        return IncomeDTO.builder()
                .id(income.getId())
                .name(income.getName())
                .icon(income.getIcon())
                .categoryId(income.getCategory() != null?income.getCategory().getId():null)
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : "N/A")
                .amount(income.getAmount())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}
