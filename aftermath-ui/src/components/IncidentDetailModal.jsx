import React, { useState } from 'react';
import { X, Copy, Check, Terminal, FileCode, Shield, Server, Clock } from 'lucide-react';

export default function IncidentDetailModal({ incident, onClose }) {
  const [copied, setCopied] = useState(false);
  const [activeTab, setActiveTab] = useState('stacktrace');

  if (!incident) return null;

  const handleCopyRaw = () => {
    navigator.clipboard.writeText(incident.rawJson || JSON.stringify(incident, null, 2));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  let parsedRaw = null;
  try {
    if (incident.rawJson) {
      parsedRaw = JSON.parse(incident.rawJson);
    }
  } catch (e) {
    // fallback
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in">
      <div className="bg-slate-900 border border-slate-700/80 rounded-2xl w-full max-w-4xl max-h-[90vh] flex flex-col shadow-2xl overflow-hidden">
        
        {/* Header */}
        <div className="p-6 border-b border-slate-800 flex items-start justify-between bg-slate-900/50">
          <div>
            <div className="flex items-center space-x-3 mb-2">
              <span className="bg-rose-500/20 text-rose-400 font-mono font-bold text-xs px-2.5 py-1 rounded-md border border-rose-500/30">
                HTTP {incident.statusCode}
              </span>
              <span className="font-mono text-sm font-semibold text-slate-200">
                {incident.httpMethod} {incident.requestUri}
              </span>
            </div>
            <h2 className="text-lg font-semibold text-rose-400 font-mono">
              {incident.exceptionClass || 'Exception'}
            </h2>
            <p className="text-xs text-slate-400 mt-0.5">
              {incident.exceptionMessage || 'No exception message provided'}
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-white rounded-lg bg-slate-800 hover:bg-slate-700 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Metadata bar */}
        <div className="px-6 py-3 bg-slate-950/60 border-b border-slate-800 flex flex-wrap gap-4 text-xs font-mono text-slate-400">
          <div className="flex items-center space-x-1.5">
            <Server className="w-3.5 h-3.5 text-indigo-400" />
            <span>Service: <strong className="text-slate-200">{incident.serviceName || 'unknown'}</strong> ({incident.serviceVersion || '0.1.0'})</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <Clock className="w-3.5 h-3.5 text-amber-400" />
            <span>Recorded: {incident.createdAt ? new Date(incident.createdAt).toLocaleString() : 'N/A'}</span>
          </div>
          <div className="flex items-center space-x-1.5">
            <Shield className="w-3.5 h-3.5 text-cyan-400" />
            <span>TraceId: <strong className="text-slate-200">{incident.traceId || 'N/A'}</strong></span>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex border-b border-slate-800 bg-slate-900/80 px-6 space-x-6 text-xs font-medium">
          <button
            onClick={() => setActiveTab('stacktrace')}
            className={`py-3 flex items-center gap-2 border-b-2 transition ${activeTab === 'stacktrace' ? 'border-indigo-500 text-indigo-400 font-semibold' : 'border-transparent text-slate-400 hover:text-slate-200'}`}
          >
            <Terminal className="w-3.5 h-3.5" /> Stack Trace
          </button>
          <button
            onClick={() => setActiveTab('request')}
            className={`py-3 flex items-center gap-2 border-b-2 transition ${activeTab === 'request' ? 'border-indigo-500 text-indigo-400 font-semibold' : 'border-transparent text-slate-400 hover:text-slate-200'}`}
          >
            <FileCode className="w-3.5 h-3.5" /> Request Capsule & Evidence
          </button>
          <button
            onClick={() => setActiveTab('raw')}
            className={`py-3 flex items-center gap-2 border-b-2 transition ${activeTab === 'raw' ? 'border-indigo-500 text-indigo-400 font-semibold' : 'border-transparent text-slate-400 hover:text-slate-200'}`}
          >
            <Copy className="w-3.5 h-3.5" /> Raw Incident JSON
          </button>
        </div>

        {/* Body content */}
        <div className="p-6 overflow-y-auto flex-1 font-mono text-xs text-slate-300">
          {activeTab === 'stacktrace' && (
            <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 overflow-x-auto">
              <pre className="text-rose-300 leading-relaxed whitespace-pre-wrap">
                {incident.stackTrace || 'No stack trace recorded.'}
              </pre>
            </div>
          )}

          {activeTab === 'request' && (
            <div className="space-y-4">
              <div>
                <h4 className="text-slate-400 font-bold uppercase text-[10px] tracking-wider mb-2">Request Headers (Redacted)</h4>
                <div className="bg-slate-950 p-3 rounded-xl border border-slate-800 space-y-1">
                  {parsedRaw?.request?.headers ? (
                    Object.entries(parsedRaw.request.headers).map(([k, v]) => (
                      <div key={k} className="flex justify-between">
                        <span className="text-indigo-400">{k}:</span>
                        <span className={v === '[REDACTED]' ? 'text-amber-400 font-bold' : 'text-slate-300'}>{v}</span>
                      </div>
                    ))
                  ) : (
                    <p className="text-slate-500">No headers snapshot available</p>
                  )}
                </div>
              </div>

              <div>
                <h4 className="text-slate-400 font-bold uppercase text-[10px] tracking-wider mb-2">Request Payload</h4>
                <div className="bg-slate-950 p-3 rounded-xl border border-slate-800">
                  <pre className="text-slate-200 whitespace-pre-wrap">
                    {parsedRaw?.request?.body || 'Empty request body'}
                  </pre>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'raw' && (
            <div className="relative bg-slate-950 p-4 rounded-xl border border-slate-800 overflow-x-auto">
              <button
                onClick={handleCopyRaw}
                className="absolute top-3 right-3 bg-slate-800 hover:bg-slate-700 text-xs px-2.5 py-1 rounded text-slate-300 flex items-center gap-1 transition"
              >
                {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                {copied ? 'Copied' : 'Copy'}
              </button>
              <pre className="text-emerald-400 leading-relaxed">
                {JSON.stringify(parsedRaw || incident, null, 2)}
              </pre>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-4 border-t border-slate-800 bg-slate-900/50 flex justify-end">
          <button
            onClick={onClose}
            className="bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold px-4 py-2 rounded-lg transition"
          >
            Close Explorer
          </button>
        </div>
      </div>
    </div>
  );
}
