//! AFTERMATH Failure Capture & Redaction SDK for Rust Actix/Axum Web Applications
use serde::{Deserialize, Serialize};
use std::collections::HashMap;

#[derive(Serialize, Deserialize, Debug)]
pub struct RequestSnapshot {
    pub method: String,
    pub uri: String,
    pub headers: HashMap<String, String>,
    pub body: String,
    pub timestamp: u64,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct ErrorSnapshot {
    pub exception_class: String,
    pub message: String,
    pub stack_trace: String,
    pub status_code: u16,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct IncidentEvent {
    pub incident_id: String,
    pub trace_id: String,
    pub timestamp: u64,
    pub request: RequestSnapshot,
    pub error: ErrorSnapshot,
}

pub struct AftermathRustSdk {
    pub collector_url: String,
    pub service_name: String,
}

impl AftermathRustSdk {
    pub fn new(collector_url: &str, service_name: &str) -> Self {
        Self {
            collector_url: collector_url.to_string(),
            service_name: service_name.to_string(),
        }
    }

    pub fn redact_headers(&self, headers: &HashMap<String, String>) -> HashMap<String, String> {
        let sensitive = vec!["authorization", "cookie", "x-api-key", "proxy-authorization"];
        let mut redacted = HashMap::new();
        for (k, v) in headers {
            if sensitive.contains(&k.to_lowercase().as_str()) {
                redacted.insert(k.clone(), "[REDACTED]".to_string());
            } else {
                redacted.insert(k.clone(), v.clone());
            }
        }
        redacted
    }
}
