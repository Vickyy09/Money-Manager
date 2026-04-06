package com.vicky.moneymanage.controller;

import com.vicky.moneymanage.dto.ExpenseDTO;
import com.vicky.moneymanage.dto.IncomeDTO;
import com.vicky.moneymanage.services.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO){
        ExpenseDTO newExpense=expenseService.saveExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newExpense);
    }
    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpense(){
        List<ExpenseDTO> expenses=expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpenseById(id);
        return ResponseEntity.noContent().build();
    }
}
