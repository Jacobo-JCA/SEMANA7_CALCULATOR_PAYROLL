import { useEffect, useState, type ReactNode } from 'react';
import { User, Layers, PieChart, Lock, Users, Info } from 'lucide-react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { getAllEmployees } from '../../services/employeeService';
import { useAuth } from '../../contexts/AuthContext';
import { LogOut } from 'lucide-react';

interface AppLayoutProps {
  children: ReactNode;
  currentStep?: number;
}

export const AppLayout = ({ children, currentStep = 1 }: AppLayoutProps) => {
  const { id, payrollId } = useParams<{ id?: string; payrollId?: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const { logoutUser } = useAuth();
  const [hasEmployees, setHasEmployees] = useState<boolean>(false);

  useEffect(() => {
    getAllEmployees()
      .then(data => {
        setHasEmployees(data.length > 0);
      })
      .catch(() => {
        // Handle error silently or set fallback
      });
  }, [location.pathname]);

  const activeId = id || payrollId;

  const handleNav = (target: string) => {
    switch (target) {
      case 'list':
        navigate('/employees');
        break;
      case 'payroll':
        if (!hasEmployees) return;
        navigate(activeId ? `/earnings/${activeId}` : '/employees');
        break;
      case 'details':
        if (!activeId) return;
        navigate(`/edit/${activeId}`);
        break;
      case 'reports':
        if (!hasEmployees) return;
        navigate('/reports');
        break;
    }
  };

  return (
    <div className="min-h-screen bg-[#F8FAFC] flex flex-col font-inter">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-8 shrink-0">
        <div className="font-bold text-xl text-gray-900 tracking-tight">Calculator Payroll</div>
        
        <div className="flex items-center gap-8">
          <nav className="flex items-center gap-6">
            <button onClick={() => navigate('/dashboard')} className="text-sm font-medium text-gray-500 hover:text-[#004DB3] transition-colors">Dashboard</button>
            <button onClick={() => navigate('/history')} className="text-sm font-medium text-gray-500 hover:text-[#004DB3] transition-colors">History</button>
            <button onClick={() => navigate('/settings')} className="text-sm font-medium text-gray-500 hover:text-[#004DB3] transition-colors">Settings</button>
          </nav>
          <div className="flex items-center gap-4">
            <div className="bg-blue-50 text-[#004DB3] text-[11px] font-bold px-3 py-1.5 rounded-md uppercase tracking-wider">
              {currentStep > 0 ? `Step ${currentStep} of 4` : 'Overview'}
            </div>
            <div className="w-px h-6 bg-gray-200"></div>
            <button 
              onClick={logoutUser} 
              className="flex items-center gap-2 text-sm font-bold text-gray-500 hover:text-red-600 transition-colors"
              title="Sign Out"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>
      </header>

      <div className="flex flex-1 overflow-hidden">
        {/* Sidebar */}
        <aside className="w-64 bg-white border-r border-gray-200 py-6 flex flex-col shrink-0">
          <div className="px-4 space-y-2">
            <NavItem 
              icon={<Users className="w-[18px] h-[18px]" />} 
              label="Employee List" 
              active={location.pathname === '/employees'} 
              onClick={() => handleNav('list')}
            />

            <NavItem 
              icon={<Layers className="w-[18px] h-[18px]" />} 
              label="Payroll" 
              active={location.pathname.startsWith('/earnings') || location.pathname.startsWith('/deductions') || location.pathname.startsWith('/review')} 
              onClick={() => handleNav('payroll')}
              disabled={!hasEmployees}
              lockMessage={!hasEmployees ? "Register your first employee to unlock this feature" : "Select an employee first"}
              showInfo={!hasEmployees || !activeId}
            />

            <NavItem 
              icon={<User className="w-[18px] h-[18px]" />} 
              label="Employee Details" 
              active={location.pathname.startsWith('/edit') || location.pathname.startsWith('/employees/new')} 
              onClick={() => handleNav('details')}
              disabled={!activeId}
              lockMessage={!hasEmployees ? "Register your first employee to unlock this feature" : "Select an employee from the list to view details"}
              showInfo={!activeId}
            />

            <NavItem 
              icon={<PieChart className="w-[18px] h-[18px]" />} 
              label="Reports" 
              active={location.pathname === '/reports'} 
              onClick={() => handleNav('reports')}
              disabled={!hasEmployees}
              lockMessage={!hasEmployees ? "Register your first employee to unlock this feature" : "Select an employee first"}
              showInfo={!hasEmployees || !activeId}
            />
          </div>
        </aside>

        {/* Main Content */}
        <main className="flex-1 overflow-y-auto">
          <div className={`${currentStep === 0 ? 'h-full' : 'max-w-3xl mx-auto p-10'}`}>
            {children}
          </div>
        </main>
      </div>
    </div>
  );
};

const NavItem = ({ icon, label, active, onClick, disabled, lockMessage, showInfo }: { icon: ReactNode; label: string; active?: boolean; onClick?: () => void; disabled?: boolean; lockMessage?: string; showInfo?: boolean }) => {
  return (
    <div className="relative group/nav">
      <div
        onClick={!disabled ? onClick : undefined}
        className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-300 select-none ${active
          ? 'bg-blue-50/50 text-[#004DB3] font-semibold tracking-wide cursor-default uppercase'
          : disabled 
            ? 'text-gray-300 cursor-not-allowed opacity-60'
            : 'text-gray-500 hover:bg-gray-50 hover:text-gray-900 font-medium tracking-wide cursor-pointer'
          }`}
      >
        <div className="relative flex items-center justify-center">
          <div className={`${active ? 'text-[#004DB3]' : disabled ? 'text-gray-200' : 'text-gray-400'}`}>
            {icon}
          </div>
          {disabled && (
            <div className="absolute -top-1.5 -right-1.5 p-0.5 bg-white rounded-full border border-gray-100 shadow-[0_1px_2px_rgba(0,0,0,0.05)]">
              <Lock className="w-2 h-2 text-gray-300" />
            </div>
          )}
        </div>
        <span className="text-[11px] uppercase tracking-widest font-bold flex-1">{label}</span>
        {disabled && showInfo && (
          <Info className="w-3 h-3 text-gray-200" />
        )}
      </div>

      {disabled && lockMessage && (
        <div className="absolute left-full ml-4 top-1/2 -translate-y-1/2 bg-gray-900 text-white text-[10px] font-bold px-3 py-2 rounded-lg whitespace-nowrap opacity-0 group-hover/nav:opacity-100 pointer-events-none transition-all translate-x-1 group-hover/nav:translate-x-0 z-50 shadow-xl border border-gray-800">
          <div className="absolute right-full top-1/2 -translate-y-1/2 border-8 border-transparent border-r-gray-900"></div>
          {lockMessage}
        </div>
      )}
    </div>
  );
};
