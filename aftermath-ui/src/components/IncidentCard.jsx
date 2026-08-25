import React from 'react';
import { AlertCircle, Server, ArrowRight, ShieldCheck, Repeat } from 'lucide-react';

export default function IncidentCard({ incident, onSelect }) {
  return (
    <div
      onClick={() => onSelect(incident)}
      className="bg-slate-900/90 border border-slate-800 hover:border-slate-700/80 rounded-xl p-5 cursor-pointer hover:shadow-xl transition-all duration-200 group"
    >
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center space-x-2.5">
          <span className="bg-rose-500/10 text-rose-400 font-mono font-bold text-xs px-2.5 py-1 rounded-lg border border-rose-500/20">
            {incident.statusCode}
          </span>
          <span className="font-mono text-sm font-semibold text-slate-200 group-hover:text-indigo-400 transition">
            {incident.httpMethod} {incident.requestUri}
          </span>
          {incident.occurrenceCount > 1 && (
            <span className="bg-amber-500/10 text-amber-400 font-mono font-bold text-[11px] px-2 py-0.5 rounded border border-amber-500/20 flex items-center gap-1">
              <Repeat className="w-3 h-3" />
              {incident.occurrenceCount}x
            </span>
          )}
        </div>
        <span className="text-[11px] font-mono text-slate-500">
          {incident.lastSeenAt ? new Date(incident.lastSeenAt).toLocaleTimeString() : (incident.createdAt ? new Date(incident.createdAt).toLocaleTimeString() : '')}
        </span>
      </div>

      <div className="mb-3">
        <h3 className="text-sm font-bold text-rose-400 font-mono flex items-center gap-1.5">
          <AlertCircle className="w-4 h-4 shrink-0 text-rose-500" />
          <span className="truncate">{incident.exceptionClass || 'Uncaught Exception'}</span>
        </h3>
        <p className="text-xs text-slate-400 mt-1 line-clamp-2">
          {incident.exceptionMessage || 'No exception detail'}
        </p>
      </div>

      <div className="flex items-center justify-between pt-3 border-t border-slate-800/80 text-xs font-mono text-slate-400">
        <div className="flex items-center space-x-4">
          <span className="flex items-center gap-1 text-slate-300">
            <Server className="w-3.5 h-3.5 text-indigo-400" />
            {incident.serviceName || 'unknown'}
          </span>
          <span className="flex items-center gap-1 text-slate-400">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
            Redacted
          </span>
        </div>

        <div className="flex items-center gap-1 text-indigo-400 group-hover:translate-x-1 transition-transform">
          <span>View Details</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </div>
      </div>
    </div>
  );
}
