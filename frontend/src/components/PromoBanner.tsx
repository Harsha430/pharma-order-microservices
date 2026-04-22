import React from 'react';
import { Sparkles, ArrowRight } from 'lucide-react';
import { Link } from '@tanstack/react-router';

const PromoBanner: React.FC = () => {
  return (
    <div className="relative overflow-hidden bg-gradient-to-r from-emerald-600 via-teal-600 to-emerald-700 text-white py-2 px-4 shadow-md z-[60]">
      {/* Subtle animated background element */}
      <div className="absolute top-0 right-0 w-64 h-full bg-white/5 skew-x-[45deg] translate-x-1/2" />
      
      <div className="max-w-7xl mx-auto flex items-center justify-center gap-3 md:gap-6 text-xs md:text-sm font-medium">
        <div className="hidden sm:flex items-center gap-1.5 px-2 py-0.5 bg-white/20 rounded-full border border-white/10 backdrop-blur-sm">
          <Sparkles className="w-3 h-3 text-yellow-300" />
          <span className="uppercase tracking-widest text-[10px]">Offer</span>
        </div>
        
        <p className="flex items-center gap-2">
          <span className="opacity-90">Boost your immunity this summer!</span>
          <span className="font-bold bg-yellow-400 text-emerald-900 px-1.5 py-0.5 rounded text-[10px] md:text-xs">UP TO 40% OFF</span>
        </p>

        <Link 
          to="/catalog" 
          search={{ category: 'Health Packages' }}
          className="group flex items-center gap-1 font-bold border-b border-white/40 hover:border-white transition-all ml-2"
        >
          Shop Now
          <ArrowRight className="w-3.5 h-3.5 group-hover:translate-x-0.5 transition-transform" />
        </Link>
      </div>
    </div>
  );
};

export default PromoBanner;
