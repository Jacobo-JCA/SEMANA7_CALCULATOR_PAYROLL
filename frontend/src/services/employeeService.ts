import { apiFetch } from '../utils/apiClient';

const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080';

export type ContractType = 'FULL_TIME' | 'PART_TIME' | 'PROFESSIONAL_SERVICES';

export interface EmployeeRequest {
  name: string;
  contractType: ContractType;
  grossSalary: number;
}

export interface EmployeeUpdateRequest {
  name?: string;
  grossSalary?: number;
}

export interface ContractResponse {
  id: number;
  contractType: ContractType;
  startDate: string;
  endDate: string | null;
}

export interface ContractUpdateRequest {
  contractType: ContractType;
  startDate: string;
}

export interface ContractEndDateRequest {
  endDate: string;
}

export interface EmployeeResponse {
  id: number;
  name: string;
  grossSalary: number;
  activeContract: ContractResponse | null;
  createdAt: string;
}

export const createEmployee = async (data: EmployeeRequest): Promise<EmployeeResponse> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/employees`, {
    method: 'POST',
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errData = await response.json().catch(() => null);
    if (errData?.details) {
      throw new Error(errData.details.join(', '));
    }
    throw new Error(errData?.message || 'Error occurred while creating employee');
  }

  return response.json();
};

export const getEmployeeById = async (id: string): Promise<EmployeeResponse> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/employees/${id}`);

  if (!response.ok) {
    throw new Error('Employee not found or server error');
  }

  return response.json();
};

export const updateEmployee = async (id: string, data: EmployeeUpdateRequest): Promise<EmployeeResponse> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/employees/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errData = await response.json().catch(() => null);
    if (errData?.details) {
      throw new Error(errData.details.join(', '));
    }
    throw new Error(errData?.message || 'Error occurred while updating employee');
  }

  return response.json();
};

export const updateContract = async (employeeId: string, contractId: number, data: ContractUpdateRequest): Promise<ContractResponse> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/employees/${employeeId}/contracts/${contractId}`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errData = await response.json().catch(() => null);
    throw new Error(errData?.message || 'Error occurred while updating contract');
  }

  return response.json();
};

export const closeContract = async (employeeId: string, contractId: number, data: ContractEndDateRequest): Promise<void> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/employees/${employeeId}/contracts/${contractId}/end-date`, {
    method: 'PATCH',
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const errData = await response.json().catch(() => null);
    throw new Error(errData?.message || 'Error occurred while closing contract');
  }
};

export const getAllEmployees = async (): Promise<EmployeeResponse[]> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/employees`);

  if (!response.ok) {
    throw new Error('Error fetching employees');
  }

  return response.json();
};

export const deleteEmployee = async (id: number): Promise<void> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/employees/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    throw new Error('Error deleting employee');
  }
};