import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { RegisterEmployeePage } from './pages/RegisterEmployeePage';
import { EarningsPage } from './pages/EarningsPage';
import { EditEmployeePage } from './pages/EditEmployeePage';
import { DeductionsPage } from './pages/DeductionsPage';
import { ReviewPage } from './pages/ReviewPage';
import { EmployeeListPage } from './pages/EmployeeListPage';
import { AppLayout } from './components/layout/AppLayout';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { ProtectedRoute } from './components/layout/ProtectedRoute';
import { AuthProvider } from './contexts/AuthContext';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Protected Routes */}
          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<Navigate to="/employees" replace />} />
            <Route path="/employees" element={<EmployeeListPage />} />
            <Route path="/employees/new" element={<RegisterEmployeePage />} />
            <Route path="/edit/:id" element={<EditEmployeePage />} />
            <Route path="/earnings/:id" element={<EarningsPage />} />
            <Route path="/deductions/:payrollId" element={<DeductionsPage />} />
            <Route path="/review/:payrollId" element={<ReviewPage />} />
            
            {/* Dashboards Placeholders under protection */}
            <Route path="/dashboard" element={<Navigate to="/employees" replace />} />
            <Route path="/history" element={<AppLayout currentStep={0} children={<div className="h-64 flex items-center justify-center font-bold text-gray-300">HISTORY UNDER CONSTRUCTION</div>} />} />
            <Route path="/settings" element={<AppLayout currentStep={0} children={<div className="h-64 flex items-center justify-center font-bold text-gray-300">SETTINGS UNDER CONSTRUCTION</div>} />} />
            <Route path="/reports" element={<AppLayout currentStep={0} children={<div className="h-64 flex items-center justify-center font-bold text-gray-300 uppercase italic">Reports Dashboard Coming Soon</div>} />} />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
