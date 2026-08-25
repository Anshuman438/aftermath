import React from 'react';
import { Activity, ShieldCheck, Database, RefreshCw } from 'lucide-react';

export default function Navbar({ onRefresh }) {
  return (
    <header className="border-b border-slate-800 bg-slate-900/80 backdrop-blur-md sticky top-0 z-40 px-6 py-4 flex items-center justify-between">
      <div className="flex items-center space-x-3">
        <div className="bg-rose-500/10 p-2 rounded-xl border border-rose-500/20 text-rose-400">
          <Activity className="w-6 h-6 animate-pulse" />
        </div>
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
            AFTERMATH <span className="text-xs bg-rose-500/20 text-rose-400 px-2 py-0.5 rounded-full font-mono border border-rose-500/30">v0.1.0-MVP</span>
          </h1>
          <p className="text-xs text-slate-400">Production Failure & Incident Capture Console</p>
        </div>
      </div>

      <div className="flex items-center space-x-4">
        <div className="flex items-center space-x-2 text-xs bg-slate-800/80 px-3 py-1.5 rounded-lg text-slate-300 border border-slate-700">
          <Database className="w-3.5 h-3.5 text-emerald-400" />
          <span>SQLite Engine Connected</span>
        </div>
        <div className="flex items-center space-x-2 text-xs bg-slate-800/80 px-3 py-1.5 rounded-lg text-slate-300 border border-slate-700">
          <ShieldCheck className="w-3.5 h-3.5 text-cyan-400" />
          <span>PII Redaction Active</span>
        </div>
        <button
          onClick={onRefresh}
          className="flex items-center space-x-1.5 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          <span>Refresh</span>
        </button>
      </div>
    </header>
  );
}
