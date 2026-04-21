import React from 'react';
import { Sparkles, ArrowRight, Zap } from 'lucide-react';
import { Link } from '@tanstack/react-router';

const PromoBanner: React.FC = () => {
  return (
    <div className="relative overflow-hidden bg-gradient-to-r from-emerald-600 to-teal-700 rounded-3xl mb-12 shadow-2xl group transition-all duration-500 hover:shadow-emerald-200/50">
      {/* Animated shapes */}
      <div className="absolute top-0 right-0 w-64 h-64 bg-white/10 rounded-full -translate-y-1/2 translate-x-1/2 blur-3xl group-hover:scale-125 transition-transform duration-700" />
      <div className="absolute bottom-0 left-0 w-48 h-48 bg-teal-400/20 rounded-full translate-y-1/2 -translate-x-1/2 blur-2xl" />
      
      <div className="relative px-8 py-12 md:px-12 md:py-16 flex flex-col md:flex-row items-center justify-between gap-8">
        <div className="text-center md:text-left max-w-2xl">
          <div className="inline-flex items-center gap-2 px-3 py-1 bg-emerald-500/30 border border-emerald-400/30 rounded-full text-emerald-50 text-sm font-semibold mb-6 backdrop-blur-sm animate-pulse">
            <Sparkles className="w-4 h-4" />
            Seasonal Wellness Offer
          </div>
          
          <h2 className="text-4xl md:text-5xl font-extrabold text-white mb-6 leading-tight tracking-tight">
            Boost Your Immunity <br />
            <span className="text-teal-200">This Summer Sale</span>
          </h2>
          
          <p className="text-emerald-50/90 text-lg md:text-xl mb-8 leading-relaxed max-w-xl">
            Get up to <span className="font-bold text-white text-2xl">40% OFF</span> on health packages and immunity boosters. Limited time wellness deals curated just for you.
          </p>
          
          <div className="flex flex-wrap justify-center md:justify-start gap-4">
            <Link 
              to="/catalog" 
              search={{ category: 'Health Packages' }}
              className="bg-white text-emerald-700 px-8 py-4 rounded-2xl font-bold text-lg flex items-center gap-2 transition-all hover:bg-emerald-50 hover:scale-105 active:scale-95 shadow-lg shadow-black/10"
            >
              Shop Packages <ArrowRight className="w-5 h-5" />
            </Link>
            <Link 
              to="/catalog" 
              className="bg-emerald-500/20 backdrop-blur-md border border-white/20 text-white px-8 py-4 rounded-2xl font-bold text-lg transition-all hover:bg-emerald-500/30 hover:scale-105 active:scale-95"
            >
              View All Offers
            </Link>
          </div>
        </div>
        
        <div className="relative hidden lg:block">
          <div className="absolute inset-0 bg-emerald-400/20 blur-[100px] rounded-full animate-pulse" />
          <div className="relative w-80 h-80 bg-white/5 backdrop-blur-xl border border-white/10 rounded-[40px] flex items-center justify-center rotate-6 group-hover:rotate-3 transition-transform duration-500">
             <div className="text-center">
                <div className="bg-emerald-100 p-4 rounded-2xl inline-block mb-4 shadow-inner">
                  <Zap className="w-12 h-12 text-emerald-600 fill-emerald-600" />
                </div>
                <div className="text-white text-4xl font-black mb-1">UP TO </div>
                <div className="text-teal-300 text-6xl font-black italic">40%</div>
                <div className="text-white/80 font-bold tracking-widest mt-2">DISCOUNT</div>
             </div>
          </div>
          
          {/* Floating badges */}
          <div className="absolute -top-4 -right-4 bg-yellow-400 text-emerald-900 font-black px-4 py-2 rounded-xl text-sm shadow-xl animate-bounce">
            FLASH DEAL
          </div>
          <div className="absolute -bottom-4 -left-4 bg-emerald-400 text-white font-black px-4 py-2 rounded-xl text-sm shadow-xl">
            FREE DELIVERY
          </div>
        </div>
      </div>
    </div>
  );
};

export default PromoBanner;
