import React, { useState } from 'react';
import { X, Copy, Check, Terminal, FileCode, Shield, Server, Clock, Play, RotateCcw, AlertTriangle, CheckCircle } from 'lucide-react';
import { triggerReplay } from '../api/collectorClient';

export default function IncidentDetailModal({ incident, onClose }) {
  const [copied, setCopied] = useState(false);
  const [activeTab, setActiveTab] = useState('stacktrace');
  const [replaying, setReplaying] = useState(false);
  const [replayResult, setReplayResult] = useState(null);
  const [replayError, setReplayError] = useState(null);
  const [targetUrl, setTargetUrl] = useState('http://localhost:8082');

  if (!incident) return null;

  const handleCopyRaw = () => {
    navigator.clipboard.writeText(incident.rawJson || JSON.stringify(incident, null, 2));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleRunReplay = async () => {
    setReplaying(true);
    setReplayError(null);
    try {
      const res = await triggerReplay(incident.incidentId, targetUrl);
      setReplayResult(res);
      setActiveTab('replay');
    } catch (err) {
      setReplayError('Failed to execute replay against target service. Make sure target service is running.');
    } finally {
      setReplaying(false);
    }
  };

  let parsedRaw = null;
  try {
    if (incident.rawJson) {
      parsedRaw = JSON.parse(incident.rawJson);
    }
  } catch (e) {}

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
          
          <div className="flex items-center space-x-2">
            <button
              onClick={handleRunReplay}
              disabled={replaying}
              className="flex items-center space-x-1.5 bg-rose-600 hover:bg-rose-500 disabled:opacity-50 text-white text-xs font-semibold px-3 py-1.5 rounded-lg transition shadow-lg shadow-rose-600/20"
            >
              {replaying ? <RotateCcw className="w-3.5 h-3.5 animate-spin" /> : <Play className="w-3.5 h-3.5 fill-current" />}
              <span>{replaying ? 'Replaying...' : 'Replay Incident'}</span>
            </button>
            
            <button
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-white rounded-lg bg-slate-800 hover:bg-slate-700 transition"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Metadata bar */}
        <div className="px-6 py-3 bg-slate-950/60 border-b border-slate-800 flex flex-wrap gap-4 text-xs font-mono text-slate-400">
          <div className="flex items-center space-x-1.5">
            <Server className="w-3.5 h-3.5 text-indigo-400" />
            <span>Service: <strong className="text-slate-200">{incident.serviceName || 'unknown'}</strong></span>
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
            <FileCode className="w-3.5 h-3.5" /> Request Capsule
          </button>
          {replayResult && (
            <button
              onClick={() => setActiveTab('replay')}
              className={`py-3 flex items-center gap-2 border-b-2 transition ${activeTab === 'replay' ? 'border-indigo-500 text-indigo-400 font-semibold' : 'border-transparent text-slate-400 hover:text-slate-200'}`}
            >
              <RotateCcw className="w-3.5 h-3.5 text-rose-400" /> Replay Results
            </button>
          )}
          <button
            onClick={() => setActiveTab('raw')}
            className={`py-3 flex items-center gap-2 border-b-2 transition ${activeTab === 'raw' ? 'border-indigo-500 text-indigo-400 font-semibold' : 'border-transparent text-slate-400 hover:text-slate-200'}`}
          >
            <Copy className="w-3.5 h-3.5" /> Raw Incident JSON
          </button>
        </div>

        {/* Body content */}
        <div className="p-6 overflow-y-auto flex-1 font-mono text-xs text-slate-300">
          
          {replayError && (
            <div className="mb-4 p-3 rounded-lg bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 shrink-0" />
              <span>{replayError}</span>
            </div>
          )}

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

          {activeTab === 'replay' && replayResult && (
            <div className="space-y-4">
              <div className="p-4 rounded-xl border bg-slate-950 flex items-center justify-between border-slate-800">
                <div className="flex items-center space-x-3">
                  {replayResult.reproduced ? (
                    <div className="bg-emerald-500/20 text-emerald-400 p-2 rounded-lg border border-emerald-500/30">
                      <CheckCircle className="w-5 h-5" />
                    </div>
                  ) : (
                    <div className="bg-rose-500/20 text-rose-400 p-2 rounded-lg border border-rose-500/30">
                      <AlertTriangle className="w-5 h-5" />
                    </div>
                  )}
                  <div>
                    <h4 className="font-bold text-slate-200 text-sm">
                      {replayResult.reproduced ? 'BUG REPRODUCED SUCCESSFULLY' : 'REPLAY DIFFERENT RESPONSE'}
                    </h4>
                    <p className="text-[11px] text-slate-400 mt-0.5">
                      Target: {replayResult.targetBaseUrl} | Time: {replayResult.executionTimeMs}ms
                    </p>
                  </div>
                </div>

                <div className="flex items-center space-x-3 font-mono text-xs">
                  <div className="text-right">
                    <span className="block text-slate-500 text-[10px]">ORIGINAL</span>
                    <span className="text-rose-400 font-bold">HTTP {replayResult.originalStatusCode}</span>
                  </div>
                  <span className="text-slate-600">➔</span>
                  <div>
                    <span className="block text-slate-500 text-[10px]">REPLAYED</span>
                    <span className="text-amber-400 font-bold">HTTP {replayResult.replayedStatusCode}</span>
                  </div>
                </div>
              </div>

              <div>
                <h4 className="text-slate-400 font-bold uppercase text-[10px] tracking-wider mb-2">Replayed Response Body</h4>
                <div className="bg-slate-950 p-3 rounded-xl border border-slate-800">
                  <pre className="text-rose-300 whitespace-pre-wrap">
                    {replayResult.replayedResponseBody || 'Empty response body'}
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
        <div className="p-4 border-t border-slate-800 bg-slate-900/50 flex justify-between items-center">
          <div className="flex items-center space-x-2">
            <span className="text-[11px] text-slate-400">Target Base URL:</span>
            <input
              type="text"
              value={targetUrl}
              onChange={(e) => setTargetUrl(e.target.value)}
              className="bg-slate-950 border border-slate-800 rounded px-2.5 py-1 text-xs text-slate-200 font-mono focus:outline-none focus:border-indigo-500"
            />
          </div>

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
