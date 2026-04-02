import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, Search, UserPlus, Pencil, Play, Users, DollarSign, Calculator, Briefcase, TrendingUp, XCircle, CheckCircle2 } from 'lucide-react';
import { AppLayout } from '../components/layout/AppLayout';
import { getAllEmployees, closeContract, type EmployeeResponse } from '../services/employeeService';

export const EmployeeListPage = () => {
  const [employees, setEmployees] = useState<EmployeeResponse[]>([]);
  const [selectedEmployee, setSelectedEmployee] = useState<EmployeeResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [closeDate, setCloseDate] = useState<string>('');
  const [statusMsg, setStatusMsg] = useState<{ type: 'success' | 'error', text: string } | null>(null);
  const navigate = useNavigate();

  const fetchEmployees = () => {
    setIsLoading(true);
    getAllEmployees()
      .then(data => {
        setEmployees(data);
        setIsLoading(false);
        if (selectedEmployee) {
          const updated = data.find(e => e.id === selectedEmployee.id);
          if (updated) setSelectedEmployee(updated);
        }
      })
      .catch(() => setIsLoading(false));
  };

  useEffect(() => {
    fetchEmployees();
  }, []);

  const handleCloseContract = async () => {
    if (!selectedEmployee || !selectedEmployee.activeContract || !closeDate) return;
    setIsSaving(true);
    setStatusMsg(null);
    try {
      await closeContract(selectedEmployee.id.toString(), selectedEmployee.activeContract.id, { endDate: closeDate });
      setStatusMsg({ type: 'success', text: "Contract closed successfully!" });
      setCloseDate('');
      fetchEmployees();
      setTimeout(() => setStatusMsg(null), 3000);
    } catch (err: any) {
      setStatusMsg({ type: 'error', text: err.message || 'Error closing contract' });
      setIsSaving(false);
    }
  };

  const filteredEmployees = employees.filter(emp => 
    emp.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
    (emp.activeContract?.contractType || '').toLowerCase().includes(searchTerm.toLowerCase())
  );

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
  };

  if (isLoading && employees.length === 0) {
    return (
      <AppLayout currentStep={0}>
        <div className="flex h-full items-center justify-center text-gray-400 font-medium">Loading records...</div>
      </AppLayout>
    );
  }

  // Empty State
  if (employees.length === 0) {
    return (
      <AppLayout currentStep={0}>
        <div className="h-full flex flex-col items-center justify-center text-center p-20 animate-fade-in">
          <div className="w-24 h-24 bg-blue-50 rounded-full flex items-center justify-center text-[#004DB3] mb-6 shadow-sm border border-blue-100">
            <Users className="w-12 h-12" strokeWidth={1.5} />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 mb-2">No employees registered yet</h1>
          <p className="text-gray-500 max-w-sm mb-8 leading-relaxed">
            Begin by adding your first employee to the system to unlock all payroll and reporting features.
          </p>
          <button 
            onClick={() => navigate('/register')}
            className="bg-[#004DB3] hover:bg-[#003d8f] text-white px-8 py-3.5 rounded-xl font-bold flex items-center gap-3 transition-all shadow-md active:scale-95"
          >
            <UserPlus className="w-5 h-5" />
            Add Your First Employee
          </button>
        </div>
      </AppLayout>
    );
  }

  return (
    <AppLayout currentStep={0}>
      <div className="flex h-full w-full overflow-hidden animate-fade-in">
        
        {/* Left Column: Compact Employee List (30%) */}
        <div className="w-[320px] lg:w-[380px] border-r border-gray-100 flex flex-col bg-white shrink-0 shadow-[4px_0_24px_rgba(0,0,0,0.02)] z-10">
          <div className="p-6 border-b border-gray-50 flex flex-col gap-4 bg-white sticky top-0">
            <div className="flex items-center justify-between">
              <h2 className="font-bold text-gray-900 flex items-center gap-2">
                <Users className="w-4 h-4 text-[#004DB3]" />
                Directory
                <span className="text-[10px] bg-blue-50 text-[#004DB3] px-1.5 py-0.5 rounded ml-1">{employees.length}</span>
              </h2>
              <button 
                onClick={() => navigate('/register')}
                className="p-2 bg-blue-50 text-[#004DB3] rounded-lg hover:bg-blue-100 transition-colors"
                title="Add New Employee"
              >
                <UserPlus className="w-4 h-4" />
              </button>
            </div>
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-gray-400" />
              <input 
                type="text" 
                placeholder="Search..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full bg-gray-50 border-none rounded-lg py-2 pl-9 pr-4 text-xs focus:ring-1 focus:ring-blue-100 transition-all"
              />
            </div>
          </div>

          <div className="flex-1 overflow-y-auto divide-y divide-gray-50 scrollbar-thin">
            {filteredEmployees.map((emp) => (
              <div 
                key={emp.id}
                onClick={() => {
                   setSelectedEmployee(emp);
                   setStatusMsg(null);
                }}
                className={`p-4 cursor-pointer transition-all flex items-center gap-4 group hover:bg-slate-50 relative ${
                  selectedEmployee?.id === emp.id ? 'bg-blue-50/40' : ''
                }`}
              >
                {selectedEmployee?.id === emp.id && (
                  <div className="absolute left-0 top-0 bottom-0 w-1 bg-[#004DB3] rounded-r-full shadow-[0_0_12px_rgba(0,77,179,0.3)]" />
                )}
                
                <div className="w-10 h-10 rounded-xl bg-slate-800 flex items-end justify-center overflow-hidden shrink-0 shadow-sm border border-slate-700/50">
                   <User className="w-8 h-8 text-blue-400 fill-blue-400 translate-y-1" strokeWidth={1} />
                </div>

                <div className="flex flex-col min-w-0 flex-1">
                  <span className={`font-bold text-[14px] truncate transition-colors ${selectedEmployee?.id === emp.id ? 'text-[#004DB3]' : 'text-gray-800'}`}>
                    {emp.name}
                  </span>
                  <div className="flex items-center gap-2 mt-0.5">
                    <span className="text-[9px] uppercase tracking-wider font-bold text-gray-400 truncate">
                      {emp.activeContract?.contractType.replace('_', ' ') || 'NO ACTIVE CONTRACT'}
                    </span>
                    <span className="text-gray-300">•</span>
                    <span className="text-[10px] font-bold text-[#059669]">
                      {formatCurrency(emp.grossSalary)}/mo
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="p-4 border-t border-gray-50 bg-gray-50/40">
            <button 
              onClick={() => navigate('/register')}
              className="w-full py-2.5 px-4 bg-white border border-gray-200 rounded-lg text-xs font-bold text-gray-600 hover:text-[#004DB3] hover:border-blue-200 flex items-center justify-center gap-2 transition-all shadow-sm active:scale-95"
            >
              <UserPlus className="w-3.5 h-3.5" />
              Add New Employee
            </button>
          </div>
        </div>

        {/* Right Column: Employee Details (70%) */}
        <div className="flex-1 overflow-y-auto bg-[#F8FAFC]">
          {!selectedEmployee ? (
            <div className="h-full flex flex-col items-center justify-center text-gray-300 animate-pulse">
              <div className="text-5xl mb-4">👈</div>
              <p className="text-sm font-bold tracking-widest uppercase">Select an employee to view details</p>
            </div>
          ) : (
            <div key={selectedEmployee.id} className="p-10 animate-fade-in flex flex-col gap-8 max-w-4xl mx-auto">
              
              {/* Profile Header */}
              <div className="flex justify-between items-start">
                <div className="flex gap-6 items-center">
                  <div className="w-20 h-20 bg-slate-900 rounded-3xl flex items-end justify-center overflow-hidden shrink-0 shadow-xl p-1 border border-slate-700">
                    <User className="w-16 h-16 text-blue-400 fill-blue-400 translate-y-1.5" strokeWidth={1} />
                  </div>
                  <div className="flex flex-col gap-1">
                    <div className="flex items-center gap-3">
                      <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight">{selectedEmployee.name}</h1>
                      <span className={`text-[10px] font-bold px-3 py-1 rounded-full uppercase tracking-widest border ${
                        selectedEmployee.activeContract ? 'bg-[#E6F9F0] text-[#059669] border-[#D1FAE5]' : 'bg-red-50 text-red-600 border-red-100'
                      }`}>
                         {selectedEmployee.activeContract ? 'Active Employee' : 'Inactive / Archive'}
                      </span>
                    </div>
                    <div className="flex items-center gap-2 text-gray-500 font-medium text-sm">
                      <Briefcase className="w-4 h-4 text-gray-400" />
                      <span>Senior Payroll Administrator</span>
                      <span className="text-gray-300">•</span>
                      <span className="font-bold text-[#004DB3]">{selectedEmployee.activeContract?.contractType.replace('_', ' ') || 'NO ACTIVE CONTRACT'}</span>
                    </div>
                  </div>
                </div>
                
                <div className="flex gap-2">
                </div>
              </div>

              {statusMsg && (
                <div className={`p-4 rounded-xl border text-sm flex items-center gap-3 animate-fade-in ${
                  statusMsg.type === 'error' ? 'bg-red-50 text-red-700 border-red-100' : 'bg-emerald-50 text-emerald-700 border-emerald-100'
                }`}>
                  {statusMsg.type === 'error' ? <XCircle className="w-5 h-5" /> : <CheckCircle2 className="w-5 h-5" />}
                  {statusMsg.text}
                </div>
              )}

              {/* Salary Components Breakdown */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-50 flex flex-col gap-3">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] font-bold text-gray-400 uppercase tracking-[0.2em]">Gross Salary</span>
                    <div className="p-1.5 bg-blue-50 rounded-lg text-[#004DB3]">
                      <DollarSign className="w-4 h-4" />
                    </div>
                  </div>
                  <div className="flex flex-col">
                    <span className="text-2xl font-bold text-gray-900">{formatCurrency(selectedEmployee.grossSalary)}</span>
                    <span className="text-[10px] text-gray-400 font-medium">Monthly amount</span>
                  </div>
                </div>

                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-50 flex flex-col gap-3">
                  <div className="flex items-center justify-between text-emerald-600">
                    <span className="text-[10px] font-bold text-gray-400 uppercase tracking-[0.2em]">Monthly Net</span>
                    <div className="p-1.5 bg-emerald-50 rounded-lg">
                      <TrendingUp className="w-4 h-4" />
                    </div>
                  </div>
                  <div className="flex flex-col">
                    <span className="text-2xl font-bold text-emerald-700">{formatCurrency(selectedEmployee.grossSalary * 0.9888)}</span>
                    <span className="text-[10px] text-emerald-600/60 font-medium">Estimated (Before Taxes)</span>
                  </div>
                </div>

                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-50 flex flex-col gap-3">
                  <div className="flex items-center justify-between text-gray-400">
                    <span className="text-[10px] font-bold text-gray-400 uppercase tracking-[0.2em]">Frequency</span>
                    <div className="p-1.5 bg-gray-50 rounded-lg text-gray-400">
                      <Play className="w-4 h-4" />
                    </div>
                  </div>
                  <div className="flex flex-col">
                    <span className="text-2xl font-bold text-gray-900">Monthly</span>
                    <span className="text-[10px] text-gray-400 font-medium">Payroll Cycle</span>
                  </div>
                </div>
              </div>

              {/* Main Action Section */}
              {selectedEmployee.activeContract && (
                <div className="bg-[#004DB3]/[0.02] border border-[#004DB3]/10 rounded-[28px] p-8 mt-4 flex items-center justify-between">
                  <div className="flex flex-col gap-1">
                    <h3 className="text-lg font-bold text-gray-900">Ready for Calculation?</h3>
                    <p className="text-sm text-gray-500 max-w-sm">Execute the automated payroll process for {selectedEmployee.name.split(' ')[0]} using current parameters.</p>
                  </div>
                  <button 
                    onClick={() => navigate(`/earnings/${selectedEmployee.id}`)}
                    className="bg-[#004DB3] hover:bg-[#003d8f] text-white px-8 py-4 rounded-2xl font-bold flex items-center gap-3 transition-all shadow-lg active:scale-95 group"
                  >
                    Calculate Payroll
                    <Calculator className="w-5 h-5 group-hover:rotate-12 transition-transform" />
                  </button>
                </div>
              )}

              <div className="mt-4 grid grid-cols-2 gap-8 px-4">
                <div className="flex flex-col gap-1">
                  <span className="text-[10px] font-bold text-gray-400 tracking-[0.15em] uppercase">Contract Start</span>
                  <span className="text-sm font-bold text-gray-700">{selectedEmployee.activeContract?.startDate || 'N/A'}</span>
                </div>
                <div className="flex flex-col gap-1">
                  <span className="text-[10px] font-bold text-gray-400 tracking-[0.15em] uppercase">Employee Code</span>
                  <span className="text-sm font-bold text-gray-700">EMP-{selectedEmployee.id}293</span>
                </div>
              </div>

              {/* Contract Closure Section (Glassmorphism effect) - MOVED HERE */}
              {selectedEmployee.activeContract && (
                <div className="bg-slate-900/5 backdrop-blur-md border border-slate-200/50 rounded-[28px] p-8 mt-4 overflow-hidden relative group">
                  <div className="absolute top-0 right-0 p-8 text-slate-200 opacity-20 transform translate-x-4 -translate-y-4 group-hover:translate-x-2 group-hover:-translate-y-2 transition-transform">
                    <XCircle className="w-32 h-32" strokeWidth={0.5} />
                  </div>

                  <div className="relative z-10">
                    <h3 className="text-[16px] font-bold text-slate-800 mb-1">Finalize Active Contract</h3>
                    <p className="text-[13px] text-slate-500 mb-6 max-w-md">Setting an end date will archive this contract. This is performed after contract fulfillment.</p>

                    <div className="flex gap-4 items-end max-w-md">
                      <div className="flex-1 space-y-2">
                        <label className="text-[10px] font-bold text-slate-400 tracking-widest uppercase">Termination Date</label>
                        <input 
                          type="date"
                          value={closeDate}
                          onChange={(e) => setCloseDate(e.target.value)}
                          className="w-full bg-white/50 border border-slate-200 rounded-xl py-2 px-4 text-sm focus:outline-none focus:ring-2 focus:ring-[#004DB3]/20 transition-all font-medium"
                        />
                      </div>
                      <button 
                        type="button"
                        onClick={handleCloseContract}
                        disabled={!closeDate || isSaving}
                        className="bg-slate-800 hover:bg-slate-900 text-white px-6 py-2.5 rounded-xl text-xs font-bold transition-all disabled:opacity-30 disabled:grayscale whitespace-nowrap"
                      >
                        {isSaving ? 'Processing...' : 'Close Contract'}
                      </button>
                    </div>
                  </div>
                </div>
              )}

            </div>
          )}
        </div>

      </div>
      
      {/* Scrollbar Customization */}
      <style>{`
        .scrollbar-thin::-webkit-scrollbar { width: 4px; }
        .scrollbar-thin::-webkit-scrollbar-track { background: transparent; }
        .scrollbar-thin::-webkit-scrollbar-thumb { background: #E2E8F0; border-radius: 20px; }
        .scrollbar-thin::-webkit-scrollbar-thumb:hover { background: #CBD5E1; }
        
        .animate-fade-in {
          animation: fadeIn 0.4s ease-out forwards;
        }
        
        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(4px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </AppLayout>
  );
};
