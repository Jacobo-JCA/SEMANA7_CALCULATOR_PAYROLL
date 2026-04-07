import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Lock, User as UserIcon, ArrowRight, ShieldCheck } from 'lucide-react';

export const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { loginUser } = useAuth();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      await loginUser({ username, password });
    } catch (err: any) {
      setError(err.message || 'Invalid credentials');
    } finally {
      setIsLoading(false);
    }
  };

  const handleBypass = () => {
    // Hidden ease-of-use feature for quick development bypassing manual entry if desired
    setUsername('admin');
    setPassword('123');
  };

  return (
    <div className="min-h-screen bg-[#F8FAFC] flex flex-col justify-center font-inter sm:px-6 lg:px-8 relative overflow-hidden">
      {/* Decorative background blobs */}
      <div className="absolute top-0 right-0 -mr-20 -mt-20 w-96 h-96 rounded-full bg-blue-100 opacity-50 blur-3xl" />
      <div className="absolute bottom-0 left-0 -ml-20 -mb-20 w-80 h-80 rounded-full bg-blue-50 opacity-50 blur-3xl" />

      <div className="sm:mx-auto sm:w-full sm:max-w-md relative z-10">
        <div className="flex justify-center mb-6">
          <div className="bg-white p-3 rounded-2xl shadow-sm border border-gray-100 inline-flex items-center justify-center">
            <ShieldCheck className="h-10 w-10 text-[#004DB3]" strokeWidth={1.5} />
          </div>
        </div>
        <h2 className="text-center text-3xl tracking-tight font-extrabold text-gray-900">
          Calculator Payroll
        </h2>
        <p className="mt-2 text-center text-sm text-gray-600">
          Sign in to access the secure administrative portal
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md relative z-10">
        <div className="bg-white py-10 px-6 sm:px-10 shadow-[0_8px_30px_rgb(0,0,0,0.04)] rounded-2xl border border-gray-100">
          <form className="space-y-6" onSubmit={handleLogin}>
            
            {error && (
              <div className="rounded-xl border border-red-100 bg-red-50 p-4 animate-in fade-in slide-in-from-top-2 duration-300">
                <div className="flex items-center text-sm text-red-600">
                  <span className="font-medium mr-1">Authentication Failed:</span> {error}
                </div>
              </div>
            )}

            <div>
              <label className="block text-sm font-semibold text-gray-700 tracking-wide">
                Username
              </label>
              <div className="mt-2 relative rounded-xl shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                  <UserIcon className="h-5 w-5" />
                </div>
                <input
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="block w-full pl-10 pr-3 py-3 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-[#004DB3]/20 focus:border-[#004DB3] sm:text-sm transition-all text-gray-900 placeholder:text-gray-400"
                  placeholder="Enter your username"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-semibold text-gray-700 tracking-wide">
                Password
              </label>
              <div className="mt-2 relative rounded-xl shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                  <Lock className="h-5 w-5" />
                </div>
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="block w-full pl-10 pr-3 py-3 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-[#004DB3]/20 focus:border-[#004DB3] sm:text-sm transition-all text-gray-900 placeholder:text-gray-400"
                  placeholder="••••••••"
                />
              </div>
            </div>

            <div className="flex items-center justify-between">
              <div className="text-sm">
                <span onClick={handleBypass} className="font-medium text-[#004DB3] hover:text-[#003882] cursor-pointer transition-colors">
                  Fill Demo Credentials
                </span>
              </div>
              <div className="text-sm">
                <a href="#" className="font-medium text-gray-500 hover:text-gray-900 transition-colors">
                  Forgot password?
                </a>
              </div>
            </div>

            <div>
              <button
                type="submit"
                disabled={isLoading}
                className="group relative w-full flex justify-center items-center py-3 px-4 border border-transparent text-sm font-bold rounded-xl text-white bg-[#004DB3] hover:bg-[#003882] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#004DB3] transition-all disabled:opacity-70 disabled:cursor-not-allowed shadow-md hover:shadow-lg"
              >
                {isLoading ? (
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                ) : (
                  <>
                    Sign In
                    <ArrowRight className="ml-2 h-4 w-4 opacity-70 group-hover:translate-x-1 transition-transform" />
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};
