package com.csw.servlet;

import com.csw.model.Employee;
import com.csw.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "EmployeeServlet", urlPatterns = "/api/employees")
public class EmployeeServlet extends HttpServlet {
    private transient EmployeeService employeeService;
    private transient ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.employeeService = new EmployeeService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Employee> employees = employeeService.getEmployees();
        writeJson(response, HttpServletResponse.SC_OK, employees);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Employee employee = objectMapper.readValue(request.getInputStream(), Employee.class);
            validateForCreate(employee);
            Employee created = employeeService.addEmployees(employee);
            writeJson(response, HttpServletResponse.SC_CREATED, created);
        } catch (JsonProcessingException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Employee employee = objectMapper.readValue(request.getInputStream(), Employee.class);
            validateForUpdate(employee);
            boolean updated = employeeService.updateEmployee(employee);
            if (!updated) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Employee not found: id=" + employee.getId());
                return;
            }
            writeJson(response, HttpServletResponse.SC_OK, employee);
        } catch (JsonProcessingException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void validateForCreate(Employee employee) {
        validateSharedFields(employee);
        if (employee.getId() != null && employee.getId() <= 0) {
            throw new IllegalArgumentException("id must be greater than 0 when provided");
        }
    }

    private void validateForUpdate(Employee employee) {
        validateSharedFields(employee);
        if (employee.getId() == null || employee.getId() <= 0) {
            throw new IllegalArgumentException("id is required and must be greater than 0 for update");
        }
    }

    private void validateSharedFields(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        if (employee.getSalary() < 0) {
            throw new IllegalArgumentException("salary must be >= 0");
        }
    }

    private void writeJson(HttpServletResponse response, int status, Object payload) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), payload);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = toJson(Map.of("error", message == null ? "Unexpected error" : message));
        response.getWriter().write(json);
    }

    private String toJson(Object payload) throws JsonProcessingException {
        return objectMapper.writeValueAsString(payload);
    }
}
