package com.csw.dao;

import com.csw.db.Database;
import com.csw.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDao {
    public EmployeeDao() {
        Database.initialize();
    }

    public List<Employee> getEmployees() {
        String sql = "SELECT id, name, salary FROM employees ORDER BY id";
        List<Employee> employees = new ArrayList<>();

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                employees.add(mapRow(resultSet));
            }
            return employees;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load employees", e);
        }
    }

    public Employee addEmployee(Employee employee) {
        if (employee.getId() == null) {
            return insertWithoutId(employee);
        }
        return insertWithId(employee);
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET name = ?, salary = ? WHERE id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employee.getName());
            statement.setDouble(2, employee.getSalary());
            statement.setInt(3, employee.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update employee", e);
        }
    }

    private Employee insertWithoutId(Employee employee) {
        String sql = "INSERT INTO employees(name, salary) VALUES(?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, employee.getName());
            statement.setDouble(2, employee.getSalary());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setId(generatedKeys.getInt(1));
                }
            }
            return employee;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to add employee", e);
        }
    }

    private Employee insertWithId(Employee employee) {
        String sql = "INSERT INTO employees(id, name, salary) VALUES(?, ?, ?)";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, employee.getId());
            statement.setString(2, employee.getName());
            statement.setDouble(3, employee.getSalary());
            statement.executeUpdate();
            return employee;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to add employee", e);
        }
    }

    private Employee mapRow(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getDouble("salary")
        );
    }
}
