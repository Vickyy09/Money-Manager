package com.vicky.moneymanage.controller;

import com.vicky.moneymanage.dto.ExpenseDTO;
import com.vicky.moneymanage.dto.IncomeDTO;
import com.vicky.moneymanage.services.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO){
        IncomeDTO newIncome=incomeService.saveIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newIncome);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getIncome(){
        List<IncomeDTO> incomes=incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.ok(incomes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncomeById(id);
        return ResponseEntity.noContent().build();
    }
}
