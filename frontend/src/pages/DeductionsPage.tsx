import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { User, Download, Info } from 'lucide-react';
import { AppLayout } from '../components/layout/AppLayout';
import { getPayrollById, confirmPayroll, type PayrollResponse } from '../services/payrollService';
import { useNavigate } from 'react-router-dom';

export const DeductionsPage = () => {
  const { payrollId } = useParams<{ payrollId: string }>();
  const [payroll, setPayroll] = useState<PayrollResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isConfirming, setIsConfirming] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (payrollId) {
      getPayrollById(payrollId)
        .then((data) => {
          setPayroll(data);
          setIsLoading(false);
        })
        .catch((err) => {
          setError(err.message || 'Error loading payroll summary');
          setIsLoading(false);
        });
    }
  }, [payrollId]);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
  };

  const handleConfirm = async () => {
    if (!payrollId) return;
    setIsConfirming(true);
    try {
      await confirmPayroll(payrollId);
      navigate(`/review/${payrollId}`);
    } catch (err: any) {
      setError(err.message || 'Error confirming payroll');
      setIsConfirming(false);
    }
  };

  if (isLoading) {
    return (
      <AppLayout currentStep={3}>
        <div className="flex h-64 items-center justify-center font-medium text-gray-500">Loading payroll summary...</div>
      </AppLayout>
    );
  }

  if (error || !payroll) {
    return (
      <AppLayout currentStep={3}>
        <div className="bg-red-50 text-red-700 p-4 rounded-lg border border-red-100">{error || 'Payroll data not found'}</div>
      </AppLayout>
    );
  }

  return (
    <AppLayout currentStep={3}>
      <div className="flex flex-col gap-6">
        
        {/* Header Title and Progress */}
        <div className="flex flex-col gap-1">
          <h1 className="text-[28px] font-bold text-gray-900 tracking-tight">Payroll Summary</h1>
          <p className="text-gray-500 text-sm">Finalize the calculations for the current billing cycle.</p>
        </div>

        {/* Custom Progress Bar */}
        <div className="h-1.5 w-full bg-gray-100 rounded-full overflow-hidden mt-2">
          <div className="w-3/4 bg-[#8EF1AF] h-full rounded-full transition-all duration-500"></div>
        </div>

        {/* Main Result Card */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 pt-10 mt-4 flex flex-col gap-8">
          
          <div className="flex justify-between items-start">
            <div className="flex gap-4 items-center">
               <div className="w-[60px] h-[60px] bg-[#1E293B] rounded-[14px] flex items-end justify-center overflow-hidden shrink-0 p-1 relative">
                <User className="absolute -bottom-1 w-12 h-12 text-[#38BDF8] fill-[#38BDF8]" strokeWidth={1} />
              </div>
              <div className="flex flex-col">
                <h2 className="text-xl font-bold text-gray-900 leading-tight">{payroll.employeeName}</h2>
                <div className="flex items-center gap-1.5 text-xs text-gray-500 font-medium mt-0.5">
                  <span>Senior Payroll Administrator</span>
                  <span className="text-gray-300">•</span>
                  <span>{payroll.contractType.replace('_', ' ')}</span>
                </div>
              </div>
            </div>
            <div className="bg-[#E6F9F0] text-[#059669] text-[10px] font-bold px-3 py-1 rounded-full uppercase tracking-wider">
              Active Cycle
            </div>
          </div>

          {/* Breakdown Lines */}
          <div className="flex flex-col gap-6 py-4">
            
            <div className="flex justify-between items-center text-gray-600 font-medium">
              <span className="text-gray-500 tracking-wide">Gross Salary</span>
              <span className="text-gray-900 font-bold text-lg">{formatCurrency(payroll.grossSalary)}</span>
            </div>

            <div className="flex justify-between items-center text-gray-600 font-medium">
              <div className="flex items-center gap-3">
                <span className="text-gray-500 tracking-wide">Deductions</span>
                <span className="bg-red-50 text-red-600 text-[10px] font-bold px-1.5 py-0.5 rounded leading-none">
                  {payroll.deductionPercentage}%
                </span>
              </div>
              <span className="text-red-500 font-bold text-lg">-{formatCurrency(payroll.deductionAmount)}</span>
            </div>

            <div className="flex justify-between items-center text-gray-600 font-medium">
              <div className="flex items-center gap-3">
                <span className="text-gray-500 tracking-wide">Bonus</span>
                <span className="bg-[#E6F9F0] text-[#059669] text-[10px] font-bold px-1.5 py-0.5 rounded leading-none">
                  {payroll.bonusPercentage}%
                </span>
              </div>
              <span className="text-[#059669] font-bold text-lg">+{formatCurrency(payroll.bonusAmount)}</span>
            </div>

            <div className="h-px bg-gray-100 my-2"></div>

            <div className="flex flex-col gap-0.5">
               <div className="flex justify-between items-end">
                <div className="flex flex-col">
                  <span className="text-[10px] font-bold text-gray-400 tracking-widest uppercase mb-1">Net Salary</span>
                  <span className="text-[40px] font-bold text-gray-900 leading-none">
                    {formatCurrency(payroll.netSalary)}
                  </span>
                </div>
                <div className="flex items-center gap-1.5 text-[10px] text-gray-400 font-medium italic translate-y-[-8px]">
                  Net = Gross - Deduction + Bonus
                </div>
              </div>
            </div>

          </div>
        </div>

        {/* Action Bar */}
        <div className="flex items-center gap-10 mt-6 pl-4">
           <button 
            onClick={handleConfirm}
            disabled={isConfirming}
            className="bg-[#0051B3] hover:bg-[#003d8f] text-white px-8 py-4 rounded-xl font-bold flex items-center gap-3 transition-all shadow-md active:scale-95 group disabled:opacity-70"
          >
            {isConfirming ? 'Confirming...' : 'Confirm & Finalize Cycle'}
            <Download className="w-5 h-5 group-hover:translate-y-0.5 transition-transform" />
          </button>

          <button className="flex items-center gap-2 text-sm font-bold text-gray-600 hover:text-gray-900 transition-colors">
            <Info className="w-4 h-4" />
            Review Breakdown
          </button>
        </div>

      </div>
    </AppLayout>
  );
};
