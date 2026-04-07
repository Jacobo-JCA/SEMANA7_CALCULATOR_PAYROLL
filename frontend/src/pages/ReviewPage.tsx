import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { CheckCircle2, FileText, ArrowLeft, Download } from 'lucide-react';
import { AppLayout } from '../components/layout/AppLayout';
import { getPayrollById, downloadPayrollPdf, type PayrollResponse } from '../services/payrollService';

export const ReviewPage = () => {
  const { payrollId } = useParams<{ payrollId: string }>();
  const [payroll, setPayroll] = useState<PayrollResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isDownloading, setIsDownloading] = useState(false);
  const [showSuccess, setShowSuccess] = useState(false);

  useEffect(() => {
    if (payrollId) {
      getPayrollById(payrollId)
        .then(setPayroll)
        .finally(() => setIsLoading(false));
    }
  }, [payrollId]);

  const handleDownloadPDF = async () => {
    if (!payrollId || !payroll) return;
    setIsDownloading(true);
    try {
      const blob = await downloadPayrollPdf(payrollId);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `payroll-voucher-${payrollId}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      
      setShowSuccess(true);
      setTimeout(() => setShowSuccess(false), 5000);
    } catch (err) {
      alert('Error downloading PDF voucher');
    } finally {
      setIsDownloading(false);
    }
  };

  if (isLoading) return <AppLayout currentStep={4}><div className="flex h-64 items-center justify-center">Loading...</div></AppLayout>;

  return (
    <AppLayout currentStep={4}>
      <div className="flex flex-col gap-8">
        <div className="flex flex-col gap-2">
          <h1 className="text-2xl font-bold text-gray-900">Final Review</h1>
          <p className="text-gray-500 text-sm italic">Verification of payroll generation for {payroll?.employeeName}</p>
        </div>

        <div className="bg-emerald-50 border border-emerald-100 rounded-2xl p-8 flex flex-col items-center text-center gap-4">
          <div className="p-4 bg-emerald-100/50 rounded-full text-emerald-600">
            <CheckCircle2 className="w-12 h-12" />
          </div>
          <h2 className="text-xl font-bold text-emerald-900">Payroll Successfully Calculated</h2>
          <p className="text-emerald-700/80 max-w-md">The payroll record has been permanently generated in the system. You can now proceed to issue specific documentation.</p>
        </div>

        <div className="bg-white rounded-xl border border-gray-100 p-6 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <div className="p-3 bg-gray-50 rounded-lg text-gray-400">
              <FileText className="w-6 h-6" />
            </div>
            <div className="flex flex-col">
              <span className="font-bold text-gray-900">Payroll Voucher #{payroll?.id}</span>
               <span className="text-[10px] text-gray-500 uppercase tracking-widest font-bold">STATUS: CONFIRMED</span>
            </div>
          </div>
          {showSuccess && (
            <div className="flex items-center gap-2 px-3 py-1 bg-emerald-100 text-emerald-700 rounded-full text-[11px] font-bold animate-pulse">
              <CheckCircle2 className="w-3 h-3" />
              DOWNLOAD SUCCESSFUL
            </div>
          )}
          <button className="text-sm font-bold text-[#004DB3] hover:underline underline-offset-4">
            View Details
          </button>
        </div>

        <div className="flex flex-col gap-4 mt-4">
          <button 
            onClick={handleDownloadPDF}
            disabled={isDownloading}
            className="w-full bg-[#004DB3] hover:bg-[#003d8f] text-white py-4 rounded-xl font-bold flex items-center justify-center gap-3 transition-all shadow-lg active:scale-95 group disabled:opacity-75"
          >
            {isDownloading ? 'Preparing Document...' : 'Download PDF Document'}
            <Download className="w-5 h-5 group-hover:translate-y-0.5 transition-transform" />
          </button>

          <button 
            onClick={() => window.location.href = '/employees'} 
            className="w-full text-sm font-bold text-gray-400 hover:text-gray-900 flex items-center justify-center gap-2 transition-colors py-2"
          >
            <ArrowLeft className="w-4 h-4" />
            Finish and Return to Directory
          </button>
        </div>
      </div>
    </AppLayout>
  );
};
