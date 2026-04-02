import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { UserPlus, ArrowRight, CheckCircle2 } from 'lucide-react';
import { AppLayout } from '../components/layout/AppLayout';
import { useEmployee } from '../hooks/useEmployee';

const schema = z.object({
  name: z.string().min(1, 'El nombre es obligatorio').max(100, 'Máximo 100 caracteres').regex(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$/, 'Solo se permiten letras y espacios'),
  contractType: z.enum(['FULL_TIME', 'PART_TIME', 'PROFESSIONAL_SERVICES'], { message: 'Seleccione un tipo válido' }),
  grossSalary: z.number({ message: 'Debe ser un número' }).positive('El salario debe ser mayor a cero'),
});

type FormData = z.infer<typeof schema>;

export const RegisterEmployeePage = () => {
  const navigate = useNavigate();
  const { registerEmployee, isLoading, error } = useEmployee();
  
  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (data: FormData) => {
    const result = await registerEmployee(data);
    if (result) {
      navigate(`/earnings/${result.id}`);
    }
  };

  return (
    <AppLayout currentStep={1}>
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-10">
        
        <div className="flex gap-4 items-start mb-10">
          <div className="p-3 bg-[#F0F5FF] border border-blue-50 rounded-xl text-[#004DB3]">
            <UserPlus className="w-6 h-6" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-gray-900 tracking-tight">Core Identification</h1>
            <p className="text-sm text-gray-500 mt-1">Standardized tax and identity parameters</p>
          </div>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-red-50 text-red-700 border border-red-200 rounded-lg text-sm">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
          
          <div className="space-y-2">
            <label className="text-xs font-bold text-gray-500 tracking-wider uppercase">
              Full Name <span className="text-red-500">*</span>
            </label>
            <input 
              {...register('name')}
              className={`w-full bg-transparent border-b py-2 text-gray-900 placeholder-gray-400 focus:outline-none transition-colors ${errors.name ? 'border-red-500' : 'border-gray-200 focus:border-[#004DB3]'}`}
              placeholder="e.g. Alexander Hamilton"
            />
            {errors.name && <p className="text-xs text-red-500 mt-1">{errors.name.message}</p>}
          </div>

          <div className="flex gap-10">
            <div className="flex-1 space-y-2">
              <label className="text-xs font-bold text-gray-500 tracking-wider uppercase">
                Contract Type <span className="text-red-500">*</span>
              </label>
              <select 
                {...register('contractType')}
                className={`w-full bg-transparent border-b py-2 text-gray-900 focus:outline-none transition-colors appearance-none cursor-pointer ${errors.contractType ? 'border-red-500' : 'border-gray-200 focus:border-[#004DB3]'}`}
                defaultValue=""
              >
                <option value="" disabled className="text-gray-400">Select an option</option>
                <option value="FULL_TIME">Full Time</option>
                <option value="PART_TIME">Part Time</option>
                <option value="PROFESSIONAL_SERVICES">Professional Services</option>
              </select>
              {errors.contractType && <p className="text-xs text-red-500 mt-1">{errors.contractType.message}</p>}
            </div>

            <div className="flex-1 space-y-2">
              <label className="text-xs font-bold text-gray-500 tracking-wider uppercase">
                Monthly Gross Salary <span className="text-red-500">*</span>
              </label>
              <div className="flex items-center gap-2 border-b focus-within:border-[#004DB3] transition-colors py-2">
                <span className="text-gray-400 font-medium">$</span>
                <input 
                  type="number"
                  step="0.01"
                  {...register('grossSalary', { valueAsNumber: true })}
                  className="w-full bg-transparent text-gray-900 placeholder-gray-400 focus:outline-none"
                  placeholder="2000"
                />
              </div>
              {errors.grossSalary && <p className="text-xs text-red-500 mt-1">{errors.grossSalary.message}</p>}
            </div>
          </div>

          <div className="flex items-center gap-2 text-xs text-[#059669] pt-2 font-medium">
            <CheckCircle2 className="w-[14px] h-[14px] fill-[#10B981] text-white" />
            Schema validation active (Zod)
          </div>

          <div className="pt-8 mt-10 border-t border-gray-100 flex items-center justify-between">
            <button type="button" className="text-sm font-semibold text-gray-500 hover:text-gray-900 py-2 transition-colors">
              &larr; Cancel
            </button>
            <button 
              type="submit" 
              disabled={isLoading}
              className="bg-[#004DB3] hover:bg-[#003d8f] text-white px-6 py-2.5 rounded-lg font-semibold flex items-center gap-2 transition-colors disabled:opacity-70 text-sm"
            >
              {isLoading ? 'Saving...' : 'Save and Continue'}
              <ArrowRight className="w-[18px] h-[18px]" />
            </button>
          </div>

        </form>
      </div>
    </AppLayout>
  );
};
