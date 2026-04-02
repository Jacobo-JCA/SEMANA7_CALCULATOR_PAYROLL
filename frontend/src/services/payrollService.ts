const PAYROLL_API_URL = import.meta.env.VITE_PAYROLL_API_URL || 'http://localhost:8082/api/v1';

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

export const calculatePayroll = async (employeeId: string): Promise<PayrollResponse> => {
  const response = await fetch(`${PAYROLL_API_URL}/payroll/calculate/${employeeId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to calculate payroll');
  }

  return response.json();
};

export const getPayrollById = async (id: string): Promise<PayrollResponse> => {
  const response = await fetch(`${PAYROLL_API_URL}/payroll/${id}`);

  if (!response.ok) {
    throw new Error('Payroll calculation not found');
  }

  return response.json();
};

export const getPayrollByEmployeeId = async (employeeId: string): Promise<PayrollResponse | null> => {
  const response = await fetch(`${PAYROLL_API_URL}/payroll/employee/${employeeId}`);
  if (response.status === 404) return null;
  if (!response.ok) throw new Error('Error fetching payroll for employee');
  return response.json();
};

export const confirmPayroll = async (payrollId: string): Promise<PayrollResponse> => {
  const response = await fetch(`${PAYROLL_API_URL}/payroll/${payrollId}/confirm`, {
    method: 'PATCH',
  });
  if (!response.ok) throw new Error('Error confirming payroll');
  return response.json();
};

export const downloadPayrollPdf = async (payrollId: string) => {
  const response = await fetch(`${PAYROLL_API_URL}/payroll/${payrollId}/pdf`);
  if (!response.ok) throw new Error('Error downloading PDF');
  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `payroll-voucher-${payrollId}.pdf`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  window.URL.revokeObjectURL(url);
};
