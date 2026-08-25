import React, { useState, useEffect, useCallback } from 'react';
import Navbar from './components/Navbar';
import SearchBar from './components/SearchBar';
import IncidentCard from './components/IncidentCard';
import IncidentDetailModal from './components/IncidentDetailModal';
import { fetchIncidents } from './api/collectorClient';
import { AlertTriangle, Inbox, ChevronLeft, ChevronRight } from 'lucide-react';

export default function App() {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [selectedIncident, setSelectedIncident] = useState(null);

  const loadIncidents = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchIncidents(page, 10, search);
      setIncidents(data.content || []);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);
    } catch (err) {
      setError('Could not connect to Aftermath Collector on http://localhost:8090. Make sure the aftermath-collector service is running.');
    } finally {
      setLoading(false);
    }
  }, [page, search]);

  useEffect(() => {
    loadIncidents();
  }, [loadIncidents]);

  return (
    <div className="min-h-screen flex flex-col bg-slate-950 text-slate-100">
      <Navbar onRefresh={loadIncidents} />

      <main className="flex-1 max-w-7xl w-full mx-auto px-6 py-8">
        
        {/* Top Control Bar */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
          <div>
            <h2 className="text-2xl font-bold text-white tracking-tight">Captured Incidents</h2>
            <p className="text-xs text-slate-400 mt-1">
              Showing {incidents.length} of {totalElements} captured production failure capsules
            </p>
          </div>

          <SearchBar search={search} setSearch={(s) => { setSearch(s); setPage(0); }} />
        </div>

        {/* Error Banner */}
        {error && (
          <div className="mb-6 p-4 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 flex items-center space-x-3 text-sm">
            <AlertTriangle className="w-5 h-5 shrink-0 text-rose-400" />
            <span>{error}</span>
          </div>
        )}

        {/* Loading Skeleton */}
        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {[1, 2, 3, 4].map((i) => (
              <div key={i} className="h-36 bg-slate-900/50 rounded-xl border border-slate-800 animate-pulse p-4">
                <div className="h-4 bg-slate-800 w-1/3 rounded mb-3"></div>
                <div className="h-6 bg-slate-800 w-2/3 rounded mb-2"></div>
                <div className="h-4 bg-slate-800 w-1/2 rounded"></div>
              </div>
            ))}
          </div>
        ) : incidents.length === 0 ? (
          /* Empty State */
          <div className="py-16 text-center border border-dashed border-slate-800 rounded-2xl bg-slate-900/30">
            <Inbox className="w-12 h-12 text-slate-600 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-300">No Incidents Found</h3>
            <p className="text-xs text-slate-500 max-w-sm mx-auto mt-1">
              {search ? 'No failure capsules match your filter query.' : 'Trigger a failure in the sample application to capture your first incident!'}
            </p>
          </div>
        ) : (
          /* Grid of Incidents */
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {incidents.map((incident) => (
              <IncidentCard
                key={incident.incidentId}
                incident={incident}
                onSelect={(inc) => setSelectedIncident(inc)}
              />
            ))}
          </div>
        )}

        {/* Pagination Bar */}
        {totalPages > 1 && (
          <div className="flex items-center justify-between mt-8 pt-4 border-t border-slate-800">
            <button
              disabled={page === 0}
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              className="flex items-center space-x-1 text-xs font-semibold px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-800 transition"
            >
              <ChevronLeft className="w-4 h-4" /> Previous
            </button>
            <span className="text-xs font-mono text-slate-400">
              Page {page + 1} of {totalPages}
            </span>
            <button
              disabled={page + 1 >= totalPages}
              onClick={() => setPage((p) => p + 1)}
              className="flex items-center space-x-1 text-xs font-semibold px-3 py-1.5 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 disabled:opacity-40 disabled:cursor-not-allowed hover:bg-slate-800 transition"
            >
              Next <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        )}

        {/* Detail Modal */}
        {selectedIncident && (
          <IncidentDetailModal
            incident={selectedIncident}
            onClose={() => setSelectedIncident(null)}
          />
        )}
      </main>
    </div>
  );
}
