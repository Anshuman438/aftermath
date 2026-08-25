const API_BASE = 'http://localhost:8090/api/v1';

export async function fetchIncidents(page = 0, size = 20, search = '') {
  try {
    const url = new URL(`${API_BASE}/incidents`);
    url.searchParams.append('page', page);
    url.searchParams.append('size', size);
    if (search) {
      url.searchParams.append('search', search);
    }
    const res = await fetch(url.toString());
    if (!res.ok) {
      throw new Error(`HTTP error ${res.status}`);
    }
    return await res.json();
  } catch (err) {
    console.error('Failed to fetch incidents:', err);
    throw err;
  }
}

export async function fetchIncidentById(incidentId) {
  try {
    const res = await fetch(`${API_BASE}/incidents/${incidentId}`);
    if (!res.ok) {
      throw new Error(`HTTP error ${res.status}`);
    }
    return await res.json();
  } catch (err) {
    console.error(`Failed to fetch incident ${incidentId}:`, err);
    throw err;
  }
}
