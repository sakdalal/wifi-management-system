package com.sak.wifi.Controller;

import com.sak.wifi.dto.PlanRequestDTO;
import com.sak.wifi.entity.Plan;
import com.sak.wifi.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<Plan> createPlan(@RequestBody PlanRequestDTO request){
        System.out.println(request.getPlanName());
        return ResponseEntity.ok(planService.createPlan(request));
    }

    @GetMapping()
    public ResponseEntity<List<Plan>> getPlans(){
        return ResponseEntity.ok(planService.getAllPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> getPlan(@PathVariable Long id){
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Plan> updatePlan(@PathVariable Long id,
                                           @RequestBody PlanRequestDTO request){

        return ResponseEntity.ok(planService.updatePlan(id,request));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlan(@PathVariable Long id){
        return ResponseEntity.ok(planService.deletePlan(id));
    }

}
