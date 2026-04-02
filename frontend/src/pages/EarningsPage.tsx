import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { User, Lock, Pencil, ArrowRight } from 'lucide-react';
import { AppLayout } from '../components/layout/AppLayout';
import { getEmployeeById, type EmployeeResponse } from '../services/employeeService';
import { calculatePayroll } from '../services/payrollService';

export const EarningsPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [employee, setEmployee] = useState<EmployeeResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isCalculating, setIsCalculating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      getEmployeeById(id)
        .then((data) => {
          setEmployee(data);
          setIsLoading(false);
        })
        .catch((err) => {
          setError(err.message || 'Error loading employee');
          setIsLoading(false);
        });
    }
  }, [id]);

  const handleConfirmCalculate = async () => {
    if (!id) return;
    setIsCalculating(true);
    setError(null);
    try {
      const response = await calculatePayroll(id);
      // Navigate to Step 3 with the new payroll ID
      navigate(`/deductions/${response.id}`);
    } catch (err: any) {
      setError(err.message || 'Error calculating payroll');
    } finally {
      setIsCalculating(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
  };

  const getContractLabel = (type: string) => {
    switch (type) {
      case 'FULL_TIME': return 'Full Time Indefinite';
      case 'PART_TIME': return 'Part Time';
      case 'PROFESSIONAL_SERVICES': return 'Professional Services';
      default: return type;
    }
  };

  if (isLoading) {
    return (
      <AppLayout currentStep={2}>
        <div className="flex h-64 items-center justify-center">Loading employee details...</div>
      </AppLayout>
    );
  }

  if (error || !employee) {
    return (
      <AppLayout currentStep={2}>
        <div className="text-red-500 bg-red-50 p-4 rounded-lg">{error || 'Employee not found'}</div>
      </AppLayout>
    );
  }

  // Simulation for the view to match the design (optional/informational)

  // Formatting date
  const effectiveDate = employee.activeContract?.startDate 
    ? new Date(employee.activeContract.startDate).toLocaleDateString('en-US', {
        month: 'long', day: '2-digit', year: 'numeric'
      })
    : 'N/A';

  return (
    <AppLayout currentStep={2}>
      <div className="flex flex-col gap-6">

        {/* Header Title and Progress */}
        <div className="flex items-center justify-between">
          <h1 className="text-[28px] font-bold text-gray-900 tracking-tight">Review & Edit</h1>
          <div className="flex flex-col items-end gap-2 w-48">
            <span className="text-sm font-semibold text-gray-600">50% Complete</span>
            <div className="h-1.5 w-full bg-gray-200 rounded-full overflow-hidden flex">
              <div className="w-1/2 bg-[#004DB3] h-full"></div>
            </div>
          </div>
        </div>

        {/* Lock Banner */}
        <div className="bg-[#8EF1AF] rounded-lg p-4 flex gap-3 items-start shadow-sm border border-[#7BD297]/30">
          <div className="bg-[#5ECC82] p-2 rounded-lg shrink-0 mt-0.5 shadow-sm">
            <Lock className="w-5 h-5 text-[#0A4A1C] fill-[#0A4A1C]" />
          </div>
          <div className="flex flex-col mt-0.5">
            <h3 className="font-bold text-[#0A4A1C]">Calculations Locked</h3>
            <p className="text-[#156E2F] text-sm">The current payroll cycle has been verified and is ready for final approval.</p>
          </div>
        </div>

        {/* Employee Card Details */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-8 flex flex-col gap-6">
          <div className="flex justify-between items-start">
            <div className="flex gap-5 items-center">
              {/* Profile Picture Placeholder */}
              <div className="w-[84px] h-[84px] bg-[#1E293B] rounded-[18px] flex items-end justify-center overflow-hidden shrink-0 shadow-inner p-2 relative">
                <User className="absolute -bottom-2 w-16 h-16 text-[#38BDF8] fill-[#38BDF8]" strokeWidth={1} />
              </div>
              <div className="flex flex-col">
                <h2 className="text-xl font-bold text-gray-900">{employee.name}</h2>
                <p className="text-gray-500 text-sm font-medium mt-0.5">Senior Systems Analyst</p>
                <div className="flex gap-2 mt-2">
                   <span className="px-2 py-0.5 rounded text-[11px] font-bold tracking-wider uppercase bg-[#E0E7FF] text-[#3730A3]">
                    {(employee.activeContract?.contractType || '').replace('_', '-')}
                  </span>
                  <span className="px-2 py-0.5 rounded text-[11px] font-bold tracking-wider uppercase bg-gray-100 text-gray-600">
                    DEPT: TECH
                  </span>
                </div>
              </div>
            </div>
            <button
              onClick={() => navigate(`/edit/${id}`)}
              className="flex items-center gap-1.5 text-sm font-bold text-[#004DB3] hover:text-[#003d8f] transition-colors pr-2"
            >
              <Pencil className="w-3.5 h-3.5 fill-current" />
              Edit Record
            </button>
          </div>

          <div className="grid grid-cols-2 gap-y-6 mt-4">
            <div className="flex flex-col gap-1">
              <span className="text-[10px] font-bold text-gray-400 tracking-widest uppercase">Contract Type</span>
              <span className="font-medium text-gray-900">{getContractLabel(employee.activeContract?.contractType || '')}</span>
            </div>
            <div className="flex flex-col gap-1">
              <span className="text-[10px] font-bold text-gray-400 tracking-widest uppercase">Monthly Gross Salary</span>
              <span className="font-bold text-gray-900">{formatCurrency(employee.grossSalary)}</span>
            </div>
            <div className="flex flex-col gap-1">
              <span className="text-[10px] font-bold text-gray-400 tracking-widest uppercase">Effective Date</span>
              <span className="font-medium text-gray-900">{effectiveDate}</span>
            </div>
            <div className="flex flex-col gap-1">
              <span className="text-[10px] font-bold text-gray-400 tracking-widest uppercase">Pay Frequency</span>
              <span className="font-medium text-gray-900">Every month</span>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex flex-col items-center mt-6 gap-4">
          <button
            disabled={isCalculating}
            onClick={handleConfirmCalculate}
            className="w-full bg-[#004DB3] hover:bg-[#003d8f] disabled:opacity-75 disabled:cursor-not-allowed text-white text-[15px] py-4 rounded-xl font-bold tracking-wide flex justify-center items-center gap-2 transition-colors shadow-sm"
          >
            {isCalculating ? 'Calculating...' : 'Confirm & Calculate'}
            <ArrowRight className="w-5 h-5 stroke-[2.5px]" />
          </button>

          <button className="text-[13px] font-semibold text-gray-500 hover:text-gray-800 transition-colors">
            Save as Draft for Later
          </button>
        </div>

      </div>
    </AppLayout>
  );
};
