import { apiFetch } from '../utils/apiClient';

const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080';

export interface PayrollResponse {
  id: number;
  employeeId: number;
  employeeName: string;
  contractType: string;
  grossSalary: number;
  deductionPercentage: number;
  deductionAmount: number;
  bonusPercentage: number;
  bonusAmount: number;
  netSalary: number;
  confirmed: boolean;
  createdAt: string;
  updatedAt: string;
}

export const calculatePayroll = async (employeeId: number | string): Promise<PayrollResponse> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/payroll/calculate/${employeeId}`, {
    method: 'POST',
  });

  if (!response.ok) {
    throw new Error('Error calculating payroll');
  }

  return response.json();
};

export const confirmPayroll = async (payrollId: number | string): Promise<PayrollResponse> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/payroll/${payrollId}/confirm`, {
    method: 'PATCH',
  });

  if (!response.ok) {
    throw new Error('Error confirming payroll');
  }

  return response.json();
};

export const getPayrollById = async (payrollId: number | string): Promise<PayrollResponse> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/payroll/${payrollId}`);

  if (!response.ok) {
    throw new Error('Payroll not found');
  }

  return response.json();
};

export const getPayrollByEmployeeId = async (employeeId: number | string): Promise<PayrollResponse> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/payroll/employee/${employeeId}`);

  if (!response.ok) {
    throw new Error('Payroll not found');
  }

  return response.json();
};

export const downloadPayrollPdf = async (payrollId: number | string): Promise<Blob> => {
  const response = await apiFetch(`${GATEWAY_URL}/api/v1/payroll/${payrollId}/pdf`);

  if (!response.ok) {
    throw new Error('Error downloading PDF');
  }

  return response.blob();
};