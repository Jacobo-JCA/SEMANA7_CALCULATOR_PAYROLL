import { useState } from 'react';
import { createEmployee, type EmployeeRequest, type EmployeeResponse } from '../services/employeeService';

export const useEmployee = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const registerEmployee = async (data: EmployeeRequest): Promise<EmployeeResponse | null> => {
    setIsLoading(true);
    setError(null);
    try {
      const result = await createEmployee(data);
      return result;
    } catch (err: any) {
      setError(err.message || 'Error registrando empleado');
      return null;
    } finally {
      setIsLoading(false);
    }
  };

  return { registerEmployee, isLoading, error };
};
