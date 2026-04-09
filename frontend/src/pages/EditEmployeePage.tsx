import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { UserPlus, Save, Calendar, XCircle, CheckCircle2 } from 'lucide-react';
import { useParams, useNavigate } from 'react-router-dom';
import { AppLayout } from '../components/layout/AppLayout';
import { getEmployeeById, updateEmployee, updateContract, type ContractType } from '../services/employeeService';
import { getPayrollByEmployeeId } from '../services/payrollService';

const schema = z.object({
  name: z.string().min(1, 'El nombre es obligatorio').max(100, 'Máximo 100 caracteres').regex(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$/, 'Solo se permiten letras y espacios'),
  contractType: z.enum(['FULL_TIME', 'PART_TIME', 'PROFESSIONAL_SERVICES'], { message: 'Seleccione un tipo válido' }),
  grossSalary: z.number({ message: 'Debe ser un número' }).positive('El salario debe ser mayor a cero'),
  startDate: z.string().min(1, 'La fecha de inicio es obligatoria'),
});

type FormData = z.infer<typeof schema>;

export const EditEmployeePage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isLocked, setIsLocked] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [contractId, setContractId] = useState<number | null>(null);

  const { register, handleSubmit, reset, watch, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  const currentValues = watch();

  useEffect(() => {
    if (id) {
      getPayrollByEmployeeId(id).then(payroll => {
        if (payroll?.confirmed) {
          setError("Los datos no pueden modificarse porque la nómina ya fue calculada y confirmada.");
          setIsLocked(true);
        }
      }).catch(() => {
        // Silently ignore 404 errors for employees without a payroll history
      });

      getEmployeeById(id)
        .then(data => {
          setContractId(data.activeContract?.id || null);
          reset({
            name: data.name,
            contractType: data.activeContract?.contractType || 'FULL_TIME',
            grossSalary: data.grossSalary,
            startDate: data.activeContract?.startDate || ''
          });
          setIsLoading(false);
        })
        .catch(err => {
          setError(err.message || 'Error loading employee');
          setIsLoading(false);
        });
    }
  }, [id, reset]);

  const onSubmit = async (data: FormData) => {
    if (!id) return;
    setIsSaving(true);
    setError(null);
    setSuccess(null);
    try {
      // Update basic info
      await updateEmployee(id, {
        name: data.name,
        grossSalary: data.grossSalary
      });

      // Update contract if exists
      if (contractId) {
        await updateContract(id, contractId, {
          contractType: data.contractType as ContractType,
          startDate: data.startDate
        });
      }

      setSuccess("Employee and contract updated successfully!");
      setTimeout(() => navigate(`/earnings/${id}`), 1500);
    } catch (err: any) {
      setError(err.message || 'Error updating employee');
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <AppLayout currentStep={1}>
        <div className="flex h-64 items-center justify-center">Loading employee details...</div>
      </AppLayout>
    );
  }

  return (
    <AppLayout currentStep={1}>
      <div className="max-w-4xl mx-auto space-y-6 animate-fade-in">

        {/* Main Form Card */}
        <div className="bg-white rounded-[24px] shadow-sm border border-gray-100 p-10">
          <div className="flex justify-between items-start mb-10">
            <div className="flex gap-4 items-start">
              <div className="p-3 bg-[#F0F5FF] border border-blue-50 rounded-xl text-[#004DB3]">
                <UserPlus className="w-6 h-6" />
              </div>
              <div>
                <h1 className="text-xl font-bold text-gray-900 tracking-tight">Edit Profile & Contract</h1>
                <p className="text-sm text-gray-500 mt-1">Management of core parameters and active periods</p>
              </div>
            </div>
            {isLocked && (
              <div className="px-3 py-1 bg-amber-50 text-amber-700 text-[10px] font-bold uppercase tracking-wider rounded-full border border-amber-100">
                Immutable Record
              </div>
            )}
          </div>

          {(error || success) && (
            <div className={`mb-8 p-4 rounded-xl border text-sm flex items-center gap-3 ${error ? 'bg-red-50 text-red-700 border-red-100' : 'bg-emerald-50 text-emerald-700 border-emerald-100'
              }`}>
              {error ? <XCircle className="w-5 h-5" /> : <CheckCircle2 className="w-5 h-5" />}
              {error || success}
            </div>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-10">
            {/* Identity Group */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
              <div className="space-y-2">
                <label className="text-[10px] font-bold text-gray-400 tracking-widest uppercase">Full Name</label>
                <input
                  {...register('name')}
                  disabled={isLocked}
                  className={`w-full bg-transparent border-b py-2 text-gray-900 placeholder-gray-400 focus:outline-none transition-colors ${errors.name ? 'border-red-500' : 'border-gray-200 focus:border-[#004DB3]'} ${isLocked ? 'opacity-50' : ''}`}
                />
                {errors.name && <p className="text-xs text-red-500">{errors.name.message}</p>}
              </div>

              <div className="space-y-2">
                <label className="text-[10px] font-bold text-gray-400 tracking-widest uppercase">Monthly Gross Salary</label>
                <div className="flex items-center gap-2 border-b border-gray-200 focus-within:border-[#004DB3] transition-colors py-2">
                  <span className="text-gray-400 font-medium">$</span>
                  <input
                    type="number"
                    step="0.01"
                    {...register('grossSalary', { valueAsNumber: true })}
                    disabled={isLocked}
                    className="w-full bg-transparent text-gray-900 focus:outline-none"
                  />
                </div>
                {errors.grossSalary && <p className="text-xs text-red-500">{errors.grossSalary.message}</p>}
              </div>
            </div>

            {/* Contract Logistics Group */}
            <div className="pt-6 border-t border-gray-50 grid grid-cols-1 md:grid-cols-2 gap-10">
              <div className="space-y-2">
                <label className="text-[10px] font-bold text-gray-400 tracking-widest uppercase">Contract Model</label>
                <select
                  {...register('contractType')}
                  disabled={isLocked}
                  className="w-full bg-transparent border-b py-2 text-gray-900 focus:outline-none appearance-none cursor-pointer border-gray-200 focus:border-[#004DB3]"
                >
                  <option value="FULL_TIME">Full Time (100%)</option>
                  <option value="PART_TIME">Part Time</option>
                  <option value="PROFESSIONAL_SERVICES">Professional Services</option>
                </select>
              </div>

              <div className="space-y-2">
                <label className="text-[10px] font-bold text-gray-400 tracking-widest uppercase">Start Date</label>
                <div className="relative">
                  <input
                    type="date"
                    {...register('startDate')}
                    disabled={isLocked}
                    className="w-full bg-transparent border-b py-2 text-gray-900 focus:outline-none border-gray-200 focus:border-[#004DB3]"
                  />
                  <Calendar className="absolute right-2 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-300 pointer-events-none" />
                </div>
              </div>
            </div>

            <div className="pt-8 flex items-center justify-between">
              <button
                type="button"
                onClick={() => navigate(`/earnings/${id}`)}
                className="text-sm font-bold text-gray-400 hover:text-gray-600 transition-colors"
              >
                &larr; Back to Dashboard
              </button>
              <button
                type="submit"
                disabled={isSaving || isLocked}
                className="bg-[#004DB3] hover:bg-[#003d8f] text-white px-10 py-3.5 rounded-2xl font-bold flex items-center gap-3 transition-all shadow-lg active:scale-95 disabled:opacity-50"
              >
                <Save className="w-5 h-5" />
                {isSaving ? 'Synchronizing...' : 'Save All Changes'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AppLayout>
  );
};
