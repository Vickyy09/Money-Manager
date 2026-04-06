package com.vicky.moneymanage.services;

import com.vicky.moneymanage.dto.ExpenseDTO;
import com.vicky.moneymanage.entity.Category;
import com.vicky.moneymanage.entity.Expense;
import com.vicky.moneymanage.entity.Profile;
import com.vicky.moneymanage.repository.CategoryRepository;
import com.vicky.moneymanage.repository.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final ExpenseRepo expenseRepo;


    public ExpenseDTO saveExpense(ExpenseDTO dto){
        Profile profile=profileService.getCurrentProfile();
        Category category=categoryRepository.findById(dto.getCategoryId()).orElseThrow(()->new RuntimeException("Category not found"));
        Expense newExpense=toEntity(dto,profile,category);
        newExpense=expenseRepo.save(newExpense);
        return toDTO(newExpense);
    }

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<Expense> list=expenseRepo.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);
        return list.stream().map(this::toDTO).toList();

    }

    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        List<Expense> list=expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal toatalExpenseForCurrentUser(){
        Profile profile=profileService.getCurrentProfile();
        BigDecimal total=expenseRepo.findTotalExpenseByProfileId(profile.getId());
        return total !=null ? total: BigDecimal.ZERO;
    }

    public List<ExpenseDTO> filterExpenses(LocalDate startDate,LocalDate endDate,String keyword, Sort sort){
        Profile profile= profileService.getCurrentProfile();
        List<Expense> list=expenseRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyword,sort);
        return list.stream().map(this::toDTO).toList();
    }

    public void deleteExpenseById(Long expenseId){
        Profile profile=profileService.getCurrentProfile();
        Expense expense = expenseRepo.findById(expenseId).orElseThrow(()->new RuntimeException("Expense not found"));
        if(!expense.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepo.delete(expense);
    }

    public List<ExpenseDTO> getExpensesFOrUserOnDate(Long profileId,LocalDate date){
        List<Expense> list=expenseRepo.findByProfileIdAndDate(profileId,date);
        return list.stream().map(this::toDTO).toList();
    }

    private Expense toEntity(ExpenseDTO dto, Profile profile, Category category){
    return Expense.builder()
            .name(dto.getName())
            .icon(dto.getIcon())
            .amount(dto.getAmount())
            .date(dto.getDate())
            .profile(profile)
            .category(category)
            .build();
    }
    private ExpenseDTO toDTO(Expense expense){
        return ExpenseDTO.builder()
                .id(expense.getId())
                .name(expense.getName())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory() != null?expense.getCategory().getId():null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : "N/A")
                .amount(expense.getAmount())
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }

}
