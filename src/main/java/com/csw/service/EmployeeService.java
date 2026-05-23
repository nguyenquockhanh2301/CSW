package com.csw.service;

import com.csw.dao.EmployeeDao;
import com.csw.model.Employee;

import java.util.List;

public class EmployeeService {
    private final EmployeeDao employeeDao;

    public EmployeeService() {
        this.employeeDao = new EmployeeDao();
    }

    public List<Employee> getEmployees() {
        return employeeDao.getEmployees();
    }

    public Employee addEmployees(Employee employee) {
        return employeeDao.addEmployee(employee);
    }

    public boolean updateEmployee(Employee employee) {
        return employeeDao.updateEmployee(employee);
    }
}
