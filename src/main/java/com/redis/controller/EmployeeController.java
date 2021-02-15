package com.redis.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.redis.ResourceNotFoundException;
import com.redis.model.Employee;
import com.redis.repository.EmployeeRepository;

@RestController
@RequestMapping("/api")
public class EmployeeController {
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@PostMapping("/employees")
	public Employee addEmployee(@RequestBody Employee employee) {
		return employeeRepository.save(employee);
	}
	
	@GetMapping("/employees")
	public ResponseEntity<List<Employee>>getAllEmployees(){
		return ResponseEntity.ok(employeeRepository.findAll());
	}
	
	
	@GetMapping("/employees/{employeeId}")
	@Cacheable( value = "employees",key ="#employeeId")
	public Employee findEmployeeById(@PathVariable(value="employeeId") Integer employeeId) {
		System.out.println("Employee from database:: " + employeeId);
		
		return employeeRepository.findById(employeeId).orElseThrow(
				()->new ResourceNotFoundException("Employee not found" + employeeId));
	}
	
	@PutMapping("/employees/{employeeId}")
	@CachePut(value="employees", key="#employeeId")
	public Employee updateEmployee(@PathVariable(value="employeeId") Integer employeeId,
													@RequestBody Employee employeeDetails) {
		Employee employee = employeeRepository.findById(employeeId)
					.orElseThrow(()->new ResourceNotFoundException("Employee not found::" + employeeId));
		employee.setName(employeeDetails.getName());
		employee.setAddress(employeeDetails.getAddress());
		employee.setAge(employeeDetails.getAge());

		final Employee updateEmployee = employeeRepository.save(employee);
		return updateEmployee;
	}
	
	@DeleteMapping("/employee/{employeeId}")
	@CacheEvict(value="employees", allEntries = true)
	public void deleteEmployee(@PathVariable(value="employeeId") Integer employeeId) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(()->new ResourceNotFoundException("Employee not found"+employeeId));
		employeeRepository.delete(employee);
		
		
	}

}
